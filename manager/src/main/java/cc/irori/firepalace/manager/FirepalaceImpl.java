package cc.irori.firepalace.manager;

import cc.irori.firepalace.api.Firepalace;
import cc.irori.firepalace.api.game.Game;
import cc.irori.firepalace.api.game.GameInstance;
import cc.irori.firepalace.api.game.metadata.GameMetadata;
import cc.irori.firepalace.api.user.User;
import cc.irori.firepalace.common.redis.Recipient;
import cc.irori.firepalace.common.redis.RedisConfig;
import cc.irori.firepalace.common.redis.RedisParticipant;
import cc.irori.firepalace.common.redis.UpstreamPacketHandler;
import cc.irori.firepalace.common.redis.protocol.DownstreamPacket;
import cc.irori.firepalace.manager.command.GameCommand;
import cc.irori.firepalace.manager.game.GameManager;
import cc.irori.firepalace.manager.redis.UpstreamIncomingPacketRegistry;
import cc.irori.firepalace.manager.redis.UpstreamPacketHandlerImpl;
import cc.irori.firepalace.manager.user.UserManager;
import cc.irori.firepalace.manager.util.WorldActionScheduler;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.util.Config;
import java.util.UUID;
import java.util.function.Function;

public class FirepalaceImpl implements Firepalace {

  private final JavaPlugin plugin;

  private final Config<RedisConfig> redisConfig;

  private final UserManager userManager;
  private final GameManager gameManager;
  private final WorldActionScheduler worldActionScheduler;
  private final RedisParticipant<UpstreamPacketHandler, DownstreamPacket> redis;

  public FirepalaceImpl(FirepalaceManagerPlugin plugin) {
    this.plugin = plugin;

    this.redisConfig = plugin.config("RedisConfig", RedisConfig.CODEC);
    redisConfig.load().join();

    this.userManager = new UserManager(this, plugin);
    this.gameManager = new GameManager();
    this.worldActionScheduler = new WorldActionScheduler(plugin);

    if (redisConfig.get().useRemote) {
      this.redis = new RedisParticipant<>(
          redisConfig.get(),
          Recipient.UPSTREAM,
          new UpstreamPacketHandlerImpl(this),
          new UpstreamIncomingPacketRegistry()
      );
    } else {
      redis = null;
    }

    plugin.getCommandRegistry().registerCommand(new GameCommand());
  }

  @Override
  public void registerGame(GameMetadata metadata, Function<GameInstance, Game> gameFactory) {
    gameManager.registerGame(metadata, gameFactory);
  }

  @Override
  public void unregisterGame(GameMetadata metadata) {
    gameManager.unregisterGame(metadata);
  }

  @Override
  public User getUser(UUID uuid) {
    return userManager.getUser(uuid);
  }

  public UserManager getUserManager() {
    return userManager;
  }

  public GameManager getGameManager() {
    return gameManager;
  }

  public WorldActionScheduler getWorldActionScheduler() {
    return worldActionScheduler;
  }

  public RedisParticipant<UpstreamPacketHandler, DownstreamPacket> getRedis() {
    return redis;
  }

  public void shutdown() {
    redisConfig.save().join();
    if (redis != null) {
      redis.shutdown();
    }
  }

  public static FirepalaceImpl get() {
    return (FirepalaceImpl) Firepalace.api();
  }
}
