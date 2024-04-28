package bandung.se;

import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingTest {

    private final static Logger log = Logger.getLogger(LoggingTest.class.getName());

    @Test
    public void testConfigClass() {
        log.log(Level.INFO, "green color text");
        log.log(Level.WARNING, "yellow color text");
        log.log(Level.SEVERE, "red color text");
    }
}
