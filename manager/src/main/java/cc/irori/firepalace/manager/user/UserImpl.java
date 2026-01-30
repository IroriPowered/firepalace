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
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
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
    return quitGame().thenCompose(v -> {
      state = UserState.JOINING;

      GameHolder holder = firepalace.getGameManager().getGameHolder(metadata);
      if (holder == null) {
        LOGGER.atWarning().log("Tried to join an unregistered game: %s", metadata.id());
        state = UserState.IDLE;
        return CompletableFuture.failedFuture(
            new IllegalArgumentException("Game not registered: " + metadata.id()));
      }

      CompletableFuture<Game> createFuture;
      boolean creating;
      if (!holder.hasGame()) {
        LOGGER.atInfo().log("Creating new game instance for game: %s", metadata.id());
        playerRef.sendMessage(Message.raw("Creating a new game..."));

        createFuture = holder.createGame();
        creating = true;
      } else {
        createFuture = CompletableFuture.completedFuture(holder.getGame());
        creating = false;
      }

      playerRef.sendMessage(Message.join(
          Message.raw("Joining game: "),
          Message.raw(metadata.name()).color(Colors.MUSTARD)
      ));
      return createFuture.thenCompose(game -> {
        currentGame = game;
        return game.onUserPreJoin(this, creating);
      });
    })
    .thenCompose(result -> {
      if (!result.allowJoin()) {
        currentGame = null;
        state = UserState.IDLE;
        if (result.additionalInfo() == null) {
          throw new IllegalStateException(
              "User " + getName() + " disallowed to join game: " + metadata.id());
        } else {
          throw new IllegalStateException("User " + getName() + " disallowed to join game: "
              + metadata.id() + " (" + result.additionalInfo() + ")");
        }
      }

      CompletableFuture<Void> future = new CompletableFuture<>();
      World currentWorld = playerRef.getReference().getStore().getExternalData().getWorld();
      currentWorld.execute(() -> {
        Store<EntityStore> store = playerRef.getReference().getStore();
        Teleport teleport = Teleport.createForPlayer(result.world(), result.spawnPosition());
        store.addComponent(playerRef.getReference(), Teleport.getComponentType(), teleport);
      });
      firepalace.getWorldActionQueue().enqueueAdd(playerRef.getUuid(), result.world(), () -> {
        LOGGER.atInfo().log("%s joined game: %s", getName(), metadata.id());
        GameInstanceImpl instance = (GameInstanceImpl) currentGame.getGameInstance();
        instance.addUser(this);

        currentGame.onUserPostJoin(this);
        state = UserState.PLAYING;
        future.complete(null);
      });
      firepalace.getWorldActionQueue().enqueueReady(playerRef.getUuid(), result.world(),
          () -> currentGame.onUserReady(this));
      return future;
    });
  }

  public CompletableFuture<Void> quitGame() {
    if (currentGame != null) {
      GameInstanceImpl instance = (GameInstanceImpl) currentGame.getGameInstance();

      playerRef.sendMessage(Message.join(
          Message.raw("Leaving game: "),
          Message.raw(instance.getGameHolder().getMetadata().name()).color(Colors.MUSTARD)
      ));

      CompletableFuture<Void> future = new CompletableFuture<>();
      World world = playerRef.getReference().getStore().getExternalData().getWorld();
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
