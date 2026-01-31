package cc.irori.firepalace.game.creative;

import cc.irori.firepalace.api.game.Game;
import cc.irori.firepalace.api.game.GameInstance;
import cc.irori.firepalace.api.game.JoinResult;
import cc.irori.firepalace.api.user.User;
import cc.irori.firepalace.api.util.Colors;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CreativeGame extends Game {

  public CreativeGame(GameInstance instance) {
    super(instance);
  }

  @Override
  public CompletableFuture<JoinResult> onUserPreJoin(UUID uuid, boolean isCreating) {
    World world = Universe.get().getWorld("creative");

    return CompletableFuture.completedFuture(JoinResult.allow(
        world,
        world.getWorldConfig().getSpawnProvider().getSpawnPoint(world, uuid)
    ));
  }

  @Override
  public void onUserPostJoin(User user) {
    Ref<EntityStore> ref = user.getPlayerRef().getReference();
    assert ref != null;
  }

  @Override
  public void onUserReady(User user) {
    Ref<EntityStore> ref = user.getPlayerRef().getReference();
    assert ref != null;

    Player.setGameMode(ref, GameMode.Creative, ref.getStore());
  }

  @Override
  public void onUserQuit(User user) {
    Ref<EntityStore> ref = user.getPlayerRef().getReference();
    assert ref != null;

    Player.setGameMode(ref, GameMode.Adventure, ref.getStore());
  }
}
