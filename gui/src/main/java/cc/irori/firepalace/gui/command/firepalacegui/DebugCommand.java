package cc.irori.firepalace.gui.command.firepalacegui;

import cc.irori.firepalace.common.status.GameStatus;
import cc.irori.firepalace.gui.FirepalaceGuiPlugin;
import cc.irori.firepalace.gui.status.RemoteStatusResolver;
import cc.irori.firepalace.gui.status.StatusResolver;
import cc.irori.firepalace.gui.ui.GameSelectPage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class DebugCommand extends CommandBase {

  public DebugCommand() {
    super("debug", "firepalace.commands.firepalacegui.debug.desc");
  }

  @Override
  protected void executeSync(@NonNullDecl CommandContext context) {
    StatusResolver statusResolver = FirepalaceGuiPlugin.get().getStatusResolver();
    if (statusResolver instanceof RemoteStatusResolver) {
      context.sendMessage(Message.raw("Status Resolver: REMOTE"));
    } else {
      context.sendMessage(Message.raw("Status Resolver: LOCAL"));
    }

    List<GameStatus> statusList = statusResolver.resolve();
    for (int i = 0; i < statusList.size(); i++) {
      GameStatus status = statusList.get(i);
      context.sendMessage(Message.join(
          Message.raw("### Game " + (i + 1) + " ###\n"),
          Message.raw("Game ID: " + status.metadata().id() + "\n"),
          Message.raw("Game Name: " + status.metadata().name() + "\n"),
          Message.raw("Players: " + status.users().size())
      ));
    }

    if (context.isPlayer()) {
      Ref<EntityStore> ref = context.senderAsPlayerRef();
      Store<EntityStore> store = ref.getStore();
      World world = store.getExternalData().getWorld();

      world.execute(() -> {
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        Player player = store.getComponent(ref, Player.getComponentType());

        player.getPageManager().openCustomPage(ref, store, new GameSelectPage(
            playerRef,
            CustomPageLifetime.CanDismissOrCloseThroughInteraction
        ));
      });
    }
  }
}
