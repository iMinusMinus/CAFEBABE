package bandung.se;

import bandung.ee.CommonAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 应用配置
 * @see org.eclipse.microprofile.config.Config
 *
 * @author iMinusMinus
 * @date 2024-05-04
 */
public class Config implements ConfigMXBean {

    private static final Logger log = Logger.getLogger(Config.class.getName());

    protected final Map<Class, List<Converter>> converters;

    protected final Map<Class, Converter> implicitConverters = new ConcurrentHashMap<>();

    protected final List<ConfigSource> sources;

    protected final boolean expand;

    protected final PlaceholderResolver<Config> resolver;

    private Config(List<ConfigSource> sources, Map<Class, List<Converter>> converters, boolean expand, PlaceholderResolver<Config> resolver) {
        this.sources = sources;
        this.converters = converters;
        this.expand = expand;
        this.resolver = resolver;
    }

    /**
     * 获取配置
     * @param propertyName 配置项
     * @param propertyType 配置项值类型
     * @throws java.util.NoSuchElementException 如果配置值不存在
     * @throws java.lang.IllegalArgumentException 如果配置值无法转换成指定类型
     * @return 转换后的配置值
     */
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        return getOptionalValue(propertyName, propertyType)
                .orElseThrow(() -> new NoSuchElementException("no such config property: " + propertyName));
    }

    public <T> List<T> getValues(String propertyName, Class<T> propertyType) {
        Optional<List<T>> values = getOptionalValues(propertyName, propertyType);
        if (!values.isPresent() || values.get().isEmpty()) {
            throw new NoSuchElementException("no such config property: " + propertyName);
        }
        return values.get();
    }

    /**
     * 从配置源获取配置项，可被直接作为依赖注入
     * @param propertyName 配置项key
     * @return 配置项，如果对应的配置项不存在，返回值也不为null
     */
    public ConfigSource.ConfigValue getConfigValue(String propertyName) {
        throw new UnsupportedOperationException();
    }

    private Optional<String> getAndResolve(String propertyName) {
        String value = null;
        for (ConfigSource configSource : sources) {
            value = configSource.getValue(propertyName);
            if (value != null) {
                log.log(Level.FINER, "found {0} at {1}", new Object[] {propertyName, configSource.getName()});
                break;
            }
        }
        if (value == null || value.isEmpty()) {
            return Optional.empty();
        }
        if (expand) {
            value = resolver.resolve(value, this); // 0. value, 1. ${value:default}, 2. ${value${include}}, 3. $ENC(jasypt) or ${alg-mod-padding::smallrye}
        }
        return Optional.ofNullable(value);
    }

    /**
     * 获取指定配置项并转换成指定类型
     * @param propertyName 配置项key
     * @param propertyType 配置项值转换后类型
     */
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        Optional<String> value = getAndResolve(propertyName);
        if (!value.isPresent()) {
            return Optional.empty();
        }
        boolean isArray = propertyType.isArray();
        Class<?> target = propertyType;
        if (isArray) {
            target = propertyType.getComponentType();
        }
        Converter<String, ?> converter = getConverter(target)
                .orElseThrow(() -> new IllegalArgumentException("cannot convert property[" + propertyName + "] to type: " + propertyType));
        if (isArray) {
            List<String> values = parseValues(value.get());
            Object array = Array.newInstance(target, values.size());
            for (int i = 0; i < values.size(); i++) {
                Array.set(array, i, converter.convert(values.get(i)));
            }
            return (Optional<T>) Optional.of(array);
        }
        return (Optional<T>) value.map(converter::convert);
    }

    private List<String> parseValues(String value) {
        List<String> list = new ArrayList<>();
        boolean escape = false;
        int position = 0;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '\\') { // xxx Properties remove all '\', this condition never happen
                escape = !escape; // #, !, =, :
            } else if (value.charAt(i) == ',' && !escape) {
                int start = position == 0 ? 0 : position + 1;
                if (i > start) { // ignore empty value: 0,,1
                    list.add(value.substring(start, i));
                }
                position = i;
            }
        }
        if (position < value.length()) {
            String item = value.substring(position + 1).trim();
            if (item.length() > 0) {
                list.add(item);
            }
        }
        return list;
    }

    public <T> Optional<List<T>> getOptionalValues(String propertyName, Class<T> propertyType) {
        List<String> values = null;
        for (ConfigSource configSource : sources) {
            if (configSource instanceof PropertiesConfigSource) {
                values = ((PropertiesConfigSource) configSource).getIndexedProperty(propertyName);
                if (values != null) {
                    log.log(Level.FINER, "found index property {0} at {1}", new Object[] {propertyName, configSource.getName()});
                    break;
                }
            }
            String value = configSource.getValue(propertyName);
            if (value != null) {
                log.log(Level.FINER, "found {0} at {1}", new Object[] {propertyName, configSource.getName()});
                values = parseValues(value);
                break;
            }
        }
        if (values == null) {
            return Optional.empty();
        }
        Converter<String, T> converter = getConverter(propertyType)
                .orElseThrow(() -> new IllegalArgumentException("cannot convert property[" + propertyName + "] to type: " + propertyType));
        Function<String, String> expandFunc = s -> resolver.resolve(s, this); // 0. value, 1. ${value:default}, 2. ${value${include}}, 3. $ENC(jasypt) or ${alg-mod-padding::smallrye}
        Function<String, T> func = expand ? expandFunc.andThen(converter::convert) : converter::convert;
        List<T> list = values.stream().map(func).collect(Collectors.toList());
        return Optional.of(list);
    }

    /**
     * 获取配置源所有配置项key。（规范未定义顺序以及重复如何处理）
     */
    public Iterable<String> getPropertyNames() {
        Set<String> keys = new HashSet<>();
        for (ConfigSource configSource : sources) {
            keys.addAll(configSource.getPropertyNames());
        }
        return keys;
    }

    /**
     * 获取配置源
     */
    public Iterable<ConfigSource> getConfigSources() {
        return sources;
    }

    /**
     * 获取类型转换器
     * @param forType 类型
     * @return 类型转换器
     */
    public <T> Optional<Converter<String, T>> getConverter(Class<T> forType) {
        List<Converter> typeConverters = converters.get(forType);
        if (typeConverters != null) {
            return Optional.of(typeConverters.get(typeConverters.size() - 1)); // 转换器数值越大，优先级越高
        }
        // 衍生的转换器无优先级
        return Optional.ofNullable(implicitConverters.computeIfAbsent(forType, this::deriveImplicitConverter));
    }

    public <T> T unwrap(Class<T> type) {
        throw new IllegalArgumentException();
    }

    protected <T> Converter<String, T> deriveImplicitConverter(Class<T> forType) {
        Method[] methods = forType.getDeclaredMethods();
        Method valueOfMethod = null;
        Method parseMethod = null;
        for (Method method : methods) {
            if (!isCandidateConverterMethod(forType, method)) {
                continue;
            }
            if ("of".equals(method.getName()) && String.class.equals(method.getParameterTypes()[0])) {
                log.log(Level.FINE, "find 'public static T of(String)' method as implicit converter");
                return asConverter(method);
            } else if ("valueOf".equals(method.getName()) && String.class.equals(method.getParameterTypes()[0])) {
                valueOfMethod = method;
            } else if ("parse".equals(method.getName()) && CharSequence.class.equals(method.getParameterTypes()[0])) {
                parseMethod = method;
            }
        }
        if (valueOfMethod != null) {
            log.log(Level.FINE, "find 'public static T valueOf(String)' method as implicit converter");
            return asConverter(valueOfMethod);
        }
        if (parseMethod != null) {
            log.log(Level.FINE, "find 'public static T parse(CharSequence)' method as implicit converter");
            return asConverter(parseMethod);
        }
        Constructor<?>[] ctors = forType.getDeclaredConstructors();
        for (Constructor<?> ctor : ctors) {
            if (Modifier.isPublic(ctor.getModifiers()) &&
                    ctor.getParameterCount() == 1 && String.class.equals(ctor.getParameterTypes()[0])) {
                return asConverter(ctor);
            }
        }
        return str -> {throw new IllegalArgumentException();}; // avoid compute again
    }

    private <T> Converter<String, T> asConverter(Method method) {
        return args -> {
            try {
                return (T) method.invoke(method.getDeclaringClass(), args);
            } catch (Exception e) {
                throw new IllegalArgumentException("invoke implicit convert method: " + method.getName(), e);
            }
        };
    }

    private <T> Converter<String, T> asConverter(Constructor ctor) {
        return args -> {
            try {
                return (T) ctor.newInstance(args);
            } catch (Exception e) {
                throw new IllegalArgumentException("invoke constructor as implicit converter", e);
            }
        };
    }

    private <T> boolean isCandidateConverterMethod(Class<T> forType, Method method) {
        return Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers()) &&
                method.getReturnType().equals(forType) &&
                method.getParameterCount() == 1;
    }

    public static final String CONFIG_MXBEAN_NAME = "bandung.se:type=Config";

    @Override
    public String getProperty(String name) {
        return getOptionalValue(name, String.class).orElse(null);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * 配置构建辅助类
     * @see org.eclipse.microprofile.config.spi.ConfigBuilder
     */
    public static class Builder {

        private static final Logger log = Logger.getLogger(Builder.class.getName());

        /**
         * mp-config支持单个，spring支持多个
         */
        public static final String PROFILE = "mp.config.profile";

        /**
         * 低版本mp-config不支持扩展（即配置值含有占位符，需替换成引用的配置值），默认启用
         */
        public static final String PROPERTY_EXPRESSIONS_ENABLED = "mp.config.property.expressions.enabled";

        protected static final int BUILT_IN_PRIORITY = 1;

        protected final List<ConfigSource> sources = new ArrayList<>();

        protected final Map<Class, List<Converter>> converters = new HashMap<>();

        protected PlaceholderResolver<Config> placeholderResolver;

        protected String configFile = "META-INF/microprofile-config.properties";

        protected String profileFileFormat = "META-INF/microprofile-config-%s.properties";

        protected Set<String> activeProfiles = new HashSet<>();

        protected boolean disableExpand;

        private Properties transformAndRemoveProfilePrefix(Properties properties) {
            String profile = properties.getProperty(PROFILE);
            if (profile != null) {
                activeProfiles.add(profile);
            }

            Properties map = new Properties();
            Set<String> profiledKeys = new HashSet<>();
            Set<String> names = properties.stringPropertyNames();
            for (String name : names) {
                if (profiledKeys.contains(name)) {
                    continue;
                }
                if (!name.startsWith("%")) {
                    map.put(name, properties.getProperty(name));
                    continue;
                }
                int dotPosition = name.indexOf(".");
                if (dotPosition <= 0 || dotPosition == name.length() - 1) {
                    map.put(name, properties.getProperty(name));
                    continue;
                }
                String maybe = name.substring(1, dotPosition);
                for (String active : activeProfiles) {
                    if (active.equals(maybe)) {
                        String newKey = name.substring(dotPosition + 1);
                        if (!profiledKeys.add(newKey)) {
                            throw new RuntimeException("multi active profile with same key: " + newKey);
                        }
                        Object old = map.put(newKey, properties.getProperty(name));
                        if (old != null) {
                            log.log(Level.WARNING, "found duplicate key[{0}]", newKey);
                        }
                    }
                }
                // remove inactive property?
            }
            return map;
        }

        /**
         * 添加默认的配置源：classpath下指定文件、环境变量、系统属性
         */
        public Builder addDefaultSources() {
            if (activeProfiles.isEmpty()) {
                String profile = System.getProperty(PROFILE, System.getenv("MP_CONFIG_PROFILE"));
                if (profile != null) {
                    activeProfiles.add(profile);
                }
            }

            ConfigSource sysProps = new PropertiesConfigSource("sys", System.getProperties(), 400);
            sources.add(sysProps);
            ConfigSource envProps = new EnvConfigSource(System.getenv());
            sources.add(envProps);

            try {
                addConfigSource(configFile, ConfigSource.DEFAULT_ORDER, this::transformAndRemoveProfilePrefix); // %profile.prop = value --> prop = value
            } catch (IOException ioe) {
                log.log(Level.SEVERE, ioe.getMessage(), ioe);
            }

            for (String activeProfile : activeProfiles) {
                try {
                    addConfigSource(String.format(profileFileFormat, activeProfile), ConfigSource.DEFAULT_ORDER - 50, Function.identity()); // 优先级高于默认配置源
                } catch (IOException ioe) {
                    log.log(Level.SEVERE, ioe.getMessage(), ioe);
                }
            }
            Collections.sort(sources);

            // .env?
            return this;
        }

        private void addConfigSource(String file, int order, Function<Properties, Properties> transform) throws IOException {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(file);
            while (urls.hasMoreElements()) {
                URL next = urls.nextElement();
                try (InputStream is = next.openStream()) {
                    Properties config = new Properties();
                    config.load(is);
                    ConfigSource fileProps = new PropertiesConfigSource("property-file " + next, transform.apply(config), order);
                    sources.add(fileProps);
                }
            }
        }

        /**
         * 读取META-INF/services/org.eclipse.microprofile.config.spi.ConfigSource，加载配置源
         */
        public Builder addDiscoveredSources() {
            log.log(Level.WARNING, "this implementation doesn't support SPI load ConfigSource");
            return this;
        }

        /**
         * 读取META-INF/services/org.eclipse.microprofile.config.spi.Converter。加载配置值转换器
         */
        public Builder addDiscoveredConverters() {
            log.log(Level.WARNING, "this implementation doesn't support SPI load Converter");
            return this;
        }

        /**
         * 类加载器绑定的配置构建对象
         * @param loader 类加载器
         */
        public Builder forClassLoader(ClassLoader loader) {
            log.log(Level.WARNING, "this implementation doesn't support associate with ClassLoader");
            return this;
        }

        /**
         * 添加配置源
         * @param sources 配置源
         */
        public Builder withSources(ConfigSource... sources) {
            Collections.addAll(this.sources, sources);
            return this;
        }

        public Builder withConverters(Converter<String, ?>... converters) {
            for (Converter<String, ?> converter : converters) {
                Stack<Type> stack = new Stack<>();
                stack.push(converter.getClass());
                Class target = Reflections.typeToClass(Reflections.findActualTypes(converter.getClass(), Converter.class)[1], stack);
                withConverter(target, CommonAnnotations.getPriority(converter, Converter.DEFAULT_PRIORITY), converter);
            }
            return this;
        }

        /**
         * 添加类型转换器（无需通过反射获取转换类型，通常用于添加lambda转换器）
         * @param type 配置值类型
         * @param priority 转换器优先级
         * @param converter 转换器
         */
        public <T> Builder withConverter(Class<T> type, int priority, Converter<String, T> converter) {
            converters.compute(type, (t, c) -> {
                if (c == null) {
                    c = new ArrayList<>();
                }
                c.add(new Converter<String, T>() {
                    @Override
                    public int getPriority() {
                        return priority;
                    }

                    @Override
                    public T convert(String source) throws IllegalArgumentException, NullPointerException {
                        return converter.convert(source);
                    }

                });
                return c;
            });
            return this;
        }

        public Config build() {
            addGlobalConverters();
            for (Map.Entry<Class, List<Converter>> entry : converters.entrySet()) {
                Collections.sort(entry.getValue());
            }
            if (!disableExpand) { // 未设置
                String value = System.getProperty(PROPERTY_EXPRESSIONS_ENABLED);
                if (String.valueOf(Boolean.FALSE).equals(value)) {
                    this.disableExpand = true;
                }
            }
            if (!disableExpand && placeholderResolver == null) {
                addResolver(new ConfigValuePlaceholderResolver());
            }
            return new Config(sources, converters, !disableExpand, placeholderResolver);
        }

        protected Builder addGlobalConverters() {
            return withConverter(boolean.class, BUILT_IN_PRIORITY, Converter.TO_BOOL)
                    .withConverter(Boolean.class, BUILT_IN_PRIORITY, Converter.TO_BOOL)
                    .withConverter(byte.class, BUILT_IN_PRIORITY, Converter.TO_BYTE)
                    .withConverter(Byte.class, BUILT_IN_PRIORITY, Converter.TO_BYTE)
                    .withConverter(short.class, BUILT_IN_PRIORITY, Converter.TO_SHORT)
                    .withConverter(Short.class, BUILT_IN_PRIORITY, Converter.TO_SHORT)
                    .withConverter(int.class, BUILT_IN_PRIORITY, Converter.TO_INT)
                    .withConverter(Integer.class, BUILT_IN_PRIORITY, Converter.TO_INT)
                    .withConverter(OptionalInt.class, BUILT_IN_PRIORITY, Converter.TO_INT.andThen(OptionalInt::of))
                    .withConverter(long.class, BUILT_IN_PRIORITY, Converter.TO_LONG)
                    .withConverter(Long.class, BUILT_IN_PRIORITY, Converter.TO_LONG)
                    .withConverter(OptionalLong.class, BUILT_IN_PRIORITY, Converter.TO_LONG.andThen(OptionalLong::of))
                    .withConverter(float.class, BUILT_IN_PRIORITY, Converter.TO_FLOAT)
                    .withConverter(Float.class, BUILT_IN_PRIORITY, Converter.TO_FLOAT)
                    .withConverter(double.class, BUILT_IN_PRIORITY, Converter.TO_DOUBLE)
                    .withConverter(Double.class, BUILT_IN_PRIORITY, Converter.TO_DOUBLE)
                    .withConverter(OptionalDouble.class, BUILT_IN_PRIORITY, Converter.TO_DOUBLE.andThen(OptionalDouble::of))
                    .withConverter(Class.class, BUILT_IN_PRIORITY, Converter.TO_CLASS)
                    .withConverter(InetAddress.class, BUILT_IN_PRIORITY, Utils::parseIpAddress)
                    .withConverter(UUID.class, BUILT_IN_PRIORITY, Converter.TO_UUID)
                    .withConverter(Currency.class, BUILT_IN_PRIORITY, Converter.TO_CURRENCY)
                    .withConverter(Locale.class, BUILT_IN_PRIORITY, Converter.TO_LOCALE)
                    .withConverter(Pattern.class, BUILT_IN_PRIORITY, Converter.TO_PATTERN)
                    .withConverter(Path.class, BUILT_IN_PRIORITY, Converter.TO_URI.andThen(Paths::get))
                    .withConverter(URI.class, BUILT_IN_PRIORITY, Converter.TO_URI)
                    .withConverter(URL.class, BUILT_IN_PRIORITY, Converter.TO_URL)
                    .withConverter(Duration.class, BUILT_IN_PRIORITY, Converter.TO_DURATION)
                    .withConverter(Period.class, BUILT_IN_PRIORITY, Converter.TO_PERIOD)
                    .withConverter(Instant.class, BUILT_IN_PRIORITY, Converter.TO_INSTANT)
                    .withConverter(LocalDate.class, BUILT_IN_PRIORITY, Converter.TO_LOCAL_DATE)
                    .withConverter(LocalDateTime.class, BUILT_IN_PRIORITY, Converter.TO_LOCAL_DATE_TIME)
                    .withConverter(ZoneId.class, BUILT_IN_PRIORITY, Converter.TO_ZONE_ID)
                    .withConverter(TimeZone.class, BUILT_IN_PRIORITY, Converter.TO_TIMEZONE)
                    .withConverter(OffsetDateTime.class, BUILT_IN_PRIORITY, Converter.TO_OFFSET_DATE_TIME)
                    .withConverter(ZonedDateTime.class, BUILT_IN_PRIORITY, Converter.TO_ZONED_DATE_TIME);
        }

        public Builder addResolver(PlaceholderResolver<Config> resolver) {
            this.placeholderResolver = resolver;
            return this;
        }

        public Builder active(String... profiles) {
            if (!sources.isEmpty()) {
                throw new IllegalStateException("please active profiles before add config source");
            }
            Collections.addAll(activeProfiles, profiles);
            return this;
        }

        public Builder disableExpand() {
            this.disableExpand = true;
            return this;
        }
    }

    private static class EnvConfigSource extends ConfigSource {

        private final Map<String ,String> env;

        EnvConfigSource(Map<String ,String> env) {
            super("env", 300);
            this.env = env;
        }

        @Override
        public Set<String> getPropertyNames() {
            return env.keySet();
        }

        @Override
        public String getValue(String propertyName) { // k.e.y >> k_e_y >> K_E_Y
            if (propertyName == null) {
                return null;
            }
            String value = env.get(propertyName);
            if (value == null) {
                String name = propertyName.replaceAll("[./]", "_");
                value = env.get(name);
                if (value == null) {
                    value = env.get(name.toUpperCase());
                }
            }
            return value;
        }
    }

    public static class PropertiesConfigSource extends ConfigSource {

        protected final Properties properties;

        protected final Map<String, List<String>> indexedProperties;

        public PropertiesConfigSource(String name, Properties properties, Integer order) {
            super(name, order);
            this.properties = properties;
            this.indexedProperties = new HashMap<>();
            processIndexProperties();
        }

        List<String> getIndexedProperty(String key) {
            return indexedProperties.get(key);
        }

        protected void processIndexProperties() {
            Set<String> keys = properties.stringPropertyNames();
            Map<String, Map<Integer, String>> tmp =  new HashMap<>();
            for (String key : keys) {
                processIndexProperty(tmp, key, properties.getProperty(key));
            }
            for (Map.Entry<String, Map<Integer, String>> entry : tmp.entrySet()) {
                int length = entry.getValue().size();
                List<String> values = new ArrayList<>(length);
                for (int i = 0; i < length; i++) {
                    String value = entry.getValue().get(i);
                    if (value == null) {
                        throw new IllegalArgumentException("missing key[" + entry.getKey() + "] index: " + i);
                    }
                    values.add(value);
                }
                indexedProperties.put(entry.getKey(), values);
            }
        }

        private void processIndexProperty(Map<String, Map<Integer, String>> map, String key, String value) {
            int left = key.indexOf("[");
            int right = key.indexOf("]");
            if (left <= 0 || right < left) { // k.e.y --> DO NOTHING
                return;
            }
            if (right + 1 != key.length()) { // k.e[i].y 形式暂时不处理
                return;
            }
            int index;
            try {
                index = Integer.parseInt(key.substring(left + 1, right));
            } catch (NumberFormatException nfe) {
                log.log(Level.CONFIG, "config key[{0}] not index key as invalid number", key);
                return;
            }
            String keyPrefix = key.substring(0, left);
            String old = map.computeIfAbsent(keyPrefix, k -> new HashMap<>()).put(index, value);
            if (old != null) {
                throw new IllegalArgumentException("duplicate index on key: " + key);
            }
//            Map<Integer, Object> kv = map.computeIfAbsent(keyPrefix, k -> new HashMap<>());
//            if (right + 1 >= key.length()) { // k.e.y[i] --> k.e.y=[,,]; k.e[i].y[j] --> k.e=[{y:[]}]
//                kv.put(index, value);
//            } else {
//                String shortKey = key.substring(right + 1); // key[i].k1, key[i].another[j].k
//                Map<String, Map<Integer, Object>> v = (Map<String, Map<Integer, Object>>) kv.computeIfAbsent(index, k -> new HashMap<String, Map<Integer, Object>>());
//                processIndexProperty(v, shortKey, value);
//            }
        }



        @Override
        public Set<String> getPropertyNames() {
            return properties.stringPropertyNames();
        }

        @Override
        public String getValue(String propertyName) {
            return properties.getProperty(propertyName);
        }
    }

}
