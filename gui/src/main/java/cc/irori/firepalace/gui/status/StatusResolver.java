package cc.irori.firepalace.gui.status;

import cc.irori.firepalace.common.status.GameStatus;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.List;

public interface StatusResolver {

  List<GameStatus> resolve();

  void joinGame(PlayerRef playerRef, String gameId);

  default boolean exists(String gameId) {
    return resolve().stream().anyMatch(status -> status.metadata().id().equals(gameId));
  }

  default boolean existsAndIsAvailable(String gameId) {
    return resolve().stream()
        .filter(status -> status.metadata().available())
        .anyMatch(status -> status.metadata().id().equals(gameId));
  }
}
