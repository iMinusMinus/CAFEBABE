package bandung.se;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Priority;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ConfigTest {

    @Test
    public void testConfig() {
        Config config = Config.newBuilder().addDefaultSources().build();
        Assertions.assertEquals("deadbeef", config.getProperty("spring.application.name"));
        Assertions.assertEquals(3, config.getValues("spring.config.imports", String.class).size());
        Assertions.assertTrue(config.getValues("spring.cache.cacheNames", String.class).size() >= 3);
        Assertions.assertTrue(config.getOptionalValue("executable.path", String.class).isPresent()); // 环境变量
        Assertions.assertEquals("deadbeefv0.5", config.getProperty("spring.cloud.gateway.routes[0].id"));
        Assertions.assertEquals("127.0.0.1", config.getProperty("spring.cloud.client.ip-address"));
        Assertions.assertEquals("127.0.0.1:deadbeef:8080", config.getProperty("eureka.instance.instance-id"));

//        Assertions.assertEquals("apse3-az1", config.getValues("spring.cloud.gateway.routes", RouteDefinition.class).get(0).metadata.get("zone"));
//        Assertions.assertEquals("authn", config.getValues("spring.cloud.gateway.routes", RouteDefinition.class).get(0).filters.get(0).name);
    }

    @Test
    public void testIncompleteConfig() {
        Config config = Config.newBuilder().addDefaultSources().build();
        Assertions.assertEquals("${fake", config.getValue("testcase.incomplete[0]", String.class));
        Assertions.assertEquals("deep${fake", config.getValue("testcase.incomplete[1]", String.class));
        Assertions.assertEquals("${${deepfake", config.getValue("testcase.incomplete[2]", String.class));
        Assertions.assertEquals("deep${fake", config.getValue("testcase.incomplete[3]", String.class));
        Assertions.assertEquals("deepfake${value", config.getValue("testcase.incomplete[4]", String.class));
        Assertions.assertEquals("deep${fake${value", config.getValue("testcase.incomplete[5]", String.class));
        Assertions.assertEquals("deep${fake:value", config.getValue("testcase.incomplete[6]", String.class));
        Assertions.assertEquals("deep${fake:val${ue", config.getValue("testcase.incomplete[7]", String.class));
    }

    @Test
    public void testRedundantConfig() {
        Config config = Config.newBuilder().addDefaultSources().build();
        Assertions.assertEquals("fa}ke", config.getValue("testcase.redundant[0]", String.class));
        Assertions.assertEquals("fa:k}e", config.getValue("testcase.redundant[1]", String.class));
        Assertions.assertEquals("fa}k:e", config.getValue("testcase.redundant[2]", String.class));
        Assertions.assertEquals("fa}k:e}", config.getValue("testcase.redundant[3]", String.class));
        Assertions.assertEquals(":news", config.getValue("testcase.redundant[4]", String.class));
    }

    @Test
    public void testCompositeConfig() {
        Config config = Config.newBuilder().addDefaultSources().build();
        String appName = config.getProperty("spring.application.name");
        Assertions.assertFalse(config.getOptionalValue("prefix", String.class).isPresent());
        Assertions.assertFalse(config.getOptionalValue("middle", String.class).isPresent());
        Assertions.assertFalse(config.getOptionalValue("suffix", String.class).isPresent());
        Assertions.assertEquals(appName, config.getValue("testcase.composite[0]", String.class));
        Assertions.assertEquals(appName, config.getValue("testcase.composite[1]", String.class));
        Assertions.assertEquals(appName, config.getValue("testcase.composite[2]", String.class));
        Assertions.assertEquals(appName, config.getValue("testcase.composite[3]", String.class));
        Assertions.assertEquals(appName, config.getValue("testcase.composite[4]", String.class));
        Assertions.assertEquals(appName, config.getValue("testcase.composite[5]", String.class));
        String prefix = "my-name-is:", suffix = "!", other = "han!";
        Assertions.assertEquals(prefix + appName, config.getValue("testcase.composite[6]", String.class));
        Assertions.assertEquals(prefix + appName, config.getValue("testcase.composite[7]", String.class));
        Assertions.assertEquals(prefix + appName + suffix, config.getValue("testcase.composite[8]", String.class));
        Assertions.assertEquals(prefix + appName + suffix, config.getValue("testcase.composite[9]", String.class));
        Assertions.assertEquals(prefix + other, config.getValue("testcase.composite[10]", String.class));
        Assertions.assertEquals(prefix + other, config.getValue("testcase.composite[11]", String.class));
        Assertions.assertEquals(prefix + appName, config.getValue("testcase.composite[12]", String.class));
        Assertions.assertEquals(prefix + appName, config.getValue("testcase.composite[13]", String.class));
        Assertions.assertEquals(prefix + appName + suffix, config.getValue("testcase.composite[14]", String.class));
        Assertions.assertEquals(prefix + appName + suffix, config.getValue("testcase.composite[15]", String.class));
        Assertions.assertEquals(prefix + other, config.getValue("testcase.composite[16]", String.class));
        Assertions.assertEquals(prefix + other, config.getValue("testcase.composite[17]", String.class));
        Assertions.assertEquals(prefix + appName, config.getValue("testcase.composite[18]", String.class));
        Assertions.assertEquals(prefix + appName, config.getValue("testcase.composite[19]", String.class));
        Assertions.assertEquals(prefix + appName + suffix, config.getValue("testcase.composite[20]", String.class));
        Assertions.assertEquals(prefix + appName + suffix, config.getValue("testcase.composite[21]", String.class));
        Assertions.assertEquals(prefix + other, config.getValue("testcase.composite[22]", String.class));
        Assertions.assertEquals(prefix + other, config.getValue("testcase.composite[23]", String.class));
    }

    @Test
    public void testConfigWithCustomConverter() {
        System.setProperty("test.date", "0");
        Config config = Config.newBuilder().addDefaultSources().withConverters(new TestConverter()).build();
        Assertions.assertNotNull(config.getValue("test.date", Date.class));
    }

    @Priority(-100)
    protected static class TestConverter implements Converter<String, Date> {

        @Override
        public Date convert(String source) throws IllegalArgumentException, NullPointerException {
            return new Date(Long.valueOf(source));
        }
    }

    @Getter
    @Setter
    @ToString
    protected static class RouteDefinition {

        private String id;

        private Map<String, Object> metadata;

        private List<FilterDefinition> filters;

//        public static RouteDefinition of(String value) {
//
//        }
    }

    @Getter
    @Setter
    @ToString
    protected static class FilterDefinition {

        private String name;

        private Map<String, Object> args;

    }
}
