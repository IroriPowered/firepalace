package cc.irori.firepalace.gui.status;

import cc.irori.firepalace.common.status.GameStatus;
import java.util.List;
import java.util.UUID;

public interface StatusResolver {

  List<GameStatus> resolve();

  void joinGame(UUID uuid, String gameId);
}
