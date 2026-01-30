package cc.irori.firepalace.gui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class GuiConfig {

  public static final BuilderCodec<GuiConfig> CODEC = BuilderCodec
      .builder(GuiConfig.class, GuiConfig::new)
      .append(
          new KeyedCodec<>("MainServerAddress", Codec.STRING),
          (config, newValue, extraInfo) -> config.mainServerAddress
              = newValue,
          (config, extraInfo) -> config.mainServerAddress
      ).add()
      .append(
          new KeyedCodec<>("MinigameServerAddress", Codec.STRING),
          (config, newValue, extraInfo) -> config.minigameServerAddress
              = newValue,
          (config, extraInfo) -> config.minigameServerAddress
      ).add()
      .build();

  public String mainServerAddress = "irori.cc";
  public String minigameServerAddress = "mg.irori.cc";
}
