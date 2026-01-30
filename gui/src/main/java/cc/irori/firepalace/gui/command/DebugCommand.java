package cc.irori.firepalace.gui.command;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class DebugCommand extends CommandBase {

  public DebugCommand() {
    super("debug", "firepalace.commands.firepalacegui.debug.desc");
  }

  @Override
  protected void executeSync(@NonNullDecl CommandContext context) {

  }
}
