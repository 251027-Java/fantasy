package core;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import dev.revature.fantasy.logger.GlobalLogger;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.LoggerFactory;

public abstract class BaseTest {
    @BeforeAll
    static void disableGlobalLogger() {
        Logger logger = (Logger) LoggerFactory.getLogger(GlobalLogger.class);
        logger.setLevel(Level.OFF);
    }
}
