package cc.irori.firepalace.gui.command.mg;

import cc.irori.firepalace.gui.ui.GameSelectPage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class MgCommand extends AbstractPlayerCommand {

  public MgCommand() {
    super("mg", "firepalace.commands.mg.desc");

    addSubCommand(new MgJoinCommand());
    setPermissionGroup(GameMode.Adventure);
  }

  @Override
  protected void execute(@NonNullDecl CommandContext context,
                         @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
                         @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
    world.execute(() -> {
      Player player = store.getComponent(ref, Player.getComponentType());

      player.getPageManager().openCustomPage(ref, store, new GameSelectPage(
          playerRef,
          CustomPageLifetime.CanDismissOrCloseThroughInteraction
      ));
    });
  }
}
