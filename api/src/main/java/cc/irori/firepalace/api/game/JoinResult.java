package cc.irori.firepalace.api.game;

import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.universe.world.World;
import javax.annotation.Nullable;

public record JoinResult(
    boolean allowJoin,
    World world,
    Transform spawnPosition,
    @Nullable String additionalInfo
) {

  public static JoinResult allow(World world, Transform spawnPosition) {
    return new JoinResult(true, world, spawnPosition, null);
  }

  public static JoinResult deny(String reason) {
    return new JoinResult(false, null, null, reason);
  }
}
