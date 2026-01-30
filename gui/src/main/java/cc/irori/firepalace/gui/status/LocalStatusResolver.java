package cc.irori.firepalace.gui.status;

import cc.irori.firepalace.common.status.GameStatus;
import cc.irori.firepalace.manager.FirepalaceImpl;
import cc.irori.firepalace.manager.user.UserImpl;
import cc.irori.firepalace.manager.util.GameUtil;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.List;

public class LocalStatusResolver implements StatusResolver {

  @Override
  public List<GameStatus> resolve() {
    FirepalaceImpl firepalace = FirepalaceImpl.get();
    return GameUtil.getGameStatusList(firepalace);
  }

  @Override
  public void joinGame(PlayerRef playerRef, String gameId) {
    FirepalaceImpl firepalace = FirepalaceImpl.get();
    UserImpl user = firepalace.getUserManager().getUser(playerRef);
    GameUtil.joinGameById(firepalace, user, gameId);
  }
}
