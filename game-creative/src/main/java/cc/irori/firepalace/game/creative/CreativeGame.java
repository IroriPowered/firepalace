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
import java.util.concurrent.CompletableFuture;

public class CreativeGame extends Game {

  public CreativeGame(GameInstance instance) {
    super(instance);
  }

  @Override
  public CompletableFuture<JoinResult> onUserPreJoin(User user, boolean isCreating) {
    World world = Universe.get().getWorld("creative");

    return CompletableFuture.completedFuture(JoinResult.allow(
        world,
        world.getWorldConfig().getSpawnProvider().getSpawnPoint(world, user.getUuid())
    ));
  }

  @Override
  public void onUserPostJoin(User user) {
    Ref<EntityStore> ref = user.getPlayerRef().getReference();
    assert ref != null;

    user.getPlayerRef().sendMessage(Message.raw("Welcome to Creative Freebuild!!")
        .color(Colors.GREEN_LIGHT));
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
    user.getPlayerRef().sendMessage(Message.raw("Bye!!")
        .color(Colors.GREEN_LIGHT));
  }
}
