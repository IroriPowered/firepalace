package cc.irori.firepalace.api.user;

import cc.irori.firepalace.api.game.Game;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.UUID;
import javax.annotation.Nullable;

public interface User {

  String getName();

  UUID getUuid();

  PlayerRef getPlayerRef();

  @Nullable Game getCurrentGame();

  UserState getState();
}
