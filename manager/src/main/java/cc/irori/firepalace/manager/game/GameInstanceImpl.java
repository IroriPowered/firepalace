package cc.irori.firepalace.manager.game;

import cc.irori.firepalace.api.game.GameInstance;
import cc.irori.firepalace.api.user.User;
import cc.irori.firepalace.manager.user.UserImpl;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameInstanceImpl implements GameInstance {

  private final GameHolder gameHolder;
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
  }

  public GameHolder getGameHolder() {
    return gameHolder;
  }
}
