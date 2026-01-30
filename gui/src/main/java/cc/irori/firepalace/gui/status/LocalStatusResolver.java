package cc.irori.firepalace.gui.status;

import cc.irori.firepalace.api.user.User;
import cc.irori.firepalace.common.status.GameStatus;
import cc.irori.firepalace.common.util.Logs;
import cc.irori.firepalace.manager.FirepalaceImpl;
import cc.irori.firepalace.manager.game.GameHolder;
import cc.irori.firepalace.manager.user.UserImpl;
import com.hypixel.hytale.logger.HytaleLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocalStatusResolver implements StatusResolver {

  private static final HytaleLogger LOGGER = Logs.logger();

  @Override
  public List<GameStatus> resolve() {
    FirepalaceImpl firepalace = FirepalaceImpl.get();
    List<GameStatus> statusList = new ArrayList<>();
    for (GameHolder holder : firepalace.getGameManager().getAllGameHolders()) {
      statusList.add(new GameStatus(
          holder.getMetadata(),
          holder.getGameInstance().getUsers().stream()
              .map(User::getUuid)
              .toList()
      ));
    }
    return statusList;
  }

  @Override
  public void joinGame(UUID uuid, String gameId) {
    FirepalaceImpl firepalace = FirepalaceImpl.get();
    UserImpl user = firepalace.getUserManager().getUser(uuid);

    GameHolder holder = firepalace.getGameManager().getGameHolder(gameId);
    if (holder == null) {
      LOGGER.atWarning().log("Join request for unknown game: %s", gameId);
      return;
    }

    user.joinGame(holder.getMetadata());
  }
}
