package cc.irori.firepalace.manager.game;

import cc.irori.firepalace.api.game.Game;
import cc.irori.firepalace.api.game.GameInstance;
import cc.irori.firepalace.api.game.metadata.GameMetadata;
import cc.irori.firepalace.manager.FirepalaceImpl;
import cc.irori.firepalace.manager.util.GameUtil;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class GameManager {

  private static final int STATUS_UPDATE_INTERVAL_SECONDS = 30;

  private final Map<String, GameHolder> games = new ConcurrentHashMap<>();
  private final ScheduledExecutorService statusExecutor =
      Executors.newSingleThreadScheduledExecutor();

  public GameManager(FirepalaceImpl firepalace) {
    statusExecutor.scheduleAtFixedRate(() -> GameUtil.sendGameStatusPacket(firepalace),
        STATUS_UPDATE_INTERVAL_SECONDS, STATUS_UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS);
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

  public boolean isGameAvailable(String id) {
    return games.containsKey(id) && games.get(id).getMetadata().available();
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

  public void shutdown() {
    statusExecutor.shutdownNow();
  }
}
