package cc.irori.firepalace.manager.redis;

import cc.irori.firepalace.api.util.EventActionQueue;
import cc.irori.firepalace.common.redis.IncomingPacketRegistry;
import cc.irori.firepalace.common.redis.PacketHandler;
import cc.irori.firepalace.common.redis.UpstreamPacketHandler;
import cc.irori.firepalace.common.redis.protocol.UpstreamPacket;
import cc.irori.firepalace.common.redis.protocol.impl.downstream.DownstreamAcceptJoinPacket;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamRequestJoinPacket;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamRequestStatusPacket;
import cc.irori.firepalace.common.util.Logs;
import cc.irori.firepalace.manager.FirepalaceImpl;
import cc.irori.firepalace.manager.game.GameHolder;
import cc.irori.firepalace.manager.user.UserImpl;
import cc.irori.firepalace.manager.util.GameUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;

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
  public void handleRequestJoin(UpstreamRequestJoinPacket packet) {
    GameHolder holder = firepalace.getGameManager().getGameHolder(packet.gameId());
    if (holder == null) {
        LOGGER.atWarning().log("Cannot handle join request for unknown game ID: %s",
            packet.gameId());
        return;
    }

    GameUtil.tryCreateGame(holder)
        .thenCompose(creating -> GameUtil.tryPreJoin(holder, packet.uuid(), creating))
        .thenAccept(result -> firepalace.getRedis().sendPacket(new DownstreamAcceptJoinPacket(holder.getMetadata(), packet.uuid())));

    firepalace.getWorldActionQueue().enqueueJoin(packet.uuid(),
        EventActionQueue.ANY_WORLD, event -> {
          PlayerConnectEvent connectEvent = (PlayerConnectEvent) event;
          UserImpl user = firepalace.getUserManager().getUser(packet.uuid());
          if (user == null) {
            LOGGER.atWarning().log("Cannot handle join request for unknown user: %s",
                packet.uuid());
            return;
          }

          GameUtil.joinGameById(firepalace, user, packet.gameId(), connectEvent).join();
        });
  }

  @Override
  public void handleRequestStatus(UpstreamRequestStatusPacket packet) {
    GameUtil.sendGameStatusPacket(firepalace);
  }
}
