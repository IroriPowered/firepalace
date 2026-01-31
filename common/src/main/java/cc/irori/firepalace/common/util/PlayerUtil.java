package cc.irori.firepalace.common.util;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.concurrent.CompletableFuture;

public class PlayerUtil {

  // Private constructor to prevent instantiation
  private PlayerUtil() {
  }

  public static void referToServer(PlayerRef playerRef, String address) {
    String[] addressPart = address.split(":");
    String host = addressPart[0];
    int port = addressPart.length > 1 ? Integer.parseInt(addressPart[1]) : 5520;

    playerRef.referToServer(host, port);
  }

  public static void teleport(Holder<EntityStore> holder, Transform transform) {
    TransformComponent transformComponent = holder.ensureAndGetComponent(TransformComponent.getComponentType());
    transformComponent.setPosition(transform.getPosition());
    HeadRotation headRotationComponent = holder.ensureAndGetComponent(HeadRotation.getComponentType());
    headRotationComponent.teleportRotation(transform.getRotation());
  }
}
