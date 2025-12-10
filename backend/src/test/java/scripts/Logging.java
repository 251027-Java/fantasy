package scripts;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

public class Logging {
    public static final Logger ROOT_LOGGER = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("root");

    public static void disableLogging() {
        ROOT_LOGGER.setLevel(Level.OFF);
    }

    public static void enableLogging(Level level) {
        ROOT_LOGGER.setLevel(level);
    }
}
