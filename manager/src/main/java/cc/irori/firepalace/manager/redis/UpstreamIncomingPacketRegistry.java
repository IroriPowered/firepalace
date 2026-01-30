package cc.irori.firepalace.manager.redis;

import cc.irori.firepalace.common.redis.IncomingPacketRegistry;
import cc.irori.firepalace.common.redis.UpstreamPacketHandler;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamQueueJoinPacket;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamRequestStatusPacket;

public class UpstreamIncomingPacketRegistry extends IncomingPacketRegistry<UpstreamPacketHandler> {

  public UpstreamIncomingPacketRegistry() {
    register(UpstreamQueueJoinPacket.ID, UpstreamQueueJoinPacket::new);
    register(UpstreamRequestStatusPacket.ID, UpstreamRequestStatusPacket::new);
  }
}
