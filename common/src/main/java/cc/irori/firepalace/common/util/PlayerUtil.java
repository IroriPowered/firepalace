package cc.irori.firepalace.common.util;

import com.hypixel.hytale.server.core.universe.PlayerRef;

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
}
