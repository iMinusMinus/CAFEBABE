package bandung.ee.json;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.json.bind.annotation.JsonbTypeSerializer;
import javax.json.bind.annotation.JsonbVisibility;
import javax.json.bind.config.BinaryDataStrategy;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.json.bind.config.PropertyOrderStrategy;
import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Queue;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

public class ObjectJsonMapperTest {

    private static JavaSEDataStructure base;

    @BeforeAll
    protected static void setUp() throws MalformedURLException, URISyntaxException {
        base = new JavaSEDataStructure();
        base.setZ(true);
        base.setB((byte) 'B');
        base.setC('C');
        base.setS((short) 1);
        base.setI(10000000);
        base.setL(10000000L);
        base.setF(3.14F);
        base.setD(3.14159265);
        base.setWz(Boolean.FALSE);
        base.setWb(Byte.valueOf("98"));
        base.setWc(Character.valueOf('X'));
        base.setWs(Short.valueOf("10000"));
        base.setWf(Float.valueOf("9.8"));
        base.setNumber(BigDecimal.TEN);
        base.setLongLong(new BigInteger("-221"));
        base.setDecimal(new BigDecimal("1840"));
        base.setStr("自强不息，博学笃志");
        base.setUrl(new URL("https://www.w3c.org/"));
        base.setUri(base.getUrl().toURI());
        base.setOi(OptionalInt.empty());
        base.setOl(OptionalLong.of(10000L));
        base.setOd(OptionalDouble.of(2.7d));
        base.setOe(Optional.of(TestEnum.HIGH));
        base.setDate(new Date());
        base.setCalendar(new GregorianCalendar());
        base.setTz(TimeZone.getTimeZone("Asia/Shanghai"));
        base.setStz(new SimpleTimeZone(6, "Asia/Urumqi"));
        base.setInstant(Instant.EPOCH);
        base.setDuration(Duration.of(3L, ChronoUnit.SECONDS));
        base.setPeriod(Period.of(276, 0, 0));
        base.setLocalDate(LocalDate.of(8, 7, 6));
        base.setLocalTime(LocalTime.of(13, 45));
        base.setLocalDateTime(LocalDateTime.of(1644, 3, 19, 9, 0));
        base.setZonedDateTime(ZonedDateTime.of(base.getLocalDateTime(), ZoneId.of("Asia/Shanghai")));
        base.setZoneId(ZoneId.of("Asia/Harbin"));
        base.setZoneOffset(ZoneOffset.ofHours(5));
        base.setOffsetDateTime(OffsetDateTime.of(1644, 3, 19, 1, 0, 0, 0, ZoneOffset.UTC));
        base.setOffsetTime(OffsetTime.of(1, 0, 0, 0, ZoneOffset.UTC));
        base.setGregorianCalendar(new GregorianCalendar());
        List<Map<String, Set<Number>>> collection = new ArrayList<>();
        Map<String, Set<Number>> map = new TreeMap<>();
        Set<Number> set = new LinkedHashSet<>();
        set.add(BigDecimal.ONE);
        set.add(2);
        set.add(null);
        map.put("seq-1", set);
        map.put("seq-2", Collections.emptySet());
        collection.add(map);
        base.setBa("Hello World!".getBytes(StandardCharsets.UTF_8));
        base.setCollection(collection);
    }

    @Getter
    @Setter
    @ToString
    protected static class JavaSEDataStructure {

        private boolean z;

        private byte b;

        private char c;

        private short s;

        private int i;

        private long l;

        private float f;

        private double d;

        private Boolean wz;

        private Byte wb;

        private Character wc;

        private Short ws;

        private Integer wi;

        private Long wl;

        private Float wf;

        private Double wd;

        private BigInteger longLong;

        private BigDecimal decimal;

        private Number number;

        private String str;

        private URL url;

        private URI uri;

        private OptionalInt oi;

        private OptionalLong ol;

        private OptionalDouble od;

        private Optional<TestEnum> oe;

        private Date date;

        private Calendar calendar;

        private GregorianCalendar gregorianCalendar;

        private TimeZone tz;

        private SimpleTimeZone stz;

        private Instant instant;

        private Duration duration;

        private Period period;

        private LocalDate localDate;

        private LocalTime localTime;

        private LocalDateTime localDateTime;

        private ZonedDateTime zonedDateTime;

        private ZoneId zoneId;

        private ZoneOffset zoneOffset;

        private OffsetDateTime offsetDateTime;

        private OffsetTime offsetTime;

        private List<Map<String, Set<Number>>> collection;

        private byte[] ba;
    }

    @Test
    @DisplayName("基础数据结构")
    public void testDefaultMapping() {
        JsonbConfig config = new JsonbConfig();
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        String json = jsonb.toJson(base);
        System.out.println(json);
        Assertions.assertTrue(json.contains("\"z\":true"));
        Assertions.assertTrue(json.contains("\"number\":10"));
        Assertions.assertFalse(json.contains("\"oi\":"));
        Assertions.assertTrue(json.contains("\"ol\":10000"));
        Assertions.assertTrue(json.contains("\"date\":\"20"));
        Assertions.assertTrue(json.contains("\"localDate\":"));
        Assertions.assertTrue(json.contains("\"localDateTime\":"));
        Assertions.assertTrue(json.contains("\"zonedDateTime\":"));
        Assertions.assertTrue(json.contains("\"collection\":[{"));
        Assertions.assertTrue(json.contains("\"ba\":["));

        JavaSEDataStructure struct = jsonb.fromJson(json, JavaSEDataStructure.class);
        Assertions.assertNull(struct.getWi());
        Assertions.assertNotNull(struct.getCalendar());
        Assertions.assertNotNull(struct.getCollection());
        Assertions.assertEquals(base.getB(), struct.getB());
        Assertions.assertArrayEquals(base.getBa(), struct.getBa());

    }

