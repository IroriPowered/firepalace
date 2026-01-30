package cc.irori.firepalace.common.util;

import com.hypixel.hytale.logger.HytaleLogger;
import java.security.CodeSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public class Logs {

  private static final StackWalker WALKER = StackWalker.getInstance(
      StackWalker.Option.RETAIN_CLASS_REFERENCE);
  private static final String DEFAULT_LOGGER_NAME = "Firepalace";
  private static final Map<CodeSource, String> LOGGER_NAMES = new ConcurrentHashMap<>();

  public static void setupLogger(String name) {
    CodeSource callerSource = getCallerSource();
    if (callerSource != null) {
      LOGGER_NAMES.put(callerSource, name);
    }
  }

  public static HytaleLogger logger() {
    CodeSource callerSource = getCallerSource();
    String loggerName = DEFAULT_LOGGER_NAME;
    if (callerSource != null) {
      loggerName = LOGGER_NAMES.getOrDefault(callerSource, DEFAULT_LOGGER_NAME);
    }
    return HytaleLogger.get(loggerName);
  }

  private static @Nullable CodeSource getCallerSource() {
    Class<?> caller = WALKER.walk(
        frames -> frames
            .map(StackWalker.StackFrame::getDeclaringClass)
            .filter(clazz ->
                !clazz.getPackageName().startsWith("cc.irori.firepalace.common"))
            .findFirst()
            .orElse(null)
    );
    return caller != null ? caller.getProtectionDomain().getCodeSource() : null;
  }
}
