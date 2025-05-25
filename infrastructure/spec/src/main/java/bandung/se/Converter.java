package bandung.se;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 类型转换器
 *
 * @see org.springframework.core.convert.converter.Converter
 * @see org.eclipse.microprofile.config.spi.Converter
 */
@FunctionalInterface
public interface Converter<S, T> extends Comparable<Converter<S, T>> {

    int DEFAULT_PRIORITY = 100;

    default int getPriority() {
        return DEFAULT_PRIORITY;
    }

    T convert(S source) throws IllegalArgumentException, NullPointerException;

    default <U> Converter<S, U> andThen(Converter<? super T, ? extends U> after) {
        Objects.requireNonNull(after);
        return (S s) -> {
            T t = convert(s);
            return Optional.ofNullable(t).map(after::convert).orElse(null);
        };
    }

    default int compareTo(Converter<S, T> other) {
        return Integer.compare(getPriority(), other.getPriority());
    }

    Converter<String, Boolean> TO_BOOL = str -> Stream.of("true", "yes", "y", "on", "1").anyMatch(t -> t.equalsIgnoreCase(str));
    Converter<String, Byte> TO_BYTE = Byte::parseByte;
    Converter<String, Short> TO_SHORT = Short::parseShort;
    Converter<String, Integer> TO_INT = Integer::parseInt;
    Converter<String, Long> TO_LONG = Long::parseLong;
    Converter<String, Float> TO_FLOAT = Float::parseFloat;
    Converter<String, Double> TO_DOUBLE = Double::parseDouble;

    Converter<String, Class> TO_CLASS = klazz -> {
        try {
            return Class.forName(klazz);
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException(cnfe.getMessage(), cnfe);
        }
    };

    Converter<String, URI> TO_URI = URI::create;
    Converter<String, URL> TO_URL = str -> {
        try {
            return new URL(str);
        } catch (MalformedURLException mue) {
            throw new IllegalArgumentException(mue.getMessage(), mue);
        }
    };

    Converter<String, UUID> TO_UUID = UUID::fromString;

    Converter<String, Currency> TO_CURRENCY = Currency::getInstance;
    Converter<String, Locale> TO_LOCALE = Locale::forLanguageTag;

    Converter<String, Pattern> TO_PATTERN = Pattern::compile;

    Converter<String, Duration> TO_DURATION = Duration::parse;
    Converter<String, Period> TO_PERIOD = Period::parse;
    Converter<String, Instant> TO_INSTANT = Instant::parse;
    Converter<String, LocalDate> TO_LOCAL_DATE = LocalDate::parse;
    Converter<String, LocalDateTime> TO_LOCAL_DATE_TIME = LocalDateTime::parse;
    Converter<String, ZoneId> TO_ZONE_ID = ZoneId::of;
    Converter<String, TimeZone> TO_TIMEZONE = TimeZone::getTimeZone;
    Converter<String, OffsetDateTime> TO_OFFSET_DATE_TIME = OffsetDateTime::parse;
    Converter<String, ZonedDateTime> TO_ZONED_DATE_TIME = ZonedDateTime::parse;

}
