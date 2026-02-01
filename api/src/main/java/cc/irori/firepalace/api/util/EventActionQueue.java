package cc.irori.firepalace.api.util;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IBaseEvent;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
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
import java.util.function.Consumer;

public class EventActionQueue {

  public static final World ANY_WORLD = null;

  private final Map<QueueKey, List<Consumer<IBaseEvent<?>>>> queue = new ConcurrentHashMap<>();

  public EventActionQueue(JavaPlugin plugin) {
    plugin.getEventRegistry().register(PlayerConnectEvent.class, event -> {
      lookupAndRun(new QueueKey(event.getPlayerRef().getUuid(), event.getWorld(), ActionType.JOIN), event);
      lookupAndRun(new QueueKey(event.getPlayerRef().getUuid(), ANY_WORLD, ActionType.JOIN), event);
    });

    plugin.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, event -> {
      Holder<EntityStore> holder = event.getHolder();
      PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());

      lookupAndRun(new QueueKey(playerRef.getUuid(), event.getWorld(), ActionType.ADD), event);
      lookupAndRun(new QueueKey(playerRef.getUuid(), ANY_WORLD, ActionType.ADD), event);
    });

    plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
      World world = event.getPlayer().getWorld();
      world.execute(() -> {
        PlayerRef playerRef = event.getPlayerRef().getStore()
            .getComponent(event.getPlayerRef(), PlayerRef.getComponentType());

        lookupAndRun(new QueueKey(playerRef.getUuid(), world, ActionType.READY), event);
        lookupAndRun(new QueueKey(playerRef.getUuid(), ANY_WORLD, ActionType.READY), event);
      });
    });
  }

  public void enqueueJoin(UUID uuid, World world, Consumer<IBaseEvent<?>> action) {
    enqueue(uuid, world, ActionType.JOIN, action);
  }

  public void enqueueAdd(UUID uuid, World world, Consumer<IBaseEvent<?>> action) {
    enqueue(uuid, world, ActionType.ADD, action);
  }

  public void enqueueReady(UUID uuid, World world, Consumer<IBaseEvent<?>> action) {
    enqueue(uuid, world, ActionType.READY, action);
  }

  private void enqueue(UUID uuid, World world, ActionType actionType, Consumer<IBaseEvent<?>> action) {
    PlayerRef playerRef = Universe.get().getPlayer(uuid);
    if (playerRef != null) {
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null) {
        World currentWorld = ref.getStore().getExternalData().getWorld();
        if (world == ANY_WORLD || currentWorld.equals(world)) {
          if (currentWorld.isInThread()) {
            action.accept(null);
          } else {
            currentWorld.execute(() -> action.accept(null));
          }
          return;
        }
      }
    }

    QueueKey key = new QueueKey(uuid, world, actionType);
    queue.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(action);
  }

  private void lookupAndRun(QueueKey key, IBaseEvent<?> event) {
    List<Consumer<IBaseEvent<?>>> actions = queue.remove(key);
    if (actions != null) {
      for (Consumer<IBaseEvent<?>> action : actions) {
        action.accept(event);
      }
    }
  }

  record QueueKey(UUID uuid, World world, ActionType actionType) {
  }

  enum ActionType {
    JOIN,
    ADD,
    READY
  }
}
