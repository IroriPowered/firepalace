package cc.irori.firepalace.gui.command.firepalacegui;

import cc.irori.firepalace.common.status.GameStatus;
import cc.irori.firepalace.gui.FirepalaceGuiPlugin;
import cc.irori.firepalace.gui.status.StatusResolver;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class DebugCommand extends CommandBase {

  public DebugCommand() {
    super("debug", "firepalace.commands.firepalacegui.debug.desc");
  }

  @Override
  protected void executeSync(@NonNullDecl CommandContext context) {
    StatusResolver statusResolver = FirepalaceGuiPlugin.get().getStatusResolver();
    if (FirepalaceGuiPlugin.get().isLocal()) {
      context.sendMessage(Message.raw("Status Resolver: LOCAL"));
    } else {
      context.sendMessage(Message.raw("Status Resolver: REMOTE"));
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
  }
}
