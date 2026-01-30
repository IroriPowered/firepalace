package cc.irori.firepalace.manager;

import cc.irori.firepalace.api.Firepalace;
import cc.irori.firepalace.common.util.Logs;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class FirepalaceManagerPlugin extends JavaPlugin {

  private FirepalaceImpl firepalace;

  public FirepalaceManagerPlugin(@NonNullDecl JavaPluginInit init) {
    super(init);
    Logs.setupLogger("Firepalace-Manager");
  }

  @Override
  protected void start() {
    this.firepalace = new FirepalaceImpl(this);
    Firepalace.setup(firepalace);
  }

  @Override
  protected void shutdown() {
    firepalace.shutdown();
  }

  protected <T> Config<T> config(String name, BuilderCodec<T> configCodec) {
    return withConfig(name, configCodec);
  }
}
