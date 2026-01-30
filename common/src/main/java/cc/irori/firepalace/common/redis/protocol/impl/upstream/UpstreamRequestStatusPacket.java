package cc.irori.firepalace.common.redis.protocol.impl.upstream;

import cc.irori.firepalace.common.redis.UpstreamPacketHandler;
import cc.irori.firepalace.common.redis.protocol.UpstreamPacket;
import org.bson.BsonDocument;

public class UpstreamRequestStatusPacket implements UpstreamPacket {

  public static final String ID = "request_status";

  public UpstreamRequestStatusPacket() {
  }

  public UpstreamRequestStatusPacket(BsonDocument root) {
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public void serialize(BsonDocument root) {
  }

  @Override
  public void handle(UpstreamPacketHandler handler) {
    handler.handleRequestStatus(this);
  }
}
