package bandung.ee.json;

import bandung.se.Conventions;
import bandung.se.JavaBean;
import bandung.se.JavaProperty;
import bandung.se.JvmIntrospector;
import bandung.se.NamingConvention;
import bandung.se.Pair;
import bandung.se.Polymorphism;
import bandung.se.Reflections;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.stream.JsonParsingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import jakarta.json.JsonArray;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.config.BinaryDataStrategy;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.json.bind.config.PropertyOrderStrategy;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DateTimeException;
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
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.time.zone.ZoneRulesException;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <ul>Default Mapping
 *     <li>Basic Java Types
 *         <ul>
 *             <li>String</li>
 *             <li>Character</li>
 *             <li>Byte</li>
 *             <li>Short</li>
 *             <li>Integer</li>
 *             <li>Long</li>
 *             <li>Float</li>
 *             <li>Double</li>
 *             <li>Boolean</li>
 *             <li>Number</li>
 *         </ul>
 *     </li>
 *     <li>Specific Standard Java SE Types
 *         <ul>
 *             <li>BigInteger</li>
 *             <li>BigDecimal</li>
 *             <li>URL</li>
 *             <li>URI</li>
 *             <li>Optional</li>
 *             <li>OptionalInt</li>
 *             <li>OptionalLong</li>
 *             <li>OptionalDouble</li>
 *         </ul>
 *     </li>
 *     <li>Dates
 *         <ul>
 *             <li>Date</li>
 *             <li>Calendar</li>
 *             <li>GregorianCalendar</li>
 *             <li>TimeZone</li>
 *             <li>SimpleTimeZone</li>
 *             <li>Instant</li>
 *             <li>Duration</li>
 *             <li>Period</li>
 *             <li>LocalDate</li>
 *             <li>LocalTime</li>
 *             <li> LocalDateTime</li>
 *             <li>ZonedDateTime</li>
 *             <li>ZoneId</li>
 *             <li>ZoneOffset</li>
 *             <li>OffsetDateTime</li>
 *             <li>OffsetTime</li>
 *         </ul>
 *     </li>
 *     <li>Untyped mapping
 *         <ul>当Java类型为Object时，根据json解析出来的类型来决定
 *             <li>json object被映射为Map&#60;String, Object&#62;</li>
 *             <li>json array被映射为List&#60;Object&#62;</li>
 *             <li>json string被映射为String</li>
 *             <li>json number被映射为BigDecimal</li>
 *             <li>json true/false被映射为Boolean</li>
 *             <li>json null被映射为null</li>
 *         </ul>
 *     </li>
 *     <li>Java Class
 *          <ul>
 *              <li>反序列化时，优先通过public的setter，非public或对应名称方法不存在时使用字段设置</li>
 *              <li>序列化时，优先使用public的getter，非public或对应名称方法不存在时直接使用字段</li>
 *              <li>禁止对final、static、transient的字段进行反序列化</li>
 *              <li>必须支持final字段的序列化，禁止对static、transient字段进行序列化</li>
 *              <li>getter和setter对应的字段可以不存在，也需支持序列化/反序列化</li>
 *              <li>需支持对修饰符为public/protected的内部类序列化</li>
 *              <li>需支持对匿名内部类进行序列化，禁止对匿名内部类进行反序列化</li>
 *          </ul>
 *     </li>
 *     <li>Polymorphic Types</li>
 *     <li>Enum
 *         <ul>
 *             <li>序列化枚举类使用name()方法，反序列化使用valueOf()方法</li>
 *         </ul>
 *     </li>
 *     <li>Interfaces
 *         <ul>
 *             <li>对集合的反序列化见集合部分</li>
 *         </ul>
 *     </li>
 *     <li>Collections
 *         <ul>
 *             <li>Collection</li>
 *             <li>Map</li>
 *             <li>Set</li>
 *             <li>HashSet</li>
 *             <li>NavigableSet</li>
 *             <li>SortedSet</li>
 *             <li>TreeSet</li>
 *             <li>LinkedHashSet</li>
 *             <li>HashMap</li>
 *             <li>NavigableMap</li>
 *             <li>SortedMap</li>
 *             <li>TreeMap</li>
 *             <li>LinkedHashMap</li>
 *             <li>List</li>
 *             <li>ArrayList</li>
 *             <li>LinkedList</li>
 *             <li>Deque</li>
 *             <li>ArrayDeque</li>
 *             <li>Queue</li>
 *             <li>PriorityQueue</li>
 *         </ul>
 *     </li>
 *     <li>Arrays
 *         <ul>
 *             <li>需支持上述类型的数组，包括多维数组的序列化和反序列化</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * @author iMinusMinus
 * @date 2025-01-18
 */
public class ObjectJsonMapper implements Jsonb {

    private static final Logger log = Logger.getLogger(ObjectJsonMapper.class.getName());

    static final ZoneId UTC = ZoneId.of("UTC");

    /**
     * <a href="https://datatracker.ietf.org/doc/html/rfc3339#section-5.6">Internet Date/Time Format</a>
     */
    static final DateTimeFormatter IJSON_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral('T')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .appendLiteral('Z')
            .appendOffset("+HH:MM", "+00:00")
            .toFormatter();

    static final Function<Instant, ZonedDateTime> TO_ZONED = x -> x.atZone(UTC);

    static final Function<DateTimeFormatter, DateTimeFormatter> WITH_ZONE = x -> x.getZone() != null ? x : x.withZone(UTC);

    static final Function<Long, LocalDateTime> LONG_TO_LOCAL_DATETIME = x -> LocalDateTime.ofInstant(Instant.ofEpochMilli(x), UTC);

    static final Function<Long, OffsetDateTime> LONG_TO_OFFSET_DATETIME = x -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(x), UTC);

    private static final int INVISIBLE = 0;

    private static final int FIELD = 1;

    private static final int PROPERTY = 6;

    private final JsonParserFactory jsonParserFactory;

    private final JsonGeneratorFactory jsonGeneratorFactory;

    private final JsonBuilderFactory jsonBuilderFactory;

    private final Charset encoding;

    private final JsonbConfig config;

    private final Map<Class<?>, JsonbSerializer<?>> builtInSerializers = new HashMap<>();

    private final Map<Class<?>, JsonbDeserializer<?>> builtInDeserializers = new HashMap<>();

    static final SimpleMapping<String> STR_MAPPING = new SimpleMapping<>(Function.identity());

    public ObjectJsonMapper(JsonbConfig config, JsonProvider jsonpProvider) {
        Boolean formatting = (Boolean) config.getProperty(JsonbConfig.FORMATTING).orElse(Boolean.FALSE);
        Map<String, Object> configInUse;
        if (formatting) {
            configInUse = new HashMap<>(config.getAsMap());
            configInUse.put(JsonGenerator.PRETTY_PRINTING, true);
        } else {
            configInUse = config.getAsMap();
        }
        this.config = config;
        this.jsonParserFactory = jsonpProvider.createParserFactory(configInUse);
        this.jsonGeneratorFactory = jsonpProvider.createGeneratorFactory(configInUse);
        this.jsonBuilderFactory = jsonpProvider.createBuilderFactory(configInUse);
        Optional<Object> enc = config.getProperty(JsonbConfig.ENCODING);
        this.encoding = enc.map(o -> Charset.forName((String) o)).orElse(StandardCharsets.UTF_8);
        initBuiltIn();
    }

    void initBuiltIn() {
        Locale locale = (Locale) config.getProperty(JsonbConfig.LOCALE).orElse(Locale.getDefault());
        boolean ijson = (boolean) config.getProperty(JsonbConfig.STRICT_IJSON).orElse(false);
        String dateFormat = (String) config.getProperty(JsonbConfig.DATE_FORMAT).orElse(null);
        boolean datetimeAsNumber = false;
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(dateFormat)) {
            datetimeAsNumber = true;
            dateFormat = null;
        } else if (JsonbDateFormat.DEFAULT_FORMAT.equals(dateFormat)) {
            dateFormat = null;
        }
        Optional<DateTimeFormatter> df = Optional.ofNullable(dateFormat).map(x -> DateTimeFormatter.ofPattern(x, locale));
        CharMapping characterMapping = new CharMapping();
        CharMapping charMapping = new CharMapping(Conventions.C);
        BoolMapping booleanMapping = new BoolMapping();
        BoolMapping boolMapping = new BoolMapping(Conventions.Z);
        SimpleMapping<URL> urlMapping = new SimpleMapping<>(s -> {
            try {
                return new URL(s);
            } catch (MalformedURLException mue) {
                throw new JsonbException(mue.getMessage(), mue);
            }});
        SimpleMapping<URI> uriMapping = new SimpleMapping<>(URI::create);
        DateTimeSerializer<Date> dateSerializer;
        DateTimeDeserializer<Date> dateDeserializer;
        CalendarSerializer<Calendar> calendarSerializer;
        CalendarDeserializer calendarDeserializer;
        DateTimeSerializer<Instant> instantSerializer;
        DateTimeDeserializer<Instant> instantDeserializer = new DateTimeDeserializer<>(Instant::ofEpochMilli, df.map(WITH_ZONE).orElse(DateTimeFormatter.ISO_INSTANT.withZone(UTC).withLocale(locale)), (s, f) -> Instant.from(f.parse(s)));
        DateTimeSerializer<LocalDate> localDateSerializer;
        DateTimeDeserializer<LocalDate> localDateDeserializer = new DateTimeDeserializer<>(TO_ZONED.compose(Instant::ofEpochMilli).andThen(ZonedDateTime::toLocalDate), df.orElse(DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale)), LocalDate::parse);
        DateTimeSerializer<LocalDateTime> localDateTimeSerializer;
        DateTimeDeserializer<LocalDateTime> localDateTimeDeserializer = new DateTimeDeserializer<>(LONG_TO_LOCAL_DATETIME, df.orElse(DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(locale)), LocalDateTime::parse);
        DateTimeSerializer<ZonedDateTime> zonedDateTimeSerializer;
        DateTimeDeserializer<ZonedDateTime> zonedDatetimeDeserializer = new DateTimeDeserializer<>(Conventions.J2I.andThen(TO_ZONED), df.map(WITH_ZONE).orElse(DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(locale)), ZonedDateTime::parse);
        DateTimeSerializer<OffsetDateTime> offsetDateTimeSerializer = new DateTimeSerializer<>(df.orElse(DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(locale)));
        if (datetimeAsNumber) {
            dateSerializer = new DateTimeSerializer<>(Date::getTime);
            dateDeserializer = new DateTimeDeserializer<>(Date::new, DateTimeFormatter.ISO_DATE_TIME.withZone(UTC).withLocale(locale), (s, f) -> Date.from(ZonedDateTime.parse(s, f).toInstant()));
            calendarSerializer = new CalendarSerializer<>();
            calendarDeserializer = new CalendarDeserializer(null, locale);
            instantSerializer = new DateTimeSerializer<>(Conventions.I2J);
            localDateSerializer = new DateTimeSerializer<>(Conventions.I2J.compose(x -> x.atStartOfDay(UTC).toInstant()));
            localDateTimeSerializer = new DateTimeSerializer<>(Conventions.I2J.compose(x -> x.atZone(UTC).toInstant()));
            zonedDateTimeSerializer = new DateTimeSerializer<>(Conventions.I2J.compose(ZonedDateTime::toInstant));
            offsetDateTimeSerializer = new DateTimeSerializer<>(Conventions.I2J.compose(OffsetDateTime::toInstant));
        } else if (ijson) {
            dateSerializer = new DateTimeSerializer<>(IJSON_DATE_FORMATTER.withZone(UTC), Date::toInstant);
            dateDeserializer = new DateTimeDeserializer<>(Date::new, IJSON_DATE_FORMATTER, (s, f) -> Date.from(ZonedDateTime.parse(s, f).toInstant()));
            calendarSerializer = new CalendarSerializer<>(IJSON_DATE_FORMATTER, locale);
            calendarDeserializer = new CalendarDeserializer(IJSON_DATE_FORMATTER, locale);
            instantSerializer = new DateTimeSerializer<>(IJSON_DATE_FORMATTER.withZone(UTC));
            instantDeserializer = new DateTimeDeserializer<>(Instant::ofEpochMilli, IJSON_DATE_FORMATTER.withZone(UTC), (s, f) -> Instant.from(f.parse(s)));
            localDateSerializer = new DateTimeSerializer<>(IJSON_DATE_FORMATTER.withZone(UTC), x -> x.atTime(LocalTime.of(0, 0, 0)).atZone(UTC));
            localDateDeserializer = new DateTimeDeserializer<>(TO_ZONED.compose(Instant::ofEpochMilli).andThen(ZonedDateTime::toLocalDate), IJSON_DATE_FORMATTER, LocalDate::parse);
            localDateTimeSerializer = new DateTimeSerializer<>(IJSON_DATE_FORMATTER, x -> x.atZone(UTC));
            localDateTimeDeserializer = new DateTimeDeserializer<>(LONG_TO_LOCAL_DATETIME, IJSON_DATE_FORMATTER, LocalDateTime::parse);
            zonedDateTimeSerializer = new DateTimeSerializer<>(IJSON_DATE_FORMATTER);
            zonedDatetimeDeserializer = new DateTimeDeserializer<>(Conventions.J2I.andThen(TO_ZONED), IJSON_DATE_FORMATTER.withZone(UTC), ZonedDateTime::parse);
        } else {
            dateSerializer = new DateTimeSerializer<>(df.map(WITH_ZONE).orElse(DateTimeFormatter.ISO_DATE_TIME.withZone(UTC).withLocale(locale)), Date::toInstant);
            dateDeserializer = new DateTimeDeserializer<>(Date::new, df.map(WITH_ZONE).orElse(DateTimeFormatter.ISO_DATE_TIME.withZone(UTC).withLocale(locale)), (s, f) -> Date.from(ZonedDateTime.parse(s, f).toInstant()));
            calendarSerializer = new CalendarSerializer<>(df.orElse(null), locale);
            calendarDeserializer = new CalendarDeserializer(df.orElse(null), locale);
            instantSerializer = new DateTimeSerializer<>(df.map(WITH_ZONE).orElse(DateTimeFormatter.ISO_INSTANT.withZone(UTC).withLocale(locale)));
            localDateSerializer = new DateTimeSerializer<>(df.orElse(DateTimeFormatter.ISO_LOCAL_DATE.withZone(UTC).withLocale(locale)));
            localDateTimeSerializer = new DateTimeSerializer<>(df.map(WITH_ZONE).orElse(DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(locale)));
            zonedDateTimeSerializer = new DateTimeSerializer<>(df.orElse(DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(locale)));
        }
        Function<String, TimeZone> tzDeserializer = x -> {
            try {
                ZoneId zoneId = ZoneId.of(x);
                return new SimpleTimeZone(LocalDateTime.now().atZone(zoneId).getOffset().getTotalSeconds() * 1000, zoneId.getId());
            } catch (ZoneRulesException zre) {
                throw new JsonbException(zre.getMessage(), zre);
            }
        };
        SimpleMapping<TimeZone> timezoneMapping = new SimpleMapping<>(tzDeserializer, TimeZone::getID);

        SimpleMapping<ZoneId> zoneIdMapping = new SimpleMapping<>(ZoneId::of, ZoneId::getId);
        SimpleMapping<ZoneOffset> zoneOffsetMapping = new SimpleMapping<>(ZoneOffset::of, ZoneOffset::getId);
        SimpleMapping<Duration> durationMapping = new SimpleMapping<>(Duration::parse);
        SimpleMapping<Period> periodMapping = new SimpleMapping<>(Period::parse);
        String binaryData = (String) config.getProperty(JsonbConfig.BINARY_DATA_STRATEGY).orElse(null);
        Base64.Encoder encoder = null;
        Base64.Decoder decoder = null;
        if (ijson || BinaryDataStrategy.BASE_64_URL.equals(binaryData)) {
            encoder = Base64.getUrlEncoder();
            decoder = Base64.getUrlDecoder();
        } else if (BinaryDataStrategy.BASE_64.equals(binaryData)) {
            encoder = Base64.getEncoder();
            decoder = Base64.getDecoder();
        }
        builtInSerializers.put(String.class, STR_MAPPING);
        builtInSerializers.put(Character.class, characterMapping);
        builtInSerializers.put(Byte.class, new NumberSerializer<Byte>());
        builtInSerializers.put(Short.class, new NumberSerializer<Short>());
        builtInSerializers.put(Integer.class, new NumberSerializer<Integer>());
        builtInSerializers.put(Long.class, new NumberSerializer<Long>());
        builtInSerializers.put(Float.class, new NumberSerializer<Float>());
        builtInSerializers.put(Double.class, new NumberSerializer<Double>());
        builtInSerializers.put(Boolean.class, booleanMapping);
        builtInSerializers.put(Number.class, new NumberSerializer<>());
        builtInSerializers.put(BigInteger.class, new NumberSerializer<BigInteger>());
        builtInSerializers.put(BigDecimal.class, new NumberSerializer<BigDecimal>());
        builtInSerializers.put(URL.class, urlMapping);
        builtInSerializers.put(URI.class, uriMapping);
        builtInSerializers.put(Character.TYPE, charMapping);
        builtInSerializers.put(Byte.TYPE, builtInSerializers.get(Byte.class));
        builtInSerializers.put(Short.TYPE, builtInSerializers.get(Short.class));
        builtInSerializers.put(Integer.TYPE, builtInSerializers.get(Integer.class));
        builtInSerializers.put(Long.TYPE, builtInSerializers.get(Long.class));
        builtInSerializers.put(Float.TYPE, builtInSerializers.get(Float.class));
        builtInSerializers.put(Double.TYPE, builtInSerializers.get(Double.class));
        builtInSerializers.put(Boolean.TYPE, boolMapping);
        builtInSerializers.put(OptionalInt.class, new NumberSerializer<>(OptionalInt::getAsInt));
        builtInSerializers.put(OptionalLong.class, new NumberSerializer<>(OptionalLong::getAsLong));
        builtInSerializers.put(OptionalDouble.class, new NumberSerializer<>(OptionalDouble::getAsDouble));
        builtInSerializers.put(Date.class, dateSerializer); // java.sql.Date, java.sql.Time, java.sql.Timestamp
        builtInSerializers.put(Calendar.class, calendarSerializer); // java.util.GregorianCalendar
        builtInSerializers.put(TimeZone.class, timezoneMapping);
        builtInSerializers.put(Instant.class, instantSerializer);
        builtInSerializers.put(Duration.class, durationMapping);
        builtInSerializers.put(Period.class, periodMapping);
        builtInSerializers.put(LocalDate.class, localDateSerializer);
        builtInSerializers.put(LocalTime.class, new DateTimeSerializer<>(df.orElse(DateTimeFormatter.ISO_LOCAL_TIME.withLocale(locale))));
        builtInSerializers.put(LocalDateTime.class, localDateTimeSerializer);
        builtInSerializers.put(ZonedDateTime.class, zonedDateTimeSerializer);
        builtInSerializers.put(ZoneId.class, zoneIdMapping);
        builtInSerializers.put(ZoneOffset.class, zoneOffsetMapping);
        builtInSerializers.put(OffsetDateTime.class, offsetDateTimeSerializer);
        builtInSerializers.put(OffsetTime.class, new DateTimeSerializer<>(df.orElse(DateTimeFormatter.ISO_OFFSET_TIME.withLocale(locale))));
        builtInSerializers.put(JsonValue.class, new JsonValueSerializer()); // JsonStructure: JsonObject, JsonArray; JsonNumber; JsonString
        builtInSerializers.put(boolean[].class, new BooleanArraySerializer());
        builtInSerializers.put(byte[].class, new ByteArraySerializer(encoder));
        builtInSerializers.put(char[].class, new CharArraySerializer());
        builtInSerializers.put(short[].class, new ShortArraySerializer());
        builtInSerializers.put(int[].class, new IntArraySerializer());
        builtInSerializers.put(long[].class, new LongArraySerializer());
        builtInSerializers.put(float[].class, new FloatArraySerializer());
        builtInSerializers.put(double[].class, new DoubleArraySerializer());

        builtInDeserializers.put(String.class, STR_MAPPING);
        builtInDeserializers.put(Character.class, characterMapping);
        builtInDeserializers.put(Byte.class, new NumberDeserializer<>(Byte::parseByte));
        builtInDeserializers.put(Short.class, new NumberDeserializer<>(Short::parseShort));
        builtInDeserializers.put(Integer.class, new NumberDeserializer<>(Integer::parseInt));
        builtInDeserializers.put(Long.class, new NumberDeserializer<>(Long::parseLong));
        builtInDeserializers.put(Float.class, new NumberDeserializer<>(Float::parseFloat));
        builtInDeserializers.put(Double.class, new NumberDeserializer<>(Double::parseDouble));
        builtInDeserializers.put(Number.class, new NumberDeserializer<Number>(BigDecimal::new));
        builtInDeserializers.put(Boolean.class, booleanMapping);
        builtInDeserializers.put(BigInteger.class, new NumberDeserializer<>(BigInteger::new));
        builtInDeserializers.put(BigDecimal.class, new NumberDeserializer<>(BigDecimal::new));
        builtInDeserializers.put(URL.class, urlMapping);
        builtInDeserializers.put(URI.class, uriMapping);
        builtInDeserializers.put(Byte.TYPE, new NumberDeserializer<>(Byte::parseByte, Conventions.B));
        builtInDeserializers.put(Short.TYPE, new NumberDeserializer<>(Short::parseShort, Conventions.S));
        builtInDeserializers.put(Integer.TYPE, new NumberDeserializer<>(Integer::parseInt, Conventions.I));
        builtInDeserializers.put(Long.TYPE, new NumberDeserializer<>(Long::parseLong, Conventions.J));
        builtInDeserializers.put(Float.TYPE, new NumberDeserializer<>(Float::parseFloat, Conventions.F));
        builtInDeserializers.put(Double.TYPE, new NumberDeserializer<>(Double::parseDouble, Conventions.D));
        builtInDeserializers.put(Boolean.TYPE, boolMapping);
        builtInDeserializers.put(Character.TYPE, charMapping);
        builtInDeserializers.put(OptionalInt.class, new NumberDeserializer<>(Conventions.S2I.andThen(Conventions.I2O), OptionalInt::empty));
        builtInDeserializers.put(OptionalLong.class, new NumberDeserializer<>(Conventions.S2J.andThen(Conventions.J2O), OptionalLong::empty));
        builtInDeserializers.put(OptionalDouble.class, new NumberDeserializer<>(Conventions.S2D.andThen(Conventions.D2O), OptionalDouble::empty));
        builtInDeserializers.put(Date.class, dateDeserializer);
        builtInDeserializers.put(Calendar.class, calendarDeserializer);
        builtInDeserializers.put(GregorianCalendar.class, calendarDeserializer);
        builtInDeserializers.put(TimeZone.class, timezoneMapping);
        builtInDeserializers.put(SimpleTimeZone.class, timezoneMapping);
        builtInDeserializers.put(Instant.class, instantDeserializer);
        builtInDeserializers.put(Duration.class, durationMapping);
        builtInDeserializers.put(Period.class, periodMapping);
        builtInDeserializers.put(LocalDate.class, localDateDeserializer);
        builtInDeserializers.put(LocalTime.class, new DateTimeDeserializer<>(null, df.orElse(DateTimeFormatter.ISO_LOCAL_TIME.withLocale(locale)), LocalTime::parse));
        builtInDeserializers.put(LocalDateTime.class, localDateTimeDeserializer);
        builtInDeserializers.put(ZonedDateTime.class, zonedDatetimeDeserializer);
        builtInDeserializers.put(ZoneId.class, zoneIdMapping);
        builtInDeserializers.put(ZoneOffset.class, zoneOffsetMapping);
        builtInDeserializers.put(OffsetDateTime.class, new DateTimeDeserializer<>(LONG_TO_OFFSET_DATETIME, df.orElse(DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(locale)), OffsetDateTime::parse));
        builtInDeserializers.put(OffsetTime.class, new DateTimeDeserializer<>(null, df.orElse(DateTimeFormatter.ISO_OFFSET_TIME.withLocale(locale)), OffsetTime::parse));
        builtInDeserializers.put(boolean[].class, new BooleanArrayDeserializer());
        builtInDeserializers.put(byte[].class, new ByteArrayDeserializer(decoder));
        builtInDeserializers.put(char[].class, new CharArrayDeserializer());
        builtInDeserializers.put(short[].class, new ShortArrayDeserializer());
        builtInDeserializers.put(int[].class, new IntArrayDeserializer());
        builtInDeserializers.put(long[].class, new LongArrayDeserializer());
        builtInDeserializers.put(float[].class, new FloatArrayDeserializer());
        builtInDeserializers.put(double[].class, new DoubleArrayDeserializer());
        builtInDeserializers.put(Object.class, new UntypedMapping());
