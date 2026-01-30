package cc.irori.firepalace.common.redis;

import cc.irori.firepalace.common.redis.protocol.UpstreamPacket;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamQueueJoinPacket;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamRequestStatusPacket;

public interface UpstreamPacketHandler extends PacketHandler<UpstreamPacket> {

  void handleQueueJoin(UpstreamQueueJoinPacket packet);

  void handleRequestStatus(UpstreamRequestStatusPacket packet);
}
