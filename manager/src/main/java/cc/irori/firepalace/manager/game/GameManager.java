package cc.irori.firepalace.manager.game;

import cc.irori.firepalace.api.game.Game;
import cc.irori.firepalace.api.game.GameInstance;
import cc.irori.firepalace.api.game.metadata.GameMetadata;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class GameManager {

  private final Map<String, GameHolder> games = new ConcurrentHashMap<>();

  public GameManager() {
  }

  public void registerGame(GameMetadata metadata, Function<GameInstance, Game> gameFactory) {
    GameHolder holder = new GameHolder(metadata, gameFactory);
    games.put(metadata.id(), holder);
  }

  public void unregisterGame(GameMetadata metadata) {
    games.remove(metadata.id());
  }

  public boolean isGameRegistered(String id) {
    return games.containsKey(id);
  }

  public GameHolder getGameHolder(String id) {
    return games.get(id);
  }

  public GameHolder getGameHolder(GameMetadata metadata) {
    return games.get(metadata.id());
  }

  public Collection<GameHolder> getAllGameHolders() {
    return games.values();
  }
}
