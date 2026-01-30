package cc.irori.firepalace.gui.command;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class FirepalaceGuiCommand extends AbstractCommandCollection {

  public FirepalaceGuiCommand() {
    super("firepalacegui", "firepalace.commands.firepalacegui.desc");

    addSubCommand(new DebugCommand());
  }
}
