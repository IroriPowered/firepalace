package cc.irori.firepalace.game.creative;

import cc.irori.firepalace.api.Firepalace;
import cc.irori.firepalace.api.game.metadata.GameMetadata;
import cc.irori.firepalace.api.game.metadata.GameTag;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class CreativeGamePlugin extends JavaPlugin {

  private static final GameMetadata METADATA = new GameMetadata(
      "creative",
      "Creative Freebuild",
      "[仮テキスト] クリエイティブモードで自由に建築! Hytaleの強力な建築ツールを使って、創造を楽しもう。",
      List.of(GameTag.SOLO),
      true
  );

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
