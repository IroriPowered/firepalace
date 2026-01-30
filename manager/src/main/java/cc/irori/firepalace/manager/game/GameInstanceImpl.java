package cc.irori.firepalace.manager.game;

import cc.irori.firepalace.api.game.GameInstance;
import cc.irori.firepalace.api.game.JoinResult;
import cc.irori.firepalace.api.user.User;
import cc.irori.firepalace.manager.user.UserImpl;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class GameInstanceImpl implements GameInstance {

  private final GameHolder gameHolder;
  private final Map<UUID, JoinResult> preJoinResults = new ConcurrentHashMap<>();
  private final Set<UserImpl> users = new HashSet<>();

  public GameInstanceImpl(GameHolder holder) {
    this.gameHolder = holder;
  }

  @Override
  public Collection<User> getUsers() {
    return List.copyOf(users);
  }

  public void addUser(UserImpl user) {
    users.add(user);
  }

  public void removeUser(UserImpl user) {
    users.remove(user);
    preJoinResults.remove(user.getUuid());
  }

  public GameHolder getGameHolder() {
    return gameHolder;
  }

  public CompletableFuture<JoinResult> handlePreJoin(UUID uuid, boolean isCreating) {
    return gameHolder.getGame().onUserPreJoin(uuid, isCreating)
        .thenApply(result -> {
          preJoinResults.put(uuid, result);
          return result;
        });
  }

  public boolean isPreJoinHandled(UUID uuid) {
    return preJoinResults.containsKey(uuid);
  }

  public JoinResult getPreJoinResult(UUID uuid) {
    return preJoinResults.get(uuid);
  }
}
