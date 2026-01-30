package cc.irori.firepalace.api.game;

import cc.irori.firepalace.api.user.User;
import java.util.concurrent.CompletableFuture;

public abstract class Game {

  private final GameInstance instance;

  public Game(GameInstance instance) {
    this.instance = instance;
  }

  public GameInstance getGameInstance() {
    return instance;
  }

  public abstract CompletableFuture<JoinResult> onUserPreJoin(User user, boolean isCreating);

  public abstract void onUserPostJoin(User user);

  public abstract void onUserReady(User user);

  public abstract void onUserQuit(User user);
}
