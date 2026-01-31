package cc.irori.firepalace.manager.command;

import cc.irori.firepalace.api.game.Game;
import cc.irori.firepalace.common.util.Logs;
import cc.irori.firepalace.manager.FirepalaceImpl;
import cc.irori.firepalace.manager.game.GameInstanceImpl;
import cc.irori.firepalace.manager.user.UserImpl;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class QuitCommand extends AbstractPlayerCommand {

  private static final HytaleLogger LOGGER = Logs.logger();

  public QuitCommand() {
    super("quit", "firepalace.commands.game.quit.desc");
    addAliases("leave");
  }

  @Override
  protected void execute(@NonNullDecl CommandContext context,
                         @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
                         @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
    FirepalaceImpl firepalace = FirepalaceImpl.get();
    UserImpl user = firepalace.getUserManager().getUser(playerRef);

    Game game = user.getCurrentGame();
    if (game == null) {
      context.sendMessage(Message.join(
          Message.raw("You are not currently in a game.")
      ));
      return;
    }

    GameInstanceImpl instance = (GameInstanceImpl) game.getGameInstance();

    user.quitGame()
        .exceptionally(t -> {
          LOGGER.atSevere().withCause(t).log("Error while player %s tried to quit game %s",
              playerRef.getUsername(), instance.getGameHolder().getMetadata().id());
          playerRef.getPacketHandler().disconnect("Failed to quit the game!");
          return null;
        });
  }
}
