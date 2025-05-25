package bandung.se;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigTest {

    @Test
    public void testConfig() {
        Config config = Config.newBuilder().addDefaultSources().build();
        Assertions.assertEquals("A", config.getProperty("key"));
//        Assertions.assertEquals(2, config.getValue("array", String[].class).length);
        Assertions.assertEquals(3, config.getValues("list", Integer.class).size());
        Assertions.assertTrue(config.getOptionalValue("path", String.class).isPresent());
        Assertions.assertEquals("exist", config.getProperty("placeholder.withDefault"));
        Assertions.assertEquals("exist! bye", config.getProperty("inner.value"));
    }
}
