package cc.irori.firepalace.common.redis.protocol.impl.downstream;

import cc.irori.firepalace.common.redis.DownstreamPacketHandler;
import cc.irori.firepalace.common.redis.protocol.DownstreamPacket;
import java.util.UUID;
import org.bson.BsonDocument;
import org.bson.BsonString;

public record DownstreamAcceptJoinPacket(UUID uuid) implements DownstreamPacket {

  public static final String ID = "accept_join";

  public DownstreamAcceptJoinPacket(BsonDocument root) {
    this(UUID.fromString(root.getString("uuid").getValue()));
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public void handle(DownstreamPacketHandler handler) {
    handler.handleAcceptJoin(this);
  }

  @Override
  public void serialize(BsonDocument root) {
    root.append("uuid", new BsonString(uuid.toString()));
  }
}
