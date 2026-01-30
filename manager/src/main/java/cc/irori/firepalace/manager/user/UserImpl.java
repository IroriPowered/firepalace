package cc.irori.firepalace.manager.user;

import cc.irori.firepalace.api.game.Game;
import cc.irori.firepalace.api.game.metadata.GameMetadata;
import cc.irori.firepalace.api.user.User;
import cc.irori.firepalace.api.user.UserState;
import cc.irori.firepalace.api.util.Colors;
import cc.irori.firepalace.common.util.Logs;
import cc.irori.firepalace.manager.FirepalaceImpl;
import cc.irori.firepalace.manager.game.GameHolder;
import cc.irori.firepalace.manager.game.GameInstanceImpl;
import cc.irori.firepalace.manager.util.GameUtil;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;

public class UserImpl implements User {

  private static final HytaleLogger LOGGER = Logs.logger();

  private final FirepalaceImpl firepalace;
  private final PlayerRef playerRef;

  private Game currentGame;
  private UserState state = UserState.IDLE;

  public UserImpl(FirepalaceImpl firepalace, PlayerRef playerRef) {
    this.firepalace = firepalace;
    this.playerRef = playerRef;
  }

  @Override
  public String getName() {
    return playerRef.getUsername();
  }

  @Override
  public UUID getUuid() {
    return playerRef.getUuid();
  }

  @Override
  public PlayerRef getPlayerRef() {
    return playerRef;
  }

  @Override
  public @Nullable Game getCurrentGame() {
    return currentGame;
  }

  public CompletableFuture<Void> joinGame(GameMetadata metadata) {
    return joinGame(metadata, null);
  }

  public CompletableFuture<Void> joinGame(GameMetadata metadata, @Nullable PlayerConnectEvent connectEvent) {
    return quitGame().thenCompose(v -> {
      state = UserState.JOINING;

      GameHolder holder = firepalace.getGameManager().getGameHolder(metadata);
      if (holder == null) {
        LOGGER.atWarning().log("Tried to join an unregistered game: %s", metadata.id());
        state = UserState.IDLE;
        return CompletableFuture.failedFuture(
            new IllegalArgumentException("Game not registered: " + metadata.id()));
      }

      return GameUtil.tryCreateGame(holder);
    }).thenCompose(creating -> {
      playerRef.sendMessage(Message.join(
          Message.raw("Joining game: "),
          Message.raw(metadata.name()).color(Colors.MUSTARD)
      ));

      GameHolder holder = firepalace.getGameManager().getGameHolder(metadata);
      return GameUtil.tryPreJoin(holder, playerRef.getUuid(), creating);
    })
    .thenCompose(result -> {
      if (!result.allowJoin()) {
        state = UserState.IDLE;
        if (result.additionalInfo() == null) {
          throw new IllegalStateException(
              "User " + getName() + " disallowed to join game: " + metadata.id());
        } else {
          throw new IllegalStateException("User " + getName() + " disallowed to join game: "
              + metadata.id() + " (" + result.additionalInfo() + ")");
        }
      }

      CompletableFuture<Void> future;
      if (connectEvent != null) {
        connectEvent.setWorld(result.world());

        Holder<EntityStore> holder = connectEvent.getHolder();
        TransformComponent transformComponent = holder.ensureAndGetComponent(TransformComponent.getComponentType());
        transformComponent.setPosition(result.spawnPosition().getPosition());
        HeadRotation headRotationComponent = holder.ensureAndGetComponent(HeadRotation.getComponentType());
        headRotationComponent.teleportRotation(result.spawnPosition().getRotation());
        future = CompletableFuture.completedFuture(null);
      } else {
        World currentWorld = playerRef.getReference().getStore().getExternalData().getWorld();
        currentWorld.execute(() -> {
          Store<EntityStore> store = playerRef.getReference().getStore();
          Teleport teleport = Teleport.createForPlayer(result.world(), result.spawnPosition());
          store.addComponent(playerRef.getReference(), Teleport.getComponentType(), teleport);
        });
        future = new CompletableFuture<>();
      }

      GameHolder holder = firepalace.getGameManager().getGameHolder(metadata);
      currentGame = holder.getGame();

      firepalace.getWorldActionQueue().enqueueAdd(playerRef.getUuid(), result.world(), event -> {
        LOGGER.atInfo().log("%s joined game: %s", getName(), metadata.id());
        GameInstanceImpl instance = (GameInstanceImpl) currentGame.getGameInstance();
        instance.addUser(this);

        currentGame.onUserPostJoin(this);
        state = UserState.PLAYING;

        if (connectEvent == null) {
          future.complete(null);
        }
      });
      firepalace.getWorldActionQueue().enqueueReady(playerRef.getUuid(), result.world(),
          event -> currentGame.onUserReady(this));
      return future;
    });
  }

  public CompletableFuture<Void> quitGame() {
    if (currentGame != null) {
      GameInstanceImpl instance = (GameInstanceImpl) currentGame.getGameInstance();

      CompletableFuture<Void> future = new CompletableFuture<>();
      Ref<EntityStore> ref = playerRef.getReference();

      if (ref == null) {
        currentGame = null;
        return CompletableFuture.completedFuture(null);
      }

      World world = ref.getStore().getExternalData().getWorld();
      playerRef.sendMessage(Message.join(
          Message.raw("Leaving game: "),
          Message.raw(instance.getGameHolder().getMetadata().name()).color(Colors.MUSTARD)
      ));

      world.execute(() -> {
        LOGGER.atInfo().log("%s quit game: %s",
            getName(), instance.getGameHolder().getMetadata().id());
        currentGame.onUserQuit(this);
        instance.removeUser(this);
        currentGame = null;
        state = UserState.IDLE;
        future.complete(null);
      });
      return future;
    }
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public UserState getState() {
    return state;
  }

  public void setState(UserState state) {
    this.state = state;
  }
}
