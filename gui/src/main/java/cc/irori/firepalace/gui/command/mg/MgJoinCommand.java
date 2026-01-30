package cc.irori.firepalace.gui.command.mg;

import cc.irori.firepalace.api.util.Colors;
import cc.irori.firepalace.gui.FirepalaceGuiPlugin;
import cc.irori.firepalace.gui.status.StatusResolver;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class MgJoinCommand extends AbstractPlayerCommand {

  private final RequiredArg<String> idArg;

  public MgJoinCommand() {
    super("join", "firepalace.commands.mg.join.desc");

    this.idArg = withRequiredArg("id", "firepalace.commands.mg.join.id.desc", ArgTypes.STRING);
  }

  @Override
  protected void execute(@NonNullDecl CommandContext context,
                         @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
                         @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
    String gameId = idArg.get(context);
    StatusResolver resolver = FirepalaceGuiPlugin.get().getStatusResolver();
    if (!resolver.exists(gameId)) {
      playerRef.sendMessage(Message.join(
          Message.raw("Invalid game ID: "),
          Message.raw(gameId).color(Colors.MUSTARD)
      ));
      return;
    }

    FirepalaceGuiPlugin.get().getStatusResolver().joinGame(playerRef, idArg.get(context));
  }
}
