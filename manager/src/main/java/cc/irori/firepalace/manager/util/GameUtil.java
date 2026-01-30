package cc.irori.firepalace.manager.util;

import cc.irori.firepalace.api.user.User;
import cc.irori.firepalace.api.util.Colors;
import cc.irori.firepalace.common.redis.protocol.impl.downstream.DownstreamStatusPacket;
import cc.irori.firepalace.common.status.GameStatus;
import cc.irori.firepalace.common.util.Logs;
import cc.irori.firepalace.manager.FirepalaceImpl;
import cc.irori.firepalace.manager.game.GameHolder;
import cc.irori.firepalace.manager.user.UserImpl;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GameUtil {

  private static final HytaleLogger LOGGER = Logs.logger();

  // Private constructor to prevent instantiation
  private GameUtil() {
  }

  public static void sendGameStatusPacket(FirepalaceImpl firepalace) {
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

  public static CompletableFuture<Void> joinGameById(FirepalaceImpl firepalace, UserImpl user, String gameId) {
    if (!firepalace.getGameManager().isGameRegistered(gameId)) {
      user.getPlayerRef().sendMessage(Message.join(
          Message.raw("Invalid game ID: "),
          Message.raw(gameId).color(Colors.MUSTARD)
      ));
      return CompletableFuture.completedFuture(null);
    }

    GameHolder holder = firepalace.getGameManager().getGameHolder(gameId);
    return user.joinGame(holder.getMetadata())
        .exceptionally(t -> {
          LOGGER.atSevere().withCause(t).log("Error while player %s tried to join game %s",
              user.getPlayerRef().getUsername(), gameId);
          user.getPlayerRef().getPacketHandler().disconnect("Failed to join the game!");
          return null;
        });
  }
}
