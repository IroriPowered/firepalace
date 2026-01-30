package cc.irori.firepalace.common.redis.protocol.impl.upstream;

import cc.irori.firepalace.common.redis.UpstreamPacketHandler;
import cc.irori.firepalace.common.redis.protocol.UpstreamPacket;
import java.util.UUID;
import org.bson.BsonDocument;
import org.bson.BsonString;

public record UpstreamRequestJoinPacket(UUID uuid, String gameId) implements UpstreamPacket {

  public static final String ID = "queue_join";

  public UpstreamRequestJoinPacket(BsonDocument root) {
    this(
        UUID.fromString(root.getString("uuid").getValue()),
        root.getString("game_id").getValue()
    );
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public void handle(UpstreamPacketHandler handler) {
    handler.handleRequestJoin(this);
  }

  @Override
  public void serialize(BsonDocument root) {
    root.append("uuid", new BsonString(uuid.toString()));
    root.append("game_id", new BsonString(gameId));
  }
}
