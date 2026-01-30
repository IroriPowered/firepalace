package cc.irori.firepalace.manager.command;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class GameCommand extends AbstractCommandCollection {

  public GameCommand() {
    super("game", "firepalace.commands.game.desc");

    addSubCommand(new JoinCommand());
    addSubCommand(new QuitCommand());
  }
}
