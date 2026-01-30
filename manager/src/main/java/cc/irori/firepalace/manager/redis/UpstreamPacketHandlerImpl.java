package cc.irori.firepalace.manager.redis;

import cc.irori.firepalace.api.util.WorldActionQueue;
import cc.irori.firepalace.common.redis.IncomingPacketRegistry;
import cc.irori.firepalace.common.redis.PacketHandler;
import cc.irori.firepalace.common.redis.UpstreamPacketHandler;
import cc.irori.firepalace.common.redis.protocol.UpstreamPacket;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamQueueJoinPacket;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamRequestStatusPacket;
import cc.irori.firepalace.common.util.Logs;
import cc.irori.firepalace.manager.FirepalaceImpl;
import cc.irori.firepalace.manager.user.UserImpl;
import cc.irori.firepalace.manager.util.GameUtil;
import com.hypixel.hytale.logger.HytaleLogger;

public class UpstreamPacketHandlerImpl implements UpstreamPacketHandler {

  private static final HytaleLogger LOGGER = Logs.logger();

  private final FirepalaceImpl firepalace;

  public UpstreamPacketHandlerImpl(FirepalaceImpl firepalace) {
    this.firepalace = firepalace;
  }

  @Override
  public IncomingPacketRegistry<? extends PacketHandler<UpstreamPacket>> getPacketRegistry() {
    return UpstreamIncomingPacketRegistry.INSTANCE;
  }

  @Override
  public void handleQueueJoin(UpstreamQueueJoinPacket packet) {
    firepalace.getWorldActionQueue().enqueueReady(packet.uuid(),
        WorldActionQueue.ANY_WORLD, () -> {
          UserImpl user = firepalace.getUserManager().getUser(packet.uuid());
          if (user == null) {
            LOGGER.atWarning().log("Cannot handle join request for unknown user: %s",
                packet.uuid());
            return;
          }

          GameUtil.joinGameById(firepalace, user, packet.gameId());
        });
  }

  @Override
  public void handleRequestStatus(UpstreamRequestStatusPacket packet) {
    GameUtil.sendGameStatusPacket(firepalace);
  }
}
