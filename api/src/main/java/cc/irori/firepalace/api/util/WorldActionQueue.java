package cc.irori.firepalace.api.util;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorldActionQueue {

  public static final World ANY_WORLD = null;

  private final Map<QueueKey, List<Runnable>> queue = new ConcurrentHashMap<>();

  public WorldActionQueue(JavaPlugin plugin) {
    plugin.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, event -> {
      Holder<EntityStore> holder = event.getHolder();
      PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());

      lookupAndRun(new QueueKey(playerRef.getUuid(), event.getWorld(), ActionType.ADD));
      lookupAndRun(new QueueKey(playerRef.getUuid(), ANY_WORLD, ActionType.ADD));
    });

    plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
      World world = event.getPlayer().getWorld();
      PlayerRef playerRef = event.getPlayerRef().getStore()
          .getComponent(event.getPlayerRef(), PlayerRef.getComponentType());

      lookupAndRun(new QueueKey(playerRef.getUuid(), world, ActionType.READY));
      lookupAndRun(new QueueKey(playerRef.getUuid(), ANY_WORLD, ActionType.READY));
    });
  }

  public void enqueueAdd(UUID uuid, World world, Runnable action) {
    enqueue(uuid, world, ActionType.ADD, action);
  }

  public void enqueueReady(UUID uuid, World world, Runnable action) {
    enqueue(uuid, world, ActionType.READY, action);
  }

  private void enqueue(UUID uuid, World world, ActionType actionType, Runnable action) {
    PlayerRef playerRef = Universe.get().getPlayer(uuid);
    if (playerRef != null) {
      World currentWorld = playerRef.getReference().getStore().getExternalData().getWorld();
      if (world == ANY_WORLD || currentWorld.equals(world)) {
        if (currentWorld.isInThread()) {
          action.run();
        } else {
          currentWorld.execute(action);
        }
        return;
      }
    }

    QueueKey key = new QueueKey(uuid, world, actionType);
    queue.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(action);
  }

  private void lookupAndRun(QueueKey key) {
    List<Runnable> actions = queue.remove(key);
    if (actions != null) {
      for (Runnable action : actions) {
        action.run();
      }
    }
  }

  record QueueKey(UUID uuid, World world, ActionType actionType) {
  }
  
  enum ActionType {
    ADD,
    READY
  }
}
