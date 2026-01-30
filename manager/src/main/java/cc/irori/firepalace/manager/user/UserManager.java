package cc.irori.firepalace.manager.user;

import cc.irori.firepalace.manager.FirepalaceImpl;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

  private final FirepalaceImpl firepalace;
  private final Map<UUID, UserImpl> users = new ConcurrentHashMap<>();

  public UserManager(FirepalaceImpl firepalace, JavaPlugin plugin) {
    this.firepalace = firepalace;

    plugin.getEventRegistry().register(PlayerConnectEvent.class, event -> {
      PlayerRef playerRef = event.getPlayerRef();

      if (!users.containsKey(playerRef.getUuid())) {
        UserImpl user = new UserImpl(firepalace, playerRef);
        users.put(playerRef.getUuid(), user);
      }
    });

    plugin.getEventRegistry().register(PlayerDisconnectEvent.class, event -> {
      UserImpl user = users.get(event.getPlayerRef().getUuid());
      if (user != null) {
        user.quitGame().thenAccept(v -> users.remove(event.getPlayerRef().getUuid()));
      }
    });
  }

  public UserImpl getUser(UUID uuid) {
    return users.get(uuid);
  }

  public UserImpl getUser(PlayerRef playerRef) {
    return users.get(playerRef.getUuid());
  }

  public Set<UserImpl> getAllUsers() {
    return Set.copyOf(users.values());
  }
}
