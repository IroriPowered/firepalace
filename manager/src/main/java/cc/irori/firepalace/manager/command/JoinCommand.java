package cc.irori.firepalace.manager.command;

import cc.irori.firepalace.common.util.Logs;
import cc.irori.firepalace.manager.FirepalaceImpl;
import cc.irori.firepalace.manager.user.UserImpl;
import cc.irori.firepalace.manager.util.GameUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class JoinCommand extends AbstractPlayerCommand {

  private static final HytaleLogger LOGGER = Logs.logger();

  private final RequiredArg<String> idArg;

  public JoinCommand() {
    super("join", "firepalace.commands.game.join.desc");

    this.idArg = withRequiredArg("id", "firepalace.commands.game.join.id.desc", ArgTypes.STRING);
  }

  @Override
  protected void execute(@NonNullDecl CommandContext context,
                         @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
                         @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
    FirepalaceImpl firepalace = FirepalaceImpl.get();
    UserImpl user = firepalace.getUserManager().getUser(playerRef);
    String id = idArg.get(context);

    GameUtil.joinGameById(firepalace, user, id);
  }
}
