package cc.irori.firepalace.gui.ui;

import cc.irori.firepalace.api.game.metadata.GameTag;
import cc.irori.firepalace.common.status.GameStatus;
import cc.irori.firepalace.common.util.PlayerUtil;
import cc.irori.firepalace.gui.FirepalaceGuiPlugin;
import cc.irori.shodo.BuiltInFontData;
import cc.irori.shodo.TextBox;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.PatchStyle;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class GameSelectPage extends InteractiveCustomUIPage<GameSelectPage.PageData> {

  private static final String KEY_GAME_TO_JOIN = "GameToJoin";

  private static final int GUI_CONTENT_WIDTH = 610;
  private static final int GUI_CONTENT_HEIGHT = 130;
  private static final int GUI_ICON_WIDTH = 231;
  private static final int GUI_TAG_SECTION_HEIGHT = 24;

  public GameSelectPage(@NonNullDecl PlayerRef playerRef,
                        @NonNullDecl
                  CustomPageLifetime lifetime) {
    super(playerRef, lifetime, PageData.CODEC);
  }

  @Override
  public void build(@NonNullDecl Ref<EntityStore> ref,
                    @NonNullDecl UICommandBuilder uiCommandBuilder,
                    @NonNullDecl UIEventBuilder uiEventBuilder,
                    @NonNullDecl Store<EntityStore> store) {
    uiCommandBuilder.append("Firepalace/GameMenu.ui");

    List<GameStatus> statusList = FirepalaceGuiPlugin.get().getStatusResolver().resolve();
    for (int i = 0; i < statusList.size(); i++) {
        appendGameCard(uiCommandBuilder, uiEventBuilder, statusList.get(i), i);
    }

    if (FirepalaceGuiPlugin.get().isLocal()) {
      uiCommandBuilder.set("#MainServerButtonContainer.Visible", true);
      uiEventBuilder.addEventBinding(
          CustomUIEventBindingType.Activating,
          "#MainServerButton",
          EventData.of(KEY_GAME_TO_JOIN, "_mainServer"),
          true
      );
    }
  }

  @Override
  public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref,
                              @NonNullDecl Store<EntityStore> store, @NonNullDecl PageData data) {
    PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
    Player player = store.getComponent(ref, Player.getComponentType());
    player.getPageManager().setPage(ref, store, Page.None);

    if (data.gameToJoin == null) {
      return;
    }
    if (data.gameToJoin.equals("_mainServer")) {
      PlayerUtil.referToServer(playerRef, FirepalaceGuiPlugin.get().getGuiConfig().mainServerAddress);
      return;
    }
    FirepalaceGuiPlugin.get().getStatusResolver().joinGame(playerRef, data.gameToJoin);
  }

  private static void appendGameCard(UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder, GameStatus game, int index) {
    TextBox textBox = TextBox.builder()
        .setWidth(GUI_CONTENT_WIDTH - GUI_ICON_WIDTH - 10)
        .setHeight(GUI_CONTENT_HEIGHT - GUI_TAG_SECTION_HEIGHT - 10)
        .setFont(BuiltInFontData.INSTANCE.ofScale(1.0))
        .setShadow(false)
        .setCleanupPeriodSeconds(0)
        .build();
    textBox.typesetter().addMessage(game.metadata().description());

    uiCommandBuilder.append("#GameCardContainer", "Firepalace/GameCard.ui");

    String selector = "#GameCardContainer[" + index + "] ";
    uiCommandBuilder.set(selector + "#GameName.Text", game.metadata().name());
    textBox.render(uiCommandBuilder, selector + "#GameDescription");

    for (int i = 0; i < game.metadata().tags().size(); i++) {
      appendTag(uiCommandBuilder, selector + "#TagContainer", game.metadata().tags().get(i));
    }

    int userCount = game.users().size();
    PatchStyle indicator = new PatchStyle();
    indicator.setTexturePath(Value.of("Firepalace/OnlineIndicator.png"));
    indicator.setColor(Value.of(userCount == 0 ? "#555555" : "#55ff55"));

    PatchStyle onlineText = new PatchStyle();
    onlineText.setTexturePath(Value.of("Firepalace/OnlineText.png"));
    onlineText.setColor(Value.of(userCount == 0 ? "#808080" : "#7fff7f"));

    uiCommandBuilder.setObject(selector + "#OnlineIndicator.Background", indicator);
    uiCommandBuilder.setObject(selector + "#OnlineText.Background", onlineText);

    if (userCount > 0) {
      uiCommandBuilder.set(selector + "#OnlineNumber.Visible", true);
      uiCommandBuilder.set(selector + "#OnlineNumber.Text", String.valueOf(userCount));
    } else {
      uiCommandBuilder.set(selector + "#OfflineNumber.Visible", true);
    }

    uiCommandBuilder.set(selector + "#GameIcon.Background", "Firepalace/GameIcon/" + game.metadata().id() + ".png");

    if (game.metadata().available()) {
      uiEventBuilder.addEventBinding(
          CustomUIEventBindingType.Activating,
          selector + "#GameButton",
          new EventData()
              .append(KEY_GAME_TO_JOIN, game.metadata().id()),
          true
      );
    } else {
      uiCommandBuilder.set(selector + "#GameButton.Disabled", true);
    }
  }

  private static void appendTag(UICommandBuilder uiCommandBuilder, String selector, GameTag tag) {
    // CHECKSTYLE.OFF: LineLength
    uiCommandBuilder.appendInline(selector, String.format("Group { Anchor: (Width: %d); Group { Anchor: (Width: %d); Background: PatchStyle(TexturePath: \"Firepalace/TagFrame.png\", Border: 8); } Group { Anchor: (Width: %d); Background: \"Firepalace/GameTag/%s.png\"; } }",
        tag.getImageWidth() + 6,
        tag.getImageWidth(),
        tag.getImageWidth(),
        tag.getId()
    ));
    // CHECKSTYLE.ON: LineLength
  }

  public static class PageData {

    static final BuilderCodec<PageData> CODEC = BuilderCodec.builder(PageData.class, PageData::new)
        .addField(
            new KeyedCodec<>(KEY_GAME_TO_JOIN, Codec.STRING),
            (data, value) -> data.gameToJoin = value,
            data -> data.gameToJoin
        )
        .build();

    private String gameToJoin = null;
  }
}
