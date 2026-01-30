package cc.irori.firepalace.gui;

import cc.irori.firepalace.common.redis.DownstreamPacketHandler;
import cc.irori.firepalace.common.redis.Recipient;
import cc.irori.firepalace.common.redis.RedisConfig;
import cc.irori.firepalace.common.redis.RedisParticipant;
import cc.irori.firepalace.common.redis.protocol.UpstreamPacket;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamRequestStatusPacket;
import cc.irori.firepalace.common.util.Logs;
import cc.irori.firepalace.gui.command.firepalacegui.FirepalaceGuiCommand;
import cc.irori.firepalace.gui.command.mg.MgCommand;
import cc.irori.firepalace.gui.redis.DownstreamPacketHandlerImpl;
import cc.irori.firepalace.gui.status.LocalStatusResolver;
import cc.irori.firepalace.gui.status.RemoteStatusResolver;
import cc.irori.firepalace.gui.status.StatusResolver;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class FirepalaceGuiPlugin extends JavaPlugin {

  private static FirepalaceGuiPlugin instance;

  private final Config<RedisConfig> redisConfig;
  private final Config<GuiConfig> guiConfig;

  private HytaleLogger logger;
  private RedisParticipant<DownstreamPacketHandler, UpstreamPacket> redis;
  private StatusResolver statusResolver;

  public FirepalaceGuiPlugin(@NonNullDecl JavaPluginInit init) {
    super(init);
    instance = this;
    Logs.setupLogger("Firepalace-GUI");

    redisConfig = withConfig("RedisConfig", RedisConfig.CODEC);
    guiConfig = withConfig("GuiConfig", GuiConfig.CODEC);
  }

  @Override
  protected void start() {
    logger = Logs.logger();
    redisConfig.load().join();
    guiConfig.load().join();

    getCommandRegistry().registerCommand(new FirepalaceGuiCommand());
    getCommandRegistry().registerCommand(new MgCommand());

    if (redisConfig.get().useRemote) {
      logger.atInfo().log("Using remote status resolver");
      redis = new RedisParticipant<>(
          redisConfig.get(),
          Recipient.DOWNSTREAM,
          new DownstreamPacketHandlerImpl(this)
      );
      statusResolver = new RemoteStatusResolver(this);

      redis.sendPacket(new UpstreamRequestStatusPacket());
    } else {
      logger.atInfo().log("Using local status resolver");
      redis = null;
      statusResolver = new LocalStatusResolver();
    }
  }

  @Override
  protected void shutdown() {
    redisConfig.save().join();
    guiConfig.save().join();
    if (redis != null) {
      redis.shutdown();
    }
  }

  public RedisParticipant<DownstreamPacketHandler, UpstreamPacket> getRedis() {
    return redis;
  }

  public StatusResolver getStatusResolver() {
    return statusResolver;
  }

  public GuiConfig getGuiConfig() {
    return guiConfig.get();
  }

  public static FirepalaceGuiPlugin get() {
    return instance;
  }
}
