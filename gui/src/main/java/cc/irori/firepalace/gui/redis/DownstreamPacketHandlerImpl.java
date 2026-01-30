package cc.irori.firepalace.gui.redis;

import cc.irori.firepalace.common.redis.DownstreamPacketHandler;
import cc.irori.firepalace.common.redis.IncomingPacketRegistry;
import cc.irori.firepalace.common.redis.PacketHandler;
import cc.irori.firepalace.common.redis.protocol.DownstreamPacket;
import cc.irori.firepalace.common.redis.protocol.impl.downstream.DownstreamStatusPacket;
import cc.irori.firepalace.gui.FirepalaceGuiPlugin;

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
  public void handleStatusPacket(DownstreamStatusPacket packet) {

  }
}
