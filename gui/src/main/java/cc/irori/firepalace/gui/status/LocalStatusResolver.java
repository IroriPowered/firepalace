package cc.irori.firepalace.gui.status;

import cc.irori.firepalace.common.status.GameStatus;
import cc.irori.firepalace.manager.FirepalaceImpl;
import cc.irori.firepalace.manager.user.UserImpl;
import cc.irori.firepalace.manager.util.GameUtil;
import java.util.List;
import java.util.UUID;

public class LocalStatusResolver implements StatusResolver {

  @Override
  public List<GameStatus> resolve() {
    FirepalaceImpl firepalace = FirepalaceImpl.get();
    return GameUtil.getGameStatusList(firepalace);
  }

  @Override
  public void joinGame(UUID uuid, String gameId) {
    FirepalaceImpl firepalace = FirepalaceImpl.get();
    UserImpl user = firepalace.getUserManager().getUser(uuid);
    GameUtil.joinGameById(firepalace, user, gameId);
  }
}