    @Test
    @DisplayName("基础数据结构反序列化，缺失字段、json多出字段、json事件类型和java类型不一致")
    public void testDeserializeWithMissingField() {
        String json = "{\"mustIgnore\":1,\"ba\":[72,101,108,108,111,32,87,111,114,108,100,33],\"c\":\"C\",\"calendar\":\"2025-01-11\",\"collection\":[{\"seq-1\":[1,2,null],\"seq-2\":[]}],\"d\":3.14159265,\"date\":\"2025-01-11T09:41:45.948Z[UTC]\",\"decimal\":\"1840\",\"duration\":\"PT3S\",\"f\":3.14,\"gregorianCalendar\":\"2025-01-11T17:41:45.965+08:00[Asia/Shanghai]\",\"i\":10000000,\"instant\":\"1970-01-01T00:00:00Z\",\"l\":10000000,\"localDate\":\"0008-07-06\",\"localDateTime\":\"1644-03-19T09:00:00\",\"localTime\":\"13:45:00\",\"longLong\":-221,\"number\":10,\"od\":2.7,\"oe\":\"h\",\"offsetDateTime\":\"1644-03-19T01:00:00Z\",\"offsetTime\":\"01:00:00Z\",\"ol\":10000,\"period\":\"P276Y\",\"s\":1,\"str\":\"自强不息，博学笃志\",\"stz\":\"Asia/Shanghai\",\"tz\":\"Asia/Shanghai\",\"uri\":\"https://www.w3c.org/\",\"url\":\"https://www.w3c.org/\",\"wb\":98,\"wc\":\"X\",\"wf\":9.8,\"ws\":10000,\"wz\":false,\"z\":true,\"zoneId\":\"Asia/Harbin\",\"zoneOffset\":\"+05:00\",\"zonedDateTime\":\"1644-03-19T09:00:00+08:05:43[Asia/Shanghai]\"}";
        JsonbConfig config = new JsonbConfig();
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        JavaSEDataStructure struct = jsonb.fromJson(json, JavaSEDataStructure.class);
        System.out.println(struct);
        Assertions.assertEquals((byte) 0, struct.getB());
        Assertions.assertTrue(base.getDecimal().compareTo(struct.getDecimal()) == 0);
    }

    @Test
    @DisplayName("指定命名策略、指定日期格式化、指定排序、指定可见性、指定写null值、指定byte[]数组格式化方式，基础数据结构")
    public void testDefaultMappingWithConfig() {
        JsonbConfig config = new JsonbConfig();
        config.withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_DASHES);
        config.withDateFormat(JsonbDateFormat.TIME_IN_MILLIS, Locale.getDefault());
        config.withPropertyOrderStrategy(PropertyOrderStrategy.REVERSE);
        config.withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
            @Override
            public boolean isVisible(Field field) {
                return false;
            }

