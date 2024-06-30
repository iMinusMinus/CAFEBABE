package bandung.ee.servlet;

import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参阅RFC：
 * <ul>
 *     <li><a href="https://www.rfc-editor.org/rfc/rfc2183">The Content-Disposition Header Field</a></li>
 *     <li><a href="https://www.rfc-editor.org/rfc/rfc6266">Use of the Content-Disposition Header Field in the Hypertext Transfer Protocol</a></li>
 * </ul>
 *
 * @see org.springframework.http.ContentDisposition
 *
 * @author iMinusMinus
 * @date 2024-06-23
 */
public final class ContentDisposition {

    @Getter
    private final String type;

    private final Map<String, Object> parameters;

    /**
     *  indicating it can be displayed inside the Web page, or as the Web page
     */
    public static final String INLINE_TYPE = "inline";

    /**
     * indicating it should be downloaded; most browsers presenting a 'Save as' dialog,
     * prefilled with the value of the filename parameters if present
     */
    public static final String ATTACHMENT_TYPE = "attachment";

    public static final String FORM_DATA_TYPE = "form-data";

    public static final String NAME_PARAM = "name";

    public static final String FILENAME_PARAM = "filename";

    public static final String FILENAME_I10N_PARAM = "filename*";

    public static final String CREATION_DATE_PARAM = "creation-date";

    public static final String MODIFICATION_DAE_PARAM = "modification-date";

    public static final String READ_DATE_PARAM = "read-date";

    public static final String SIZE_PARAM = "size";

    private final static Pattern BASE64_ENCODED_PATTERN =
            Pattern.compile("=\\?([0-9a-zA-Z-_]+)\\?B\\?([+/0-9a-zA-Z]+=*)\\?=");

    private final static Pattern QUOTED_PRINTABLE_ENCODED_PATTERN =
            Pattern.compile("=\\?([0-9a-zA-Z-_]+)\\?Q\\?([!->@-~]+)\\?=");

    public String getName() {
        return (String) parameters.get(NAME_PARAM);
    }

    /**
     * filename和filename*同时存在时filename*优先，某些user-agent不支持filename*时使用filename
     * @return filename
     */
    public String getFileName() {
        Object filename = parameters.get(FILENAME_I10N_PARAM);
        if (filename == null) {
            filename = parameters.get(FILENAME_PARAM);
        }
        return (String) filename;
    }

    public long getSize() {
        Object size = parameters.get(SIZE_PARAM);
        return size == null ? -1 : (long) size;
    }

    private ContentDisposition(String type, Map<String, Object> parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    public static ContentDisposition valueOf(String contentDisposition) {
        char[] ca = contentDisposition.toCharArray();
        String type = null;
        int position = 0;
        boolean found = false, quoted = false, escaped = false;
        Map<String, Object> parameters = new HashMap<>();
        for (int i = 0; i < ca.length; i++) {
            switch (ca[i]) {
                case ';':
                    if (!quoted) {
                        found = true;
                    }
                    break;
                case '"':
                    if (!escaped) {
                        quoted = !quoted;
                    }
                    break;
                case '\\':
                    escaped = !escaped;
                    break;
            }
            boolean end = (i == ca.length - 1);
            if (!found && !end) {
                continue;
            }
            String tmp = new String(ca, position, (end ? ca.length : i) - position);
            if (type == null) {
                position = i + 1;
                type = tmp;
            } else {
                int index = tmp.indexOf("=");
                String key = tmp.substring(0, index);
                String value = tmp.startsWith("\"", index + 1) && tmp.endsWith("\"") ?
                        tmp.substring(index + 2, tmp.length() - 1) :
                        tmp.substring(index + 1);
                handleParameter(parameters, key.trim().toLowerCase(), value);
                position = i + 1;
            }
            found = false;
        }
        if (!INLINE_TYPE.equalsIgnoreCase(type) && !ATTACHMENT_TYPE.equalsIgnoreCase(type) && !FORM_DATA_TYPE.equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("unknown type: " + type);
        }
        return new ContentDisposition(type, parameters);
    }

    private static void handleParameter(Map<String, Object> parameters, String key, String value) {
        switch (key) {
            case NAME_PARAM:
                parameters.put(key, value);
                break;
            case FILENAME_PARAM: // filename=quoted-string
                parameters.put(key, decodeRfc2047FilenameIfPossible(value));
                break;
            case FILENAME_I10N_PARAM: // filename* = ext-value. see https://www.rfc-editor.org/rfc/rfc5987#section-3.2
                // filename*="UTF-8'en'actual value", filename*="iso-8859-1''actual value"
                Charset cs = StandardCharsets.US_ASCII;
                int position = value.indexOf("'");
                int next = value.indexOf("'", position + 1);
                if (position > 0 && next > 0) {
                    try {
                        cs = Charset.forName(value.substring(0, position));
                    } catch (UnsupportedCharsetException uce) {
                        throw new IllegalArgumentException("invalid charset of filename*: " + value.substring(0, position));
                    }
                    value = value.substring(next + 1);
                }
                parameters.put(key, decodeRfc5987Filename(value, cs));
                break;
            case CREATION_DATE_PARAM: // pass-through
            case MODIFICATION_DAE_PARAM: // pass-through
            case READ_DATE_PARAM: // pass-through
                parameters.put(key, ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME)); // see https://www.rfc-editor.org/rfc/rfc822
                break;
            case SIZE_PARAM:
                parameters.put(key, Long.parseLong(value));
                break;
        }
    }

