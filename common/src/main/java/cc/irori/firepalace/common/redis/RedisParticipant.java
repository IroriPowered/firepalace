package cc.irori.firepalace.common.redis;

import cc.irori.firepalace.common.redis.protocol.Packet;
import cc.irori.firepalace.common.util.Logs;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.io.BasicOutputBuffer;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.RedisClient;

public class RedisParticipant<R extends PacketHandler<?>, S extends Packet<?>> {

  public static final String PACKET_ID_FIELD = "_id";

  private static final BsonDocumentCodec DOCUMENT_CODEC = new BsonDocumentCodec();
  private static final EncoderContext ENCODER_CONTEXT =
      EncoderContext.builder().isEncodingCollectibleDocument(true).build();
  private static final DecoderContext DECODER_CONTEXT =
      DecoderContext.builder().build();

  private final Recipient me;
  private final RedisClient client;
  private final Jedis pubSub;

  public RedisParticipant(RedisConfig config, Recipient me, R packetHandler) {
    this.me = me;

    ConnectionPoolConfig connectionPoolConfig = new ConnectionPoolConfig();
    connectionPoolConfig.setMaxIdle(1);

    client = RedisClient.builder()
        .poolConfig(connectionPoolConfig)
        .hostAndPort(config.redisHost, config.redisPort)
        .build();
    pubSub = new Jedis(config.redisHost, config.redisPort);

    new Thread(() -> {
      pubSub.subscribe(new Receiver<>(packetHandler), me.getId()
          .getBytes(StandardCharsets.UTF_8));
    }, "Redis packet listener thread").start();
  }

  public void sendPacket(S packet) {
    sendPacket(packet, packet.getRecipient());
  }

  public void sendPacket(S packet, Recipient to) {
    BsonDocument document = new BsonDocument();
    document.put(PACKET_ID_FIELD, new BsonString(packet.getId()));
    packet.serialize(document);

    try (BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonBinaryWriter writer = new BsonBinaryWriter(buffer)) {
      DOCUMENT_CODEC.encode(writer, document, ENCODER_CONTEXT);
      client.publish(to.getId().getBytes(StandardCharsets.UTF_8), buffer.toByteArray());
    }
  }

  public Recipient getRecipient() {
    return me;
  }

  public void shutdown() {
    pubSub.close();
    client.close();
  }

  static class Receiver<T extends PacketHandler<?>> extends BinaryJedisPubSub {

    private final T packetHandler;

    public Receiver(T packetHandler) {
      this.packetHandler = packetHandler;
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
      try (BsonBinaryReader reader = new BsonBinaryReader(ByteBuffer.wrap(message))) {
        BsonDocument document = DOCUMENT_CODEC.decode(reader, DECODER_CONTEXT);
        //noinspection unchecked
        Packet<T> packet =
            (Packet<T>) packetHandler.getPacketRegistry().createPacket(document);
        packet.handle(packetHandler);
      } catch (Exception e) {
        Logs.logger().atSevere().withCause(e).log("Failed to handle incoming Redis packet");
      }
    }
  }
}
