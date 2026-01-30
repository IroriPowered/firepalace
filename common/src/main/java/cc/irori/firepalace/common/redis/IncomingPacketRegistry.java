package cc.irori.firepalace.common.redis;

import cc.irori.firepalace.common.redis.protocol.Packet;
import java.util.HashMap;
import java.util.Map;
import org.bson.BsonDocument;

public class IncomingPacketRegistry<T extends PacketHandler<?>> {

  private final Map<String, PacketFactory<T>> constructors = new HashMap<>();

  protected IncomingPacketRegistry() {
  }

  public void register(String id, PacketFactory<T> constructor) {
    constructors.put(id, constructor);
  }

  public Packet<T> createPacket(BsonDocument root) {
    String id = root.getString(RedisParticipant.PACKET_ID_FIELD).getValue();
    PacketFactory<T> constructor = constructors.get(id);
    if (constructor == null) {
      throw new IllegalArgumentException("Unknown packet id: " + id);
    }
    return constructor.create(root);
  }
}
