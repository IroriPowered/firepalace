package cc.irori.firepalace.api.game;

import cc.irori.firepalace.api.user.User;
import java.util.Collection;

public interface GameInstance {

  Collection<User> getUsers();
}
