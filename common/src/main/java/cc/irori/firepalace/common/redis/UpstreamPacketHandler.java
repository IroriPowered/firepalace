package cc.irori.firepalace.common.redis;

import cc.irori.firepalace.common.redis.protocol.UpstreamPacket;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamRequestJoinPacket;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamRequestStatusPacket;

public interface UpstreamPacketHandler extends PacketHandler<UpstreamPacket> {

  void handleRequestJoin(UpstreamRequestJoinPacket packet);

  void handleRequestStatus(UpstreamRequestStatusPacket packet);
}
