package cc.irori.firepalace.gui.redis;

import cc.irori.firepalace.common.redis.DownstreamPacketHandler;
import cc.irori.firepalace.common.redis.protocol.impl.downstream.DownstreamStatusPacket;
import cc.irori.firepalace.gui.FirepalaceGuiPlugin;

public class DownstreamPacketHandlerImpl implements DownstreamPacketHandler {

  private final FirepalaceGuiPlugin plugin;

  public DownstreamPacketHandlerImpl(FirepalaceGuiPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void handleStatusPacket(DownstreamStatusPacket packet) {

  }
}
