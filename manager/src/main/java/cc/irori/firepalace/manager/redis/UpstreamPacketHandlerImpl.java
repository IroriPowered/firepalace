package cc.irori.firepalace.manager.redis;

import cc.irori.firepalace.api.user.User;
import cc.irori.firepalace.api.util.WorldActionQueue;
import cc.irori.firepalace.common.redis.UpstreamPacketHandler;
import cc.irori.firepalace.common.redis.protocol.impl.downstream.DownstreamStatusPacket;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamQueueJoinPacket;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamRequestStatusPacket;
import cc.irori.firepalace.common.status.GameStatus;
import cc.irori.firepalace.common.util.Logs;
import cc.irori.firepalace.manager.FirepalaceImpl;
import cc.irori.firepalace.manager.game.GameHolder;
import cc.irori.firepalace.manager.user.UserImpl;
import com.hypixel.hytale.logger.HytaleLogger;
import java.util.ArrayList;
import java.util.List;

public class UpstreamPacketHandlerImpl implements UpstreamPacketHandler {

  private static final HytaleLogger LOGGER = Logs.logger();

  private final FirepalaceImpl firepalace;

  public UpstreamPacketHandlerImpl(FirepalaceImpl firepalace) {
    this.firepalace = firepalace;
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
          GameHolder holder = firepalace.getGameManager().getGameHolder(packet.gameId());
          if (holder == null) {
            LOGGER.atWarning().log("Join request for unknown game: %s",
                packet.gameId());
            return;
          }

          user.joinGame(holder.getMetadata());
        });
  }

  @Override
  public void handleRequestStatus(UpstreamRequestStatusPacket packet) {
    List<GameStatus> statusList = new ArrayList<>();
    for (GameHolder holder : firepalace.getGameManager().getAllGameHolders()) {
      statusList.add(new GameStatus(
          holder.getMetadata(),
          holder.getGameInstance().getUsers().stream()
              .map(User::getUuid)
              .toList()
      ));
    }
    firepalace.getRedis().sendPacket(new DownstreamStatusPacket(statusList));
  }
}
