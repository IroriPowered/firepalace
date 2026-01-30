package cc.irori.firepalace.gui.status;

import cc.irori.firepalace.common.redis.protocol.impl.upstream.UpstreamRequestJoinPacket;
import cc.irori.firepalace.common.status.GameStatus;
import cc.irori.firepalace.gui.FirepalaceGuiPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.List;

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
  public void joinGame(PlayerRef playerRef, String gameId) {
    plugin.getRedis().sendPacket(new UpstreamRequestJoinPacket(playerRef.getUuid(), gameId));
  }

  public void updateStatusList(List<GameStatus> statusList) {
    this.statusList = statusList;
  }
}