//        builtInDeserializers.put(JsonString.class, jsonValueDeserializer);
//        builtInDeserializers.put(JsonNumber.class, jsonValueDeserializer);
//        builtInDeserializers.put(JsonValue.class, jsonValueDeserializer);
//        builtInDeserializers.put(JsonObject.class, jsonValueDeserializer);
//        builtInDeserializers.put(JsonArray.class, jsonValueDeserializer);
    }


    @Override
    public <T> T fromJson(String str, Class<T> type) throws JsonbException {
        return fromJson(new StringReader(str), type);
    }

    @Override
    public <T> T fromJson(String str, Type runtimeType) throws JsonbException {
        return fromJson(new StringReader(str), runtimeType);
    }

    @Override
    public <T> T fromJson(Reader reader, Class<T> type) throws JsonbException {
        try (JsonParser jsonParser = jsonParserFactory.createParser(reader)) {
            return new JsonbDeserializationContext(config, jsonBuilderFactory, builtInDeserializers).deserialize(type, jsonParser);
        }
    }

    @Override
    public <T> T fromJson(Reader reader, Type runtimeType) throws JsonbException {
        try (JsonParser jsonParser = jsonParserFactory.createParser(reader)) {
            return new JsonbDeserializationContext(config, jsonBuilderFactory, builtInDeserializers).deserialize(runtimeType, jsonParser);
        }
    }

    @Override
    public <T> T fromJson(InputStream stream, Class<T> type) throws JsonbException {
        try (JsonParser jsonParser = jsonParserFactory.createParser(stream, encoding)) {
            return new JsonbDeserializationContext(config, jsonBuilderFactory, builtInDeserializers).deserialize(type, jsonParser);
        }
    }

    @Override
    public <T> T fromJson(InputStream stream, Type runtimeType) throws JsonbException {
        try (JsonParser jsonParser = jsonParserFactory.createParser(stream, encoding)) {
            return new JsonbDeserializationContext(config, jsonBuilderFactory, builtInDeserializers).deserialize(runtimeType, jsonParser);
        }
    }

    @Override
    public String toJson(Object object) throws JsonbException {
        StringWriter writer = new StringWriter();
        toJson(object, writer);
        return writer.toString();
    }

    @Override
    public String toJson(Object object, Type runtimeType) throws JsonbException {
        StringWriter writer = new StringWriter();
        toJson(object, runtimeType, writer);
        return writer.toString();
    }

    @Override
    public void toJson(Object object, Writer writer) throws JsonbException {
        Objects.requireNonNull(writer);
        try (JsonGenerator jsonGenerator = jsonGeneratorFactory.createGenerator(writer)) { // close writer not required
            new JsonbSerializationContext(config, builtInSerializers).serialize(object, jsonGenerator);
        }
    }

    @Override
    public void toJson(Object object, Type runtimeType, Writer writer) throws JsonbException {
        Objects.requireNonNull(writer);
        try (JsonGenerator jsonGenerator = jsonGeneratorFactory.createGenerator(writer)) { // close writer not required
            new JsonbSerializationContext(config, builtInSerializers, runtimeType).serialize(object, jsonGenerator);
        }
    }

    @Override
    public void toJson(Object object, OutputStream stream) throws JsonbException {
        Objects.requireNonNull(stream);
        try (JsonGenerator jsonGenerator = jsonGeneratorFactory.createGenerator(stream, encoding)) {
            new JsonbSerializationContext(config, builtInSerializers).serialize(object, jsonGenerator);
        }
    }

    @Override
    public void toJson(Object object, Type runtimeType, OutputStream stream) throws JsonbException {
        Objects.requireNonNull(stream);
        try (JsonGenerator jsonGenerator = jsonGeneratorFactory.createGenerator(stream, encoding)) {
            new JsonbSerializationContext(config, builtInSerializers, runtimeType).serialize(object, jsonGenerator);
        }
    }

    @Override
    public void close() throws Exception {
        //  rewrite bytecode
    }

    private static class PrefetchEventJsonParser implements JsonParser {

        private final JsonParser delegate;

        private final JsonParser.Event[] prefetched;

        private final String readValue;

        private final JsonBuilderFactory jsonBuilderFactory;

        private int eventUnread;

        private int valueUnread;

        PrefetchEventJsonParser(JsonBuilderFactory jsonBuilderFactory, JsonParser delegate, JsonParser.Event prefetched) {
            this.jsonBuilderFactory = jsonBuilderFactory;
            this.delegate = delegate;
            this.prefetched = new JsonParser.Event[] {prefetched};
            this.readValue = null;
            this.eventUnread = 1;
        }

        PrefetchEventJsonParser(JsonBuilderFactory jsonBuilderFactory,
                                JsonParser delegate, JsonParser.Event prefetched, String readValue) { // {"key":string
            this.jsonBuilderFactory = jsonBuilderFactory;
            this.delegate = delegate;
            this.prefetched = new JsonParser.Event[] {prefetched, Event.START_OBJECT};
            this.readValue = readValue;
            this.eventUnread = 2;
            this.valueUnread = 1;
        }

        @Override
        public boolean hasNext() { return eventUnread > 0 || delegate.hasNext(); }

        @Override
        public Event next() {
            if (eventUnread > 0) {
                return prefetched[--eventUnread];
            }
            return delegate.next();
        }

        @Override
        public String getString() {
            if (valueUnread > 0) {
                valueUnread--;
                return readValue;
            }
            return delegate.getString();
        }

        @Override
        public boolean isIntegralNumber() { return delegate.isIntegralNumber(); }

        @Override
        public int getInt() { return delegate.getInt(); }

        @Override
        public long getLong() { return delegate.getLong(); }

        @Override
        public BigDecimal getBigDecimal() { return delegate.getBigDecimal(); }

        @Override
        public JsonLocation getLocation() { return delegate.getLocation(); }

        @Override
        public JsonObject getObject() {
            if (eventUnread > 0) {
                eventUnread = 0;
                JsonObjectBuilder builder = jsonBuilderFactory.createObjectBuilder();
                JsonParser.Event event;
                while (hasNext() && ((event = next()) != Event.END_OBJECT)) {
                    if (event != Event.KEY_NAME) {
                        throw new JsonParsingException("expect name after '{'", getLocation());
                    }
                    String key = getString();
                    next();
                    builder.add(key, getValue());
                }
                return builder.build();
            }
            return delegate.getObject();
        }

        @Override
        public JsonValue getValue() { return delegate.getValue(); }

        @Override
        public JsonArray getArray() { return delegate.getArray(); }

        @Override
        public Stream<JsonValue> getArrayStream() { return delegate.getArrayStream(); }

        @Override
        public Stream<Map.Entry<String, JsonValue>> getObjectStream() { return delegate.getObjectStream(); }

        @Override
        public Stream<JsonValue> getValueStream() { return delegate.getValueStream(); }

        @Override
        public void skipArray() { delegate.skipArray(); }

        @Override
        public void skipObject() { delegate.skipObject(); }

        @Override
        public void close() { delegate.close(); }
    }

    private static class JsonbContext {

        protected final JsonbConfig jsonbConfig;

        protected final AnnotationIntrospector annotationIntrospector;

        /**
         *
         * <ul><a href="https://datatracker.ietf.org/doc/html/rfc7493">The I-JSON Message Format</a>
         *     <li>字符必须使用UTF-8 编码，且编码正确</li>
         *     <li>浮点数精度不超出IEEE 754规范的双精度，不在[-2^53 + 1, 2^53 - 1]范围内的整数需编码为字符串</li>
         *     <li>JSON object不能有重名的key</li>
         *     <li>为兼容<a href="https://datatracker.ietf.org/doc/html/rfc4627">旧规范</a>，顶层JSON结构必须是object或array</li>
         *     <li>当JSON对应的java属性不存在时必须忽略</li>
         *     <li>日期时间需使用<a href="https://datatracker.ietf.org/doc/html/rfc3339">ISO 8601格式</a></li>
         *     <li>使用base64ur格式编码二进制数据</li>
         * </ul>
         */
        protected final boolean strictInternetJson;

        protected final PropertyNamingStrategy customNamingStrategy;

        protected final boolean caseInsensitive;

        protected final PropertyVisibilityStrategy customVisibilityStrategy;

        protected final Map<Type, Pair<JsonbAdapter<?, ?>, Type>> adapters;

        protected final String customBinaryDataStrategy;

        protected final String dateFormat;

        protected final Locale locale;

        JsonbContext(JsonbConfig jsonbConfig) {
            this.jsonbConfig = jsonbConfig;
            this.annotationIntrospector = (AnnotationIntrospector) jsonbConfig.getProperty(BindingProvider.JSONB_ANNOTATION_INTROSPECTOR)
                    .orElse(new AnnotationIntrospector());
            this.strictInternetJson = (boolean) jsonbConfig.getProperty(JsonbConfig.STRICT_IJSON).orElse(false);
            Optional<Object> customNamingStrategy = jsonbConfig.getProperty(JsonbConfig.PROPERTY_NAMING_STRATEGY);
            if (customNamingStrategy.isPresent()) {
                this.caseInsensitive = PropertyNamingStrategy.CASE_INSENSITIVE.equals(customNamingStrategy.get());
                Object configuredNamingStrategy = customNamingStrategy.get();
                if (PropertyNamingStrategy.IDENTITY.equals(configuredNamingStrategy)) {
                    this.customNamingStrategy = null;
                } else if (configuredNamingStrategy instanceof PropertyNamingStrategy) {
                    this.customNamingStrategy = (PropertyNamingStrategy) configuredNamingStrategy;
                } else {
                    this.customNamingStrategy = PropertyNamingStrategyFactory.getObject(configuredNamingStrategy);
                }
            } else {
                this.caseInsensitive = false;
                this.customNamingStrategy = null;
            }
            this.customVisibilityStrategy = (PropertyVisibilityStrategy) jsonbConfig.getProperty(JsonbConfig.PROPERTY_VISIBILITY_STRATEGY)
                    .orElse(null);
            JsonbAdapter<?, ?>[] adapters = (JsonbAdapter[]) jsonbConfig.getProperty(JsonbConfig.ADAPTERS).orElse(null);
            if (adapters != null && adapters.length > 0) {
                this.adapters = new HashMap<>(adapters.length);
                for (JsonbAdapter<?, ?> adapter : adapters) {
                    Type[] ss = Reflections.findActualTypes(adapter.getClass(), JsonbAdapter.class);
                    assert ss.length == 2;
                    this.adapters.put(ss[0], new Pair<>(adapter, ss[1]));
                }
            } else {
                this.adapters = Collections.emptyMap();
            }
            this.customBinaryDataStrategy = strictInternetJson ?
                    BinaryDataStrategy.BASE_64_URL :
                    (String) jsonbConfig.getProperty(JsonbConfig.BINARY_DATA_STRATEGY).orElse(null);
            String dateFormat = (String) jsonbConfig.getProperty(JsonbConfig.DATE_FORMAT).orElse(null);
            if (strictInternetJson && dateFormat != null) {
                log.log(Level.WARNING, "DO NOT configure 'jsonb.strict-ijson' and 'jsonb.date-format' at same time");
                this.dateFormat = null;
            } else {
                this.dateFormat = dateFormat;
            }
            this.locale = (Locale) jsonbConfig.getProperty(JsonbConfig.LOCALE).orElse(Locale.getDefault());
        }


        static Object newInstance(Class<?> klazz) {
            if (!klazz.isInterface() && !Modifier.isAbstract(klazz.getModifiers())) {
                Constructor<?>[] ctors = klazz.getDeclaredConstructors();
                for (Constructor<?> ctor : ctors) {
                    if (ctor.getParameterCount() == 0 &&
                            (Modifier.isPublic(ctor.getModifiers())) || Modifier.isProtected(ctor.getModifiers())) {
                        try {
                            Object[] args = new Object[0];
                            return ctor.newInstance(args);
                        } catch (Exception e) {
                            throw new JsonbException(e.getMessage(), e);
                        }
                    }
                }
            }
            throw new JsonbException("default constructor is not present or is not in accessible scope: " + klazz);
        }

        /**
         * JsonbTransient注解的属性不被序列化，同时按规范要求检查不能有其他JSON-B注解
         * @param field 属性
         * @param method getter/setter方法
         * @param customVisibilityStrategy 可见性策略
         * @return 使用字段还是getter/setter方法进行序列化/反序列化
         */
        protected int visibility(Field field, Method method, PropertyVisibilityStrategy customVisibilityStrategy) {
            int visible = INVISIBLE;
            if (field != null) {
                boolean fieldVisible = customVisibilityStrategy != null ?
                        customVisibilityStrategy.isVisible(field) : Modifier.isPublic(field.getModifiers());
                visible |= fieldVisible ? FIELD : INVISIBLE;
            }
            if (method != null) {
                boolean methodVisible = customVisibilityStrategy != null ?
                        customVisibilityStrategy.isVisible(method) : Modifier.isPublic(method.getModifiers());
                visible |= methodVisible ? PROPERTY : INVISIBLE;
            }
            return visible;
        }

        protected boolean isNumber(Class<?> klazz) {
            if (Number.class.isAssignableFrom(klazz)) {
                return true;
            }
            if (klazz.isPrimitive() && !Boolean.TYPE.equals(klazz) && !Character.TYPE.equals(klazz)) {
                return true;
            }
            return OptionalInt.class.equals(klazz) || OptionalLong.class.equals(klazz) || OptionalDouble.class.equals(klazz);
        }

    }

    /**
     * @see com.fasterxml.jackson.databind.SerializerProvider
     * @see com.fasterxml.jackson.databind.ser.DefaultSerializerProvider
     */
    private static class JsonbSerializationContext extends JsonbContext implements SerializationContext {

        private final Type runtimeType;

        private final Map<Class<?>, JsonbSerializer<?>> builtIn;

        private final boolean root;

        protected final boolean writeNull;

        private final Charset encoding;

        private final String customOrderStrategy;

        private final NumberFormat numberFormat;

//        private final boolean parametersRequired; // Config::withCreatorParametersRequired

        private final Map<Type, JsonbSerializer<?>> serializers; // global serializers

        private final Map<Type, JsonbSerializer> serializerCache;

        private final Stack<Type> hints = new Stack<>();

        private final Map<Type, JsonbSerializer> inCreation;

        private final Set<Object> recursionReference = new HashSet<>(32);

        JsonbSerializationContext(JsonbConfig jsonbConfig, Map<Class<?>, JsonbSerializer<?>> builtIn) {
            this(jsonbConfig, builtIn, null, true);
        }

        JsonbSerializationContext(JsonbConfig jsonbConfig, Map<Class<?>, JsonbSerializer<?>> builtIn, Type runtimeType) {
            this(jsonbConfig, builtIn, runtimeType, true);
        }

        JsonbSerializationContext(JsonbConfig jsonbConfig, Map<Class<?>, JsonbSerializer<?>> builtIn,
                                  Type runtimeType, boolean root) {
            super(jsonbConfig);
            this.builtIn = builtIn;
            this.runtimeType = runtimeType;
            this.root = root;
            this.writeNull = (boolean) jsonbConfig.getProperty(JsonbConfig.NULL_VALUES).orElse(false);
            this.encoding = jsonbConfig.getProperty(JsonbConfig.ENCODING).map(t -> Charset.forName((String) t))
                    .orElse(StandardCharsets.UTF_8);
            this.customOrderStrategy = (String) jsonbConfig.getProperty(JsonbConfig.PROPERTY_ORDER_STRATEGY)
                    .orElse(PropertyOrderStrategy.LEXICOGRAPHICAL); // java类先序列化父类，同一个类的属性之间默认按字母排序
            this.numberFormat = null;
            JsonbSerializer<?>[] serializers = (JsonbSerializer<?>[]) jsonbConfig.getProperty(JsonbConfig.SERIALIZERS)
                    .orElse(null);
            if (serializers != null && serializers.length > 0) {
                this.serializers = new HashMap<>(serializers.length + 1);
                for (JsonbSerializer<?> serializer : serializers) {
                    Type[] types = Reflections.findActualTypes(serializer.getClass(), JsonbSerializer.class);
                    this.serializers.put(types[0], serializer);
                }
            } else {
                this.serializers = new HashMap<>(1);
            }
            this.serializers.put(JsonValue.class, new JsonValueSerializer());
            this.serializerCache = (Map<Type, JsonbSerializer>) jsonbConfig.getProperty(BindingProvider.JSONB_SERIALIZER_CACHE)
                    .orElse(null);
            this.inCreation = new HashMap<>();
        }

        @Override
        public <T> void serialize(String key, T object, JsonGenerator generator) {
            if (object == null) {
                if (writeNull) {
                    generator.writeNull(key);
                }
                return;
            }
            generator.writeKey(key);
            findValueSerializer(object.getClass(), object, false).serialize(object, generator, this);
        }

        @Override
        public <T> void serialize(T object, JsonGenerator generator) {
            findValueSerializer(runtimeType, object, root).serialize(object, generator, this);
        }

        public JsonbSerializer findKeySerializer(Type runtimeType, Object obj) {
            if (obj == null) {
                throw new JsonbException("json key must not null");
            }
            if (!String.class.equals(obj.getClass())) {
                throw new JsonbException("json key must string type");
            }
            return STR_MAPPING;
        }

        public <T> JsonbSerializer<T> findValueSerializer(Type runtimeType, T obj, boolean root) {
            if (runtimeType == null) {
                runtimeType = obj == null ? Object.class : obj.getClass();
            }
            JsonbSerializer<T> cache = serializerCache == null ? null : serializerCache.get(runtimeType);
            if (root && cache != null) {
                return cache;
            }
            cache = findTypeSerializer(runtimeType, writeNull, numberFormat, dateFormat, Optional.ofNullable(locale).map(Locale::getLanguage).orElse(null));
            if (root && serializerCache != null) {
                serializerCache.put(runtimeType, cache);
            }
            return cache;
        }

        private <T> JsonbSerializer<?> findBuiltInOrCreate(Class<T> klazz, NumberFormat numberFormat,
                                                           String dateFormat, String locale) {
            if (!Objects.equals(numberFormat, this.numberFormat) && isNumber(klazz)) {
                if (OptionalInt.class.equals(klazz)) {
                    return new NumberSerializer<>(numberFormat, OptionalInt::getAsInt);
                } else if (OptionalLong.class.equals(klazz)) {
                    return new NumberSerializer<>(numberFormat, OptionalLong::getAsLong);
                } else if (OptionalDouble.class.equals(klazz)) {
                    return new NumberSerializer<>(numberFormat, OptionalDouble::getAsDouble);
                }
                return new NumberSerializer<>(numberFormat);
            }
            boolean same = (strictInternetJson && dateFormat == null) || (!strictInternetJson && Objects.equals(dateFormat, this.dateFormat));
            boolean asNumber = JsonbDateFormat.TIME_IN_MILLIS.equals(dateFormat);
            Optional<DateTimeFormatter> formatter = Optional.empty();
            Locale l10n = locale == null || JsonbDateFormat.DEFAULT_LOCALE.equals(locale) ?
                    Locale.getDefault() : new Locale(locale);
            if (!JsonbDateFormat.TIME_IN_MILLIS.equals(dateFormat) &&
                    !JsonbDateFormat.DEFAULT_FORMAT.equals(dateFormat) &&
                    dateFormat != null) {
                formatter = Optional.of(DateTimeFormatter.ofPattern(dateFormat, l10n));
            }
            // 如果是Date、Calendar、Temporal类型，且不是默认格式，需要重新创建序列化器
            if (!same) { // Date, Calendar, Instant, LocalDate, LocalDateTime
                if (Date.class.isAssignableFrom(klazz)) {
                    return asNumber ?
                            new DateTimeSerializer<>(Date::getTime) :
                            new DateTimeSerializer<>(formatter.map(WITH_ZONE).orElse(DateTimeFormatter.ISO_DATE_TIME.withZone(UTC).withLocale(l10n)), Date::toInstant);
                } else if (Calendar.class.isAssignableFrom(klazz)) {
                    return asNumber ? new CalendarSerializer<>() : new CalendarSerializer<>(formatter.orElse(null), l10n);
                } else if (Instant.class.equals(klazz)) {
                    return asNumber ?
                            new DateTimeSerializer<>(Instant::toEpochMilli) :
                            new DateTimeSerializer<Instant>(formatter.map(WITH_ZONE).orElse(DateTimeFormatter.ISO_INSTANT.withZone(UTC).withLocale(l10n)));
                } else if (LocalDate.class.equals(klazz)) {
                    return asNumber ?
                            new DateTimeSerializer<LocalDate>(Conventions.I2J.compose(x -> x.atStartOfDay(UTC).toInstant())) :
                            new DateTimeSerializer<LocalDate>(formatter.orElse(DateTimeFormatter.ISO_LOCAL_DATE.withZone(UTC).withLocale(l10n)));
                } else if (LocalDateTime.class.equals(klazz)) {
                    return asNumber ?
                            new DateTimeSerializer<LocalDateTime>(Conventions.I2J.compose(x -> x.atZone(UTC).toInstant())):
                            new DateTimeSerializer<LocalDateTime>(formatter.map(WITH_ZONE).orElse(DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(l10n)));
                }
                if (LocalTime.class.equals(klazz)) {
                    return new DateTimeSerializer<LocalTime>(formatter.orElse(DateTimeFormatter.ISO_LOCAL_TIME.withLocale(l10n)));
                } else if (ZonedDateTime.class.equals(klazz)) {
                    return asNumber ?
                            new DateTimeSerializer<>(Conventions.I2J.compose(ZonedDateTime::toInstant)) :
                            new DateTimeSerializer<ZonedDateTime>(formatter.orElse(DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(l10n)));
                } else if (OffsetDateTime.class.equals(klazz)) {
                    return asNumber ?
                            new DateTimeSerializer<>(Conventions.I2J.compose(OffsetDateTime::toInstant)) :
                            new DateTimeSerializer<OffsetDateTime>(formatter.orElse(DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(l10n)));
                } else if (OffsetTime.class.equals(klazz)) {
                    return new DateTimeSerializer<OffsetTime>(formatter.orElse(DateTimeFormatter.ISO_OFFSET_TIME.withLocale(l10n)));
                }
            }

            Class<?> current = klazz;
            while (current != Object.class && current != null) { // primitive type has no parent
                JsonbSerializer<?> simpleSerializer = builtIn.get(current); // 如java.sql.Date可被java.util.Date的序列化器序列化
                if (simpleSerializer != null) {
                    return simpleSerializer;
                }
                current = current.getSuperclass(); // care about interface ?
            }
            return null;
        }

        protected <A, T> JsonbSerializer<T> findTypeSerializer(Type runtimeType, boolean writeNull, NumberFormat numberFormat,
                                                               String dateFormat, String locale) {
            Optional<Type> componentType = Optional.empty();
            if (runtimeType instanceof GenericArrayType) {
                componentType = Optional.of(Reflections.searchComponentType((GenericArrayType) runtimeType, hints));
            }
            Class klazz = Reflections.typeToClass(runtimeType, hints);
            // 1. 开发者使用了注解
            JsonbSerializer<T> userDefinedSer = (JsonbSerializer<T>) annotationIntrospector.findSerializer(klazz);
            if (userDefinedSer != null) {
                return userDefinedSer;
            }
            JsonbAdapter<T, A> beanAdapter = annotationIntrospector.findAdapter(klazz);
            if (beanAdapter != null) {
                Type[] jsonType = Reflections.findActualTypes(beanAdapter.getClass(), JsonbAdapter.class);
                JsonbSerializer<A> beanSerializer = findTypeSerializer(jsonType[1], writeNull, numberFormat, dateFormat, locale); // XXX 新序列化器使用Original还是Adapted上的注解？
                return new AdapterSerializer<>(beanAdapter, beanSerializer);
            }
            // 2. 开发者注册了全局序列化器/适配器
            JsonbSerializer<T> candidate = (JsonbSerializer<T>) serializers.get(runtimeType);
            if (candidate != null) {
                return candidate;
            }
            for (Map.Entry<Type, JsonbSerializer<?>> entry : serializers.entrySet()) {
                if (Reflections.typeToClass(entry.getKey(), hints).isAssignableFrom(klazz)) { // XXX 可能存在多个可以序列化该类型超类的序列化器
                    if (candidate != null) {
                        log.log(Level.WARNING,
                                "multiply JsonbSerializer found for class[{0}]: {1}, {2}",
                                new Object[] {klazz, candidate.getClass(), entry.getValue().getClass()}
                        );
                    }
                    candidate = (JsonbSerializer<T>) entry.getValue();
                }
            }
            if (candidate != null) {
                return candidate;
            }
            Pair<JsonbAdapter<?, ?>, Type> adapterPairs = adapters.get(runtimeType); // can JsonbAdapter<Number, ?> handle JsonbAdapter<BigDecimal, ?>
            if (adapterPairs != null) {
                JsonbSerializer<?> adaptedSerializer = findTypeSerializer(adapterPairs.getRight(), writeNull, numberFormat, dateFormat, locale);
                return new AdapterSerializer(adapterPairs.getLeft(), adaptedSerializer);
            }
            // 3. 基础Java SE定义的数据结构：原始类型及其包装类、日期时间、URL、URI
            JsonbSerializer<T> built = findBuiltInOrCreate(klazz, numberFormat, dateFormat, locale); // 早于数组： byte[] <--> base64/base64-url
            if (built != null) {
                return built;
            }

            hints.push(runtimeType);
            // 4. 集合、数组、枚举、java bean
            Type at0 = runtimeType instanceof ParameterizedType ?
                    ((ParameterizedType) runtimeType).getActualTypeArguments()[0] :
                    Object.class;
            if (Object.class.equals(klazz)) {
                candidate = (JsonbSerializer<T>) new UntypedMapping(writeNull, numberFormat, dateFormat, locale);
            } else if (Optional.class.equals(klazz)) {
                JsonbSerializer<?> secondarySerializer = findTypeSerializer(at0, writeNull, numberFormat, dateFormat, locale);
                candidate = new OptionalSerializer(secondarySerializer, writeNull);
            } else if (Map.class.isAssignableFrom(klazz)) {
                Type at1 = runtimeType instanceof ParameterizedType ?
                        ((ParameterizedType) runtimeType).getActualTypeArguments()[1] :
                        Object.class;
                JsonbSerializer<?> secondarySerializer = findTypeSerializer(at1, writeNull, numberFormat, dateFormat, locale);
                candidate = new MapSerializer(recursionReference, customOrderStrategy, writeNull, secondarySerializer);
            } else if (Collection.class.isAssignableFrom(klazz)) {
                JsonbSerializer<?> secondarySerializer = findTypeSerializer(at0, writeNull, numberFormat, dateFormat, locale);
                candidate = new CollectionSerializer(recursionReference, secondarySerializer);
            } else if (klazz.isArray()) {
                JsonbSerializer<?> secondarySerializer = findTypeSerializer(componentType.orElse(klazz.getComponentType()), writeNull, numberFormat, dateFormat, locale);
                candidate = new ArraySerializer(recursionReference, secondarySerializer);
            } else if (Enum.class.isAssignableFrom(klazz)) {
                candidate = new EnumMapping(hints.toArray(new Type[0]), annotationIntrospector.findEnumValues(klazz), caseInsensitive);
            } else {
                Polymorphism<String> polymorphism = annotationIntrospector.findSubtypes(klazz); // 普通类、抽象类、接口都可能是多态来源，仅凭类型信息无法判断是具体类还是中间类型
                candidate = polymorphism != null ? new PolymorphismTypSerializer<>(polymorphism, writeNull, numberFormat, dateFormat, locale) :
                        createBeanSerializer(runtimeType, klazz, writeNull, numberFormat, dateFormat, locale);
            }
            hints.pop();
            return candidate;
        }

        protected <T> JsonbSerializer<T> createBeanSerializer(Type runtimeType, Class<T> klazz,
                                                              boolean writeNull, NumberFormat numberFormat, String dateFormat, String locale) {
            // public、protected的内部类和静态内部类可被序列化化；匿名类可被序列化
            if (klazz.isMemberClass() && !Modifier.isPublic(klazz.getModifiers()) && !Modifier.isProtected(klazz.getModifiers())) {
                throw new JsonbException("only public/protected nest class can be serialized");
            }
            // 类型自关联，间接依赖等情况需避免死循环
            JsonbSerializer<T> zygote = inCreation.get(runtimeType);
            if (zygote != null) {
                // 如果类型在循环中，序列化时需检查是否存在循环引用
                return zygote;
            }
            JavaBeanSerializer<T> beanSerializer = new JavaBeanSerializer<>(recursionReference);
            inCreation.put(runtimeType, beanSerializer);

            JavaBeanPropertySerializer<T, Object>[] serializers = null;
            Set<String> keys = new HashSet<>();
            PropertyVisibilityStrategy visibilityStrategy = Optional.ofNullable(annotationIntrospector.findVisibility(klazz, true))
                    .orElse(customVisibilityStrategy);
            boolean nillable = Optional.ofNullable(annotationIntrospector.findNillable(klazz, true))
                    .orElse(writeNull);
            numberFormat = Optional.ofNullable(annotationIntrospector.findNumberFormat(klazz, true))
                    .orElse(numberFormat);
            JsonbDateFormat wildScopeDateFormat = annotationIntrospector.findDateTimeFormat(klazz, true);
            if (wildScopeDateFormat != null) {
                dateFormat = wildScopeDateFormat.value();
                locale = wildScopeDateFormat.locale();
            }
            Class<?> current = klazz;
            while (current != Object.class) {
                String[] ordered = annotationIntrospector.findSerializationPropertyOrder(current);
                Map<String, Integer> fastOrdered = nameIndexed(ordered);
                int orderedLength = fastOrdered.size();
                JavaBean beanInfo = JavaBean.introspect(current);
                int maxPropertyQuantity = beanInfo.getProperties().length;
                JavaBeanPropertySerializer<T, Object>[] orderedSerializers = new JavaBeanPropertySerializer[maxPropertyQuantity];
                int serializableProperty = 0;
                for (int i = 0; i < maxPropertyQuantity; i++) {
                    JavaProperty<T, Object> property = beanInfo.getProperties()[i];
                    // property name resolution phase 1
                    String customPropertyName = customNamingStrategy != null ?
                            customNamingStrategy.translateName(property.getName()) :
                            property.getName();

                    Optional<String> propertyName = Optional.empty();
                    Optional<JsonbSerializer<?>> serializer = Optional.empty();
                    Optional<JsonbAdapter<?, ?>> adapter = Optional.empty();
                    Optional<JsonbDateFormat> propertyDateFormat = Optional.empty();
                    Optional<NumberFormat> propertyNumberFormat = Optional.empty();
                    Optional<Boolean> propertyNillable = Optional.empty();
                    if (property.getProperty() != null) {
                        annotationIntrospector.hasIgnoreMarker(property.getProperty());
                        propertyName = Optional.ofNullable(annotationIntrospector.findNameForSerialization(property.getProperty()));
                        serializer = Optional.ofNullable(annotationIntrospector.findSerializer(property.getProperty()));
                        adapter = Optional.ofNullable(annotationIntrospector.findAdapter(property.getProperty()));
                        propertyDateFormat = Optional.ofNullable(annotationIntrospector.findDateTimeFormat(property.getProperty()));
                        propertyNumberFormat = Optional.ofNullable(annotationIntrospector.findNumberFormat(property.getProperty()));
                        propertyNillable = Optional.ofNullable(annotationIntrospector.findNillable(property.getProperty()));
                    }

                    Optional<JavaBeanPropertySerializer<T, Object>> ser = createPropertySerializer(property,
                            visibilityStrategy,
                            propertyName.orElse(customPropertyName), // property name resolution phase 2
                            propertyNillable.orElse(nillable),
                            adapter.orElse(null),
                            serializer.orElse(null),
                            propertyNumberFormat.orElse(numberFormat),
                            propertyDateFormat.map(JsonbDateFormat::value).orElse(dateFormat),
                            propertyDateFormat.map(JsonbDateFormat::locale).orElse(locale));

                    if (ser.isPresent()) {
                        if (!keys.add(ser.get().propertyName)) { // a. 类和父类有同名属性时：JSON-B 规范要求父类属性先序列化，Java规范为子类覆盖父类方法； b. 个性化属性名称导致重复
                            throw new JsonbException("MUST NOT produce JSON documents with members with duplicate names");
                        }
                        serializableProperty++;
                        Integer index = fastOrdered.remove(property.getName());
                        orderedSerializers[index != null ? index : fastOrdered.size() + i] = ser.get(); // 规范要求注解指定的顺序为原始属性名称
                    }
                }
                // 将null移到数组尾部
                if (serializableProperty < orderedSerializers.length) {
                    moveNullTail(orderedSerializers);
                }
                // 1. 类的属性顺序是否已全部指定，即是否存在部分需序列化为JSON的属性未指定顺序
                int orderedProperty = orderedLength - fastOrdered.size();
                boolean reorderPartial = orderedProperty < serializableProperty;
                // 2. 类的属性顺序部分已指定，剩余的按全局排序规则
                if (reorderPartial && PropertyOrderStrategy.LEXICOGRAPHICAL.equals(customOrderStrategy)) { // 规范要求全局配置的排序按属性转换后名称
                    Arrays.sort(orderedSerializers, orderedProperty, serializableProperty);
                } else if (reorderPartial && PropertyOrderStrategy.REVERSE.equals(customOrderStrategy)) {
                    Arrays.sort(orderedSerializers, orderedProperty, serializableProperty, Comparator.reverseOrder());
                }
                // 3. 按规范要求父类属性先被序列化
                if (serializers != null) {
                    JavaBeanPropertySerializer<T, Object>[] array = new JavaBeanPropertySerializer[serializers.length + serializableProperty];
                    System.arraycopy(orderedSerializers, 0, array, 0, serializableProperty);
                    System.arraycopy(serializers, 0, array, serializableProperty, serializers.length);
                    serializers = array;
                } else if (serializableProperty == orderedSerializers.length) {
                    serializers = orderedSerializers;
                } else {
                    serializers = new JavaBeanPropertySerializer[serializableProperty];
                    System.arraycopy(orderedSerializers, 0, serializers, 0, serializableProperty);
                }

                current = current.getSuperclass();
            }
            beanSerializer.setPropertySerializers(serializers);
            inCreation.remove(runtimeType);
            return beanSerializer;
        }

        private Map<String, Integer> nameIndexed(String[] named) {
            if (named == null || named.length == 0) {
                return Collections.emptyMap();
            }
            Map<String, Integer> namedPairs = new HashMap<>();
            for (int i = 0; i < named.length; i++) {
                namedPairs.put(named[i], i);
            }
            return namedPairs;
        }

        private void moveNullTail(Object[] array) {
            int next = 0; // skip founded null
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    continue;
                }
                next = next <= i ? (i + 1) : next;
                for (int j = next; j < array.length; j++) {
                    if (array[j] != null) {
                        array[i] = array[j];
                        array[j] = null;
                        next = j + 1;
                        break;
                    }
                }
            }
        }

        protected <D, S, T> Optional<JavaBeanPropertySerializer<D, T>> createPropertySerializer(JavaProperty<D, T> property,
                                                                                                PropertyVisibilityStrategy visibilityStrategy,
                                                                                                String nameToSerialize,
                                                                                                boolean writeNull,
                                                                                                JsonbAdapter adapter,
                                                                                                JsonbSerializer serializer,
                                                                                                NumberFormat numberFormat,
                                                                                                String dateFormat,
                                                                                                String locale) {
            // 1. 字段为static、transient不序列化
            if (property.getProperty() != null) {
                if (Modifier.isStatic(property.getProperty().getModifiers()) ||
                        Modifier.isTransient(property.getProperty().getModifiers())) {
                    return Optional.empty();
                }
            }
            // 2. 根据可见性决定字段、方法是否可作为序列化的来源；默认规则为使用属性的public getter方法序列化（即便对应的字段不存在），不存在时使用public的字段序列化
            int visibility = visibility(property.getProperty(), property.getGetter(), visibilityStrategy);
            if (visibility == INVISIBLE) {
                return Optional.empty();
            }
            boolean useGetter = (visibility & PROPERTY) != 0;
            AccessibleObject ann = useGetter ? property.getGetter() : property.getProperty();
            // 3. 是否有忽略标记
            if (annotationIntrospector.hasIgnoreMarker(ann)) {
                return Optional.empty();
            }
            Type propertyType;
            JsonbDateFormat jsonbDateFormat = null;
            Function<D, T> getter = x -> property.get(x, useGetter, !useGetter);
            if (useGetter) {
                nameToSerialize = Optional.ofNullable(annotationIntrospector.findNameForSerialization(ann))
                        .orElse(nameToSerialize);
                writeNull = Optional.ofNullable(annotationIntrospector.findNillable(ann)).orElse(writeNull);
                adapter = Optional.ofNullable(annotationIntrospector.findAdapter(ann)).orElse(adapter);
                serializer = Optional.ofNullable(annotationIntrospector.findSerializer(ann)).orElse(serializer);
                jsonbDateFormat = annotationIntrospector.findDateTimeFormat(ann);
                numberFormat = Optional.ofNullable(annotationIntrospector.findNumberFormat(ann)).orElse(numberFormat);
                propertyType = property.getGetter().getGenericReturnType();
            } else {
                propertyType = property.getProperty().getGenericType();
            }
            if (jsonbDateFormat != null) {
                dateFormat = jsonbDateFormat.value();
                locale = jsonbDateFormat.locale();
            }
            if (serializer == null && adapter != null) {
                // 如果同时出现JsonbTypeAdapter注解和JsonbTypeSerializer/JsonbTypeDeserializer注解如何处理： 报错？ 忽略某一个？ 根据JsonbAdapter信息重新查找/创建一个JsonbSerializer（此时是否保留个性化注解信息）？
                Type[] types = Reflections.findActualTypes(adapter.getClass(), JsonbAdapter.class);
                serializer = findTypeSerializer(types[1], writeNull, numberFormat, dateFormat, locale);
                serializer = new AdapterSerializer<T, S>(adapter, serializer);
            } else if (serializer == null) {
                // 属性类型为GenericArrayType时，从参数runtimeType可能可以获取到泛型信息(ParameterizedType::getActualTypeArguments)，但是无法知晓是第几个
                serializer = findTypeSerializer(propertyType, writeNull, numberFormat, dateFormat, locale);
            }
            JavaBeanPropertySerializer<D, T> ser = new JavaBeanPropertySerializer<>(nameToSerialize, writeNull, getter, serializer);
            return Optional.of(ser);
        }

    }

    /**
     * @see com.fasterxml.jackson.databind.DeserializationContext
     */
    private static class JsonbDeserializationContext extends JsonbContext implements DeserializationContext {

        private final JsonBuilderFactory jsonBuilderFactory;

        private final Map<Type, JsonbDeserializer> deserializers; // global deserializers

        private final boolean parametersRequired;

        private final Map<Class<?>, JsonbDeserializer<?>> builtIn;

        private final Map<Type, JsonbDeserializer> cache;

        private final boolean root;

        private final NumberFormat numberFormat;

        private final Stack<Type> hints = new Stack<>();

        private final Map<Type, JsonbDeserializer> inCreation;

        JsonbDeserializationContext(JsonbConfig jsonbConfig, JsonBuilderFactory jsonBuilderFactory,
                                    Map<Class<?>, JsonbDeserializer<?>> builtIn) {
            super(jsonbConfig);
            this.jsonBuilderFactory = jsonBuilderFactory;
            JsonbDeserializer[] deserializers = (JsonbDeserializer[]) jsonbConfig.getProperty(JsonbConfig.DESERIALIZERS)
                    .orElse(null);
            if (deserializers == null || deserializers.length == 0) {
                this.deserializers = new HashMap<>(1);
            } else {
                this.deserializers = new HashMap<>(deserializers.length + 1);
                for (JsonbDeserializer dser : deserializers) {
                    Type[] handles = Reflections.findActualTypes(dser.getClass(), JsonbDeserializer.class);
                    this.deserializers.put(handles[0], dser);
                }
            }
            this.deserializers.put(JsonValue.class, new JsonValueDeserializer());
            this.parametersRequired = (boolean) jsonbConfig.getProperty(JsonbConfig.CREATOR_PARAMETERS_REQUIRED).orElse(false);
            this.builtIn = builtIn;
            this.cache = (Map<Type, JsonbDeserializer>) jsonbConfig.getProperty(BindingProvider.JSONB_DESERIALIZER_CACHE)
                    .orElse(null);
            this.root = true;
            this.numberFormat = null;
            this.inCreation = new HashMap<>();
        }

        @Override
        public <T> T deserialize(Class<T> clazz, JsonParser parser) {
            if (root) {
                JsonParser.Event current = parser.next();
                checkStructure(current);
                return deserialize(current, clazz, parser);
            } else {
                return (T) findTypeDeserializer(clazz, numberFormat, dateFormat, locale).deserialize(parser, this, clazz);
            }
        }

        @Override
        public <T> T deserialize(Type type, JsonParser parser) {
            if (root) {
                JsonParser.Event current = parser.next();
                checkStructure(current);
                return deserialize(current, type, parser);
            } else {
                return (T) findTypeDeserializer(type, numberFormat, dateFormat, locale).deserialize(parser, this, type);
            }
        }

        private void checkStructure(JsonParser.Event top) {
            if (strictInternetJson) {
                if (top != JsonParser.Event.START_OBJECT && top != JsonParser.Event.START_ARRAY) {
                    throw new JsonbException("For maximum interoperability, JSON objects or JSON arrays at the top level of JSON texts was accepted");
                }
            }
        }

        private <T> T deserialize(JsonParser.Event current, Type type, JsonParser parser) {
            JsonbDeserializer<T> deserializer = null;
            if (root && cache != null) {
                deserializer = cache.get(type);
            }

            if (deserializer == null) {
                deserializer = findTypeDeserializer(type, numberFormat, dateFormat, locale);
                if (root && cache != null) {
                    cache.put(type, deserializer);
                }
            }
            return deserializer.deserialize(new PrefetchEventJsonParser(jsonBuilderFactory, parser, current), this, type); // 已事先读取一个Event，让JsonbDeserializer可重新获取该事件
        }

        private <T> JsonbDeserializer findBuiltInOrCreate(Class<T> klazz, NumberFormat numberFormat, String dateFormat, Locale locale) {
            if (!Objects.equals(numberFormat, this.numberFormat) && isNumber(klazz)) {
                if (OptionalInt.class.equals(klazz)) {
                    return new NumberDeserializer<>(numberFormat, Conventions.S2I.andThen(Conventions.I2O), OptionalInt::empty);
                } else if (OptionalLong.class.equals(klazz)) {
                    return new NumberDeserializer<>(numberFormat, Conventions.S2J.andThen(Conventions.J2O), OptionalLong::empty);
                } else if (OptionalDouble.class.equals(klazz)) {
                    return new NumberDeserializer<>(numberFormat, Conventions.S2D.andThen(Conventions.D2O), OptionalDouble::empty);
                } else if (Byte.TYPE.equals(klazz)) {
                    return new NumberDeserializer<>(numberFormat, Conventions.S2B, Conventions.B);
                } else if (Short.TYPE.equals(klazz)) {
                    return new NumberDeserializer<>(numberFormat, Conventions.S2S, Conventions.S);
                } else if (Integer.TYPE.equals(klazz)) {
                    return new NumberDeserializer<>(numberFormat, Conventions.S2I, Conventions.I);
                } else if (Long.TYPE.equals(klazz)) {
                    return new NumberDeserializer<>(numberFormat, Conventions.S2J, Conventions.J);
                } else if (Float.TYPE.equals(klazz)) {
                    return new NumberDeserializer<>(numberFormat, Conventions.S2F, Conventions.F);
                } else if (Double.TYPE.equals(klazz)) {
                    return new NumberDeserializer<>(numberFormat, Conventions.S2D, Conventions.D);
                }
                return new NumberDeserializer<>(numberFormat, null, null);
            }
            boolean same = (strictInternetJson && dateFormat == null) || (!strictInternetJson && Objects.equals(dateFormat, this.dateFormat)) ;
            Optional<DateTimeFormatter> formatter = Optional.empty();
            if (JsonbDateFormat.TIME_IN_MILLIS.equals(dateFormat) || JsonbDateFormat.DEFAULT_FORMAT.equals(dateFormat)) {
                formatter = strictInternetJson ? Optional.of(IJSON_DATE_FORMATTER) : Optional.empty();
            } else if (dateFormat != null){
                formatter = locale == null ?
                        Optional.of(DateTimeFormatter.ofPattern(dateFormat)) :
                        Optional.of(DateTimeFormatter.ofPattern(dateFormat, locale));
            }
            if (!same) { // Date, Calendar, Instant, LocalDate, LocalDateTime
                if (Date.class.isAssignableFrom(klazz)) {
                    return new DateTimeDeserializer<>(Date::new, formatter.map(WITH_ZONE).orElse(DateTimeFormatter.ISO_DATE_TIME.withZone(UTC).withLocale(locale)), (s, f) -> Date.from(ZonedDateTime.parse(s, f).toInstant()));
                } else if (Calendar.class.isAssignableFrom(klazz)) {
                    return new CalendarDeserializer(formatter.orElse(null), locale);
                } else if (Instant.class.equals(klazz)) {
                    return new DateTimeDeserializer<>(Instant::ofEpochMilli, formatter.map(WITH_ZONE).orElse(DateTimeFormatter.ISO_INSTANT.withZone(UTC).withLocale(locale)), (s, f) -> Instant.from(f.parse(s)));
                } else if (LocalDate.class.equals(klazz)) {
                    return new DateTimeDeserializer<>(TO_ZONED.compose(Instant::ofEpochMilli).andThen(ZonedDateTime::toLocalDate), formatter.orElse(DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale)), LocalDate::parse);
                } else if (LocalDateTime.class.equals(klazz)) {
                    return new DateTimeDeserializer<>(LONG_TO_LOCAL_DATETIME, formatter.orElse(DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(locale)), LocalDateTime::parse);
                }
                if (LocalTime.class.equals(klazz)) {
                    return new DateTimeDeserializer<>(null, formatter.orElse(DateTimeFormatter.ISO_LOCAL_TIME.withLocale(locale)), LocalTime::parse);
                } else if (ZonedDateTime.class.equals(klazz)) {
                    return new DateTimeDeserializer<>(Conventions.J2I.andThen(TO_ZONED), formatter.map(WITH_ZONE).orElse(DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(locale)), ZonedDateTime::parse);
                } else if (OffsetDateTime.class.equals(klazz)) {
                    return new DateTimeDeserializer<>(LONG_TO_OFFSET_DATETIME, formatter.orElse(DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(locale)), OffsetDateTime::parse);
                } else if (OffsetTime.class.equals(klazz)) {
                    return new DateTimeDeserializer<>(null, formatter.orElse(DateTimeFormatter.ISO_OFFSET_TIME.withLocale(locale)), OffsetTime::parse);
                }
            }

            return builtIn.get(klazz); // JsonbDeserializer<java.util.Date> --> java.sql.Date, klazz == java.sql.Timestamp, type incompatible!
        }

        <T, A> JsonbDeserializer<T> findTypeDeserializer(Type type, NumberFormat numberFormat, String dateFormat, Locale locale) {
            Optional<Type> componentType = Optional.empty();
            if (type instanceof GenericArrayType) {
                componentType = Optional.of(Reflections.searchComponentType((GenericArrayType) type, hints));
            }
            Class klazz = Reflections.typeToClass(type, hints);
            // 1. 开发者有标记
            JsonbDeserializer<T> customDeserializer = annotationIntrospector.findDeserializer(klazz);
            if (customDeserializer != null) {
                return customDeserializer;
            }
            JsonbAdapter<T, A> beanAdapter = annotationIntrospector.findAdapter(klazz); // 属性本身没有JsonbTypeAdapter/JsonbDeserializer注解，但对应类型上有
            if (beanAdapter != null) {
                Type[] adaptedType = Reflections.findActualTypes(beanAdapter.getClass(), JsonbAdapter.class);
                JsonbDeserializer<A> beanDeserializer = findTypeDeserializer(adaptedType[1], numberFormat, dateFormat, locale);
                return new AdapterDeserializer<>(beanAdapter, beanDeserializer, adaptedType[1]);
            }
            // 2. 开发者注册了全局适配器/反序列化器
            JsonbDeserializer<T> globalDeserializer = deserializers.get(type);
            if (globalDeserializer != null) {
                return globalDeserializer;
            }
            for (Map.Entry<Type, JsonbDeserializer> entry : deserializers.entrySet()) {
                if (Reflections.typeToClass(entry.getKey(), null).isAssignableFrom(klazz)) {
                    if (globalDeserializer != null) {
                        log.log(Level.WARNING,
                                "multiply JsonbDeserializer found for class[{0}]: {1}, {2}",
                                new Object[] {klazz, globalDeserializer.getClass(), entry.getValue().getClass()}
                        );
                    }
                    globalDeserializer = entry.getValue();
                }
            }
            if (globalDeserializer != null) {
                return globalDeserializer;
            }
            Pair<JsonbAdapter<?, ?>, Type> adapterPair = adapters.get(type);
            if (adapterPair != null) {
                customDeserializer = findTypeDeserializer(adapterPair.getRight(), numberFormat, dateFormat, locale);
                return new AdapterDeserializer(adapterPair.getLeft(), customDeserializer, adapterPair.getRight());
            }
            // 3. 属于Java SE基础数据结构
            JsonbDeserializer<T> deserializer = findBuiltInOrCreate(klazz, numberFormat, dateFormat, locale);
            if (deserializer != null) {
                return deserializer;
            }

            hints.push(type);
            Type[] usefulHints = hints.toArray(new Type[0]);
            // 4. Map, Collection, Array, Enum, Java Bean
            Type at0 = type instanceof ParameterizedType ?
                    ((ParameterizedType) type).getActualTypeArguments()[0] :
                    Object.class;
            if (Optional.class.isAssignableFrom(klazz)) {
                JsonbDeserializer<?> secondary = findTypeDeserializer(at0, numberFormat, dateFormat, locale);
                deserializer = new OptionalDeserializer(jsonBuilderFactory, secondary);
            } else if (Map.class.isAssignableFrom(klazz)) {
                Type at1 = type instanceof ParameterizedType ?
                        ((ParameterizedType) type).getActualTypeArguments()[1] :
                        Object.class;
                JsonbDeserializer<?> secondary = findTypeDeserializer(at1, numberFormat, dateFormat, locale);
                deserializer = (JsonbDeserializer<T>) new MapDeserializer(usefulHints, secondary);
            } else if (Collection.class.isAssignableFrom(klazz)) {
                JsonbDeserializer<?> secondary = findTypeDeserializer(at0, numberFormat, dateFormat, locale);
                deserializer = new CollectionDeserializer(usefulHints, jsonBuilderFactory, secondary);
            } else if (klazz.isArray()) {
                JsonbDeserializer<?> secondary = findTypeDeserializer(componentType.orElse(klazz.getComponentType()), numberFormat, dateFormat, locale);
                deserializer = new ArrayDeserializer(usefulHints, jsonBuilderFactory, secondary);
            } else if (Enum.class.isAssignableFrom(klazz)){
                deserializer = new EnumMapping(usefulHints, annotationIntrospector.findEnumValues(klazz), caseInsensitive);
            } else {
                Polymorphism<String> polymorphism = annotationIntrospector.findSubtypes(klazz);
                deserializer = polymorphism != null ? new PolymorphismTypeDeserializer<>(polymorphism, jsonBuilderFactory, numberFormat, dateFormat, locale) :
                        createBeanDeserializer(klazz);
            }
            hints.pop();
            return deserializer;
        }

        <T> JsonbDeserializer<T> createBeanDeserializer(Class<T> klazz) {
            JsonbDeserializer beanDeser = inCreation.get(runtimeType);
            if (beanDeser != null) {
                return beanDeser;
            }
            // public、protected的内部类和静态内部类时可被反序列化；匿名类不可被反序列化
            if (klazz.isAnonymousClass()) {
                throw new JsonbException("Deserialization into anonymous classes is not supported");
            }
            if (klazz.isMemberClass() && !Modifier.isPublic(klazz.getModifiers()) && !Modifier.isProtected(klazz.getModifiers())) {
                throw new JsonbException("Nested classes should be public or protected");
            }

            JavaBeanDeserializer beanDeserializer = new JavaBeanDeserializer(hints.toArray(new Type[0]), caseInsensitive);
            inCreation.put(runtimeType, beanDeserializer);

            JavaBean<T> beanInfo = JavaBean.introspect(klazz);
            Map<String, JavaProperty> propertyMappings = Arrays.stream(beanInfo.getProperties())
                    .collect(Collectors.toMap(JavaProperty::getName, Function.identity()));
            PropertyVisibilityStrategy visibilityStrategy = Optional.ofNullable(annotationIntrospector.findVisibility(klazz, true))
                    .orElse(customVisibilityStrategy);
            JsonbDateFormat dateTimeFormatter = annotationIntrospector.findDateTimeFormat(klazz, true);
            NumberFormat numberFormat = annotationIntrospector.findNumberFormat(klazz, true);
            List<JavaBeanPropertyDeserializer<T, ?>> deserializers = new ArrayList<>(beanInfo.getProperties().length);
            JavaBeanInstantiator<T> beanInstantiator; // 有protected或public的无参构造函数，或有初始化方法
            Executable executable = annotationIntrospector.findCreator(klazz);
            if (executable == null && JvmIntrospector.isRecord(klazz)) {
                executable = klazz.getDeclaredConstructors()[0];
            }
            boolean nonStaticMember = beanInfo.isNonStaticMemberClass();
            if (executable != null) {
                // 对象初始化参数不一定对应属性
                Map<String, Integer> nameIndexPairs = executable.getParameterCount() == 0 ?
                        Collections.emptyMap() :
                        new HashMap<>(executable.getParameterCount());
                Parameter[] parameters = executable.getParameters();
                int start = klazz.isMemberClass() && !Modifier.isStatic(klazz.getModifiers()) ? 1 : 0;
                for (int i = start; i < parameters.length; i++) {
                    String annotatedName = annotationIntrospector.findNameForDeserialization(parameters[i - start]);
                    String argName = Optional.ofNullable(annotatedName)
                            .orElse(parameters[i].getName()); // 编译时保留debug信息，则从类文件中可以获取常参数名，否则只能收到arg0形式
                    JavaProperty property = propertyMappings.get(argName); // 是否要从字段上获取个性化信息？
                    if (property == null) {
                        throw new JsonbException("can not found property named '" + argName + "' for type " + klazz);
                    }
                    if (property.getProperty() == null ||
                            Modifier.isStatic(property.getProperty().getModifiers()) ||
                            Modifier.isTransient(property.getProperty().getModifiers()) ||
                            annotationIntrospector.hasIgnoreMarker(property.getProperty())) {
                        throw new JsonbException("constructor argument must not static or transient");
                    }
                    JsonbDateFormat argDateFormat = Optional.ofNullable(annotationIntrospector.findDateTimeFormat(parameters[i - start]))
                            .orElse(dateTimeFormatter);
                    NumberFormat argNumberFormat = Optional.ofNullable(annotationIntrospector.findNumberFormat(parameters[i - start]))
                            .orElse(numberFormat);
                    String dateFormat = this.dateFormat;
                    Locale locale = this.locale;
                    if (argDateFormat != null) {
                        dateFormat = argDateFormat.value();
                        locale = JsonbDateFormat.DEFAULT_LOCALE.equals(argDateFormat.locale()) ? this.locale : new Locale(argDateFormat.locale());
                    }
                    JsonbDeserializer argDeserializer = findTypeDeserializer(parameters[i].getParameterizedType(), argNumberFormat, dateFormat, locale);
                    if (!argName.equals(annotatedName)) {
                        argName = customNamingStrategy != null ? customNamingStrategy.translateName(argName) : argName;
                    }
                    deserializers.add(new JavaBeanPropertyDeserializer<>(argName, strictInternetJson, parameters[i].getParameterizedType(), null, argDeserializer));
                    nameIndexPairs.put(argName, i);
                }
                beanInstantiator = new JavaBeanInstantiator<>(executable, nameIndexPairs, parametersRequired);
            } else {
                executable = Optional.ofNullable(beanInfo.getNoArgsConstructor()).
                        orElseThrow(() -> new JsonbException("no available constructor found on type: " + klazz)); // 可能是子类型，后续决定
                beanInstantiator = new JavaBeanInstantiator<>(executable, Collections.emptyMap(), parametersRequired);
            }
            beanDeserializer.withInstantiator(nonStaticMember, beanInstantiator);

            Class current = klazz;
            while (current != Object.class) {
                if (current != klazz) {
                    beanInfo = JavaBean.introspect(current);
                }
                int maxPropertyQuantity = beanInfo.getProperties().length;
                for (int i = 0; i < maxPropertyQuantity; i++) {
                    JavaProperty property = beanInfo.getProperties()[i];
                    if (current == klazz && beanInstantiator.getArgNameIndexPairs().containsKey(property.getName())) {
                        log.log(Level.INFO, "property [{0}] is constructor argument of type {1}", new Object[] {property.getName(), current.getName()});
                        continue;
                    }

                    String customPropertyName = customNamingStrategy != null ?
                            customNamingStrategy.translateName(property.getName()) :
                            property.getName();

                    Optional<String> propertyName = Optional.empty();
                    Optional<JsonbDeserializer<?>> deserializer = Optional.empty();
                    Optional<JsonbAdapter<?, ?>> adapter = Optional.empty();
                    Optional<JsonbDateFormat> propertyDateFormat = Optional.empty();
                    Optional<NumberFormat> propertyNumberFormat = Optional.empty();
                    if (property.getProperty() != null) {
                        annotationIntrospector.hasIgnoreMarker(property.getProperty());
                        propertyName = Optional.ofNullable(annotationIntrospector.findNameForSerialization(property.getProperty()));
                        deserializer= Optional.ofNullable(annotationIntrospector.findDeserializer(property.getProperty()));
                        adapter = Optional.ofNullable(annotationIntrospector.findAdapter(property.getProperty()));
                        propertyDateFormat = Optional.ofNullable(annotationIntrospector.findDateTimeFormat(property.getProperty()));
                        propertyNumberFormat = Optional.ofNullable(annotationIntrospector.findNumberFormat(property.getProperty()));
                    }

                    Optional<JavaBeanPropertyDeserializer<T, ?>> dser = createPropertyDeserializer(property,
                            visibilityStrategy,
                            propertyName.orElse(customPropertyName),
                            caseInsensitive,
                            adapter.orElse(null),
                            deserializer.orElse(null),
                            propertyDateFormat.orElse(dateTimeFormatter),
                            propertyNumberFormat.orElse(numberFormat));

                    dser.ifPresent(deserializers::add);
                }

                current = current.getSuperclass();
            }
            beanDeserializer.setPropertyDeserializers(deserializers);
            inCreation.remove(runtimeType);
            return beanDeserializer;
        }

        protected <D, T, A> Optional<JavaBeanPropertyDeserializer<D, T>> createPropertyDeserializer(JavaProperty<D, T> property,
                                                                                                    PropertyVisibilityStrategy visibilityStrategy,
                                                                                                    String nameToDeserialize,
                                                                                                    boolean caseInsensitive,
                                                                                                    JsonbAdapter adapter,
                                                                                                    JsonbDeserializer deserializer,
                                                                                                    JsonbDateFormat dateFormat,
                                                                                                    NumberFormat numberFormat) {
            // 1. 字段为transient、static、final修饰时不可反序列化
            if (property.getProperty() != null) {
                if (Modifier.isFinal(property.getProperty().getModifiers()) ||
                        Modifier.isStatic(property.getProperty().getModifiers()) ||
                        Modifier.isTransient(property.getProperty().getModifiers())) {
                    return Optional.empty();
                }
            }
            // 2. 根据可见性规则决定使用字段还是setter方法反序列化，默认可见性规则为优先使用public setter方法（即便字段不存在），不存在时使用public字段
            int visibility = visibility(property.getProperty(), property.getSetter(), visibilityStrategy);
            if (visibility == INVISIBLE) {
                return Optional.empty();
            }
            boolean useSetter = (visibility & PROPERTY) != 0;
            AccessibleObject ann = useSetter ? property.getSetter() : property.getProperty();
            Type propertyType = useSetter ? property.getSetter().getGenericParameterTypes()[0] : property.getProperty().getGenericType();
            // 3. 有忽略标记的属性不被反序列化
            if (annotationIntrospector.hasIgnoreMarker(ann)) {
                return Optional.empty();
            }
            BiConsumer<D, T> setter = (d, t) -> property.set(d, t, useSetter, !useSetter);
            if (useSetter) {
                nameToDeserialize = Optional.ofNullable(annotationIntrospector.findNameForSerialization(ann))
                        .orElse(nameToDeserialize);
                dateFormat = Optional.ofNullable(annotationIntrospector.findDateTimeFormat(ann)).orElse(dateFormat);
                numberFormat = Optional.ofNullable(annotationIntrospector.findNumberFormat(ann)).orElse(numberFormat);
                adapter = Optional.ofNullable(annotationIntrospector.findAdapter(ann)).orElse(adapter);
                deserializer = Optional.ofNullable(annotationIntrospector.findDeserializer(ann)).orElse(deserializer);
            }
            String df = this.dateFormat;
            Locale locale = this.locale;
            if (dateFormat != null) {
                df = dateFormat.value();
                locale = JsonbDateFormat.DEFAULT_LOCALE.equals(dateFormat.locale()) ? this.locale : new Locale(dateFormat.locale());
            }
            if (deserializer == null && adapter != null) {
                Type adaptedType = Reflections.findActualTypes(adapter.getClass(), JsonbAdapter.class)[1];
                deserializer = findTypeDeserializer(adaptedType, numberFormat, df, locale);
                deserializer = new AdapterDeserializer<T, A>(adapter, deserializer, adaptedType);
            } else if (deserializer == null) {
                deserializer = findTypeDeserializer(propertyType, numberFormat, df, locale);
            }
            return Optional.of(new JavaBeanPropertyDeserializer<D, T>(nameToDeserialize, caseInsensitive, propertyType, setter, deserializer));
        }
    }

    /**
     * @see com.fasterxml.jackson.datatype.jsr353.JsonValueSerializer
     * @see org.eclipse.yasson.internal.serializer.types.JsonValueSerializer
     */
    static class JsonValueSerializer implements JsonbSerializer<JsonValue> {

        @Override
        public void serialize(JsonValue obj, JsonGenerator generator, SerializationContext ctx) {
            generator.write(obj);
        }
    }

    /**
     * @see com.fasterxml.jackson.datatype.jsr353.JsonValueDeserializer
     * @see org.eclipse.yasson.internal.deserializer.types.JsonValueDeserializer
     */
    static class JsonValueDeserializer implements JsonbDeserializer<JsonValue> {

        @Override
        public JsonValue deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            JsonParser.Event event = parser.next();
            JsonValue value = switch (event) {
                case START_OBJECT -> parser.getObject(); // if Unsupported Operation: jsonBuilderFactory.createObjectBuilder
                case START_ARRAY -> parser.getArray(); // if Unsupported Operation: jsonBuilderFactory.createArrayBuilder
                // pass through
                case VALUE_STRING, VALUE_NUMBER -> parser.getValue();
                case VALUE_TRUE -> JsonValue.TRUE;
                case VALUE_FALSE -> JsonValue.FALSE;
                case VALUE_NULL -> JsonValue.NULL;
                default -> throw new IllegalStateException();
            };
            if (value instanceof JsonObject) {
                if (JsonParser.Event.END_OBJECT != parser.next()) {
                    throw new JsonbException("expect end object event");
                }
            } else if (value instanceof JsonArray) {
                if (JsonParser.Event.END_OBJECT != parser.next()) {
                    throw new JsonbException("expect end array event");
                }
            }
            return value;
        }

    }

    @RequiredArgsConstructor
    static abstract class AbstractSerializer<T> implements JsonbSerializer<T> {

        protected final Set<Object> recursionReference;

        AbstractSerializer() {
            this.recursionReference = null;
        }

        protected boolean isNullOrEmpty(Object obj) {
            return obj == null ||
                    OptionalInt.empty().equals(obj) ||
                    OptionalLong.empty().equals(obj) ||
                    OptionalDouble.empty().equals(obj) ||
                    Optional.empty().equals(obj);
        }

        protected void writeStart(JsonGenerator generator) {}

        @Override
        public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
            if (recursionReference != null && !recursionReference.add(obj)) { // 对象引用，可以为自身引用或引用其他对象；层级上可以为同层级引用相同对象，或跨层级引用相同对象。循环引用将导致序列化将无法正常结束
                throw new JsonbException("circular reference detected!");
            }
            writeStart(generator);
            marshall(obj, generator, ctx);
            writeEnd(generator);
            if (recursionReference != null) {
                recursionReference.remove(obj);
            }
        }

        protected abstract void marshall(T obj, JsonGenerator generator, SerializationContext ctx);

        protected void writeEnd(JsonGenerator generator) {}
    }

    static abstract class AbstractDeserializer<T> implements JsonbDeserializer<T> {

        protected final Stack<Type> hints;

        AbstractDeserializer() {
            this.hints = null;
        }

        AbstractDeserializer(Type[] hints) {
            this.hints = new Stack<>();
            for (int i = 0; i < hints.length - 1; i++) {
                this.hints.push(hints[i]);
            }
        }

        protected JsonParser.Event last;

        protected abstract JsonParser.Event getExpected();

        protected T handleNull() {
            return null;
        }

        protected void check() {
            if (last != getExpected()) {
                throw new JsonbException("expected json event [" + getExpected() + "], but actual is [" + last + "]");
            }
        }

        @Override
        public T deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            last = parser.next();
            if (last == JsonParser.Event.VALUE_NULL) {
                return handleNull();
            }
            check();

            return unmarshal(parser, ctx, rtType);
        }

        protected abstract T unmarshal(JsonParser parser, DeserializationContext ctx, Type rtType);

    }

    static abstract class BaseArraySerializer<T> extends AbstractSerializer<T> {

        @Override protected void writeStart(JsonGenerator generator) { generator.writeStartArray(); }

        @Override protected void writeEnd(JsonGenerator generator) { generator.writeEnd(); }
    }

    static abstract class BaseArrayDeserializer<T> extends AbstractDeserializer<T> {
        BaseArrayDeserializer() {
            super();
        }

        BaseArrayDeserializer(Type[] hints) {
            super(hints);
        }

        @Override
        protected JsonParser.Event getExpected() {
            return JsonParser.Event.START_ARRAY;
        }

        protected T newArray(Class<?> componentType, int len) {
            return (T) Array.newInstance(componentType, len);
        }

        @Override
        protected T unmarshal(JsonParser parser, DeserializationContext ctx, Type rtType) {
            int size = 0, len = 10;
            Class<?> componentType = rtType instanceof GenericArrayType ?
                    Reflections.searchComponentType((GenericArrayType) rtType, hints) :
                    ((Class<?>) rtType).getComponentType();
            T array = newArray(componentType, len);
            int deep = 1;
            while (parser.hasNext()) {
                last = parser.next();
                if (last == JsonParser.Event.START_ARRAY) {
                    deep++;
                }
                if (last == JsonParser.Event.END_ARRAY) {
                    deep--;
                }
                if (deep == 0) {
                    break;
                }
                fill(parser, ctx, array, componentType, size);
                size++;
                if (size == len - 1) {
                    len = (int) (1.5 *  len);
                    T na =  newArray(componentType, len);
                    System.arraycopy(array, 0, na, 0, size);
                    array = na;
                }
            }
            if (size < len - 1) {
                T result = newArray(componentType, size);
                System.arraycopy(array, 0, result, 0, size);
                return result;
            } else {
                return array;
            }
        }

        protected void fill(JsonParser parser, DeserializationContext ctx, T array, Class<?> componentType, int index) {
            fill(parser, array, index);
        }

        protected void fill(JsonParser parser, T array, int index) {
            throw new AbstractMethodError();
//            Array.set(array, index, parser::get);
        }
    }

    @RequiredArgsConstructor
    static class BoolMapping implements JsonbSerializer<Boolean>, JsonbDeserializer<Boolean> {

        private final Supplier<Boolean> nullSupplier;

        BoolMapping() {
            this.nullSupplier = null;
        }

        @Override
        public Boolean deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            JsonParser.Event current = parser.next();
            if (current == JsonParser.Event.VALUE_NULL) {
                return nullSupplier != null ? nullSupplier.get() : null;
            } else if (current == JsonParser.Event.VALUE_FALSE) {
                return Boolean.FALSE;
            } else if (current == JsonParser.Event.VALUE_TRUE) {
                return Boolean.TRUE;
            } else if (current == JsonParser.Event.VALUE_STRING) {
                return Boolean.valueOf(parser.getString());
            }
            throw new JsonbException("expected VALUE_NULL/VALUE_STRING/VALUE_TRUE/VALUE_FALSE for java type Character");
        }

        @Override
        public void serialize(Boolean obj, JsonGenerator generator, SerializationContext ctx) {
            generator.write(obj);
        }
    }

    @RequiredArgsConstructor
    static class CharMapping implements JsonbSerializer<Character>, JsonbDeserializer<Character> {

        private final Supplier<Character> nullSupplier;

        CharMapping() {
            this.nullSupplier = null;
        }


        @Override
        public Character deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            JsonParser.Event current = parser.next();
            if (current == JsonParser.Event.VALUE_NULL) {
                return nullSupplier != null ? nullSupplier.get() : null;
            } else if (current == JsonParser.Event.VALUE_STRING) {
                String str = parser.getString();
                if (str.length() > 1) {
                    throw new JsonbException("more than one character for java type char/Character");
                }
                return parser.getString().charAt(0);
            } else if (current == JsonParser.Event.VALUE_NUMBER) {
                return (char) parser.getInt();
            }
            throw new JsonbException("expected VALUE_NULL or VALUE_STRING for java type char/Character");
        }

        @Override
        public void serialize(Character obj, JsonGenerator generator, SerializationContext ctx) {
            generator.write(obj.toString());
        }
    }

    static class BooleanArraySerializer extends BaseArraySerializer<boolean[]> {

        @Override
        protected void marshall(boolean[] obj, JsonGenerator generator, SerializationContext ctx) {
            for (boolean b : obj) {
                generator.write(b);
            }
        }
    }

    static class BooleanArrayDeserializer extends BaseArrayDeserializer<boolean[]> {

        @Override
        protected void fill(JsonParser parser, boolean[] array, int index) {
            if (last == JsonParser.Event.VALUE_FALSE) {
                array[index] = false;
            } else if (last == JsonParser.Event.VALUE_TRUE) {
                array[index] = true;
            } else {
                throw new JsonbException("require json event VALUE_FALSE/VALUE_TRUE, but actual is " + last);
            }
        }

    }

    @RequiredArgsConstructor
    static class ByteArraySerializer extends BaseArraySerializer<byte[]> {

        private final Base64.Encoder encoder;

        @Override
        public void serialize(byte[] obj, JsonGenerator generator, SerializationContext ctx) {
            if (encoder != null && obj != null) {
                generator.write(encoder.encodeToString(obj));
                return;
            }
            super.serialize(obj, generator, ctx);
        }

        @Override
        protected void marshall(byte[] obj, JsonGenerator generator, SerializationContext ctx) {
            for (byte b : obj) {
                generator.write(b);
            }
        }
    }

    static class ByteArrayDeserializer extends BaseArrayDeserializer<byte[]> {

        private final Base64.Decoder decoder;

        ByteArrayDeserializer(Base64.Decoder decoder) {
            this.decoder = decoder;
        }

        protected void check() {
            if (last != JsonParser.Event.START_ARRAY && (last == JsonParser.Event.VALUE_STRING && decoder == null)) {
                throw new JsonbException("expect START_ARRAY for byte array, or configure BinaryDataStrategy to accept VALUE_STRING");
            }
        }

        @Override
        protected void fill(JsonParser parser, byte[] array, int index) {
            if (last == JsonParser.Event.VALUE_NUMBER) {
                array[index] = (byte) parser.getInt();
            } else {
                throw new JsonbException("require json event VALUE_NUMBER, but actual is " + last);
            }
        }

        @Override
        protected byte[] unmarshal(JsonParser parser, DeserializationContext ctx, Type rtType) {
            if (last == JsonParser.Event.VALUE_STRING) {
                return decoder.decode(parser.getString());
            }
            return super.unmarshal(parser, ctx, rtType);
        }
    }

    static class CharArraySerializer extends BaseArraySerializer<char[]> {

        @Override
        protected void marshall(char[] obj, JsonGenerator generator, SerializationContext ctx) {
            for (char c : obj) {
                generator.write(c);
            }
        }
    }

    static class CharArrayDeserializer extends BaseArrayDeserializer<char[]> {

        @Override
        protected void fill(JsonParser parser, char[] array, int index) {
            if (last == JsonParser.Event.VALUE_NUMBER) {
                array[index] = (char) parser.getInt();
            } else if (last == JsonParser.Event.VALUE_STRING) {
                array[index] = parser.getString().charAt(0);
            } else {
                throw new JsonbException("require json event VALUE_NUMBER/VALUE_STRING, but actual is " + last);
            }
        }

    }

    static class ShortArraySerializer extends BaseArraySerializer<short[]> {

        @Override
        protected void marshall(short[] obj, JsonGenerator generator, SerializationContext ctx) {
            for (short s : obj) {
                generator.write(s);
            }
        }
    }

    static class ShortArrayDeserializer extends BaseArrayDeserializer<short[]> {

        @Override
        protected void fill(JsonParser parser, short[] array, int index) {
            if (last == JsonParser.Event.VALUE_NUMBER) {
                array[index] = (short) parser.getInt();
            } else if (last == JsonParser.Event.VALUE_STRING) {
                array[index] = Short.parseShort(parser.getString());
            } else {
                throw new JsonbException("require json event VALUE_NUMBER/VALUE_STRING, but actual is " + last);
            }
        }

    }

    static class IntArraySerializer extends BaseArraySerializer<int[]> {

        @Override
        protected void marshall(int[] obj, JsonGenerator generator, SerializationContext ctx) {
            for (int i : obj) {
                generator.write(i);
            }
        }
    }

    static class IntArrayDeserializer extends BaseArrayDeserializer<int[]> {

        @Override
        protected void fill(JsonParser parser, int[] array, int index) {
            if (last == JsonParser.Event.VALUE_NUMBER) {
                array[index] = parser.getInt();
            } else if (last == JsonParser.Event.VALUE_STRING) {
                array[index] = Integer.parseInt(parser.getString());
            } else {
                throw new JsonbException("require json event VALUE_NUMBER/VALUE_STRING, but actual is " + last);
            }
        }

    }

    static class LongArraySerializer extends BaseArraySerializer<long[]> {

        @Override
        protected void marshall(long[] obj, JsonGenerator generator, SerializationContext ctx) {
            for (long l : obj) {
                generator.write(l);
            }
        }
    }

    static class LongArrayDeserializer extends BaseArrayDeserializer<long[]> {

        @Override
        protected void fill(JsonParser parser, long[] array, int index) {
            if (last == JsonParser.Event.VALUE_NUMBER) {
                array[index] = parser.getLong();
            } else if (last == JsonParser.Event.VALUE_STRING) {
                array[index] = Long.parseLong(parser.getString());
            } else {
                throw new JsonbException("require json event VALUE_NUMBER/VALUE_STRING, but actual is " + last);
            }
        }

    }

    static class FloatArraySerializer extends BaseArraySerializer<float[]> {

        @Override
        protected void marshall(float[] obj, JsonGenerator generator, SerializationContext ctx) {
            for (float f : obj) {
                generator.write(f);
            }
        }
    }

    static class FloatArrayDeserializer extends BaseArrayDeserializer<float[]> {

        @Override
        protected void fill(JsonParser parser, float[] array, int index) {
            if (last == JsonParser.Event.VALUE_NUMBER) {
                array[index] = parser.getBigDecimal().floatValue();
            } else if (last == JsonParser.Event.VALUE_STRING) {
                array[index] = Float.parseFloat(parser.getString());
            } else {
                throw new JsonbException("require json event VALUE_NUMBER/VALUE_STRING, but actual is " + last);
            }
        }

    }

    static class DoubleArraySerializer extends BaseArraySerializer<double[]> {

        @Override
        protected void marshall(double[] obj, JsonGenerator generator, SerializationContext ctx) {
            for (double d : obj) {
                generator.write(d);
            }
        }
    }

    static class DoubleArrayDeserializer extends BaseArrayDeserializer<double[]> {

        @Override
        protected void fill(JsonParser parser, double[] array, int index) {
            if (last == JsonParser.Event.VALUE_NUMBER) {
                array[index] = parser.getBigDecimal().doubleValue();
            } else if (last == JsonParser.Event.VALUE_STRING) {
                array[index] = Double.parseDouble(parser.getString());
            } else {
                throw new JsonbException("require json event VALUE_NUMBER/VALUE_STRING, but actual is " + last);
            }
        }

    }

    static class SimpleMapping<T> implements JsonbSerializer<T>,  JsonbDeserializer<T> {

        private final Function<String, T> parseFunc;

        private final Function<T, String> toStringFunc;

        SimpleMapping(Function<String, T> parseFunc) {
            this(parseFunc, Object::toString);
        }

        SimpleMapping(Function<String, T> parseFunc, Function<T, String> toStringFunc) {
            this.parseFunc = parseFunc;
            this.toStringFunc = toStringFunc;
        }

        @Override
        public T deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            JsonParser.Event current = parser.next();
            if (current == JsonParser.Event.VALUE_NULL) {
                return null;
            }
            if (current != JsonParser.Event.VALUE_STRING) {
                throw new JsonbException("expect json event VALUE_STRING");
            }
            try {
                return parseFunc.apply(parser.getString());
            } catch (RuntimeException re) {
                throw new JsonbException(re.getMessage(), re);
            }
        }

        @Override
        public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
            try {
                generator.write(toStringFunc.apply(obj));
            } catch (RuntimeException re) {
                throw new JsonbException(re.getMessage(), re);
            }
        }
    }

    static class NumberSerializer<T> implements JsonbSerializer<T> {

        private final NumberFormat format;

        private final Function<T, Number> converter;

        NumberSerializer() {
            this.format = null;
            this.converter = null;
        }

        NumberSerializer(NumberFormat format) {
            this(format, null);
        }
        NumberSerializer(Function<T, Number> converter) {
            this(null, converter);
        }


        NumberSerializer(NumberFormat format, Function<T, Number> converter) {
            this.format = format;
            this.converter = converter;
        }

        @Override
        public void serialize(T original, JsonGenerator generator, SerializationContext ctx) {
            Number obj = converter != null ? converter.apply(original) : (Number) original;
            if (format != null) {
                generator.write(format.format(obj));
                return;
            }
            if (obj instanceof Byte) {
                generator.write(obj.byteValue());
            } else if (obj instanceof Short) {
                generator.write(obj.shortValue());
            } else if (obj instanceof Integer) {
                generator.write(obj.intValue());
            } else if (obj instanceof Long) {
                generator.write(obj.longValue());
            } else if (obj instanceof BigInteger) {
                generator.write((BigInteger) obj);
            } else if (obj instanceof BigDecimal) {
                generator.write((BigDecimal) obj);
            } else { // Float, Double, Number
                generator.write(new BigDecimal(String.valueOf(obj)));
            }
        }
    }

    @RequiredArgsConstructor
    static class NumberDeserializer<T> implements JsonbDeserializer<T> {

        private final NumberFormat format;

        private final Function<String, T> convertFunc;

        private final Supplier<T> nullSupplier;

        NumberDeserializer(Function<String, T> convertFunc) {
            this(null, convertFunc, null);
        }

        NumberDeserializer(Function<String, T> convertFunc, Supplier<T> nullSupplier) {
            this(null, convertFunc, nullSupplier);
        }

        @Override
        public T deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            JsonParser.Event current = parser.next();
            if (current == JsonParser.Event.VALUE_NULL) {
                return Optional.ofNullable(nullSupplier).map(Supplier::get).orElse(null);
            } else if (current == JsonParser.Event.VALUE_NUMBER) {
                return convert(parser.getString(), rtType, convertFunc);
            } else  if (current == JsonParser.Event.VALUE_STRING) {
                String s = parser.getString();
                try {
                    return format != null ? (T) format.parse(s) : convert(s, rtType, convertFunc);
                } catch (ParseException pe) {
                    log.log(Level.WARNING, "bind json value[{0}] with format[{1}] to number fail", new Object[] {s, format});
                    return convert(s, rtType, convertFunc); // have a try
                }
            } else {
                throw new JsonbException("expect json event VALUE_NUMBER or VALUE_STRING");
            }
        }

        private T convert(String s, Type rtType, Function<String, T> convertFunc) {
            if (convertFunc != null) {
                return convertFunc.apply(s);
            }
            throw new JsonbException("unknown number type: " + rtType);
        }
    }

    static class CalendarSerializer<T extends Calendar> implements JsonbSerializer<T> {

        private final Locale locale;

        private final DateTimeFormatter formatter;

        private final boolean asNumber;

        CalendarSerializer() {
            this.locale = null;
            this.formatter = null;
            this.asNumber = true;
        }

        CalendarSerializer(Locale locale) {
            this(null, locale);
        }

        CalendarSerializer(DateTimeFormatter formatter, Locale locale) {
            this.formatter = formatter;
            Objects.requireNonNull(locale);
            this.locale = locale;
            this.asNumber = false;
        }

        @Override
        public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
            if (asNumber) {
                generator.write(obj.getTimeInMillis());
                return;
            }

            DateTimeFormatter formatter = this.formatter != null ?
                    this.formatter :
                    (obj.isSet(Calendar.HOUR) || obj.isSet(Calendar.HOUR_OF_DAY)) ?
                            DateTimeFormatter.ISO_DATE_TIME : DateTimeFormatter.ISO_DATE;
            generator.write(
                    formatter.withZone(obj.getTimeZone().toZoneId()).withLocale(locale)
                            .format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(obj.getTimeInMillis()), obj.getTimeZone().toZoneId()))
            );
        }
    }

    @RequiredArgsConstructor
    static class CalendarDeserializer implements JsonbDeserializer<Calendar> {

        private final DateTimeFormatter formatter;

        private final Locale locale;

        @Override
        public Calendar deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            JsonParser.Event current = parser.next();
            if (current == JsonParser.Event.VALUE_NULL) {
                return null;
            } else if (current == JsonParser.Event.VALUE_NUMBER) {
                return GregorianCalendar.from(Instant.ofEpochMilli(parser.getLong()).atZone(UTC));
            } else if (current != JsonParser.Event.VALUE_STRING) {
                throw new JsonbException("expect json event VALUE_STRING");
            }
            String str = parser.getString();
            DateTimeFormatter formatter = this.formatter != null ?
                    this.formatter :
                    str.contains("T") ? DateTimeFormatter.ISO_DATE_TIME : DateTimeFormatter.ISO_DATE;
            try {
                TemporalAccessor tmp = formatter.withLocale(locale).parse(str);
                LocalTime time = Optional.ofNullable(tmp.query(TemporalQueries.localTime()))
                        .orElse(LocalTime.of(0, 0, 0));
                ZoneId zone = Optional.ofNullable(tmp.query(TemporalQueries.zone())).orElse(UTC);
                return GregorianCalendar.from(LocalDate.from(tmp).atTime(time).atZone(zone));
            } catch (DateTimeException dte) {
                log.log(Level.WARNING, "", new Object[] {str, formatter});
                try {
                    return GregorianCalendar.from(Instant.ofEpochMilli(Long.parseLong(str)).atZone(UTC));
                } catch (NumberFormatException nfe) {
                    JsonbException t = new JsonbException(dte.getMessage(), dte);
                    t.addSuppressed(nfe);
                    throw t;
                }
            }
        }
    }

    static class DateTimeSerializer<T> implements JsonbSerializer<T> {

        private final DateTimeFormatter formatter;

        private final Function<T, ? extends Temporal> cast;

        private final Function<T, Long> converter;

        DateTimeSerializer(DateTimeFormatter formatter) {
            this(formatter, Temporal.class::cast);
        }

        DateTimeSerializer(DateTimeFormatter formatter, Function<T, ? extends Temporal> cast) {
            Objects.requireNonNull(formatter);
            Objects.requireNonNull(cast);
            this.formatter = formatter;
            this.cast = cast;
            this.converter = null;
        }

        DateTimeSerializer(Function<T, Long> converter) {
            Objects.requireNonNull(converter);
            this.formatter = null;
            this.cast = null;
            this.converter = converter;
        }

        @Override
        public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
            if (converter != null)  {
                generator.write(converter.apply(obj));
            } else {
                generator.write(formatter.format(cast.apply(obj)));
            }
        }
    }

    @RequiredArgsConstructor
    static class DateTimeDeserializer<T> implements JsonbDeserializer<T> {

        private final Function<Long, T> convertFunc;

        private final DateTimeFormatter formatter;

        private final BiFunction<String, DateTimeFormatter, T> func;

        @Override
        public T deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            JsonParser.Event current = parser.next();
            if (current == JsonParser.Event.VALUE_NULL) {
                return null;
            }
            if (current == JsonParser.Event.VALUE_NUMBER) {
                if (convertFunc == null) {
                    throw new JsonbException("json event [VALUE_NUMBER] not accept for" + rtType);
                }
                return convertFunc.apply(parser.getLong());
            } else if (current == JsonParser.Event.VALUE_STRING) {
                String s = parser.getString();
                try {
                    return formatter != null ? func.apply(s, formatter) : convertFunc.apply(Long.parseLong(s));
                } catch (DateTimeException e) {
                    log.log(Level.WARNING, "bind json value[{0}] with formatter[{1}] to date time fail", new Object[] {s, formatter});
                    throw new JsonbException(e.getMessage(), e);
                } catch (NumberFormatException nfe) {
                    throw new JsonbException(nfe.getMessage(), nfe);
                }
            } else {
                throw new JsonbException("expected json event VALUE_STRING or VALUE_NUMBER");
            }
        }
    }

    @RequiredArgsConstructor
    static class OptionalSerializer<T> implements JsonbSerializer<Optional<T>> {

        private final JsonbSerializer<T> delegate;

        private final boolean writeNull;

        @Override
        public void serialize(Optional<T> obj, JsonGenerator generator, SerializationContext ctx) {
            if (obj == null || obj.isEmpty()) {
                if (writeNull) {
                    generator.writeNull();
                }
                return;
            }
            delegate.serialize(obj.get(), generator, ctx);
        }
    }

    @RequiredArgsConstructor
    static class OptionalDeserializer<T> extends AbstractDeserializer<Optional<T>> {

        private final JsonBuilderFactory jsonBuilderFactory;

        private final JsonbDeserializer<T> delegate;

        protected JsonParser.Event getExpected() {
            throw new AbstractMethodError();
        }

        protected void check() {}

        protected Optional<T> handleNull() {
            return Optional.empty();
        }

        @Override
        public Optional<T> unmarshal(JsonParser parser, DeserializationContext ctx, Type rtType) {
            Type actual = rtType instanceof ParameterizedType ? ((ParameterizedType) rtType).getActualTypeArguments()[0] : Object.class;
            return Optional.of(delegate.deserialize(new PrefetchEventJsonParser(jsonBuilderFactory, parser, last), ctx, actual));
        }
    }

    static class MapSerializer<T> extends AbstractSerializer<Map<String, T>> {

        private final String orderStrategy;

        private final boolean writeNull;

//        private final JsonbSerializer keySerializer;

        private final JsonbSerializer<T> valueSerializer;

        MapSerializer(Set<Object> recursionReference, String orderStrategy, boolean writeNull,  JsonbSerializer<T> valueSerializer) {
            super(recursionReference);
            this.orderStrategy = orderStrategy;
            this.writeNull = writeNull;
            this.valueSerializer = valueSerializer;
        }

        @Override protected void writeStart(JsonGenerator generator) { generator.writeStartObject(); }

        @Override
        public void marshall(Map<String, T> obj, JsonGenerator generator, SerializationContext ctx) {
            obj = sortIfRequired(obj);
            for (Map.Entry<String, T> entry : obj.entrySet()) {
                T value = entry.getValue();
                if (isNullOrEmpty(value)) {
                    if (writeNull) {
                        generator.writeNull(entry.getKey());
                    }
                    return;
                }
                generator.writeKey(entry.getKey());
                valueSerializer.serialize(value, generator, ctx);
            }
        }

        private Map<String, T> sortIfRequired(Map<String, T> obj) {
            if (obj instanceof SortedMap) {
                return obj;
            }
            if (PropertyOrderStrategy.LEXICOGRAPHICAL.equals(orderStrategy)) {
                return new TreeMap<>(obj);
            } else if (PropertyOrderStrategy.REVERSE.equals(orderStrategy)) {
                TreeMap<String, T> treeMap = new TreeMap<>(Comparator.reverseOrder());
                treeMap.putAll(obj);
                return treeMap;
            }
            return obj;
        }

        @Override protected void writeEnd(JsonGenerator generator) { generator.writeEnd(); }
    }

    static class MapDeserializer<T> implements JsonbDeserializer<Map<String, T>> {

        private final Stack<Type> hints;

        private final JsonbDeserializer<T> valueDeserializer;

        MapDeserializer(Type[] hints, JsonbDeserializer<T> valueDeserializer) {
            this.hints = new Stack<>();
            for (int i = 0; i < hints.length - 1; i++) {
                this.hints.push(hints[i]);
            }
            this.valueDeserializer = valueDeserializer;
        }

        @Override
        public Map<String, T> deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            Type at1 = rtType instanceof ParameterizedType ?
                    ((ParameterizedType) rtType).getActualTypeArguments()[1] : Object.class;
            JsonParser.Event current = parser.next();
            if (current != JsonParser.Event.START_OBJECT) {
                throw new JsonbException("Map context, expect event START_OBJECT");
            }

            Map<String, T> container = newMap(Reflections.typeToClass(rtType, hints));
            while (parser.hasNext()) {
                current = parser.next();
                if (current == JsonParser.Event.END_OBJECT) {
                    return container;
                }
                if (current != JsonParser.Event.KEY_NAME) {
                    throw new JsonbException("Map context, expect event KEY_NAME after START_OBJECT or VALUE_* ");
                }
                String key = parser.getString();
                T value = valueDeserializer.deserialize(parser, ctx, at1);
                container.put(key, value);
            }
            throw new JsonbException("Map context, require event END_OBJECT");
        }

        private Map<String, T> newMap(Class<?> klazz) {
            if (SortedMap.class.equals(klazz) || NavigableMap.class.equals(klazz) || TreeMap.class.equals(klazz)) { // SortedMap, NavigableMap, TreeMap
                return new TreeMap<>();
            } else if (LinkedHashMap.class.equals(klazz)) {
                return new LinkedHashMap<>();
            } else if (Map.class.equals(klazz) || AbstractMap.class.equals(klazz) || HashMap.class.equals(klazz)){ // Map, HashMap
                return new HashMap<>();
            }

            return (Map<String, T>) JsonbContext.newInstance(klazz);
        }
    }

    static class CollectionSerializer<E, T extends Iterable<E>> extends AbstractSerializer<T> {

        private final JsonbSerializer<E> elementSerializer;

        CollectionSerializer(Set<Object> recursionReference, JsonbSerializer<E> elementSerializer) {
            super(recursionReference);
            this.elementSerializer = elementSerializer;
        }

        @Override
        protected void writeStart(JsonGenerator generator) {
            generator.writeStartArray();
        }

        @Override
        public void marshall(T obj, JsonGenerator generator, SerializationContext ctx) {
            for (E item : obj) {
                if (isNullOrEmpty(item)) { // XXX 规范未说明当集合元素为null时该如何处理
                    generator.writeNull();
                } else {
                    elementSerializer.serialize(item, generator, ctx);
                }
            }
        }

        protected void writeEnd(JsonGenerator generator) {
            generator.writeEnd();
        }
    }

    static class CollectionDeserializer<E, T extends Collection<E>> extends AbstractDeserializer<T> {

        private final JsonBuilderFactory jsonBuilderFactory;

        private final JsonbDeserializer<E> elementDeserializer;

        CollectionDeserializer(Type[] hints, JsonBuilderFactory jsonBuilderFactory, JsonbDeserializer<E> elementDeserializer) {
            super(hints);
            this.jsonBuilderFactory = jsonBuilderFactory;
            this.elementDeserializer = elementDeserializer;
        }

        protected JsonParser.Event getExpected() {
            return JsonParser.Event.START_ARRAY;
        }

        @Override
        public T unmarshal(JsonParser parser, DeserializationContext ctx, Type rtType) {
            Collection<E> container = newCollection((Class<E>) Reflections.typeToClass(rtType, hints));
            Type elementType = rtType instanceof ParameterizedType ?
                    ((ParameterizedType) rtType).getActualTypeArguments()[0] : Object.class;
            int deep = 1;
            while (parser.hasNext()) {
                JsonParser.Event next = parser.next();
                if (next == JsonParser.Event.START_ARRAY) {
                    deep++;
                } else if (next == JsonParser.Event.END_ARRAY) {
                    deep--;
                }
                if (deep == 0) {
                    return (T) container;
                }
                E item = elementDeserializer.deserialize(new PrefetchEventJsonParser(jsonBuilderFactory, parser, next), ctx, elementType);
                container.add(item);
            }
            throw new JsonbException("unexpected state");
        }

        private Collection<E> newCollection(Class<E> klazz) {
            if (NavigableSet.class.equals(klazz) || SortedSet.class.equals(klazz) || TreeSet.class.equals(klazz)) {
                return new TreeSet<>();
            } else if (LinkedHashSet.class.equals(klazz)) {
                return new LinkedHashSet<>();
            } else if (Set.class.equals(klazz) || AbstractSet.class.equals(klazz) || HashSet.class.equals(klazz)) {
                return new HashSet<>();
            } else if (LinkedList.class.equals(klazz)) {
                return new LinkedList<>();
            } else if (Iterable.class.equals(klazz) ||
                    Collection.class.equals(klazz) || AbstractCollection.class.equals(klazz) ||
                    List.class.equals(klazz) || ArrayList.class.equals(klazz)) {
                return new ArrayList<>();
            } else if (Deque.class.equals(klazz) || ArrayDeque.class.equals(klazz)) {
                return new ArrayDeque<>();
            } else if (Queue.class.equals(klazz) || AbstractQueue.class.equals(klazz) || PriorityQueue.class.equals(klazz)) {
                return new PriorityQueue<>();
            }
            return (Collection<E>) JsonbContext.newInstance(klazz);
        }
    }

    static class ArraySerializer<T> extends AbstractSerializer<T[]> {

        private final JsonbSerializer<T> elementSerializer;

        ArraySerializer(Set<Object> recursionReference, JsonbSerializer<T> elementSerializer) {
            super(recursionReference);
            this.elementSerializer = elementSerializer;
        }

        @Override
        protected void writeStart(JsonGenerator generator) {
            generator.writeStartArray();
        }

        @Override
        public void marshall(T[] obj, JsonGenerator generator, SerializationContext ctx) {
            for (T element : obj) {
                if (isNullOrEmpty(element)) {
                    generator.writeNull();
                } else {
                    elementSerializer.serialize(element, generator, ctx);
                }
            }
        }

        protected void writeEnd(JsonGenerator generator) {
            generator.writeEnd();
        }
    }

    static class ArrayDeserializer<T> extends BaseArrayDeserializer<T[]> {

        private final JsonBuilderFactory jsonBuilderFactory;

        private final JsonbDeserializer<T> elementDeserializer;

        ArrayDeserializer(Type[] hints, JsonBuilderFactory jsonBuilderFactory, JsonbDeserializer<T> elementDeserializer) {
            super(hints);
            this.jsonBuilderFactory = jsonBuilderFactory;
            this.elementDeserializer = elementDeserializer;
        }

        @Override
        protected void fill(JsonParser parser, DeserializationContext ctx, T[] array, Class<?> componentType, int index) {
            array[index] = elementDeserializer.deserialize(new PrefetchEventJsonParser(jsonBuilderFactory, parser, last), ctx, componentType);
        }
    }

    @RequiredArgsConstructor
    static class UntypedMapping implements JsonbSerializer<Object>, JsonbDeserializer<Object> {

        private final boolean writeNull;

        private final NumberFormat numberFormat;

        private final String dateFormat;

        private final String locale;

        UntypedMapping() {
            this.writeNull = false;
            this.numberFormat = null;
            this.dateFormat = null;
            this.locale = null;
        }

        @Override
        public void serialize(Object obj, JsonGenerator generator, SerializationContext ctx) {
            if (obj == null) {
                if (writeNull) {
                    generator.writeNull();
                }
                return;
            }
            if (obj.getClass() == Object.class) { // 类型无法推导出，实际对象也为Object类型，视为json空对象，避免死循环
                generator.writeStartObject();
                generator.writeEnd();
                return;
            }
            ((JsonbSerializationContext) ctx).findTypeSerializer(obj.getClass(), writeNull, numberFormat, dateFormat, locale)
                    .serialize(obj, generator, ctx);
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            return deserialize(parser, parser.next());
        }

        Object deserialize(JsonParser parser, JsonParser.Event current) {
            return switch (current) {
                case START_OBJECT -> handleMap(parser);
                case START_ARRAY -> handleArray(parser);
                case VALUE_STRING -> parser.getString();
                case VALUE_NUMBER -> new BigDecimal(parser.getString());
                case VALUE_FALSE -> Boolean.FALSE;
                case VALUE_TRUE -> Boolean.TRUE;
                case VALUE_NULL -> null;
                default -> throw new JsonbException("unexpect json event");
            };
        }

        private Map<String, Object> handleMap(JsonParser parser) {
            Map<String, Object> map = new LinkedHashMap<>(); // predictable iteration order
            JsonParser.Event current;
            while (parser.hasNext() && ((current = parser.next()) !=  JsonParser.Event.END_OBJECT)) {
                if (current != JsonParser.Event.KEY_NAME) {
                    throw new JsonbException("expect event KEY_NAME");
                }
                map.put(parser.getString(), deserialize(parser, null, null));
            }
            return map;
        }

        private List<Object> handleArray(JsonParser parser) {
            List<Object> list = new ArrayList<>();
            JsonParser.Event current;
            while (parser.hasNext() && ((current = parser.next()) != JsonParser.Event.END_ARRAY)) {
                list.add(deserialize(parser, current));
            }
            return list;
        }

    }

    @RequiredArgsConstructor
    static class AdapterSerializer<S, T> implements JsonbSerializer<S> {

        private final JsonbAdapter<S, T> adapter;

        private final JsonbSerializer<T> delegate;

        @Override
        public void serialize(S obj, JsonGenerator generator, SerializationContext ctx) {
            try {
                delegate.serialize(adapter.adaptToJson(obj), generator, ctx);
            } catch (Exception e) {
                throw new JsonbException(e.getMessage(), e);
            }
        }
    }

    @RequiredArgsConstructor
    static class AdapterDeserializer<S, T> implements JsonbDeserializer<S> {

        private final JsonbAdapter<S, T> adapter;

        private final JsonbDeserializer<T> delegate;

        private final Type adaptedType;

        @Override
        public S deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            try {
                return adapter.adaptFromJson(delegate.deserialize(parser, ctx, adaptedType));
            } catch (Exception e) {
                throw new JsonbException(e.getMessage(), e);
            }
        }
    }

    private static class EnumMapping<T extends Enum<T>> extends AbstractDeserializer<T> implements JsonbSerializer<T> {

        private final Stack<Type> hints;

        private final String[] alias;

        private final boolean caseInsensitive;

//        private final boolean ordinal; // 允许序号作为序列化/反序列化值

        EnumMapping(Type[] hints, String[] alias, boolean caseInsensitive) {
            this.hints = new Stack<>();
            for (int i = 0; i < hints.length - 1; i++) { // 最后一个是枚举本身泛型或本身
                this.hints.push(hints[i]);
            }
            this.alias = alias;
            this.caseInsensitive = caseInsensitive;
        }

        @Override
        protected JsonParser.Event getExpected() {
            return JsonParser.Event.VALUE_STRING;
        }

        @Override
        public T unmarshal(JsonParser parser, DeserializationContext ctx, Type rtType) {
            String name = parser.getString();
            Class<T> klazz = (Class<T>) Reflections.typeToClass(rtType, hints);
            T[] constants = klazz.getEnumConstants();
            for (int i = 0; i < alias.length; i++) {
                if (caseInsensitive) {
                    if (alias[i].equalsIgnoreCase(name)) {
                        return constants[i];
                    }
                } else if (alias[i].equals(name)) {
                    return constants[i];
                }
            }
            throw new JsonbException("no such enum value: '" + name + "' for " + rtType);
        }

        @Override
        public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
            generator.write(alias[obj.ordinal()]);
        }
    }

    @RequiredArgsConstructor
    private static class JavaBeanInstantiator<T> {

        private final Executable executable;

        @Getter
        private final Map<String, Integer> argNameIndexPairs; // 如果大小写不敏感？

        @Getter
        private final boolean parametersRequired;

        public T instantiate(Object[] args) {
            executable.setAccessible(true);
            if (executable instanceof Constructor) {
                try {
                    return ((Constructor<T>) executable).newInstance(args);
                } catch (Exception e) {
                    throw new JsonbException(e.getMessage(), e);
                }
            }
            try {
                return (T) ((Method) executable).invoke(executable.getDeclaringClass(), args);
            } catch (Exception e) {
                throw new JsonbException(e.getMessage(), e);
            }
        }
    }

    @RequiredArgsConstructor
    private static class JavaBeanPropertySerializer<D, T>  implements JsonbSerializer<T>, Comparable<JavaBeanPropertySerializer<D, T>> {

        private final String propertyName;

        private final boolean writeNull;

        private final Function<D, T> getter;

        private final JsonbSerializer<T> delegate;

        public T get(D obj) {
            return getter.apply(obj);
        }

        private boolean isOptionalEmpty(T obj) {
            return OptionalInt.empty().equals(obj) ||
                    OptionalLong.empty().equals(obj) ||
                    OptionalDouble.empty().equals(obj) ||
                    Optional.empty().equals(obj);
        }

        @Override
        public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
            log.log(Level.FINE, "serialize property['{0}']", propertyName);
            if (obj == null || isOptionalEmpty(obj)) {
                if (writeNull) {
                    generator.writeNull(propertyName);
                }
                return;
            }
            generator.writeKey(propertyName);
            delegate.serialize(obj, generator, ctx);
        }

        @Override
        public int compareTo(JavaBeanPropertySerializer<D, T> other) {
            return propertyName.compareTo(other.propertyName);
        }
    }

    @RequiredArgsConstructor
    private static class JavaBeanPropertyDeserializer<D, T> implements JsonbDeserializer<T> {

        @Getter
        private final String name;

        @Getter
        private final boolean caseInsensitive;

        @Getter
        private final Type type;

        @Getter
        private final BiConsumer<D, T> setter;

        private final JsonbDeserializer<T> delegate;

        @Override
        public T deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            return delegate.deserialize(parser, ctx, rtType);
        }
    }

    private static class JavaBeanSerializer<T> extends AbstractSerializer<T> {

        @Setter
        private JavaBeanPropertySerializer<T, Object>[] propertySerializers;

        @Setter
        private Polymorphism<String> polymorphism;

        JavaBeanSerializer(Set<Object> recursionReference) {
            super(recursionReference);
        }

        @Override protected void writeStart(JsonGenerator generator) {
            generator.writeStartObject();
        }

        @Override
        public void marshall(T obj, JsonGenerator generator, SerializationContext ctx) {
            Set<String> wroteKeys = new HashSet<>();
            while (polymorphism != null) {
                if (polymorphism.getSubClasses().isEmpty()) {
                    log.log(Level.WARNING, "DO NOT annotate concrete type[{0}] with JsonbTypeInfo and use it as actual value", polymorphism.getType());
                    break;
                }
                for (Map.Entry<String, Polymorphism<String>> entry : polymorphism.getSubClasses().entrySet()) {
                    if (entry.getValue().getType().isInstance(obj)) {
                        wroteKeys.add(polymorphism.getBinding());
                        generator.write(polymorphism.getBinding(), entry.getKey());
                        polymorphism = entry.getValue();
                        break;
                    }
                }
            }
            for (JavaBeanPropertySerializer<T, Object> serializer : propertySerializers) {
                if (!wroteKeys.add(serializer.propertyName)) {
                    throw new JsonbException("polymorphism alias conflict with property name: " + serializer.propertyName);
                }
                serializer.serialize(serializer.get(obj), generator, ctx); // JavaBeanPropertySerializer handle null
            }
        }

        @Override protected void writeEnd(JsonGenerator generator) {
            generator.writeEnd();
        }

    }

    private static class JavaBeanDeserializer<T> extends AbstractDeserializer<T> {

        private JavaBeanInstantiator<T> instantiator;

        private Map<String, JavaBeanPropertyDeserializer<T, ?>> propertyDeserializers; // 反序列化时按流先后顺序进行

        private final boolean caseInsensitive;

        private Set<String> requiredParameters;

        private Object[] args;

        private static final Object UNSET = new Object();

        JavaBeanDeserializer(Type[] hints, boolean caseInsensitive) {
            super(hints);
            this.caseInsensitive = caseInsensitive;
        }

        JavaBeanDeserializer withInstantiator(boolean nonStaticMember, JavaBeanInstantiator<T> instantiator) {
            this.instantiator = instantiator;
            int preserve = nonStaticMember ? 1 : 0;
            this.args =  new Object[preserve + instantiator.argNameIndexPairs.size()]; // 原生类型使用默认值，Optional*类型使用空值Optional*.empty()，其他的则为null
            return this;
        }

        void setPropertyDeserializers(List<JavaBeanPropertyDeserializer<T, ?>> deserializers) {
            this.propertyDeserializers = new HashMap<>(deserializers.size());
            this.requiredParameters = new HashSet<>();
            for (JavaBeanPropertyDeserializer<T, ?> deserializer : deserializers) {
                propertyDeserializers.put(deserializer.getName(), deserializer);
                Integer index = instantiator.argNameIndexPairs.get(deserializer.name);
                if (index != null) {
                    if (instantiator.parametersRequired) {
                        requiredParameters.add(deserializer.getName());
                    } else if (Boolean.TYPE.equals(deserializer.type)){
                        args[index] = false;
                    } else if (Byte.TYPE.equals(deserializer.type)){
                        args[index] = (byte) 0;
                    } else if (Character.TYPE.equals(deserializer.type)){
                        args[index] = '\u0000';
                    } else if (Short.TYPE.equals(deserializer.type)){
                        args[index] = (short) 0;
                    } else if (Integer.TYPE.equals(deserializer.type)){
                        args[index] = 0;
                    } else if (Long.TYPE.equals(deserializer.type)){
                        args[index] = 0L;
                    } else if (Float.TYPE.equals(deserializer.type)){
                        args[index] = 0.0F;
                    } else if (Double.TYPE.equals(deserializer.type)){
                        args[index] = 0.0;
                    } else if (OptionalInt.class.equals(deserializer.type)) {
                        args[index] = OptionalInt.empty();
                    } else if (OptionalLong.class.equals(deserializer.type)) {
                        args[index] = OptionalLong.empty();
                    } else if (OptionalDouble.class.equals(deserializer.type)) {
                        args[index] = OptionalDouble.empty();
                    } else if (Optional.class.equals(Reflections.typeToClass(deserializer.type, this.hints))) {
                        args[index] = Optional.empty();
                    } // else set to null
                }
            }
        }

//        void setDeclaringObject(Object obj) {
//            args[0] = obj;
//        }

        @Override
        protected JsonParser.Event getExpected() {
            return JsonParser.Event.START_OBJECT;
        }

        @Override
        protected T unmarshal(JsonParser parser, DeserializationContext ctx, Type rtType) {
            Optional<T> obj = instantiator.argNameIndexPairs.isEmpty() ? // 非静态内部类，无需设置声明类型：O.I = o.new I()
                    Optional.of(instantiator.instantiate(this.args)) : Optional.empty();
            List<Pair<BiConsumer, Object>> lazyProperties = null;
            if (obj.isEmpty()) {
                if (instantiator.parametersRequired) {
                    Arrays.fill(args, UNSET);
                }
                lazyProperties = new ArrayList<>(propertyDeserializers.size() - instantiator.argNameIndexPairs.size());
            }
            while (parser.hasNext()) {
                last = parser.next();
                if (last == JsonParser.Event.END_OBJECT) {
                    break;
                } else if (last != JsonParser.Event.KEY_NAME) {
                    throw new JsonbException("expect json key, but " + last);
                }
                String jsonPropertyName = parser.getString();

                JavaBeanPropertyDeserializer deserializer = propertyDeserializers.get(jsonPropertyName); // 当反序列化时属性不存在时需保持默认值
                if (deserializer == null && caseInsensitive) {
                    deserializer = findCaseInsensitiveDeserializer(jsonPropertyName);
                }
                if (deserializer == null) {
                    handleUnknownProperty(jsonPropertyName, rtType, parser); // 当json中存在java对象不存在的属性时，直接忽略，而不能视为错误
                    continue;
                } else {
                    requiredParameters.remove(deserializer.getName());
                }
//                if (deserializer.delegate instanceof JavaBeanDeserializer &&
//                        ((JavaBeanDeserializer) deserializer.delegate).isInnerNonStatic()) {
//                    ((JavaBeanDeserializer) deserializer.delegate).setDeclaringObject(obj.get());
//                }
                Object value = deserializer.deserialize(parser, ctx, deserializer.type);
                if (obj.isPresent()) {
                    deserializer.setter.accept(obj.get(), value);
                    continue;
                }
                Integer index = instantiator.argNameIndexPairs.get(jsonPropertyName);
                if (index == null) {
                    lazyProperties.add(new Pair<>(deserializer.setter, value));
                } else {
                    args[index] = value;
                }
            }
            if (!requiredParameters.isEmpty()) {
                throw new JsonbException(instantiator.executable.getDeclaringClass() + " missing required properties: " + requiredParameters);
            }
            if (obj.isPresent()) {
                return obj.get();
            }
            checkRequiredParametersValueAssigned(args);
            T bean = instantiator.instantiate(args);
            for (Pair<BiConsumer, Object> lazySetter : lazyProperties) {
                lazySetter.getLeft().accept(bean, lazySetter.getRight()); // 如果value是null，则必须设置为null或调用setter方法，Optional类型除外
            }
            return bean;
        }

        private JavaBeanPropertyDeserializer<T, ?> findCaseInsensitiveDeserializer(String jsonPropertyName) {
            JavaBeanPropertyDeserializer<T, ?> candidate = null;
            for (JavaBeanPropertyDeserializer<T, ?> caseInsensitiveDeserializer : propertyDeserializers.values()) {
                if (caseInsensitiveDeserializer.getName().equalsIgnoreCase(jsonPropertyName)) {
                    if (candidate == null) {
                        candidate = caseInsensitiveDeserializer;
                    } else {
                        throw new JsonbException("multiply property has same name as case-insensitive");
                    }
                }
            }
            return candidate;
        }

        private void checkRequiredParametersValueAssigned(Object[] args) {
            if (!instantiator.parametersRequired) {
                return;
            }
            for (Object arg : args) {
                if (UNSET.equals(arg)) {
                    throw new JsonbException(instantiator.executable.getDeclaringClass() + " constructor argument missing");
                }
            }
        }

        protected void handleUnknownProperty(String propertyName, Type rtType, JsonParser parser) {
            log.log(Level.FINE, "ignore json key '{0}' for java bean: {1}", new Object[] {propertyName, rtType});
            JsonParser.Event event = parser.next();
            if (event == JsonParser.Event.START_OBJECT) {
                parser.skipObject();
            } else if (event == JsonParser.Event.START_ARRAY) {
                parser.skipArray();
            }
            // or ignore VALUE_*
        }
    }

    @RequiredArgsConstructor
    private static class PolymorphismTypSerializer<T> extends AbstractSerializer<T> {

        private final Polymorphism<String> polymorphism;

        private final boolean writeNull;

        private final NumberFormat numberFormat;

        private final String dateFormat;

        private final String locale;

        @Override
        protected void marshall(T obj, JsonGenerator generator, SerializationContext ctx) {
            if (obj == null) {
                generator.writeStartObject();
                generator.writeEnd();
                return;
            }
            JsonbSerializer<T> concreteSerializer = ((JsonbSerializationContext) ctx).findTypeSerializer(obj.getClass(), writeNull, numberFormat, dateFormat, locale);
            if (concreteSerializer instanceof JavaBeanSerializer beanSerializer) {
                beanSerializer.setPolymorphism(polymorphism);
                beanSerializer.serialize(obj, generator, ctx);
            }
        }

    }

    @RequiredArgsConstructor
    private static class PolymorphismTypeDeserializer<T> extends AbstractDeserializer<T> {

        private final Polymorphism<String> polymorphism;

        private final JsonBuilderFactory jsonBuilderFactory;

        private final NumberFormat numberFormat;

        private final String dateFormat;

        private final Locale locale;

        @Override
        protected JsonParser.Event getExpected() {
            return JsonParser.Event.START_OBJECT;
        }

        @Override
        protected T unmarshal(JsonParser parser, DeserializationContext ctx, Type rtType) {
            Polymorphism<String> current = polymorphism;
            Class<?> concrete = null;
            String key = null;
            while (parser.hasNext()) {
                last = parser.next();
                if (last != JsonParser.Event.KEY_NAME) {
                    throw new JsonbException("expect json key, but " + last);
                }
                key = parser.getString();

                if (!current.getBinding().equals(key)) {
                    if (concrete != null) {
                        break;
                    }
                    throw new JsonbException("require polymorphism key first");
                }
                last = parser.next();
                if (last != JsonParser.Event.VALUE_STRING) {
                    throw new JsonbException("polymorphism alias must be json event VALUE_STRING");
                }
                String value = parser.getString();
                Polymorphism<String> moreConcrete = current.getSubClasses().get(value);
                if (moreConcrete == null) {
                    throw new JsonbException("no such polymorphism type: " + value);
                }
                if (concrete != null && !concrete.isAssignableFrom(moreConcrete.getType())) {
                    throw new JsonbException("[" + moreConcrete + "] not sub class of [" + concrete + "]");
                }
                concrete = moreConcrete.getType();
                current = moreConcrete;
            }
            return (T) ((JsonbDeserializationContext) ctx).findTypeDeserializer(concrete, numberFormat, dateFormat, locale)
                    .deserialize(new PrefetchEventJsonParser(jsonBuilderFactory, parser, last, key), ctx, concrete);
        }
    }


    static class PropertyNamingStrategyFactory {

        static PropertyNamingStrategy getObject(Object namingType) {
            if (!(namingType instanceof String)) {
                throw new IllegalArgumentException("config[" + JsonbConfig.PROPERTY_NAMING_STRATEGY + "] should be type of String or PropertyNamingStrategy");
            }
            NamingConvention java = NamingConvention.LOWER_CAMEL_CASE;
            return switch ((String) namingType) {
                case PropertyNamingStrategy.UPPER_CAMEL_CASE -> propertyName -> java.translate(propertyName, NamingConvention.PASCAL_CASE);
                case PropertyNamingStrategy.LOWER_CASE_WITH_DASHES -> propertyName -> java.translate(propertyName, NamingConvention.KEBAB_CASE);
                case PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES -> propertyName -> java.translate(propertyName, NamingConvention.SNAKE_CASE);
                case PropertyNamingStrategy.UPPER_CAMEL_CASE_WITH_SPACES -> propertyName -> {
                    char[] cs = propertyName.toCharArray();
                    StringBuilder sb = new StringBuilder(cs.length);
                    sb.append(Character.toUpperCase(cs[0]));
                    for (int i = 1; i < cs.length; i++) {
                        if (Character.isUpperCase(cs[i])) {
                            sb.append(' ');
                        }
                        sb.append(cs[i]);
                    }
                    return sb.toString();
                }; // pass through
                case PropertyNamingStrategy.IDENTITY, PropertyNamingStrategy.CASE_INSENSITIVE -> propertyName -> propertyName;
                default -> throw new IllegalArgumentException("unknown PropertyNamingStrategy type");
            };
        }
    }

}
