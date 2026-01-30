package cc.irori.firepalace.gui.status;

import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamQueueJoinPacket;
import cc.irori.firepalace.common.status.GameStatus;
import cc.irori.firepalace.gui.FirepalaceGuiPlugin;
import java.util.List;
import java.util.UUID;

public class RemoteStatusResolver implements StatusResolver {

  private final FirepalaceGuiPlugin plugin;

  private List<GameStatus> statusList = List.of();

  public RemoteStatusResolver(FirepalaceGuiPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public List<GameStatus> resolve() {
    return statusList;
  }

  @Override
  public void joinGame(UUID uuid, String gameId) {
    plugin.getRedis().sendPacket(new UpstreamQueueJoinPacket(uuid, gameId));
  }

  public void updateStatusList(List<GameStatus> statusList) {
    this.statusList = statusList;
  }
}
