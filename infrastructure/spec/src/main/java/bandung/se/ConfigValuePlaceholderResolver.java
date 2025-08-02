package bandung.se;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class ConfigValuePlaceholderResolver implements PlaceholderResolver<Config> {

    protected final char[] placeHolderPrefix;

    protected final char[] placeHolderSuffix;

    protected final char[] valueSeparator;

    public ConfigValuePlaceholderResolver() {
        this(DEFAULT_PLACEHOLDER_PREFIX, DEFAULT_PLACEHOLDER_SUFFIX, DEFAULT_VALUE_SEPARATOR);
    }

    public ConfigValuePlaceholderResolver(String placeHolderPrefix, String placeHolderSuffix, String valueSeparator) {
        this.placeHolderPrefix = Objects.requireNonNull(placeHolderPrefix).toCharArray();
        this.placeHolderSuffix = Objects.requireNonNull(placeHolderSuffix).toCharArray();
        this.valueSeparator = Objects.requireNonNull(valueSeparator).toCharArray();
    }

    @Override
    public String resolve(String value, Config config) {
        if (value == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        char[] text = value.toCharArray();
        int offset = 0;
        for (int i = offset; i < text.length; ) {
            if (!regionMatch(text, i, placeHolderPrefix)) {
                i++;
                continue;
            }
            result.append(text, offset, i - offset);
            StringBuilder sb = new StringBuilder();
            int suffixPosition = findPlaceholderEndIndex(config, text, i, sb);
//            result.append(placeHolderPrefix);
            result.append(sb);
            if (suffixPosition == -1) {
                offset = text.length; // mark
                break;
            }
            i = suffixPosition + placeHolderSuffix.length;
            offset = i;
        }
        if (offset == 0) {
            result.append(text);
        } else {
            result.append(text, offset, text.length - offset);
        }

        return result.toString();
    }

    protected String resolve(Config config, String key, String defaultValue) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("empty key: " + key);
        }
        Optional<String> propValue = config.getOptionalValue(key, String.class);
        if (!propValue.isPresent() && defaultValue == null) {
            throw new NoSuchElementException(key);
        }
        return propValue.orElse(defaultValue);
    }

    protected int findPlaceholderEndIndex(Config config, char[] text, int offset, StringBuilder result) {
        int prefixPosition = offset + placeHolderPrefix.length;
        int defaultValuePosition = -1, suffixPosition = -1, valueSuffixPosition = -1;
        StringBuilder keyBuilder = new StringBuilder(),  valueBuilder = new StringBuilder();
        for (int i = prefixPosition; i < text.length;) {
            if (regionMatch(text, i, placeHolderPrefix)) {
                if (i > prefixPosition) {
                    int keyEnd = i;
                    if (defaultValuePosition != -1) { // ${key:prefix${defaultValue}}
                        valueBuilder.append(text, defaultValuePosition + valueSeparator.length, i - defaultValuePosition - valueSeparator.length);
                        keyEnd = defaultValuePosition;
                    }
                    if (suffixPosition == -1){ //  // ${key:prefix${defaultValue}}
                        keyBuilder.append(text, prefixPosition, keyEnd - prefixPosition);
                    } else { // ${prefix.${value}.suffix}, ${prefix.${value}.suffix:${default:value}}
                        keyBuilder.append(text, suffixPosition + placeHolderSuffix.length, keyEnd -suffixPosition - placeHolderSuffix.length);
                    }
                }
                StringBuilder placeholder = new StringBuilder();
                int position = findPlaceholderEndIndex(config, text, i, placeholder);
                if (defaultValuePosition != -1) { // ${key:prefix${value
                    valueBuilder.append(placeholder);
                    valueSuffixPosition = position;
                } else {
                    keyBuilder.append(placeholder);
                    suffixPosition = position;
                }
                if (position == -1) {
                    break;
                }
                i = suffixPosition + placeHolderSuffix.length;
                continue;
            } else if (regionMatch(text, i, placeHolderSuffix)) {
                String key, defaultValue = null;
                if (suffixPosition == -1) {
                    if (defaultValuePosition == -1) { // ${value}
                        key = new String(text, prefixPosition, i - prefixPosition);
                    } else { // ${key:value}
                        key = new String(text, prefixPosition, defaultValuePosition - prefixPosition);
                        defaultValue = new String(text, defaultValuePosition + valueSeparator.length, i - defaultValuePosition - valueSeparator.length);
                    }
                } else {
                    if (defaultValuePosition == -1) { // ${prefix.${value}.suffix}
                        key = keyBuilder.append(text, suffixPosition + placeHolderSuffix.length, i -suffixPosition - placeHolderSuffix.length).toString();
                    } else if (valueSuffixPosition != -1) { // ${key.${placeholder}.suffix:prefix.${default:value}.suffix}
                        key = keyBuilder.append(text, suffixPosition + placeHolderSuffix.length, defaultValuePosition - suffixPosition - placeHolderSuffix.length).toString();
                        defaultValue = valueBuilder.append(text, valueSuffixPosition + placeHolderSuffix.length, i - valueSuffixPosition - placeHolderSuffix.length).toString();
                    } else { // ${key.${placeholder}.suffix:default}, ${key.${placeholder}.suffix:default}
                        key = keyBuilder.append(text, suffixPosition + placeHolderSuffix.length, defaultValuePosition - suffixPosition - placeHolderSuffix.length).toString();
                        defaultValue = new String(text, defaultValuePosition + valueSeparator.length, i - defaultValuePosition - valueSeparator.length);
                    }
                }
                result.append(resolve(config, key, defaultValue));
                return i;
            } else if (defaultValuePosition == -1 && regionMatch(text, i, valueSeparator)) {
                defaultValuePosition = i;
            }
            i++;
        }
        if (suffixPosition == -1) {
            if (keyBuilder.length() > 0) { // ${prefix${value, ${prefix:val${ue
                result.append(placeHolderPrefix);
            }
            result.append(keyBuilder);
            if (valueBuilder.length() > 0) {
                result.append(valueSeparator);
            }
            result.append(valueBuilder);
        }
        if (keyBuilder.length() == 0) {
            result.append(text, offset, text.length - offset);
        }
        return suffixPosition;
    }

    private boolean regionMatch(char[] full, int start, char[] partial) {
        for (int i = start, j = 0; i < full.length && j < partial.length; i++, j++) {
            if (full[i] != partial[j]) {
                return false;
            }
        }
        return true;
    }

}
