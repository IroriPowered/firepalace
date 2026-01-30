package cc.irori.firepalace.gui.redis;

import cc.irori.firepalace.common.redis.DownstreamPacketHandler;
import cc.irori.firepalace.common.redis.IncomingPacketRegistry;
import cc.irori.firepalace.common.redis.protocol.impl.downstream.DownstreamStatusPacket;

public class DownstreamIncomingPacketRegistry
    extends IncomingPacketRegistry<DownstreamPacketHandler> {

  public static final DownstreamIncomingPacketRegistry INSTANCE =
      new DownstreamIncomingPacketRegistry();

  protected DownstreamIncomingPacketRegistry() {
    register(DownstreamStatusPacket.ID, DownstreamStatusPacket::new);
  }
}
