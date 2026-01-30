package cc.irori.firepalace.common.redis.protocol;

import cc.irori.firepalace.common.redis.Recipient;
import cc.irori.firepalace.common.redis.UpstreamPacketHandler;

public interface UpstreamPacket extends Packet<UpstreamPacketHandler> {

  @Override
  void handle(UpstreamPacketHandler handler);

  @Override
  default Recipient getRecipient() {
    return Recipient.UPSTREAM;
  }
}
