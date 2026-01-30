package cc.irori.firepalace.common.redis;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class RedisConfig {

  public static final BuilderCodec<RedisConfig> CODEC = BuilderCodec
      .builder(RedisConfig.class, RedisConfig::new)
      .append(
          new KeyedCodec<>("UseRemote", Codec.BOOLEAN),
          (config, newValue, extraInfo) -> config.useRemote = newValue,
          (config, extraInfo) -> config.useRemote
      ).add()
      .append(
          new KeyedCodec<>("RedisHost", Codec.STRING),
          (config, newValue, extraInfo) -> config.redisHost = newValue,
          (config, extraInfo) -> config.redisHost
      ).add()
      .append(
          new KeyedCodec<>("RedisPort", Codec.INTEGER),
          (config, newValue, extraInfo) -> config.redisPort = newValue,
          (config, extraInfo) -> config.redisPort
      ).add()
      .build();

  public boolean useRemote = false;
  public String redisHost = "localhost";
  public int redisPort = 6379;
}
