package bandung.se;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * 配置源可以是配置文件、系统属性、环境变量等
 *
 * @see org.eclipse.microprofile.config.spi.ConfigSource
 */
public abstract class ConfigSource implements Comparable<ConfigSource> {

    static final String ORDINARY_KEY = "config_ordinal";

    public static final int DEFAULT_ORDER = 100;

    protected final String name;

    protected final Integer order;

    public ConfigSource(String name) {
        this(name, null);
    }

    ConfigSource(String name, Integer order) {
        this.name = Objects.requireNonNull(name);
        this.order = order;
    }

    /**
     *
     * @return 配置源中的配置项快照
     */
    public abstract Set<String> getPropertyNames();

    public int getOrdinal() {
        String configOrdinal = getValue(ORDINARY_KEY);
        if (configOrdinal != null) {
            try {
                return Integer.parseInt(configOrdinal);
            } catch (NumberFormatException ignored) {

            }
        }
        if (order != null) {
            return order;
        }
        return DEFAULT_ORDER;
    }

    /**
     * 获取配置源中指定键的值
     * @param propertyName 配置属性
     * @return 配置值
     */
    public abstract String getValue(String propertyName);

    /**
     * 配置源名称，如"property-file mylocation/myprops.properties"
     * @return config source name
     */
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(ConfigSource o) {
        int value = Integer.compare(getOrdinal(), o.getOrdinal());
        return value != 0 ? value : name.compareTo(o.name);
    }

    /**
     * 配置项
     * @see org.eclipse.microprofile.config.ConfigValue
     */
    @RequiredArgsConstructor
    @Getter
    public class ConfigValue implements Serializable {

        private static final long serialVersionUID = 4217628243507027492L;

        /**
         * 配置项名称
         */
        private final String name;

        /**
         * 配置项原始值
         */
        private final String rawValue;

        /**
         * 配置项扩展值（如果不支持扩展，则value和rawValue始终相同）
         */
        private String value;

        public String getSourceName() {
            return ConfigSource.this.name;
        }

        public int getSourceOrdinal() {
            return ConfigSource.this.getOrdinal();
        }
    }
}
