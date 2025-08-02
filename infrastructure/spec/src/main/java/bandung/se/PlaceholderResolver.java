package bandung.se;

@FunctionalInterface
public interface PlaceholderResolver<CONTEXT> {

    String DEFAULT_PLACEHOLDER_PREFIX = "${";
    String DEFAULT_PLACEHOLDER_SUFFIX = "}";
    String DEFAULT_VALUE_SEPARATOR = ":";

    String resolve(String value, CONTEXT context);
}
