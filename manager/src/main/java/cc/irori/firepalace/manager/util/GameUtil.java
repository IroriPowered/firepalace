package cc.irori.firepalace.manager.util;

import cc.irori.firepalace.api.game.JoinResult;
import cc.irori.firepalace.api.user.User;
import cc.irori.firepalace.api.util.Colors;
import cc.irori.firepalace.common.redis.protocol.impl.downstream.DownstreamStatusPacket;
import cc.irori.firepalace.common.status.GameStatus;
import cc.irori.firepalace.common.util.Logs;
import cc.irori.firepalace.manager.FirepalaceImpl;
import cc.irori.firepalace.manager.game.GameHolder;
import cc.irori.firepalace.manager.game.GameInstanceImpl;
import cc.irori.firepalace.manager.user.UserImpl;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;

public class GameUtil {

  private static final HytaleLogger LOGGER = Logs.logger();

  // Private constructor to prevent instantiation
  private GameUtil() {
  }

  public static List<GameStatus> getGameStatusList(FirepalaceImpl firepalace) {
    List<GameStatus> statusList = new ArrayList<>();
    for (GameHolder holder : firepalace.getGameManager().getAllGameHolders()) {
      statusList.add(new GameStatus(
          holder.getMetadata(),
          holder.hasGame() ? holder.getGameInstance().getUsers().stream()
              .map(User::getUuid)
              .toList() : List.of()
      ));
    }
    return statusList;
  }

  public static void sendGameStatusPacket(FirepalaceImpl firepalace) {
    firepalace.getRedis().sendPacket(
        new DownstreamStatusPacket(GameUtil.getGameStatusList(firepalace)));
  }

  public static CompletableFuture<Void> joinGameById(FirepalaceImpl firepalace,
                                                     UserImpl user, String gameId) {
    return joinGameById(firepalace, user, gameId, null);
  }

  public static CompletableFuture<Void> joinGameById(FirepalaceImpl firepalace,
                                                     UserImpl user, String gameId,
                                                     @Nullable PlayerConnectEvent connectEvent) {
    if (!firepalace.getGameManager().isGameAvailable(gameId)) {
      user.getPlayerRef().sendMessage(Message.join(
          Message.raw("Invalid game ID: "),
          Message.raw(gameId).color(Colors.MUSTARD)
      ));
      return CompletableFuture.completedFuture(null);
    }

    GameHolder holder = firepalace.getGameManager().getGameHolder(gameId);
    return user.joinGame(holder.getMetadata(), connectEvent)
        .exceptionally(t -> {
          LOGGER.atSevere().withCause(t).log("Error while player %s tried to join game %s",
              user.getPlayerRef().getUsername(), gameId);
          user.getPlayerRef().getPacketHandler().disconnect("Failed to join the game!");
          return null;
        });
  }

  public static CompletableFuture<Boolean> tryCreateGame(GameHolder holder) {
    if (holder.hasGame()) {
      return CompletableFuture.completedFuture(false);
    }

    LOGGER.atInfo().log("Creating new game instance for game: %s", holder.getMetadata().id());
    return holder.createGame().thenApply(v -> true);
  }

  public static CompletableFuture<JoinResult> tryPreJoin(GameHolder holder, UUID uuid, boolean isCreating) {
    GameInstanceImpl instance = holder.getGameInstance();
    if (instance.isPreJoinHandled(uuid)) {
      return CompletableFuture.completedFuture(instance.getPreJoinResult(uuid));
    }

    return instance.handlePreJoin(uuid, isCreating);
  }
}
