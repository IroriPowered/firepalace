package cc.irori.firepalace.gui;

import cc.irori.firepalace.common.redis.DownstreamPacketHandler;
import cc.irori.firepalace.common.redis.Recipient;
import cc.irori.firepalace.common.redis.RedisConfig;
import cc.irori.firepalace.common.redis.RedisParticipant;
import cc.irori.firepalace.common.redis.protocol.UpstreamPacket;
import cc.irori.firepalace.common.util.Logs;
import cc.irori.firepalace.gui.redis.DownstreamIncomingPacketRegistry;
import cc.irori.firepalace.gui.redis.DownstreamPacketHandlerImpl;
import cc.irori.firepalace.gui.status.LocalStatusResolver;
import cc.irori.firepalace.gui.status.RemoteStatusResolver;
import cc.irori.firepalace.gui.status.StatusResolver;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class FirepalaceGuiPlugin extends JavaPlugin {

  private Config<RedisConfig> redisConfig;
  private RedisParticipant<DownstreamPacketHandler, UpstreamPacket> redis;
  private StatusResolver statusResolver;

  public FirepalaceGuiPlugin(@NonNullDecl JavaPluginInit init) {
    super(init);
    Logs.setupLogger("Firepalace-GUI");
  }

  @Override
  protected void start() {
    redisConfig = withConfig("RedisConfig", RedisConfig.CODEC);
    redisConfig.load().join();

    if (redisConfig.get().useRemote) {
      redis = new RedisParticipant<>(
          redisConfig.get(),
          Recipient.DOWNSTREAM,
          new DownstreamPacketHandlerImpl(this),
          new DownstreamIncomingPacketRegistry()
      );
      statusResolver = new RemoteStatusResolver(this);
    } else {
      redis = null;
      statusResolver = new LocalStatusResolver();
    }
  }

  @Override
  protected void shutdown() {
    redisConfig.save().join();
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
}
