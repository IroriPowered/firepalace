package cc.irori.firepalace.game.creative;

import cc.irori.firepalace.api.Firepalace;
import cc.irori.firepalace.api.game.metadata.GameMetadata;
import cc.irori.firepalace.api.game.metadata.GameTag;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class CreativeGamePlugin extends JavaPlugin {

  // CHECKSTYLE.OFF: LineLength
  private static final GameMetadata METADATA = new GameMetadata(
      "creative",
      "Creative Freebuild",
      "冒険に疲れたら、クリエイティブモードで自由に建築を楽しもう！ みんなの作品を眺めたり、誰かと一緒に建築するも良し。",
      List.of(GameTag.SOLO),
      true
  );
    // CHECKSTYLE.ON: LineLength

  public CreativeGamePlugin(@NonNullDecl JavaPluginInit init) {
    super(init);
  }

  @Override
  protected void start() {
    Firepalace.api().registerGame(
        METADATA,
        CreativeGame::new
    );
  }

  @Override
  protected void shutdown() {
    Firepalace.api().unregisterGame(METADATA);
  }
}
