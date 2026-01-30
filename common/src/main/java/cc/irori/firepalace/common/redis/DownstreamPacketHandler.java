package cc.irori.firepalace.common.redis;

import cc.irori.firepalace.common.redis.protocol.DownstreamPacket;
import cc.irori.firepalace.common.redis.protocol.impl.downstream.DownstreamAcceptJoinPacket;
import cc.irori.firepalace.common.redis.protocol.impl.downstream.DownstreamStatusPacket;

public interface DownstreamPacketHandler extends PacketHandler<DownstreamPacket> {

  void handleAcceptJoin(DownstreamAcceptJoinPacket packet);

  void handleStatus(DownstreamStatusPacket packet);
}
