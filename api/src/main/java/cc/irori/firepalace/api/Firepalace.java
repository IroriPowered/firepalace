package cc.irori.firepalace.api;

import cc.irori.firepalace.api.game.Game;
import cc.irori.firepalace.api.game.GameInstance;
import cc.irori.firepalace.api.game.metadata.GameMetadata;
import cc.irori.firepalace.api.user.User;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.UUID;
import java.util.function.Function;

public interface Firepalace {

  void registerGame(GameMetadata metadata, Function<GameInstance, Game> gameFactory);

  void unregisterGame(GameMetadata metadata);

  User getUser(UUID uuid);

  default User getUser(PlayerRef playerRef) {
    return getUser(playerRef.getUuid());
  }

  static Firepalace api() {
    return FirepalaceStore.instance;
  }

  static void setup(Firepalace firepalace) {
    FirepalaceStore.instance = firepalace;
  }
}
