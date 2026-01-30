package cc.irori.firepalace.common.redis.protocol;

import cc.irori.firepalace.common.redis.DownstreamPacketHandler;
import cc.irori.firepalace.common.redis.Recipient;

public interface DownstreamPacket extends Packet<DownstreamPacketHandler> {

  @Override
  void handle(DownstreamPacketHandler handler);

  @Override
  default Recipient getRecipient() {
    return Recipient.DOWNSTREAM;
  }
}
