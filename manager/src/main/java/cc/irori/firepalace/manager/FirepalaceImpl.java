package cc.irori.firepalace.manager;

import cc.irori.firepalace.api.Firepalace;
import cc.irori.firepalace.api.game.Game;
import cc.irori.firepalace.api.game.GameInstance;
import cc.irori.firepalace.api.game.metadata.GameMetadata;
import cc.irori.firepalace.api.user.User;
import cc.irori.firepalace.api.util.EventActionQueue;
import cc.irori.firepalace.common.redis.Recipient;
import cc.irori.firepalace.common.redis.RedisParticipant;
import cc.irori.firepalace.common.redis.UpstreamPacketHandler;
import cc.irori.firepalace.common.redis.protocol.DownstreamPacket;
import cc.irori.firepalace.common.util.Logs;
import cc.irori.firepalace.manager.command.GameCommand;
import cc.irori.firepalace.manager.game.GameManager;
import cc.irori.firepalace.manager.redis.UpstreamPacketHandlerImpl;
import cc.irori.firepalace.manager.user.UserManager;
import com.hypixel.hytale.logger.HytaleLogger;
import java.util.UUID;
import java.util.function.Function;

public class FirepalaceImpl implements Firepalace {

  private final HytaleLogger LOGGER = Logs.logger();

  private final FirepalaceManagerPlugin plugin;

  private final UserManager userManager;
  private final GameManager gameManager;
  private final EventActionQueue eventActionQueue;
  private final RedisParticipant<UpstreamPacketHandler, DownstreamPacket> redis;

  public FirepalaceImpl(FirepalaceManagerPlugin plugin) {
    this.plugin = plugin;

    plugin.redisConfig.load().join();

    this.userManager = new UserManager(this, plugin);
    this.gameManager = new GameManager(this);
    this.eventActionQueue = new EventActionQueue(plugin);

    if (plugin.redisConfig.get().useRemote) {
      LOGGER.atInfo().log("Using redis for remote status sending");
      this.redis = new RedisParticipant<>(
          plugin.redisConfig.get(),
          Recipient.UPSTREAM,
          new UpstreamPacketHandlerImpl(this)
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

  public EventActionQueue getWorldActionQueue() {
    return eventActionQueue;
  }

  public RedisParticipant<UpstreamPacketHandler, DownstreamPacket> getRedis() {
    return redis;
  }

  public void shutdown() {
    gameManager.shutdown();
    plugin.redisConfig.save().join();
    if (redis != null) {
      redis.shutdown();
    }
  }

  public static FirepalaceImpl get() {
    return (FirepalaceImpl) Firepalace.api();
  }
}
