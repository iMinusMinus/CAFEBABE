package bandung.se;

import java.util.NoSuchElementException;
import java.util.Optional;

public class ConfigValuePlaceholderResolver extends PlaceholderResolver<Config> {

    public ConfigValuePlaceholderResolver() {
        super();
    }

    public ConfigValuePlaceholderResolver(String placeHolderPrefix, String placeHolderSuffix, String valueSeparator) {
        super(placeHolderPrefix, placeHolderSuffix, valueSeparator);
    }

    @Override
    public String resolve(String value, Config config) {
        if (value == null) {
            return null;
        }
        int start = value.indexOf(placeHolderPrefix);
        int end = value.lastIndexOf(placeHolderSuffix);
        if (start < 0 || end < start) {
            return value;
        }
        int nestStart = value.indexOf(placeHolderPrefix, start + placeHolderPrefix.length());
        int nestEnd = value.lastIndexOf(placeHolderSuffix, end - placeHolderSuffix.length());
        if (nestStart > 0 && nestEnd > nestStart) {
            String nest = resolve(value.substring(nestStart, nestEnd + placeHolderSuffix.length()), config);
            return resolve(value.substring(0, nestStart) + nest + value.substring(nestEnd + placeHolderSuffix.length()), config);
        }
        int colon = value.indexOf(valueSeparator, start);
        String newKey;
        String defaultValue = null;
        if (colon > 0 && colon < end) {
            newKey = value.substring(start + placeHolderPrefix.length(), colon);
            defaultValue = value.substring(colon + 1, end).trim();
        } else {
            newKey = value.substring(start + placeHolderPrefix.length(), end);
        }
        Optional<String> newValue = config.getOptionalValue(newKey, String.class);
        if (!newValue.isPresent() && defaultValue == null) {
            throw new NoSuchElementException(newKey);
        }
        return value.substring(0, start) + newValue.orElse(defaultValue) + value.substring(end + placeHolderSuffix.length());
    }
}
