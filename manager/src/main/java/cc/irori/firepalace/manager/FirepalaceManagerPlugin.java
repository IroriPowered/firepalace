package cc.irori.firepalace.manager;

import cc.irori.firepalace.api.Firepalace;
import cc.irori.firepalace.common.redis.RedisConfig;
import cc.irori.firepalace.common.util.Logs;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class FirepalaceManagerPlugin extends JavaPlugin {

  protected final Config<RedisConfig> redisConfig;

  private FirepalaceImpl firepalace;

  public FirepalaceManagerPlugin(@NonNullDecl JavaPluginInit init) {
    super(init);
    Logs.setupLogger("Firepalace-Manager");

    this.redisConfig = withConfig("RedisConfig", RedisConfig.CODEC);
  }

  @Override
  protected void start() {
    this.firepalace = new FirepalaceImpl(this);
    Firepalace.setup(firepalace);
  }

  @Override
  protected void shutdown() {
    firepalace.shutdown();
  }
}
