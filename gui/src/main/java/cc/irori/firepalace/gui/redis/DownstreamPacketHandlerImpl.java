package cc.irori.firepalace.gui.redis;

import cc.irori.firepalace.common.redis.DownstreamPacketHandler;
import cc.irori.firepalace.common.redis.IncomingPacketRegistry;
import cc.irori.firepalace.common.redis.PacketHandler;
import cc.irori.firepalace.common.redis.protocol.DownstreamPacket;
import cc.irori.firepalace.common.redis.protocol.impl.downstream.DownstreamAcceptJoinPacket;
import cc.irori.firepalace.common.redis.protocol.impl.downstream.DownstreamStatusPacket;
import cc.irori.firepalace.common.util.PlayerUtil;
import cc.irori.firepalace.gui.FirepalaceGuiPlugin;
import cc.irori.firepalace.gui.GuiConfig;
import cc.irori.firepalace.gui.status.RemoteStatusResolver;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;

public class DownstreamPacketHandlerImpl implements DownstreamPacketHandler {

  private final FirepalaceGuiPlugin plugin;

  public DownstreamPacketHandlerImpl(FirepalaceGuiPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public IncomingPacketRegistry<? extends PacketHandler<DownstreamPacket>> getPacketRegistry() {
    return DownstreamIncomingPacketRegistry.INSTANCE;
  }

  @Override
  public void handleAcceptJoin(DownstreamAcceptJoinPacket packet) {
    PlayerRef playerRef = Universe.get().getPlayer(packet.uuid());
    if (playerRef == null) {
      return;
    }

    GuiConfig config = plugin.getGuiConfig();
    PlayerUtil.referToServer(playerRef, config.minigameServerAddress);
  }

  @Override
  public void handleStatus(DownstreamStatusPacket packet) {
    ((RemoteStatusResolver) plugin.getStatusResolver()).updateStatusList(packet.statusList());
  }
}
