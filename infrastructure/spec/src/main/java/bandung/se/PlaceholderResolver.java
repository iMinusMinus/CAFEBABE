package bandung.se;

public abstract class PlaceholderResolver<CONTEXT> {

    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";
    public static final String DEFAULT_VALUE_SEPARATOR = ":";

    protected final String placeHolderPrefix;

    protected final String placeHolderSuffix;

    protected final String valueSeparator;

    public PlaceholderResolver() {
        this(DEFAULT_PLACEHOLDER_PREFIX, DEFAULT_PLACEHOLDER_SUFFIX, DEFAULT_VALUE_SEPARATOR);
    }

    public PlaceholderResolver(String placeHolderPrefix, String placeHolderSuffix, String valueSeparator) {
        this.placeHolderPrefix = placeHolderPrefix;
        this.placeHolderSuffix = placeHolderSuffix;
        this.valueSeparator = valueSeparator;
    }

    public abstract String resolve(String value, CONTEXT context);
}
