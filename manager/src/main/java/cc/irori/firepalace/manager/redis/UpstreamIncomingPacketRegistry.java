package cc.irori.firepalace.manager.redis;

import cc.irori.firepalace.common.redis.IncomingPacketRegistry;
import cc.irori.firepalace.common.redis.UpstreamPacketHandler;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamRequestJoinPacket;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamRequestStatusPacket;

public class UpstreamIncomingPacketRegistry extends IncomingPacketRegistry<UpstreamPacketHandler> {

  public static final UpstreamIncomingPacketRegistry INSTANCE = new UpstreamIncomingPacketRegistry();

  protected UpstreamIncomingPacketRegistry() {
    register(UpstreamRequestJoinPacket.ID, UpstreamRequestJoinPacket::new);
    register(UpstreamRequestStatusPacket.ID, UpstreamRequestStatusPacket::new);
  }
}