            @Override
            public boolean isVisible(Method method) {
                return Modifier.isPublic(method.getModifiers()) && !method.getName().equals("getNumber") && !method.getName().equals("setNumber");
            }
        });
        config.withNullValues(true);
        config.withBinaryDataStrategy(BinaryDataStrategy.BASE_64_URL);
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        String json = jsonb.toJson(base);
        System.out.println(json);
        Assertions.assertTrue(json.contains("\"local-date-time\":"));
        Assertions.assertFalse(json.contains("\"date\":\"20"));
        Assertions.assertTrue(json.indexOf("\"b\":") > json.indexOf("\"d\":"));
        Assertions.assertFalse(json.contains("\"number\":\"10\""));
        Assertions.assertTrue(json.contains("\"oi\":null"));
        Assertions.assertTrue(json.contains("\"ba\":\""));

        JavaSEDataStructure struct = jsonb.fromJson(json, JavaSEDataStructure.class);
        Assertions.assertNotNull(struct.getLocalDate());
        Assertions.assertEquals(OptionalInt.empty(), struct.getOi());
        Assertions.assertArrayEquals(base.getBa(), struct.getBa());
        Assertions.assertEquals(1644, struct.getLocalDateTime().getYear());
        Assertions.assertEquals(null, struct.getNumber());
    }


    @Test
    @DisplayName("I-JSON，并指定其他配置，基础数据结构")
    public void testDefaultMappingWithIJson() {
        JsonbConfig config = new JsonbConfig();
        config.withStrictIJSON(true);
        config.withNullValues(true);
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        String json = jsonb.toJson(base);
        System.out.println(json);
        Assertions.assertTrue(json.contains("\"date\":\"20"));
        Assertions.assertTrue(json.contains("\"wi\":null"));

        JavaSEDataStructure struct = jsonb.fromJson(json, JavaSEDataStructure.class);
        Assertions.assertEquals(OptionalInt.empty(), struct.getOi());
        Assertions.assertArrayEquals(base.getBa(), struct.getBa());
        Assertions.assertEquals(1644, struct.getLocalDateTime().getYear());
    }

    protected static class AnnotatedBaseStructure extends JavaSEDataStructure {

        private String nonDuplicateName;

        @JsonbProperty(value = "str")
        public String getNonDuplicateName() {
            return nonDuplicateName;
        }

        public void setNonDuplicateName(String nonDuplicateName) {
            this.nonDuplicateName = nonDuplicateName;
        }
    }

    @Test
    @DisplayName("key同名，序列化基础数据结构抛出异常")
    public void testSerializeWithSameKey() {
        JsonbConfig config = new JsonbConfig();
        config.withStrictIJSON(true);
        config.withDateFormat(JsonbDateFormat.TIME_IN_MILLIS, Locale.getDefault());
        config.withNullValues(true);
        config.withPropertyOrderStrategy(PropertyOrderStrategy.ANY);
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        AnnotatedBaseStructure child = new AnnotatedBaseStructure();
        child.setNonDuplicateName("即便只设置一个序列化后同名key其中一个值");
        child.setWi(1024);
        child.setWl(1L);
        Assertions.assertThrows(JsonbException.class, () -> jsonb.toJson(child));
    }

    protected static class JavaSEDataStructureAdapter implements JsonbAdapter<JavaSEDataStructure, Map<String, Object>> {

        @Override
        public Map<String, Object> adaptToJson(JavaSEDataStructure obj) {
            if (obj == null) {
                return Collections.emptyMap();
            }
            Map<String, Object> m = new HashMap<>();
            m.put("date", obj.getDate());
            m.put("oi", obj.getOi());
            return m;
        }

        @Override
        public JavaSEDataStructure adaptFromJson(Map<String, Object> obj) {
            if (obj == null || obj.isEmpty()) {
                return null;
            }
            JavaSEDataStructure value = new JavaSEDataStructure();
            value.setDate(Date.from(ZonedDateTime.parse((String) obj.get("date"), DateTimeFormatter.ISO_DATE_TIME.withZone(ObjectJsonMapper.UTC)).toInstant()));
            Integer i = (Integer) obj.get("oi");
            value.setOi(i == null ? OptionalInt.empty() : OptionalInt.of(i));
            return value;
        }
    }

    @Test
    @DisplayName("指定全局适配器，基础数据结构")
    public void testDefaultMappingWithAdapter() {
        JsonbConfig config = new JsonbConfig();
        config.withNullValues(true);
        config.withPropertyOrderStrategy(PropertyOrderStrategy.ANY);
        config.withAdapters(new JavaSEDataStructureAdapter());
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        String json = jsonb.toJson(base);
        System.out.println(json);
        Assertions.assertFalse(json.contains("\"str\":"));
        Assertions.assertTrue(json.contains("\"oi\":null"));
        Assertions.assertTrue(json.contains("\"date\":\""));

        JavaSEDataStructure struct = jsonb.fromJson(json, JavaSEDataStructure.class);
        Assertions.assertNotNull(struct.getDate());
        Assertions.assertEquals(OptionalInt.empty(), struct.getOi());
        Assertions.assertEquals((byte) 0, struct.getB());
        Assertions.assertNull(struct.getCalendar());
    }

    protected static class JavaSEDataStructureSerializer implements JsonbSerializer<JavaSEDataStructure> {

        @Override
        public void serialize(JavaSEDataStructure obj, JsonGenerator generator, SerializationContext ctx) {
            generator.writeStartObject();
            generator.write("d", obj.getD());
            generator.write("b", obj.getB());
            generator.write("date", (int) (obj.getDate().getTime() / 1000));
            generator.writeEnd();
        }
    }

    protected static class JavaSEDataStructureDeserializer implements JsonbDeserializer<JavaSEDataStructure> {

        @Override
        public JavaSEDataStructure deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            JsonParser.Event current = parser.next();
            assert current == JsonParser.Event.START_OBJECT;
            JavaSEDataStructure result = new JavaSEDataStructure();
            while ((current = parser.next()) != JsonParser.Event.END_OBJECT) {
                assert current == JsonParser.Event.KEY_NAME;
                String key = parser.getString();;
                current = parser.next();
                assert current == JsonParser.Event.VALUE_NUMBER || current == JsonParser.Event.VALUE_STRING || current == JsonParser.Event.VALUE_NULL;
                if ("d".equals(key)) {
                    result.setD(Double.parseDouble(parser.getString()));
                } else if ("b".equals(key)) {
                    result.setB(Byte.parseByte(parser.getString()));
                } else if ("date".equals(key)) {
                    result.setDate(new Date(parser.getInt() * 1000L));
                } else if ("oi".equals(key)) {
                    if (current == JsonParser.Event.VALUE_NULL) {
                        result.setOi(OptionalInt.empty());
                    } else if (current == JsonParser.Event.VALUE_NUMBER) {
                        result.setOi(OptionalInt.of(parser.getInt()));
                    }
                }
            }
            return result;
        }
    }

    @Test
    @DisplayName("指定全局序列化器/反序列化器，基础数据结构")
    public void testDefaultMappingWithSerializerAndDeserializer() {
        JsonbConfig config = new JsonbConfig();
        config.withNullValues(true);
        config.withStrictIJSON(true);
        config.withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL);
        config.withSerializers(new JavaSEDataStructureSerializer());
        config.withDeserializers(new JavaSEDataStructureDeserializer());
        config.withAdapters(new JavaSEDataStructureAdapter());
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        String json = jsonb.toJson(base);
        System.out.println(json);
        Assertions.assertTrue(json.indexOf("\"b\":") > json.indexOf("\"d\":"));
        Assertions.assertTrue(json.contains("\"date\":"));
        Assertions.assertFalse(json.contains("\"date\":\""));
        Assertions.assertFalse(json.contains("\"oi\":null"));

        JavaSEDataStructure struct = jsonb.fromJson(json, JavaSEDataStructure.class);
        Assertions.assertNull(struct.getOl());
        Assertions.assertNotNull(struct.getDate());
        Assertions.assertNotEquals(0, struct.getB());
        Assertions.assertNotEquals(0.0d, struct.getD());
    }

    @ToString
    @JsonbPropertyOrder(value = {"tenantId", "timestamp", "data", "signature"})
    @JsonbVisibility(value = CustomVisibilityStrategy.class)
    @JsonbNillable
    protected static class AnnotatedDTO {

        @JsonbCreator
        protected static AnnotatedDTO getInstance(@JsonbProperty(value = "data") String data) {
            AnnotatedDTO dto = new AnnotatedDTO();
            dto.setData(data);
            return dto;
        }

        @JsonbNumberFormat(value = "#,####.00")
        private long tenantId;

        @JsonbDateFormat(value = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime timestamp;

        private byte[] signature;

        private String data;

        private boolean mark;

        private String nonce;

        @JsonbProperty(value = "TenantId")
        public long getTenantId() {
            return tenantId;
        }

        @JsonbProperty(value = "tenant-id")
        public void setTenantId(long tenantId) {
            this.tenantId = tenantId;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public byte[] getSignature() {
            return signature;
        }

        public void setSignature(byte[] signature) {
            this.signature = signature;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public boolean isMark() {
            return mark;
        }

        public void setMark(boolean mark) {
            this.mark = mark;
        }

        @JsonbTransient
        public String getNonce() {
            return nonce;
        }

        @JsonbTransient
        public void setNonce(String nonce) {
            this.nonce = nonce;
        }
    }

    protected static class CustomVisibilityStrategy implements PropertyVisibilityStrategy {

        @Override
        public boolean isVisible(Field field) {
            return !"mark".equals(field.getName());
        }

        @Override
        public boolean isVisible(Method method) {
            return !"isMark".equals(method.getName()) && !"setMark".equals(method.getName());
        }
    }

    @Test
    @DisplayName("注解有可忽略标记、可写null、别名、日期格式、数字格式、可见性、排序")
    public void testCustomMapping() {
        JsonbConfig config = new JsonbConfig();
        config.withNullValues(false);
        config.withStrictIJSON(true);
        config.withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
        config.withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL);
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        AnnotatedDTO dto = new AnnotatedDTO();
        dto.setTenantId(Integer.MAX_VALUE);
        dto.setTimestamp(LocalDateTime.now());
        dto.setSignature("time is wasting".getBytes(StandardCharsets.UTF_8));
        dto.setMark(true);
        dto.setNonce(UUID.randomUUID().toString());
        String json = jsonb.toJson(dto);
        System.out.println(json);
        Assertions.assertFalse(json.contains("\"nonce\":"));
        Assertions.assertTrue(json.contains("\"data\":null"));
        Assertions.assertTrue(json.contains("\"TenantId\":"));
        Assertions.assertFalse(json.contains("\"tenantId\":"));
        Assertions.assertFalse(json.contains("\"tenant_id\":"));
        Assertions.assertFalse(json.contains("\"tenant-id\":"));
        Assertions.assertFalse(json.substring(json.indexOf("\"timestamp\""), json.indexOf("\"timestamp\"") + 25).contains("T"));
        Assertions.assertTrue(json.substring(json.indexOf("TenantId"), json.indexOf("TenantId") + 15).contains(","));
        Assertions.assertFalse(json.contains("\"mark\""));
        Assertions.assertTrue(json.indexOf("TenantId") < json.indexOf("timestamp"));
        Assertions.assertTrue(json.indexOf("timestamp") < json.indexOf("data"));

        AnnotatedDTO result = jsonb.fromJson(json, AnnotatedDTO.class);
        Assertions.assertNull(result.getNonce());
        Assertions.assertEquals(0, result.getTenantId());
        Assertions.assertNotNull(result.getTimestamp());

        AnnotatedDTO struct = jsonb.fromJson("{\"tenant-id\":\"21,4748,3647.00\",\"timestamp\":\"2025-01-12 12:54:58\",\"nonce\":\"ignored\",\"signature\":\"dGltZSBpcyB3YXN0aW5n\"}", AnnotatedDTO.class);
        Assertions.assertEquals(Integer.MAX_VALUE, struct.getTenantId());
        Assertions.assertNull(struct.getNonce());
        Assertions.assertNotNull(struct.getTimestamp());
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @JsonbTypeAdapter(value = TestAdapter.class)
    @JsonbDateFormat(value = "yyyy-MM-dd HH:mm:ss.SSS")
    public static class AnnotatedEntity {

        private long tenantId;

        private LocalDateTime timestamp;

        private String signature;

        private String content;
    }

    protected static class TestAdapter extends BaseAdapter<AnnotatedEntity, AnnotatedDTO> {

        @Override
        public AnnotatedDTO adaptToJson(AnnotatedEntity obj) {
            if (obj == null) {
                return null;
            }
            AnnotatedDTO dto = new AnnotatedDTO();
            dto.setTenantId(obj.getTenantId());
            dto.setTimestamp(obj.getTimestamp());
            if (obj.getSignature() != null) {
                dto.setSignature(Base64.getDecoder().decode(obj.getSignature()));
            }
            dto.setData(obj.getContent());
            return dto;
        }

        @Override
        public AnnotatedEntity adaptFromJson(AnnotatedDTO obj) {
            if (obj == null) {
                return null;
            }
            AnnotatedEntity entity = new AnnotatedEntity();
            entity.setTenantId(obj.getTenantId());
            entity.setTimestamp(obj.getTimestamp());
            if (obj.getSignature() != null) {
                entity.setSignature(Base64.getEncoder().encodeToString(obj.getSignature()));
            }
            entity.setContent(obj.getData());
            return entity;
        }
    }

    @Test
    @DisplayName("注解有适配器")
    public void testCustomMappingWithAnnotatedAdapter() {
        JsonbConfig config = new JsonbConfig();
        config.withNullValues(false);
        config.withStrictIJSON(true);
        config.withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
        config.withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL);
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        AnnotatedEntity dto = new AnnotatedEntity();
        dto.setTenantId(Integer.MAX_VALUE);
        dto.setTimestamp(LocalDateTime.now().withNano(0));
        dto.setSignature(Base64.getEncoder().encodeToString("time is wasting".getBytes(StandardCharsets.UTF_8)));
        dto.setContent("Fight");
        String json = jsonb.toJson(dto);
        System.out.println(json);
        Assertions.assertFalse(json.contains("\"tenantId\":"));
        Assertions.assertFalse(json.contains("\"tenant_id\":"));
        Assertions.assertTrue(json.substring(json.indexOf("TenantId"), json.indexOf("TenantId") + 15).contains(","));
        Assertions.assertFalse(json.substring(json.indexOf("\"timestamp\""), json.indexOf("\"timestamp\"") + 25).contains("T"));
        Assertions.assertFalse(json.substring(json.indexOf("\"timestamp\""), json.indexOf("\"timestamp\"") + 32).contains("."));

        AnnotatedEntity struct = jsonb.fromJson(json.replace("TenantId", "tenant-id"), AnnotatedEntity.class);
        Assertions.assertEquals(dto, struct);

    }

    protected static class SimpleDTOSerializer implements JsonbSerializer<SimpleDTO> {

        @Override
        public void serialize(SimpleDTO obj, JsonGenerator generator, SerializationContext ctx) {
            generator.writeStartObject();
            int value = 0;
            if (obj.a) {
                value += 1;
            }
            if (obj.b) {
                value |= (1 << 1);
            }
            if (obj.c) {
                value |= (1 << 2);
            }
            if (obj.d) {
                value |= (1 << 3);
            }
            generator.write("value", value);
            generator.writeEnd();
        }
    }

    protected static class SimpleDTODeserializer implements JsonbDeserializer<SimpleDTO> {

        @Override
        public SimpleDTO deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            JsonParser.Event current = parser.next();
            assert current == JsonParser.Event.START_OBJECT;
            current = parser.next();
            if (current == JsonParser.Event.END_OBJECT) {
                return null;
            }
            assert "value".equals(parser.getString());
            current = parser.next();
            assert current == JsonParser.Event.VALUE_NUMBER;
            int value = parser.getInt();
            SimpleDTO dto = new SimpleDTO();
            if ((value & 1) != 0) {
                dto.setA(true);
            }
            if ((value & 2) != 0) {
                dto.setB(true);
            }
            if ((value & 4) != 0) {
                dto.setC(true);
            }
            if ((value & 8) != 0) {
                dto.setD(true);
            }
            return dto;
        }
    }

    @Getter
    @Setter
    @ToString
    @JsonbTypeSerializer(value = SimpleDTOSerializer.class)
    @JsonbTypeDeserializer(value = SimpleDTODeserializer.class)
    protected static class SimpleDTO {

        private boolean a;

        private boolean b;

        private boolean c;

        private boolean d;
    }

    @Test
    @DisplayName("注解有序列化器/反序列化器")
    public void testCustomMappingWithAnnotatedSerializerAndDeserializer() {
        JsonbConfig config = new JsonbConfig();
        config.withNullValues(false);
        config.withStrictIJSON(true);
        config.withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
        config.withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL);
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        SimpleDTO dto = new SimpleDTO();
        dto.setA(false);
        dto.setB(true);
        dto.setC(true);
        String json = jsonb.toJson(dto);
        Assertions.assertTrue(json.contains("\"value\":6"));

        SimpleDTO struct = jsonb.fromJson(json, SimpleDTO.class);
        Assertions.assertTrue(struct.isB() && struct.isC());
        Assertions.assertFalse(struct.isA());
        Assertions.assertFalse(struct.isD());
    }

    @Getter
    @Setter
    @ToString
    protected static class Wrapper {

        private ZonedDateTime zonedDateTime;

        private DTO dto;

        @Getter
        @Setter
        @ToString
        protected class DTO {

            private final long tenantId;

            @JsonbCreator
            public DTO(@JsonbProperty(value = "tenantId") long tenantId) {
                this.tenantId = tenantId;
            }

            private LocalDateTime timestamp;

            private byte[] signature;

            private String bodyData;
        }
    }


    @Test
    @DisplayName("内部类、非静态内部类")
    public void testDefaultMappingForInnerType() {
        JsonbConfig config = new JsonbConfig();
        config.withNullValues(false);
        config.withStrictIJSON(true);
        config.withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
        config.withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL);
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        Wrapper w = new Wrapper();
        w.setZonedDateTime(ZonedDateTime.now());
        Wrapper.DTO d = w.new DTO(1L);
        d.setTimestamp(LocalDateTime.now());
        d.setBodyData("request body");
        w.setDto(d);
        String json = jsonb.toJson(w);
        System.out.println(json);

        Wrapper struct = jsonb.fromJson(json.replace("tenant_id", "tenantId"), Wrapper.class);
        Assertions.assertNotNull(struct.getZonedDateTime());
        Assertions.assertNotNull(struct.getDto());
        Assertions.assertEquals(1, struct.getDto().getTenantId());
        Assertions.assertNotNull(struct.getDto().getTimestamp());
        Assertions.assertNotNull(struct.getDto().getBodyData());
    }


    @ParameterizedTest(name = ParameterizedTest.ARGUMENTS_WITH_NAMES_PLACEHOLDER)
    @ValueSource(strings = {
            "{\"tenantId\":1,\"timestamp\":\"2024-12-15 10:17:19.123\",\"signature\":[116,101,115,116],\"data\":\"字段不多不少，base64\"}",
            "{\"tenantId\":2,\"timestamp\":\"2024-12-15 10:17:19.123\",\"nonce\":\"1734229049142\",\"signature\":[116,101,115,116],\"data\":\"json字段比java属性多，base64\"}",
            "{\"tenantId\":3,\"timestamp\":\"2024-12-15 10:17:19.123\",\"data\":\"json字段比java属性少\"}",
            "{\"tenantId\":1,\"timestamp\":\"2024-12-15 10:17:19.123\",\"signature\":[116,101,115,116],\"data\":\"字段不多不少\"}",
            "{\"tenantId\":\"9007199254740992\",\"timestamp\":\"2024-12-15 10:17:19.123\",\"signature\":\"dGVzdA==\",\"data\":\"java属性和json事件不一致\"}",
    })
    public void testDCustomMappingWithConfigurationAndAnnotation(String json) { // JsonbDeserializer直接声明处理的类型
        JsonbConfig config = new JsonbConfig();
        config.withDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        config.withStrictIJSON(true);
        config.withDeserializers(new TestDeserializer());
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        AnnotatedDTO dto = jsonb.fromJson(json, AnnotatedDTO.class);
        System.out.println(dto);
        Assertions.assertTrue(dto.getTenantId() >= 0);
        Assertions.assertNotNull(dto.getTimestamp());
        Assertions.assertNotNull(dto.getData());

        String stringfy = jsonb.toJson(dto);
        System.out.println(stringfy);
    }

    protected static class TestDeserializer implements JsonbDeserializer<AnnotatedDTO> {

        private AnnotatedDTO obj;

        @Override
        public AnnotatedDTO deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            String key = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (parser.hasNext()) {
                JsonParser.Event current = parser.next();
                switch (current) {
                    case START_OBJECT:
                        obj = new AnnotatedDTO();
                        break;
                    case KEY_NAME:
                        key = parser.getString();
                        break;
                    case VALUE_NUMBER:
                        if ("tenantId".equals(key)) {
                            obj.setTenantId(parser.getLong());
                        } else if ("signature".equals(key)) {
                            baos.write((byte) parser.getInt());
                        }
                        break;
                    case VALUE_STRING:
                        if ("tenantId".equals(key)) {
                            obj.setTenantId(Long.parseLong(parser.getString()));
                        } else if ("timestamp".equals(key)) {
                            obj.setTimestamp(LocalDateTime.parse(parser.getString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
                        } else if ("signature".equals(key)) {
                            obj.setSignature(Base64.getDecoder().decode(parser.getString()));
                        } else if ("data".equals(key)) {
                            obj.setData(parser.getString());
                        }
                        break;
                    case VALUE_NULL:

                    case START_ARRAY:
                        if (!"signature".equals(key)) {
                            throw new JsonbException("unexpected event");
                        }
                        break;
                    case END_ARRAY:
                        if (!"signature".equals(key)) {
                            throw new JsonbException("unexpected event");
                        }
                        obj.setSignature(baos.toByteArray());
                        break;
                    case END_OBJECT:
                        return obj;

                }
            }
            throw new JsonbException("unexpected state");
        }
    }


    @Test
    public void testDefaultMappingForMap() {
        JsonbConfig config = new JsonbConfig();
        config.setProperty(BindingProvider.JSONB_ANNOTATION_INTROSPECTOR, new AnnotationIntrospector());
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        Map<String, Integer> map = new HashMap<>();
        String em = jsonb.toJson(map);
        System.out.println(em);

        Map m = jsonb.fromJson(em, Map.class);
        Assertions.assertNotNull(m);
        Assertions.assertTrue(m.isEmpty());

        map.put("key1", 1);
        map.put("key2", 2);
        String obj = jsonb.toJson(map);
        System.out.println(obj);
        Map<String, Integer> pm = jsonb.fromJson(obj, new ParameterizedType() {

            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] {String.class, Integer.class};
            }

            @Override
            public Type getRawType() {
                return Map.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        });
        Assertions.assertNotNull(pm);
        Assertions.assertEquals(2, pm.size());
        Assertions.assertEquals(1, pm.get("key1"));
    }

    @Test
    public void testDefaultMappingForCollection() {
        JsonbConfig config = new JsonbConfig();
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        List<Integer> list = Arrays.asList(1, 2, 3 ,4);
        String json = jsonb.toJson(list);
        Assertions.assertEquals("[1,2,3,4]", json);

        List<Integer> obj = jsonb.fromJson(json, new ParameterizedType() {

            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] {Integer.class};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        });

        Assertions.assertEquals(list, obj);
    }

    @Test
    public void testDefaultMappingForArrayThatBeanPropertyOrderOverrideConfigured() {
        JsonbConfig config = new JsonbConfig();
        config.setProperty(BindingProvider.JSONB_ANNOTATION_INTROSPECTOR, new AnnotationIntrospector());
        config.withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL);
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        AnnotatedDTO[] array = new AnnotatedDTO[1];
        AnnotatedDTO dto = new AnnotatedDTO();
        dto.setTimestamp(LocalDateTime.now());
        dto.setTenantId(86L);
        dto.setData("测试");
        dto.setSignature("test".getBytes(StandardCharsets.UTF_8));
        dto.setMark(true);
        array[0] = dto;
        String s = jsonb.toJson(array);
        System.out.println(s);
        Assertions.assertTrue(s.indexOf("timestamp") < s.indexOf("data"));
        Assertions.assertTrue(s.indexOf("data") < s.indexOf("signature"));

        AnnotatedDTO[] a = jsonb.fromJson(s.replace("TenantId", "tenant-id"), AnnotatedDTO[].class);
        Assertions.assertEquals(1, a.length);
        Assertions.assertEquals(dto.getTenantId(), a[0].getTenantId());
        Assertions.assertEquals(dto.getData(), a[0].getData());
        Assertions.assertArrayEquals(dto.getSignature(), a[0].getSignature());
    }

    @Test
    public void testSerializerIndirectImpl() { // JsonbSerializer父类声明可处理类型，父类实现的接口声明可处理类型
        JsonbConfig config = new JsonbConfig();
        config.setProperty(BindingProvider.JSONB_ANNOTATION_INTROSPECTOR, new AnnotationIntrospector());
        config.withSerializers(new TestSerializer());
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        AnnotatedDTO dto = new AnnotatedDTO();
        dto.setTenantId(1L);
        dto.setTimestamp(LocalDateTime.now());
        dto.setSignature("test".getBytes(StandardCharsets.UTF_8));
        dto.setData("测试内容");
        Assertions.assertTrue(jsonb.toJson(dto, AnnotatedDTO.class).contains("ts"));
    }

    static class TestSerializer implements DebugSerializer {

        @Override
        public void serialize(AnnotatedDTO obj, JsonGenerator generator, SerializationContext ctx) {
            generator.writeStartObject();
            generator.write("tenantId", obj.tenantId);
            generator.write("ts", obj.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            generator.write("signature", Base64.getMimeEncoder().encodeToString(obj.signature));
            generator.writeKey("data");
            generator.write(obj.data);
            generator.writeEnd();
        }
    }

    interface DebugSerializer extends JsonbSerializer<AnnotatedDTO> {}

    @Test
    public void testSerializeSelfReferenceWithoutCycle() {
        JsonbConfig config = new JsonbConfig();
        config.setProperty(BindingProvider.JSONB_ANNOTATION_INTROSPECTOR, new AnnotationIntrospector());
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        Fork fork = new Fork();
        fork.setAge(3);
        Fork branch = new Fork();
        branch.setAge(2);
        branch.setLeaf(true);
        fork.setBranch(branch);
        String json = jsonb.toJson(fork);
        System.out.println(json);
        Assertions.assertTrue(json.contains("branch"));
    }

    @Test
    public void testSerializeCyclicReference() {
        JsonbConfig config = new JsonbConfig();
        config.setProperty(BindingProvider.JSONB_ANNOTATION_INTROSPECTOR, new AnnotationIntrospector());
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();

        Object[] x = new Object[3];
        x[0] =1;
        x[1] = x;
        x[2] = x[0];
        Assertions.assertThrows(JsonbException.class, () -> jsonb.toJson(x));

//        Map<String, List<Object>> map = new HashMap<>();
//        List<Object> list = new ArrayList<>();
//        map.put("list", list);
//        Map<String, List<Object>> nest = new HashMap<>();
//        nest.put("inner", list);
//        list.add(nest);
//        map.put("lv", Arrays.asList(1, 2, 3));
//        Assertions.assertThrows(JsonbException.class, () -> jsonb.toJson(map));

        Fork fork = new Fork();
        fork.setAge(3);
        Fork branch = new Fork();
        branch.setAge(2);
        branch.setBranch(fork);
        fork.setBranch(branch);
        Assertions.assertThrows(JsonbException.class, () -> jsonb.toJson(fork));

        Optional<Fork> opt = Optional.of(fork);
        Assertions.assertThrows(JsonbException.class, () -> jsonb.toJson(opt));

        fork.getBranch().setBranch(null);
        System.out.println(jsonb.toJson(fork));
        Middle m = new Middle();
        m.setM(1);
        Cycle c = new Cycle();
        c.setLength(2);
        c.setFork(fork);
        m.setCycle(c);
        fork.setIndirect(m);
        Assertions.assertThrows(JsonbException.class, () -> jsonb.toJson(fork));
    }

    @Getter
    @Setter
    @ToString
    protected static class Fork {

        private int age;

        private boolean leaf;

        private Fork branch;

        private Middle indirect;
    }

    @Getter
    @Setter
    @ToString
    protected static class Middle {

        private int m;

        private Cycle cycle;
    }

    @Getter
    @Setter
    @ToString
    protected static class Cycle {

        private long length;

        private Fork fork;
    }


    @Test
    public void testCustomMappingWithIndirectAdapter() {
        JsonbConfig config = new JsonbConfig();
        config.setProperty(BindingProvider.JSONB_ANNOTATION_INTROSPECTOR, new AnnotationIntrospector());
        config.withFormatting(true);
        config.withAdapters(new AnotherAdapter());
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        Original entity = new Original();
        String json = jsonb.toJson(entity);
        System.out.println(json);
        Assertions.assertTrue(json.contains("{\n    \"tenantId\": 0\n}"));

        Original adapted = jsonb.fromJson(json, Original.class);
        Assertions.assertNotNull(adapted);
        Assertions.assertEquals(0, adapted.getTenantId());
    }

    @Test
    public void testCustomMappingWithTypeVariableAdapter() {
        JsonbConfig config = new JsonbConfig();
        config.setProperty(BindingProvider.JSONB_ANNOTATION_INTROSPECTOR, new AnnotationIntrospector());
        config.withFormatting(true);
        config.withAdapters(new TypeVariableAdapter());
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        Original entity = new Original();
        entity.setSignature(Base64.getEncoder().encodeToString("王侯将相，宁有种乎".getBytes(StandardCharsets.UTF_8)));
        String json = jsonb.toJson(entity);
        System.out.println(json);
        Assertions.assertEquals("{\n    \"signature\": \"546L5L6v5bCG55u477yM5a6B5pyJ56eN5LmO\",\n    \"tenantId\": 0\n}", json);

        Original adapted = jsonb.fromJson(json, Original.class);
        Assertions.assertNotNull(adapted);
        Assertions.assertEquals(0, adapted.getTenantId());
        Assertions.assertEquals(entity.getSignature(), adapted.getSignature());
    }

    @Getter
    @Setter
    @ToString
    protected static class Original {
        private long tenantId;

        private LocalDateTime timestamp;

        private String signature;

        private String content;
    }

    @Getter
    @Setter
    @ToString
    protected static class Adapted {
        private long tenantId;

        private LocalDateTime timestamp;

        private byte[] signature;

        private String data;
    }


    interface GenericAdapter extends JsonbAdapter<Original, Adapted> {

    }

    protected static class AnotherAdapter implements GenericAdapter {

        @Override
        public Adapted adaptToJson(Original obj) {
            if (obj == null) {
                return null;
            }
            Adapted dto = new Adapted();
            dto.setTenantId(obj.getTenantId());
            dto.setTimestamp(obj.getTimestamp());
            if (obj.getSignature() != null) {
                dto.setSignature(Base64.getDecoder().decode(obj.getSignature()));
            }
            dto.setData(obj.getContent());
            return dto;
        }

        @Override
        public Original adaptFromJson(Adapted obj) {
            if (obj == null) {
                return null;
            }
            Original entity = new Original();
            entity.setTenantId(obj.getTenantId());
            entity.setTimestamp(obj.getTimestamp());
            if (obj.getSignature() != null) {
                entity.setSignature(Base64.getEncoder().encodeToString(obj.getSignature()));
            }
            entity.setContent(obj.getData());
            return entity;
        }
    }

    static abstract class BaseAdapter<S, T> implements JsonbAdapter<S,T> {

    }

    protected static class TypeVariableAdapter<S extends Original, T extends Adapted> extends BaseAdapter<S, T> {

        @Override
        public T adaptToJson(S obj) {
            if (obj == null) {
                return null;
            }
            Adapted dto = new Adapted();
            dto.setTenantId(obj.getTenantId());
            dto.setTimestamp(obj.getTimestamp());
            if (obj.getSignature() != null) {
                dto.setSignature(Base64.getDecoder().decode(obj.getSignature()));
            }
            dto.setData(obj.getContent());
            return (T) dto;
        }

        @Override
        public S adaptFromJson(T obj) {
            if (obj == null) {
                return null;
            }
            Original entity = new Original();
            entity.setTenantId(obj.getTenantId());
            entity.setTimestamp(obj.getTimestamp());
            if (obj.getSignature() != null) {
                entity.setSignature(Base64.getEncoder().encodeToString(obj.getSignature()));
            }
            entity.setContent(obj.getData());
            return (S) entity;
        }
    }

    @Test
    public void testCustomMappingWithGenericArrayAndEnumAsProperty() throws Exception {
        JsonbConfig config = new JsonbConfig();
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        TestGeneric<BigDecimal, URL> generic = new TestGeneric<>();
        generic.setE(TestEnum.MIDDLE);
        BigDecimal[] decimals = {BigDecimal.ONE, BigDecimal.TEN};
        generic.setN(decimals);
        Queue<URL> queue = new ArrayBlockingQueue<>(10);
        queue.add(new URL("https://jcp.org/en/home/index"));
        queue.add(new URL("https://openjdk.org/"));
        generic.setQ(queue);
        Map<String, HashMap<String, URL>> m = new HashMap<>();
        HashMap<String, URL> hm = new HashMap<>();
        hm.put("jdk", new URL("https://jdk.java.net/"));
        m.put("键名", hm);
        generic.setMap(m);
        ParameterizedType pt = new ParameterizedType() {

            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] {BigDecimal.class, URL.class};
            }

            @Override
            public Type getRawType() {
                return TestGeneric.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        String json = jsonb.toJson(generic, pt);
        System.out.println(json);

        TestGeneric<BigDecimal, URL> parsed = jsonb.fromJson(json, pt);
        Assertions.assertEquals(generic.getE(), parsed.getE());
        Assertions.assertArrayEquals(generic.getN(), parsed.getN());
    }

    @ToString
    protected class GenericClass<T> {
        public T[] genericField;
    }

    @Getter
    @Setter
    @ToString
    protected class WrapperGenericClass<X> {
        protected GenericClass<X> propagatedGenericField;
    }

    @Test
    public void testCustomMappingWithImplicitType() {
        JsonbConfig config = new JsonbConfig();
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).withConfig(config).build();
        AnotherClass ao = new AnotherClass();
        GenericClass<TestEnum> g = new GenericClass<>();
        g.genericField = new TestEnum[] {TestEnum.LOW, TestEnum.HIGH};
        ao.setPropagatedGenericField(g);
        String json = jsonb.toJson(ao);
        System.out.println(json);

        AnotherClass parsed = jsonb.fromJson(json, AnotherClass.class);
        Assertions.assertArrayEquals(new TestEnum[] {TestEnum.LOW, TestEnum.HIGH}, parsed.getPropagatedGenericField().genericField);
    }

    protected class AnotherClass extends WrapperGenericClass<TestEnum> {}

    @Getter
    @Setter
    @ToString
    protected static class TestGeneric<T extends Number, V> {

        private T[] n;

        private TestEnum e;

        private Map<String, HashMap<String, V>> map;

        private Queue<V> q;
    }

    protected enum TestEnum {
        @JsonbProperty(value = "h")
        HIGH,
        @JsonbProperty(value = "m")
        MIDDLE,
        @JsonbProperty(value = "l")
        LOW;
    }

}
