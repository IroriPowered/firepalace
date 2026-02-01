package cc.irori.firepalace.gui;

import cc.irori.firepalace.common.redis.DownstreamPacketHandler;
import cc.irori.firepalace.common.redis.Recipient;
import cc.irori.firepalace.common.redis.RedisConfig;
import cc.irori.firepalace.common.redis.RedisParticipant;
import cc.irori.firepalace.common.redis.protocol.UpstreamPacket;
import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamRequestStatusPacket;
import cc.irori.firepalace.common.util.Logs;
import cc.irori.firepalace.common.util.PlayerUtil;
import cc.irori.firepalace.gui.command.firepalacegui.FirepalaceGuiCommand;
import cc.irori.firepalace.gui.command.mg.MgCommand;
import cc.irori.firepalace.gui.redis.DownstreamPacketHandlerImpl;
import cc.irori.firepalace.gui.status.LocalStatusResolver;
import cc.irori.firepalace.gui.status.RemoteStatusResolver;
import cc.irori.firepalace.gui.status.StatusResolver;
import cc.irori.firepalace.gui.ui.GameSelectPage;
import cc.irori.firepalace.manager.FirepalaceImpl;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class FirepalaceGuiPlugin extends JavaPlugin {

  private static FirepalaceGuiPlugin instance;

  private final Config<RedisConfig> redisConfig;
  private final Config<GuiConfig> guiConfig;

  private final Set<UUID> joiningUsers = new HashSet<>();

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

    getEventRegistry().register(PlayerConnectEvent.class, event -> {
      UUID uuid = event.getPlayerRef().getUuid();
      if (isLocal()) {
        FirepalaceImpl firepalace = FirepalaceImpl.get();
        if (!firepalace.getGameManager().isPreJoinHandled(uuid)) {
          joiningUsers.add(uuid);

          World world = Universe.get().getDefaultWorld();
          event.setWorld(world);
          Transform spawn = world.getWorldConfig().getSpawnProvider().getSpawnPoint(world, uuid);
          PlayerUtil.teleport(event.getHolder(), spawn);
        }
      }
    });

    getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
      if (!isLocal()) {
        return;
      }
      Ref<EntityStore> ref = event.getPlayerRef();
      Store<EntityStore> store = ref.getStore();

      event.getPlayer().getWorld().execute(() -> {
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        Player player = store.getComponent(ref, Player.getComponentType());
        UUID uuid = playerRef.getUuid();

        if (joiningUsers.remove(uuid)) {
          player.getPageManager().openCustomPage(ref, store,
              new GameSelectPage(playerRef, CustomPageLifetime.CantClose)
          );
        }
      });
    });

    getEventRegistry().register(PlayerDisconnectEvent.class, event -> {
      joiningUsers.remove(event.getPlayerRef().getUuid());
    });
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

  public boolean isLocal() {
    return !redisConfig.get().useRemote;
  }

  public static FirepalaceGuiPlugin get() {
    return instance;
  }
}
