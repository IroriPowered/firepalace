package cc.irori.firepalace.manager.game;

import cc.irori.firepalace.api.game.Game;
import cc.irori.firepalace.api.game.GameInstance;
import cc.irori.firepalace.api.game.metadata.GameMetadata;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class GameHolder {

  private final GameMetadata metadata;
  private final Function<GameInstance, Game> gameFactory;

  private GameInstanceImpl gameInstance;
  private Game game;

  public GameHolder(GameMetadata metadata, Function<GameInstance, Game> gameFactory) {
    this.metadata = metadata;
    this.gameFactory = gameFactory;
  }

  public GameMetadata getMetadata() {
    return metadata;
  }

  public boolean hasGame() {
    return game != null;
  }

  public Game getGame() {
    return game;
  }

  public GameInstanceImpl getGameInstance() {
    return gameInstance;
  }

  public CompletableFuture<Game> createGame() {
    gameInstance = new GameInstanceImpl(this);
    return CompletableFuture.supplyAsync(() -> gameFactory.apply(gameInstance))
        .thenApply(createdGame -> this.game = createdGame);
  }

  public void unloadGame() {
    this.game = null;
    this.gameInstance = null;
  }
}