    private static String decodeRfc2047FilenameIfPossible(String filename) {
        if (!filename.startsWith("?=")) {
            return filename;
        }
        Matcher matcher = BASE64_ENCODED_PATTERN.matcher(filename);
        if (matcher.find()) {
            StringBuilder builder = new StringBuilder();
            do {
                Charset charset = Charset.forName(matcher.group(1));
                byte[] decoded = Base64.getDecoder().decode(matcher.group(2));
                builder.append(new String(decoded, charset));
            }
            while (matcher.find());

            return builder.toString();
        }
        matcher = QUOTED_PRINTABLE_ENCODED_PATTERN.matcher(filename);
        if (matcher.find()) {
            StringBuilder builder = new StringBuilder();
            do {
                Charset charset = Charset.forName(matcher.group(1));
                String decoded = decodeQuotedPrintableFilename(matcher.group(2), charset);
                builder.append(decoded);
            }
            while (matcher.find());

            return builder.toString();
        }
        return filename;
    }

    private static String decodeQuotedPrintableFilename(String filename, Charset charset) {
        byte[] value = filename.getBytes(StandardCharsets.US_ASCII);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(value.length);
        for (int index = 0; index < value.length;) {
            if (value[index] == '_') {
                baos.write(' ');
                index++;
            } else if(value[index] == '=' && index < value.length - 2) {
                int hex1 = Character.digit((char) value[index + 1], 16);
                int hex2 = Character.digit((char) value[index + 2], 16);
                if (hex1 == -1 || hex2 == -1) {
                    throw new IllegalArgumentException("invalid hex sequence of filename: " + filename);
                }
                baos.write((hex1 << 4) | hex2);
                index += 3;
            } else {
                baos.write(value[index]);
                index++;
            }
        }
        return baos.toString(charset);
    }

    private static String decodeRfc5987Filename(String filename, Charset charset) {
        byte[] value = filename.getBytes(charset);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(value.length);
        for (int index = 0; index < value.length; ) {
            if (isRFC5987AttrChar(value[index])) {
                baos.write((char) value[index]);
                index++;
            } else if (value[index] == '%' && index < value.length - 2) {
                char[] array = new char[]{(char) value[index + 1], (char) value[index + 2]};
                try {
                    int pctEncode = Integer.parseInt(String.valueOf(array), 16);
                    baos.write(pctEncode);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("invalid filename*: " + filename);
                }
                index += 3;
            } else {
                throw new IllegalArgumentException("invalid filename*: " + filename);
            }
        }
        return baos.toString(charset);
    }

    private static boolean isRFC5987AttrChar(byte c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
                c == '!' || c == '#' || c == '$' || c == '&' || c == '+' || c == '-' ||
                c == '.' || c == '^' || c == '_' || c == '`' || c == '|' || c == '~';
    }

}
