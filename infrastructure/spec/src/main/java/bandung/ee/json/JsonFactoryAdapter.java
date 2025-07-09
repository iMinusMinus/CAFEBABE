package bandung.ee.json;

import bandung.se.io.UnicodeDetectingInputStream;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerationException;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import javax.json.stream.JsonParsingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * json factory
 *
 * @author iMinusMinus
 * @date 2024-10-19
 */
class JsonFactoryAdapter implements JsonGeneratorFactory, JsonParserFactory, JsonReaderFactory, JsonWriterFactory,
        JsonBuilderFactory, Cloneable {

    private static final Logger log = Logger.getLogger(JsonFactoryAdapter.class.getName());

    static final String JSONP_CONFIG_PREFIX = "jsonp.";

    static final String WRITE_NUMBER_AS_STRING_KEY = JSONP_CONFIG_PREFIX + "generator.writeNumberAsString";

    private Map<String, ?> config;

    static final char QUOTATION_MARK = '"';

    static final char REVERSE_SOLIDUS = '\\';

    static final char SOLIDUS = '/';

    static final char BACKSPACE = '\b';

    static final char FORM_FEED = '\f';

    static final char LINE_FEED = '\n'; // WS

    static final char CARRIAGE_RETURN = '\r'; // WS, Windows new line: \r\n

    static final char HORIZONTAL_TAB = '\t'; // WS

    static final char SPACE = ' '; // WS

    static final char LEFT_SQUARE_BRACKET = '[';

    static final char LEFT_CURLY_BRACKET = '{';

    static final char RIGHT_SQUARE_BRACKET = ']';

    static final char RIGHT_CURLY_BRACKET = '}';

    static final char COLON = ':';

    static final char COMMA = ',';

    static final char DOT = '.';

    static final char UPPER_EXPONENT = 'E';

    static final char LOWER_EXPONENT = 'e';

    static final char POSITIVE = '+';

    static final char NEGATIVE = '-';

    static final char[] TRUE = {'t', 'r', 'u', 'e'};

    static final char[] FALSE = {'f', 'a', 'l', 's', 'e'};

    static final char[] NULL = {'n', 'u', 'l', 'l'};

    public JsonFactoryAdapter(Map<String, ?> config) {
        this.config = config;
    }

    void setConfig(Map<String, ?> config) {
        this.config = config;
    }

    @Override
    public JsonGenerator createGenerator(Writer writer) {
        return new JsonTokenEmitter(config, writer); //  rewrite bytecode
    }

    @Override
    public JsonGenerator createGenerator(OutputStream out) {
        return new JsonTokenEmitter(config, out); //  rewrite bytecode
    }

    @Override
    public JsonGenerator createGenerator(OutputStream out, Charset charset) {
        return new JsonTokenEmitter(config, out, charset); //  rewrite bytecode
    }


    @Override
    public JsonParser createParser(Reader reader) {
        return new JsonTokenParser(config, reader); //  rewrite bytecode
    }

    @Override
    public JsonParser createParser(InputStream in) { // detect encoding: UTF-8, UTF-16, UTF-32
        return new JsonTokenParser(config, in); //  rewrite bytecode
    }

    @Override
    public JsonParser createParser(InputStream in, Charset charset) {
        return new JsonTokenParser(config, in, charset); //  rewrite bytecode
    }

    @Override
    public JsonParser createParser(JsonObject obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonParser createParser(JsonArray array) {
        throw new UnsupportedOperationException();
    }


    @Override
    public JsonReader createReader(Reader reader) {
        return new JsonReaderDelegate(createParser(reader));
    }

    @Override
    public JsonReader createReader(InputStream in) {
        return new JsonReaderDelegate(createParser(in));
    }

    @Override
    public JsonReader createReader(InputStream in, Charset charset) {
        return new JsonReaderDelegate(createParser(in, charset));
    }


    @Override
    public JsonWriter createWriter(Writer writer) {
        return new JsonWriterDelegate(createGenerator(writer));
    }

    @Override
    public JsonWriter createWriter(OutputStream out) {
        return new JsonWriterDelegate(createGenerator(out));
    }

    @Override
    public JsonWriter createWriter(OutputStream out, Charset charset) {
        return new JsonWriterDelegate(createGenerator(out, charset));
    }

    @Override
    public JsonObjectBuilder createObjectBuilder() {
        return new ObjectBuilder();
    }

    @Override
    public JsonObjectBuilder createObjectBuilder(JsonObject object) {
        return new ObjectBuilder(object);
    }

    @Override
    public JsonObjectBuilder createObjectBuilder(Map<String, Object> object) {
        return new ObjectBuilder(object);
    }

    @Override
    public JsonArrayBuilder createArrayBuilder() {
        return new ArrayBuilder();
    }

    @Override
    public  JsonArrayBuilder createArrayBuilder(JsonArray array) {
        return new ArrayBuilder(array);
    }

    @Override
    public JsonArrayBuilder createArrayBuilder(Collection<?> collection) {
        return new ArrayBuilder(collection);
    }

    @Override
    public Map<String, ?> getConfigInUse() {
        return Collections.unmodifiableMap(config);
    }

    public JsonPointer createPointer(String jsonPointer) {
        throw new UnsupportedOperationException(); //  rewrite bytecode
    }

    public JsonString createValue(String value) {
        return new StringValue(value); // rewrite bytecode?
    }

    public JsonNumber createValue(int value) {
        return new Int32Value(value); // rewrite bytecode?
    }

    public JsonNumber createValue(long value) {
        return new Int64Value(value); // rewrite bytecode?
    }

    public JsonNumber createValue(double value) {
        return new NumberValue(BigDecimal.valueOf(value)); // rewrite bytecode?
    }

    public JsonNumber createValue(BigDecimal value) {
        return new NumberValue(value); // rewrite bytecode?
    }

    public JsonNumber createValue(BigInteger value) {
        return new NumberValue(new BigDecimal(value)); // rewrite bytecode?
    }

    @Override
    public JsonFactoryAdapter clone() throws CloneNotSupportedException {
        JsonFactoryAdapter clone = (JsonFactoryAdapter) super.clone();
        clone.config = Collections.unmodifiableMap(config);
        return clone;
    }

    private enum Context {
        NOT_STARTED,
        OBJECT,
        ARRAY,
        FIELD,
        ;
    }

    static class Contextual {

        protected static final short MAX_DEPTH = Long.SIZE - 1;

        protected short depth;

        protected long objectContext;

        protected long arrayContext;

        protected long fieldContext;

        protected long firstKeyFlag = -1L;

        protected void startChild(Context context) {
            long value = 1L << depth;
            switch (context) {
                case OBJECT:
                    depth++;
                    objectContext |= value;
                    firstKeyFlag |= ((-1L >>> depth) << depth); // 重制当前json对象内嵌对象的首个元素标记
                    log.log(Level.FINEST, "start {0}th child object context", depth);
                    break;
                case ARRAY:
                    depth++;
                    arrayContext |= value;
                    firstKeyFlag |= ((-1L >>> depth) << depth); // 重制当前json对象内嵌对象的首个元素标记
                    log.log(Level.FINEST, "start {0}th child array context", depth);
                    break;
                case FIELD:
                    fieldContext |= value;
                    break;
            }
        }

        protected Context closeCurrentContext() {
            if (depth <= 0) {
                throw new IllegalStateException();
            }
            long value = 1L << (depth - 1);
            Context context = currentContext();
            switch (context) {
                case OBJECT:
                    objectContext -= value;
                    log.log(Level.FINEST, "close {0}th child object context", depth);
                    break;
                case ARRAY:
                    arrayContext -= value;
                    log.log(Level.FINEST, "close {0}th child array context", depth);
                    break;
            }
            depth--;
            return context;
        }

        protected Context currentContext() {
            int move = depth - 1;
            if (objectContext == 0L && arrayContext == 0L && fieldContext == 0L) {
                return Context.NOT_STARTED;
            } else if (((fieldContext >>> (move + 1)) & 1) != 0){
                return Context.FIELD;
            } else if (((objectContext >>> move) & 1) != 0) {
                return Context.OBJECT;
            } else if (((arrayContext >>> move) & 1) != 0) {
                return Context.ARRAY;
            } else {
                return null;
            }
        }

        protected boolean isFirstMember() {
            return ((firstKeyFlag >>> depth) & 1) != 0;
        }

        protected void markFirstMemberVisited() {
            if (isFirstMember()) { // XXX
                firstKeyFlag -= (1L << depth);
                log.log(Level.FINEST, "visit {0}th layer first member", depth);
            }
        }

        protected void restore() {
            if (depth <= 0) {
                return;
            }
            log.log(Level.FINEST, "close {0}th layer field context", depth);
            fieldContext -= (1L << depth);
        }
    }

    /**
     * json parser
     *
     * @see org.eclipse.parsson.JsonParserImpl
     * @author iMinusMinus
     * @date 2024-11-24
     */
    static class JsonTokenParser extends Contextual implements JsonParser {

        // JS的浮点数字符串长度不大于24个，整数不大于17个字符
        // BigInt没有限制，其Number.MAX_VALUE为2^1024 - 2^971，Number.MIN_VALUE为2^-1074
        private static final int INITIAL_SIZE = Byte.SIZE * Integer.SIZE;

        private final Reader reader;

        private /*final*/ short maxNestingDepth; // jackson: 1000

        private /*final*/ short maxIdentifierLength; // jackson: 50000

        private /*final*/ int maxStringLength; // jackson: 20000000

        private /*final*/ int maxNumberLength; // jackson: 1000

        private /*final*/ long maxTokenCount; // jackson: unlimited

        private Event currentEvent;

        private boolean fracOrExp;

        private long line;

        private long column;

        private long offset; // An implementation may set limits on the size of texts that it accepts

        private char[] buffer;

        private int mark;

        private int writeMark;

        private int readMark;

        private boolean closed;

        /**
         * <a href="https://www.rfc-editor.org/rfc/rfc7159">RFC 7159</a>禁止生成json时添加BOM，但解析时遇到BOM应忽略而不是出错。
         * <a href="https://www.rfc-editor.org/rfc/rfc8259">RFC 8259</a>允许解析器限制接受的流大小、对象深度、数字精度及字符串长度。
         *
         * @param config parser config
         * @param is input stream
         */
        JsonTokenParser(Map<String, ?> config, InputStream is) {
            UnicodeDetectingInputStream fis = new UnicodeDetectingInputStream(is);
            Charset charset = fis.getCharset();
            this.reader = new InputStreamReader(fis, charset);
            init(config);
        }

        private void init(Map<String, ?> config) {
            Number maxNestingDepth = (Number) config.get(JSONP_CONFIG_PREFIX + "parser.maxNestingDepth");
            this.maxNestingDepth = maxNestingDepth != null && maxNestingDepth.shortValue() > 0 ?
                    (short) Math.min(maxNestingDepth.shortValue(), MAX_DEPTH) :
                    MAX_DEPTH;
            Number maxIdentifierLength = (Number) config.get(JSONP_CONFIG_PREFIX + "parser.maxNameLength");
            this.maxIdentifierLength = maxIdentifierLength != null ? (short) Math.max(8, maxIdentifierLength.shortValue()) : (Long.SIZE - 1);
            Integer maxStringLength = (Integer) config.get(JSONP_CONFIG_PREFIX + "parser.maxStringLength");
            this.maxStringLength = maxStringLength != null ? Math.max(INITIAL_SIZE, maxStringLength) : (Short.MAX_VALUE * Byte.SIZE);
            Integer maxNumberLength = (Integer) config.get(JSONP_CONFIG_PREFIX + "parser.maxNumberLength");
            this.maxNumberLength = maxNumberLength != null ? Math.max(INITIAL_SIZE, maxNumberLength) : (Integer.SIZE * Integer.SIZE);
            Integer maxTokenCount = (Integer) config.get(JSONP_CONFIG_PREFIX + "parser.maxTokenCount");
            this.maxTokenCount = maxTokenCount != null ? maxTokenCount : (Short.MAX_VALUE * Short.SIZE);
            buffer = new char[INITIAL_SIZE];
        }

        JsonTokenParser(Map<String, ?> config, InputStream is, Charset charset) {
            this(config, new InputStreamReader(is, charset));
        }

        JsonTokenParser(Map<String, ?> config, Reader reader) {
            this.reader = reader;
            init(config);
        }

        private int readMoreTokens(int atMost) {
            if (writeMark + atMost >= readMark) {
                try {
                    int real = reader.read(buffer, readMark, buffer.length - readMark);
                    readMark += Math.max(real, 0);
                } catch (IOException ioe) {
                    throw new JsonException(ioe.getMessage(), ioe);
                }
            }
            if (maxTokenCount > 0 && offset + readMark - writeMark > maxTokenCount) {
                throw new JsonException("too many character");
            }
            return readMark - writeMark;
        }

        private void skipWhiteSpace() {
            while(true) {
                switch (buffer[writeMark]) {
                    case LINE_FEED: // pass through
                    case FORM_FEED:
                        line++;
                        column = 0;
                        break;
                    case CARRIAGE_RETURN: // pass through
                    case HORIZONTAL_TAB: // pass through
                    case SPACE:
                        column++;
                        break;
                    default:
                        mark = writeMark;
                        return;
                }
                offset++;
                writeMark++;
                if (readMoreTokens(buffer.length - readMark) <= 0) {
                    moveToHead(0);
                }
            }
        }

        private void moveToHead(int guaranteeSize) {
            if (writeMark + guaranteeSize >= readMark) {
                if (guaranteeSize > buffer.length) {
                    char[] buffer = new char[guaranteeSize];
                    System.arraycopy(this.buffer, mark, buffer, 0, readMark - mark);
                    this.buffer = buffer;
                } else if (mark > 0){
                    System.arraycopy(buffer, mark, buffer, 0, readMark - mark);
                }
                readMark -= mark;
                writeMark -= mark;
                mark = 0;
                readMoreTokens(guaranteeSize);
            }
        }

        @Override
        protected void startChild(Context context) {
            // legal: "{}", "[{}]", "[true, {}]";illegal: "{}[]", "{},{}", "true{}"
            if (depth >= maxNestingDepth) {
                throw new JsonException("json depth must not exceed " + maxNestingDepth);
            }
            super.startChild(context);
        }

        @Override
        protected Context closeCurrentContext() {
            Context closed = super.closeCurrentContext();
            markFirstMemberVisited();
            Context ctx = currentContext();
            if (ctx == Context.FIELD) {
                restore();
            }
            return closed;
        }

        private void ensureTopLevelAtMostOneJsonValue(Context context) {
            if (depth == 0 && (context != Context.NOT_STARTED || currentEvent != null)) {
                throw new JsonParsingException("json value appear multi times in json root", new TokenLocation(line, column - 1, offset));
            }
        }

        private Event nextEvent() {
//            skipWhiteSpace();
            Context context = currentContext();
            // skip colon and comma
            if (context != Context.NOT_STARTED && !isFirstMember()) {
                char c = buffer[writeMark];
                if (context == Context.FIELD && c != COLON) {
                    throw new JsonParsingException("expect ':' follow object name", new TokenLocation(line, column, offset));
                } else if (context == Context.OBJECT && c != COMMA && c != RIGHT_CURLY_BRACKET) {
                    throw new JsonParsingException("expect ',' follow json value", new TokenLocation(line, column, offset));
                } else if (context == Context.ARRAY && c != COMMA && c != RIGHT_SQUARE_BRACKET) {
                    throw new JsonParsingException("expect ',' follow json value", new TokenLocation(line, column, offset));
                }
                if (c == COLON || c == COMMA) {
                    column++;
                    offset++;
                    writeMark++;
                    mark = writeMark;
                    moveToHead(0);
                    skipWhiteSpace();
                }
                if (c == COMMA && (buffer[writeMark] == RIGHT_CURLY_BRACKET || buffer[writeMark] == RIGHT_SQUARE_BRACKET)) {
                    throw new JsonParsingException("redundant comma", new TokenLocation(line, column, offset));
                }
            }

            column++;
            offset++;
            switch (buffer[writeMark++]) {
                case LEFT_CURLY_BRACKET:
                    ensureTopLevelAtMostOneJsonValue(context);
                    if (context == Context.OBJECT) {
                        throw new JsonParsingException("object cannot be object name", new TokenLocation(line, column - 1, offset - 1));
                    }
                    startChild(Context.OBJECT);
                    return Event.START_OBJECT;
                case LEFT_SQUARE_BRACKET:
                    ensureTopLevelAtMostOneJsonValue(context);
                    if (context == Context.OBJECT) {
                        throw new JsonParsingException("array cannot be object name", new TokenLocation(line, column - 1, offset - 1));
                    }
                    startChild(Context.ARRAY);
                    return Event.START_ARRAY;
                case RIGHT_CURLY_BRACKET:
                    if (context != Context.OBJECT) {
                        throw new JsonParsingException("not in object context, but got '}'", new TokenLocation(line, column - 1, offset - 1));
                    }
                    closeCurrentContext();
                    return Event.END_OBJECT;
                case RIGHT_SQUARE_BRACKET:
                    if (context != Context.ARRAY) {
                        throw new JsonParsingException("not in array context, but got ']'", new TokenLocation(line, column - 1, offset - 1));
                    }
                    closeCurrentContext();
                    return Event.END_ARRAY;
                case 't':
                    return readLiteral(context, TRUE, Event.VALUE_TRUE);
                case 'f':
                    return readLiteral(context, FALSE, Event.VALUE_FALSE);
                case 'n':
                    return readLiteral(context, NULL, Event.VALUE_NULL);
                case QUOTATION_MARK: // 1. '{"key"'; 2. '{"key":"value","k.e.y"'
                    ensureTopLevelAtMostOneJsonValue(context);
                    readString(context == Context.OBJECT ? maxIdentifierLength : maxStringLength);
                    markFirstMemberVisited();
                    Event event = Event.VALUE_STRING;
                    if (context == Context.OBJECT) {
                        event = Event.KEY_NAME;
                        startChild(Context.FIELD);
                    } else if (context == Context.FIELD){
                        restore();
                    }
                    return event;
                case NEGATIVE: // pass through, should we accept '+' as leading sign?
                case '0': // pass through
                case '1': // pass through
                case '2': // pass through
                case '3': // pass through
                case '4': // pass through
                case '5': // pass through
                case '6': // pass through
                case '7': // pass through
                case '8': // pass through
                case '9':
                    ensureTopLevelAtMostOneJsonValue(context);
                    if (context == Context.OBJECT) {
                        throw new JsonParsingException("unquoted number cannot be object name", new TokenLocation(line, column, offset));
                    }
                    readNumber();
                    markFirstMemberVisited();
                    if (context == Context.FIELD) {
                        restore();
                    }
                    return Event.VALUE_NUMBER;
                default:
                    throw new JsonParsingException("unknown state", new TokenLocation(line, column - 1, offset));
            }
        }

        private Event readLiteral(Context ctx, char[] expected, Event value) {
            //  "[]null", "{},false", "true:[]"
            ensureTopLevelAtMostOneJsonValue(ctx);
            if (ctx == Context.OBJECT) {
                throw new JsonParsingException("true/false/null cannot be object name", new TokenLocation(line, column - 1, offset - 1));
            }


            if (mark + expected.length - 1 >= readMark) {
                moveToHead(expected.length);
            }
            int readable = readMark - mark;
            if (readable < expected.length) {
                throw new JsonParsingException("missing character for " + new String(expected), new TokenLocation(line, column + readable, offset + readable));
            }
            for (int i = 1; i < expected.length; i++) {
                if (buffer[mark + i] != expected[i]) {
                    throw new JsonParsingException("expected " + new String(expected) + "but got" + new String(buffer, mark, i), new TokenLocation(line, column + i - 1, offset + i));
                }
            }
            writeMark += expected.length - 1;
            column += expected.length - 1;
            offset += expected.length - 1;
            if (ctx == Context.FIELD) {
                restore();
            }
            markFirstMemberVisited();
            return value;
        }

        private void replaceControlCharacter(int offset, char controlCharacter) {
            buffer[writeMark - offset] = controlCharacter;
            System.arraycopy(buffer, writeMark, buffer, writeMark - (offset - 1), readMark - writeMark);
            writeMark -= (offset - 1);
            readMark -= (offset - 1);
        }

        private void readString(int maxValueLength) {
            if (readMoreTokens(buffer.length - readMark) <= 0) {
                moveToHead(0);
            }
            int unicodeMarkPosition = -1;
            boolean escape = false;
            while (true) {
                switch (buffer[writeMark++]) {
                    case REVERSE_SOLIDUS:
                        if (escape) {
                            replaceControlCharacter(2, REVERSE_SOLIDUS);
                        }
                        escape = !escape;
                        break;
                    case QUOTATION_MARK:
                        if (!escape) {
//                            escape = false;
                            offset++;
                            column++;
                            return;
                        }
                        break;
                    case SOLIDUS:
                        if (escape) {
                            escape = false;
                        }
                        break;
                    case 'b':
                        if (escape) {
                            replaceControlCharacter(2, BACKSPACE);
                            escape = false;
                        } // else unicode character, op in default branch
                        break;
                    case 'r':
                        if (escape) {
                            replaceControlCharacter(2, CARRIAGE_RETURN);
                            escape = false;
                        }
                        break;
                    case 't':
                        if (escape) {
                            replaceControlCharacter(2, HORIZONTAL_TAB);
                            escape = false;
                        }
                        break;
                    case 'n':
                        if (escape) {
                            line++;
                            column = 0;
                            replaceControlCharacter(2, LINE_FEED);
                            escape = false;
                        }
                        break;
                    case 'f': // what if \u0085(Next Line), \u2028(Line Separator), \u2029(Paragraph Separator)
                        if (escape) {
                            line++;
                            column = 0;
                            replaceControlCharacter(2, FORM_FEED);
                            escape = false;
                        } // else unicode character, op in default branch
                        break;
                    case 'u':
                        if (escape) {
                            unicodeMarkPosition = writeMark;
                            escape = false;
                        }
                        break;
                    default:
                        if (escape) {
                            throw new JsonParsingException("only control character can follow reverse solidus", getLocation());
                        }
                        if (unicodeMarkPosition >= 0 && writeMark - unicodeMarkPosition == 4) {
                            int unicode = 0;
                            for (int i = 0; i < 4; i++) {
                                if (!(buffer[unicodeMarkPosition + i] >= 'a' && buffer[unicodeMarkPosition + i] <= 'f') &&
                                        !(buffer[unicodeMarkPosition + i] >= 'A' && buffer[unicodeMarkPosition + i] <= 'F') &&
                                        !(buffer[unicodeMarkPosition + i] >= '0' && buffer[unicodeMarkPosition + i] <= '9')) {
                                    throw new JsonParsingException("unexpected char follow unicode symbol", getLocation());
                                }
                                int digit = Character.digit(buffer[unicodeMarkPosition + i], 16);
                                unicode = (unicode << 4) | digit;
                            }
                            replaceControlCharacter(6, (char) unicode);
                            unicodeMarkPosition = -1; // reset
                        }
                        break;
                }
                offset++;
                column++;
                if (writeMark - mark - 1 > maxValueLength) {
                    throw new JsonException("string length exceed max size: " + maxValueLength);
                }
                if (writeMark >= readMark) {
                    if (mark > 0 && readMark <= buffer.length) {
                        moveToHead(0);
                    }  else if (buffer.length < maxValueLength) {
                        moveToHead(Math.min((int) (buffer.length * 1.5), maxValueLength));
                    } else {
                        throw new JsonException("string length exceed max size: " + maxValueLength);
                    }
                }
            }
        }

        private void readNumber() {
            if (readMoreTokens(buffer.length - readMark) <= 0) {
                moveToHead(0);
            }
            boolean exponentFound = false;
            while (true) {
                switch (buffer[writeMark++]) {
                    case LOWER_EXPONENT: // pass through
                    case UPPER_EXPONENT: // pass through
                        if (buffer[writeMark - 2] == DOT) {
                            throw new JsonParsingException("e/E cannot direct go after dot", getLocation());
                        }
                        if (exponentFound) {
                            throw new JsonParsingException("e/E cannot appear multi times!", getLocation());
                        }
                        exponentFound = true;
                        break;
                    case DOT:
                        if (fracOrExp) {
                            throw new JsonParsingException("dot/E/e cannot appear multi times", getLocation());
                        }
                        fracOrExp = true;
                        break;
                    case NEGATIVE:
                        if (writeMark - 2 != mark &&
                                buffer[writeMark - 2] != LOWER_EXPONENT &&
                                buffer[writeMark - 2] != UPPER_EXPONENT) {
                            throw new JsonParsingException("'-' should be leading sign or go after e/E", getLocation());
                        }
                        break;
                    case POSITIVE:
                        if (buffer[writeMark - 2] != LOWER_EXPONENT && buffer[writeMark - 2] != UPPER_EXPONENT) {
                            throw new JsonParsingException("'+' should go after e/E", getLocation());
                        }
                        break;
                    case '0': // pass through
                    case '1': // pass through
                    case '2': // pass through
                    case '3': // pass through
                    case '4': // pass through
                    case '5': // pass through
                    case '6': // pass through
                    case '7': // pass through
                    case '8': // pass through
                    case '9':
                        break;
                    case COMMA: // pass through
                    case RIGHT_SQUARE_BRACKET: // pass through
                    case RIGHT_CURLY_BRACKET: // pass through
                    case SPACE: // WS, pass through
                    case LINE_FEED: // WS, pass through
                    case CARRIAGE_RETURN: // WS, pass through
                    case HORIZONTAL_TAB: // WS
                        writeMark -= 1;
                        if (buffer[writeMark - 1] < '0' || buffer[writeMark - 1] > '9') {
                            throw new JsonParsingException("number should end with 0-9", getLocation());
                        }
                        return;
                    default:
                        throw new JsonParsingException("valid number characters: -0.1eE+23456789", getLocation());
                }
                column++;
                offset++;

                if (writeMark >= readMark) {
                    if (mark > 0 && readMark <= buffer.length) {
                        moveToHead(0);
                    } else if (buffer.length < maxNumberLength) {
                        moveToHead(Math.min((int) (buffer.length * 1.5), maxNumberLength));
                    } else {
                        throw new JsonException("number length exceed max size: " + maxNumberLength);
                    }
                }
            }
        }

        private String composeExceptionMsg() {
            switch (currentEvent) {
                case START_OBJECT:
                    return "expect key name, but EOF";
                case START_ARRAY:
                    return "expect json value, but EOF";
                case KEY_NAME:
                    return "expect colon, BUT EOF";
                case END_OBJECT: // pass through
                case END_ARRAY:
                    return "expect end json structure, but EOF";
                default:
                    return "expect comma, but EOF";
            }
        }

        /**
         * 当json结果已完成，但输入流仍有非空字符，则视为JsonException。
         *
         * @return has next event
         */
        @Override
        public boolean hasNext() {
            if (closed) {
                return false;
            }
            if (readMoreTokens(buffer.length - readMark) <= 0) {
                moveToHead(0);
            }
            skipWhiteSpace();
            if (depth > 0 && writeMark >= readMark) {
                // currentEvent --> array: next may be 'value', ',value' or ']', or JsonParsingException
                throw new JsonParsingException(composeExceptionMsg(), getLocation());
            }
            return writeMark < readMark;
        }

        @Override
        public Event next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            mark = writeMark;
            fracOrExp = false;
            return currentEvent = nextEvent();
        }

        @Override
        public String getString() {
            if (currentEvent == Event.KEY_NAME ||
                    currentEvent == Event.VALUE_STRING) {
                return new String(buffer, mark + 1, writeMark - 1 - (mark + 1));
            } else if (currentEvent == Event.VALUE_NUMBER) {
                return new String(buffer, mark, writeMark - mark);
            } else {
                throw new IllegalStateException("please call getString after receive KEY_NAME/VALUE_STRING/VALUE_NUMBER event");
            }
        }

        @Override
        public boolean isIntegralNumber() {
            if (currentEvent != Event.VALUE_NUMBER) {
                throw new IllegalStateException("please call isIntegralNumber after receive VALUE_NUMBER event");
            }
            return !fracOrExp || getBigDecimal().scale() == 0;
        }

        @Override
        public int getInt() {
            if (currentEvent != Event.VALUE_NUMBER) {
                throw new IllegalStateException("please call getInt after receive VALUE_NUMBER event");
            }
            return Integer.parseInt(new String(buffer, mark, writeMark - mark));
        }

        @Override
        public long getLong() {
            if (currentEvent != Event.VALUE_NUMBER) {
                throw new IllegalStateException("please call getLong after receive VALUE_NUMBER event");
            }
            return Long.parseLong(new String(buffer, mark, writeMark - mark));
        }

        @Override
        public BigDecimal getBigDecimal() {
            if (currentEvent != Event.VALUE_NUMBER) {
                throw new IllegalStateException("please call getBigDecimal after receive VALUE_NUMBER event");
            }
            return new BigDecimal(new String(buffer, mark, writeMark - mark));
        }

        @Override
        public JsonLocation getLocation() {
            return new TokenLocation(line, column, offset);
        }

        @Override
        public JsonObject getObject() {
            if (currentEvent != Event.START_OBJECT) {
                throw new IllegalStateException("please call getObject after receive START_OBJECT event");
            }
            ObjectBuilder builder = new ObjectBuilder();
            Event event;
            while (hasNext() && (event = next()) != Event.END_OBJECT) {
                if (event != Event.KEY_NAME) {
                    throw new JsonParsingException("expect name after '{'", getLocation());
                }
                String name = getString();
                next();
                JsonValue value = getValue();
                builder.add(name, value);
            }
            return builder.build();
        }

        @Override
        public JsonValue getValue() {
            switch (currentEvent) {
                case START_ARRAY: return getArray();
                case START_OBJECT: return getObject();
                case VALUE_FALSE: return JsonValue.FALSE;
                case VALUE_TRUE: return JsonValue.TRUE;
                case VALUE_NUMBER:
                    if (isIntegralNumber() && writeMark - mark < String.valueOf(Long.MAX_VALUE).length()) {
                        return writeMark - mark >= String.valueOf(Integer.MAX_VALUE).length() ?
                                new Int64Value(getLong()) : new Int32Value(getInt());
                    }
                    return new NumberValue(getBigDecimal());
                case VALUE_STRING: return new StringValue(getString());
                case VALUE_NULL: return JsonValue.NULL;
                case END_OBJECT: // pass through
                case END_ARRAY:
                    throw new IllegalStateException("please call getValue in object/array/value context");
                default:
                    throw new JsonParsingException("wrong event", getLocation());
            }
        }

        @Override
        public JsonArray getArray() {
            if (currentEvent != Event.START_ARRAY) {
                throw new IllegalStateException("please call getArray after receive start array event");
            }
            ArrayBuilder builder = new ArrayBuilder();
            while (hasNext() && next() != Event.END_ARRAY) {
                builder.add(getValue());
            }
            return builder.build();
        }

        // getArrayStream, getObjectStream, getValueStream

        @Override
        public void skipArray() {
            if (currentEvent != Event.START_ARRAY) {
                return;
            }
            int currentDepth = depth;
            while (true) {
                Event next = next();
                if (next == Event.END_ARRAY && currentDepth - 1 == depth) {
                    break;
                }
            }
        }

        @Override
        public void skipObject() {
            if (currentEvent != Event.START_OBJECT) {
                return;
            }
            int currentDepth = depth;
            while (true) {
                Event next = next();
                if (next == Event.END_OBJECT && currentDepth - 1 == depth) {
                    break;
                }
            }
        }

        @Override
        public void close() {
            closed = true;
            buffer = null;
            try {
                reader.close();
            } catch (IOException ioe) {
                throw new JsonException(ioe.getMessage(), ioe);
            }
        }
    }

    private static class TokenLocation implements JsonLocation {

        private final long lineNumber;

        private final long columnNumber;

        private final long streamOffset;

        TokenLocation(long lineNumber, long columnNumber, long streamOffset) {
            this.lineNumber = lineNumber;
            this.columnNumber = columnNumber;
            this.streamOffset = streamOffset;
        }

        @Override
        public long getLineNumber() {
            return lineNumber;
        }

        @Override
        public long getColumnNumber() {
            return columnNumber;
        }

        @Override
        public long getStreamOffset() {
            return streamOffset;
        }

        @Override
        public String toString() {
            return "{\"line\": " + lineNumber + ", \"column\": " + columnNumber + "}";
        }
    }

    /**
     * json generator
     *
     * @see org.eclipse.parsson.JsonGeneratorImpl
     * @see org.eclipse.parsson.JsonPrettyGeneratorImpl
     *
     * @author iMinusMinus
     * @date 2024-10-27
     */
    private static class JsonTokenEmitter extends Contextual implements JsonGenerator {

        private final boolean prettyPrint;

        private final int indentSize;

        private final Writer writer;

        private final Boolean writeNumberAsString;

        JsonTokenEmitter(Map<String, ?> config, OutputStream os) {
            this(config, os, StandardCharsets.UTF_8);
        }

        /**
         *
         * @param config generator config
         * @param os output stream
         * @param charset JSON text SHALL be encoded in UTF-8, UTF-16, or UTF-32.
         */
        JsonTokenEmitter(Map<String, ?> config, OutputStream os, Charset charset) {
            this(config, new OutputStreamWriter(os, charset));
        }

        JsonTokenEmitter(Map<String, ?> config, Writer writer) {
            Object tmp = config.get(JsonGenerator.PRETTY_PRINTING);
            if (tmp instanceof Boolean) {
                this.indentSize = 4;
                this.prettyPrint = (Boolean) tmp;
            } else if (tmp instanceof String) {
                this.indentSize = 4;
                this.prettyPrint = Boolean.parseBoolean((String) tmp);
            } else if (tmp instanceof Integer) {
                this.indentSize = (Integer) tmp;
                this.prettyPrint = indentSize > 0;
            } else {
                this.prettyPrint = false;
                this.indentSize = 0;
            }
            this.writeNumberAsString = (Boolean) config.get(WRITE_NUMBER_AS_STRING_KEY);
            this.writer = writer;
        }

        @Override
        protected void startChild(Context context) {
            if (depth >= (Long.BYTES - 1)) {
                throw new JsonGenerationException("maximum json depth is 63");
            }
            super.startChild(context);
        }

        @Override
        protected Context closeCurrentContext() {
            if (depth <= 0) {
                throw new JsonGenerationException("no any context");
            }
            Context closed = super.closeCurrentContext();
            char end;
            switch (closed) {
                case OBJECT:
                    end = RIGHT_CURLY_BRACKET;
                    break;
                case ARRAY:
                    end = RIGHT_SQUARE_BRACKET;
                    break;
                default:
                    throw new JsonGenerationException("please call writeEnd in json structure context");
            }
            if (prettyPrint) {
                writeChar(LINE_FEED);
                int total = depth * indentSize;
                for (int i = 0; i < total; i++) {
                    writeChar(SPACE);
                }
            }
            writeChar(end);
            if (depth > 0) {
                Context ctx = currentContext();
                if (ctx == Context.FIELD) {
                    restore(); // {"name":[]} or {"name":{}}
                }
            }
            return closed;
        }

        @Override
        protected Context currentContext() {
            Context context = super.currentContext();
            if (context == null) {
                throw new JsonGenerationException("bad context");
            }
            return context;
        }

        private void writeComma() {
            writeComma(currentContext());
        }

        private void writeComma(Context current) {
            if (current == Context.FIELD) {
                return;
            }
            if (!isFirstMember()) {
                writeChar(COMMA);
            } else { // 标记后续元素在当前对象不再是首个
                markFirstMemberVisited();
            }
            if (prettyPrint && current != Context.NOT_STARTED) {
                writeChar(LINE_FEED);
                int total = depth * indentSize;
                for (int i = 0; i < total; i++) {
                    writeChar(SPACE);
                }
            }
        }

        private void writeColon() {
            writeChar(COLON);
            if (prettyPrint) {
                writeChar(SPACE);
            }
        }

        private void writeChar(char c) {
            try {
                writer.write(c);
            } catch (IOException ioe) {
                throw new JsonException(ioe.getMessage(), ioe);
            }
        }

        private void checkContext(Context current) {
            if (current != Context.OBJECT) {
                throw new JsonGenerationException("please call write(String, T) in json object context");
            }
        }

        @Override
        public JsonGenerator writeStartObject() {
            Context context = currentContext();
            if (context == Context.OBJECT) { // '{', '[{', '{"name": {' is OK
                throw new JsonGenerationException("cannot write json object directly, may be you should write name first");
            } else if (context == Context.NOT_STARTED && !isFirstMember()) { // '[],{}', '{},{}'
                throw new JsonGenerationException("multi json structure present at root level");
            }
            writeComma(context); // [{},{}]
            writeChar(LEFT_CURLY_BRACKET);
            startChild(Context.OBJECT);
            return this;
        }

        @Override
        public JsonGenerator writeStartObject(String name) {
            if (currentContext() != Context.OBJECT) {
                throw new JsonGenerationException("please call writeStartObject(String) in json object context");
            }
            return writeKey(name).writeStartObject();
        }

        @Override
        public JsonGenerator writeKey(String name) {
            Context context = currentContext();
            if (context != Context.OBJECT) {
                throw new JsonGenerationException("please call writeKey in json object context");
            }
            writeComma(context);
            writeEscapedString(name);
            writeColon();
            startChild(Context.FIELD);
            return this;
        }

        private void writeEscapedString(String str) {
            writeString(str, true);
        }

        private void writeString(String str, boolean escape) {
            if (escape) {
                writeChar(QUOTATION_MARK);
            }
            char[] cs = str.toCharArray();
            for (char c : cs) {
                switch (c) {
                    case QUOTATION_MARK: // pass through
                    case REVERSE_SOLIDUS:
                        writeChar(REVERSE_SOLIDUS);
                        writeChar(c);
                        break;
//                    case SOLIDUS:
//                        writeChar(REVERSE_SOLIDUS);
//                        writeChar(SOLIDUS);
//                        break;
                    case BACKSPACE:
                        writeChar(REVERSE_SOLIDUS);
                        writeChar('b');
                        break;
                    case FORM_FEED:
                        writeChar(REVERSE_SOLIDUS);
                        writeChar('f');
                        break;
                    case LINE_FEED:
                        writeChar(REVERSE_SOLIDUS);
                        writeChar('n');
                        break;
                    case CARRIAGE_RETURN:
                        writeChar(REVERSE_SOLIDUS);
                        writeChar('r');
                        break;
                    case HORIZONTAL_TAB:
                        writeChar(REVERSE_SOLIDUS);
                        writeChar('t');
                        break;
                    default:
                        assert c >= 0x20; // 0x9, 0xA, 0xD, 0x20 took as white space
//                        if (c <= 0x7F) {
                            writeChar(c);
//                        } else {
//                            String hex = "0000" + Integer.toHexString(c);
//                            writeChar(REVERSE_SOLIDUS);
//                            writeChar('u');
//                            writeChar(hex.charAt(hex.length() - 4));
//                            writeChar(hex.charAt(hex.length() - 3));
//                            writeChar(hex.charAt(hex.length() - 2));
//                            writeChar(hex.charAt(hex.length() - 1));
//                        }
                }
            }
            if (escape) {
                writeChar(QUOTATION_MARK);
            }
        }

        @Override
        public JsonGenerator writeStartArray() {
            Context context = currentContext();
            if (context == Context.OBJECT) {
                throw new JsonGenerationException("please do not call writeStartArray in json object context");
            } else if (context == Context.NOT_STARTED && !isFirstMember()) {
                throw new JsonGenerationException("multi json structure present at root level");
            }
            writeComma(context); // [[],[]]
            writeChar(LEFT_SQUARE_BRACKET);
            startChild(Context.ARRAY);
            return this;
        }

        @Override
        public JsonGenerator writeStartArray(String name) {
//            checkContext(); // writeKey checked
            return writeKey(name).writeStartArray();
        }

        @Override
        public JsonGenerator write(String name, JsonValue value) {
//            checkContext();
            return writeKey(name).write(value);
        }

        @Override
        public JsonGenerator write(String name, String value) {
            Context current = currentContext();
            checkContext(current);
            writeComma(current);
            writeEscapedString(name);
            writeColon();
            writeEscapedString(value);
            return this;
        }

        @Override
        public JsonGenerator write(String name, BigInteger value) {
            Context current = currentContext();
            checkContext(current);
            writeComma(current);
            writeEscapedString(name);
            writeColon();
            writeString(value.toString(), writeNumberAsString(value));
            return this;
        }

        @Override
        public JsonGenerator write(String name, BigDecimal value) {
            Context current = currentContext();
            checkContext(current);
            writeComma(current);
            writeEscapedString(name);
            writeColon();
            writeString(value.toString(), writeNumberAsString(value));
            return this;
        }

        @Override
        public JsonGenerator write(String name, int value) {
            Context current = currentContext();
            checkContext(current);
            writeComma(current);
            writeEscapedString(name);
            writeColon();
            writeString(String.valueOf(value), false);
            return this;
        }

        @Override
        public JsonGenerator write(String name, long value) {
            Context current = currentContext();
            checkContext(current);
            writeComma(current);
            writeEscapedString(name);
            writeColon();
            writeString(String.valueOf(value), writeNumberAsString(value));
            return this;
        }

        @Override
        public JsonGenerator write(String name, double value) {
            Context current = currentContext();
            checkContext(current);
            writeComma(current);
            writeEscapedString(name);
            writeColon();
            writeString(String.valueOf(value), writeNumberAsString(value));
            return this;
        }

        @Override
        public JsonGenerator write(String name, boolean value) {
            Context current = currentContext();
            checkContext(current);
            writeComma(current);
            writeEscapedString(name);
            writeColon();
            char[] cs = value ? TRUE : FALSE;
            for (char c : cs) {
                writeChar(c);
            }
            return this;
        }

        @Override
        public JsonGenerator writeNull(String name) {
            Context current = currentContext();
            checkContext(current);
            writeComma(current);
            writeEscapedString(name);
            writeColon();
            for (char c : NULL) {
                writeChar(c);
            }
            return this;
        }

        @Override
        public JsonGenerator writeEnd() {
            closeCurrentContext();
            return this;
        }

        @Override
        public JsonGenerator write(JsonValue value) {
            Context context = currentContext();
            if (context != Context.ARRAY && context != Context.FIELD) { // '[member]', '{"name": member}'
                throw new JsonGenerationException("please call write in json array or json filed context");
            }
            switch (value.getValueType()) {
                case ARRAY :
                    writeStartArray();
                    for (JsonValue val : value.asJsonArray()) {
                        write(val);
                    }
                    writeEnd();
                    break;
                case OBJECT:
                    writeStartObject();
                    for (Map.Entry<String, JsonValue> entry : value.asJsonObject().entrySet()) {
                        write(entry.getKey(), entry.getValue());
                    }
                    writeEnd();
                    break;
                case NUMBER:
                    JsonNumber num = (JsonNumber) value;
                    if (num.isIntegral()) {
                        write(num.longValue());
                    } else {
                        write(num.bigDecimalValue());
                    }
                    break;
                case STRING:
                    write(((JsonString) value).getString());
                    break;
                case TRUE:
                    write(true);
                    break;
                case FALSE:
                    write(false);
                    break;
                case NULL:
                    writeNull();
                    break;
            }
            return this;
        }

        @Override
        public JsonGenerator write(String value) {
            Context ctx = currentContext();
            if (ctx == Context.OBJECT) {
                throw new JsonGenerationException("please call write(String) in root/array/field context");
            }
            writeComma(ctx);
            writeEscapedString(value);

            if (ctx == Context.FIELD) {
                restore();
            }
            return this;
        }

        @Override
        public JsonGenerator write(BigDecimal value) {
            writeComma();
            writeString(String.valueOf(value), writeNumberAsString(value));
            if (currentContext() == Context.FIELD) {
                restore();
            }
            return this;
        }

        @Override
        public JsonGenerator write(BigInteger value) {
            writeComma();
            writeString(String.valueOf(value), writeNumberAsString(value));
            if (currentContext() == Context.FIELD) {
                restore();
            }
            return this;
        }

        @Override
        public JsonGenerator write(int value) {
            writeComma();
            writeString(String.valueOf(value), false);
            if (currentContext() == Context.FIELD) {
                restore();
            }
            return this;
        }

        @Override
        public JsonGenerator write(long value) {
            writeComma();
            writeString(String.valueOf(value), writeNumberAsString(value));
            if (currentContext() == Context.FIELD) {
                restore();
            }
            return this;
        }

        @Override
        public JsonGenerator write(double value) {
            if (Double.isInfinite(value) || Double.isNaN(value)) {
                throw new NumberFormatException("cannot write Infinite or NaN");
            }
            writeComma();
            writeString(String.valueOf(value), writeNumberAsString(value));
            if (currentContext() == Context.FIELD) {
                restore();
            }
            return this;
        }

        @Override
        public JsonGenerator write(boolean value) {
            Context ctx = currentContext();
            if (ctx == Context.OBJECT) {
                throw new JsonGenerationException("please call write(String) in root/array/field context");
            }
            writeComma(ctx);
            char[] cs = value ? TRUE : FALSE;
            for (char c : cs) {
                writeChar(c);
            }

            if (ctx == Context.FIELD) {
                restore();
            }

            return this;
        }

        @Override
        public JsonGenerator writeNull() {
            Context ctx = currentContext();
            if (ctx == Context.OBJECT) {
                throw new JsonGenerationException("please call write(String) in root/array/field context");
            }
            writeComma(ctx);
            for (char c : NULL) {
                writeChar(c);
            }

            if (ctx == Context.FIELD) {
                restore();
            }

            return this;
        }

        private boolean writeNumberAsString(long value) {
            if (writeNumberAsString != null) {
                return writeNumberAsString;
            }
            return value > JS_MAX_SAFE_INTEGER || value < JS_MIN_SAFE_INTEGER;
        }

        private boolean writeNumberAsString(double value) {
            if (writeNumberAsString != null) {
                return writeNumberAsString;
            }
            return value > JS_MAX_NUMBER.doubleValue() || value < JS_MIN_NUMBER.doubleValue();
        }

        private boolean writeNumberAsString(BigInteger value) {
            if (writeNumberAsString != null) {
                return writeNumberAsString;
            }
            return value.compareTo(JS_MAX_SAFE_NUMBER) > 0  || value.compareTo(JS_MIN_SAFE_NUMBER) < 0;
        }

        private boolean writeNumberAsString(BigDecimal value) {
            if (writeNumberAsString != null) {
                return writeNumberAsString;
            }
            if (value.scale() == 0) {
                return value.compareTo(JS_MAX_SAFE_DECIMAL) > 0  || value.compareTo(JS_MIN_SAFE_DECIMAL) < 0;
            } else {
                return value.compareTo(JS_MAX_NUMBER) > 0 || value.compareTo(JS_MIN_NUMBER) < 0;
            }
        }

        @Override
        public void close() {
            if (currentContext() != Context.NOT_STARTED || isFirstMember()) {
                throw new JsonGenerationException("incomplete json");
            }
            try {
                writer.close();
            } catch (IOException e) {
                throw new JsonException(e.getMessage(), e);
            }
        }

        @Override
        public void flush() {
            try {
                writer.flush();
            } catch (IOException e) {
                throw new JsonException(e.getMessage(), e);
            }
        }
    }

    private static class JsonReaderDelegate implements JsonReader {

        private final JsonParser jsonParser;

        private boolean done;

        JsonReaderDelegate(JsonParser jsonParser) {
            this.jsonParser = jsonParser;
            done = false;
        }

        @Override
        public JsonStructure read() {
            if (done) {
                throw new IllegalStateException("read canceled");
            }
            done = true;
            if (!jsonParser.hasNext()) {
                throw new JsonException("nothing to read");
            }
            try {
                JsonParser.Event event = jsonParser.next();
                return event == JsonParser.Event.START_ARRAY ? jsonParser.getArray() : jsonParser.getObject();
            } catch (IllegalStateException ise) {
                throw new JsonParsingException(ise.getMessage(), ise, jsonParser.getLocation());
            }
        }

        @Override
        public JsonObject readObject() {
            if (done) {
                throw new IllegalStateException("read canceled");
            }
            done = true;
            if (!jsonParser.hasNext()) {
                throw new JsonException("nothing to read");
            }
            try {
                jsonParser.next();
                return jsonParser.getObject();
            } catch (IllegalStateException ise) {
                throw new JsonParsingException(ise.getMessage(), ise, jsonParser.getLocation());
            }
        }

        @Override
        public JsonArray readArray() {
            if (done) {
                throw new IllegalStateException("read canceled");
            }
            done = true;
            if (!jsonParser.hasNext()) {
                throw new JsonException("nothing to read");
            }
            try {
                jsonParser.next();
                return jsonParser.getArray();
            } catch (IllegalStateException ise) {
                throw new JsonParsingException(ise.getMessage(), ise, jsonParser.getLocation());
            }
        }

        @Override
        public JsonValue readValue() {
            if (done) {
                throw new IllegalStateException("read canceled");
            }
            done = true;
            if (!jsonParser.hasNext()) {
                throw new JsonException("nothing to read");
            }
            try {
                jsonParser.next();
                return jsonParser.getValue();
            } catch (IllegalStateException ise) {
                throw new JsonParsingException(ise.getMessage(), ise, jsonParser.getLocation());
            }
        }

        @Override
        public void close() {
            done = true;
            jsonParser.close();
        }
    }

    private static class JsonWriterDelegate implements JsonWriter {

        private final JsonGenerator jsonGenerator;

        private boolean done;

        JsonWriterDelegate(JsonGenerator jsonGenerator) {
            this.jsonGenerator = jsonGenerator;
            done = false;
        }

        @Override
        public void writeArray(JsonArray array) {
            if (done) {
                throw new IllegalStateException("write canceled");
            }
            done = true;
            jsonGenerator.writeStartArray();
            for (JsonValue value : array) {
                jsonGenerator.write(value);
            }
            jsonGenerator.writeEnd();
            jsonGenerator.flush();
        }

        @Override
        public void writeObject(JsonObject object) {
            if (done) {
                throw new IllegalStateException("write canceled");
            }
            done = true;
            jsonGenerator.writeStartObject();
            for (Map.Entry<String, JsonValue> entry : object.entrySet()) {
                jsonGenerator.write(entry.getKey(), entry.getValue());
            }
            jsonGenerator.writeEnd();
            jsonGenerator.flush();
        }

        @Override
        public void write(JsonStructure value) {
            if (value instanceof JsonArray) {
                writeArray((JsonArray) value);
            } else {
                writeObject((JsonObject) value);
            }
        }

        @Override
        public void write(JsonValue value) {
            switch (value.getValueType()) {
                case OBJECT : writeObject((value.asJsonObject())); break;
                case ARRAY: writeArray(value.asJsonArray()); break;
                default:
                    if (done) {
                        throw new IllegalStateException("write canceled");
                    }
                    done = true;
                    jsonGenerator.write(value);
            }
        }

        @Override
        public void close() {
            done = true;
            jsonGenerator.close();
        }
    }

    static JsonValue convert(Object jobject) {
        if (jobject == null) {
            return JsonValue.NULL;
        } else if (jobject instanceof JsonValue) {
            return (JsonValue) jobject;
        } else if (jobject instanceof JsonArrayBuilder) {
            return ((JsonArrayBuilder) jobject).build();
        } else if (jobject instanceof JsonObjectBuilder) {
            return ((JsonObjectBuilder) jobject).build();
        } else if (jobject instanceof Collection) {
            ArrayBuilder builder = new ArrayBuilder((Collection) jobject);
            return builder.build();
        } else if (jobject instanceof Map) {
            ObjectBuilder builder = new ObjectBuilder((Map<String, Object>) jobject);
            return builder.build();
        } else if (jobject instanceof Optional) {
            Optional opt = (Optional) jobject;
            if (opt.isPresent()) {
                return convert(opt.get());
            }
            return JsonValue.NULL; // or just remove?
        } else if (jobject instanceof Boolean) {
            return ((Boolean) jobject) ? JsonValue.TRUE : JsonValue.FALSE;
        } else if (jobject instanceof Integer) {
            return new Int32Value((Integer) jobject);
        } else if (jobject instanceof Long) {
            return new Int64Value((Long) jobject);
        } else if (jobject instanceof Float) {
            return new NumberValue(BigDecimal.valueOf((Float) jobject));
        } else if (jobject instanceof Double) {
            return new NumberValue(BigDecimal.valueOf((Double) jobject));
        } else if (jobject instanceof BigInteger) {
            return new NumberValue(new BigDecimal((BigInteger) jobject));
        } else if (jobject instanceof BigDecimal) {
            return new NumberValue((BigDecimal) jobject);
        } else if (jobject instanceof String) {
            return new StringValue((String) jobject);
        }
        Class<?> klazz = jobject.getClass();
        if (klazz.isArray()) {
            ArrayBuilder builder = new ArrayBuilder();
            // component type is primitive
            if (klazz.getComponentType() == Boolean.TYPE) {
                boolean[] array = (boolean[]) jobject;
                for (boolean value : array) {
                    builder.add(value);
                }
                return builder.build();
            } else if (klazz.getComponentType() == Byte.TYPE) {
                byte[] array = (byte[]) jobject;
                for (byte value : array) {
                    builder.add(value);
                }
                return builder.build();
            } else if (klazz.getComponentType() == Character.TYPE) {
                char[] array = (char[]) jobject;
                for (char value : array) {
                    builder.add(value);
                }
                return builder.build();
            } else if (klazz.getComponentType() == Short.TYPE) {
                short[] array = (short[]) jobject;
                for (short value : array) {
                    builder.add(value);
                }
                return builder.build();
            } else if (klazz.getComponentType() == Integer.TYPE) {
                int[] array = (int[]) jobject;
                for (int value : array) {
                    builder.add(value);
                }
                return builder.build();
            } else if (klazz.getComponentType() == Long.TYPE) {
                long[] array = (long[]) jobject;
                for (long value : array) {
                    builder.add(value);
                }
                return builder.build();
            } else if (klazz.getComponentType() == Float.TYPE) {
                float[] array = (float[]) jobject;
                for (float value : array) {
                    builder.add(value);
                }
                return builder.build();
            } else if (klazz.getComponentType() == Double.TYPE) {
                double[] array = (double[]) jobject;
                for (double value : array) {
                    builder.add(value);
                }
                return builder.build();
            }

            Object[] array = (Object[]) jobject;
            for (Object item : array) {
                builder.add(convert(item));
            }
            return builder.build();
        }
        throw new UnsupportedOperationException("cannot write to json, java object is " + jobject);
    }

    private static class ObjectBuilder implements JsonObjectBuilder {

        private final ObjectStructure object;

        public ObjectBuilder() {
            this(JsonValue.EMPTY_JSON_OBJECT);
        }

        public ObjectBuilder(JsonObject object) {
            this.object = new ObjectStructure();
            if (object != null && !object.isEmpty()) {
                this.object.putAll(object);
            }
        }

        public ObjectBuilder(Map<String, Object> map) {
            this.object = new ObjectStructure();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                object.put(entry.getKey(), convert(entry.getValue()));
            }
        }

        @Override
        public JsonObjectBuilder add(String name, JsonValue value) {
            Objects.requireNonNull(name);
            Objects.requireNonNull(value);
            object.put(name, value);
            return this;
        }

        @Override
        public JsonObjectBuilder add(String name, String value) {
            Objects.requireNonNull(name);
            Objects.requireNonNull(value);
            object.put(name, new StringValue(value));
            return this;
        }

        @Override
        public JsonObjectBuilder add(String name, BigInteger value) {
            Objects.requireNonNull(name);
            Objects.requireNonNull(value);
            object.put(name, new NumberValue(new BigDecimal(value)));
            return this;
        }

        @Override
        public JsonObjectBuilder add(String name, BigDecimal value) {
            Objects.requireNonNull(name);
            Objects.requireNonNull(value);
            object.put(name, new NumberValue(value));
            return this;
        }

        @Override
        public JsonObjectBuilder add(String name, int value) {
            Objects.requireNonNull(name);
            object.put(name, new Int32Value(value));
            return this;
        }

        @Override
        public JsonObjectBuilder add(String name, long value) {
            Objects.requireNonNull(name);
            object.put(name, new Int64Value(value));
            return this;
        }

        @Override
        public JsonObjectBuilder add(String name, double value) {
            Objects.requireNonNull(name);
            object.put(name, new NumberValue(BigDecimal.valueOf(value)));
            return this;
        }

        @Override
        public JsonObjectBuilder add(String name, boolean value) {
            Objects.requireNonNull(name);
            object.put(name, value ? JsonValue.TRUE : JsonValue.FALSE);
            return this;
        }

        @Override
        public JsonObjectBuilder addNull(String name) {
            Objects.requireNonNull(name);
            object.put(name, JsonValue.NULL);
            return this;
        }

        @Override
        public JsonObjectBuilder add(String name, JsonObjectBuilder builder) {
            Objects.requireNonNull(name);
            Objects.requireNonNull(builder);
            object.put(name, builder.build());
            return this;
        }

        @Override
        public JsonObjectBuilder add(String name, JsonArrayBuilder builder) {
            Objects.requireNonNull(name);
            Objects.requireNonNull(builder);
            object.put(name, builder.build());
            return this;
        }

        @Override
        public JsonObject build() {
            return object.isEmpty() ? JsonValue.EMPTY_JSON_OBJECT : object;
        }

    }

    /**
     * An object is an unordered collection of zero or more name/value pairs
     */
    private static class ObjectStructure extends HashMap<String, JsonValue> implements JsonObject {

        @Override
        public JsonArray getJsonArray(String name) {
            return Optional.ofNullable(get(name)).map(JsonValue::asJsonArray).orElse(null);
        }

        @Override
        public JsonObject getJsonObject(String name) {
            return Optional.ofNullable(get(name)).map(JsonValue::asJsonObject).orElse(null);
        }

        @Override
        public JsonNumber getJsonNumber(String name) {
            return (JsonNumber) get(name);
        }

        @Override
        public JsonString getJsonString(String name) {
            return (JsonString) get(name);
        }

        @Override
        public String getString(String name) {
            return getJsonString(name).getString();
        }

        @Override
        public String getString(String name, String defaultValue) {
            return Optional.ofNullable(getJsonString(name)).map(JsonString::getString).orElse(defaultValue);
        }

        @Override
        public int getInt(String name) {
            return getJsonNumber(name).intValue();
        }

        @Override
        public int getInt(String name, int defaultValue) {
            return Optional.of(getJsonNumber(name)).map(JsonNumber::intValue).orElse(defaultValue);
        }

        @Override
        public boolean getBoolean(String name) {
            JsonValue value = get(name);
            if (value == JsonValue.TRUE) {
                return true;
            } else if (value == JsonValue.FALSE) {
                return false;
            } else if (value == null) {
                throw new NullPointerException("no mapping for name: " + name);
            } else {
                throw new ClassCastException("not a json true or false value");
            }
        }

        @Override
        public boolean getBoolean(String name, boolean defaultValue) {
            JsonValue value = get(name);
            if (value == JsonValue.TRUE) {
                return true;
            } else if (value == JsonValue.FALSE) {
                return false;
            } else if (value == null) {
                return defaultValue;
            } else {
                throw new ClassCastException("not a json true or false value");
            }
        }

        @Override
        public boolean isNull(String name) {
            JsonValue value = get(name);
            if (value == null) {
                throw new NullPointerException("no mapping for name: " + name);
            }
            return value == JsonValue.NULL;
        }

        @Override
        public ValueType getValueType() {
            return ValueType.OBJECT;
        }

        @Override
        public JsonObject asJsonObject() {
            return this;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(LEFT_CURLY_BRACKET);
            for (Map.Entry<String, JsonValue> entry : entrySet()) {
                sb.append(new StringValue(entry.getKey()))
                        .append(':')
                        .append(entry.getValue().toString())
                        .append(',');
            }
            sb.setCharAt(sb.length() - 1, RIGHT_CURLY_BRACKET);
            return sb.toString();
        }
    }

    private static class ArrayBuilder implements JsonArrayBuilder {

        private final ArrayStructure array;

        public ArrayBuilder() {
            this(JsonValue.EMPTY_JSON_ARRAY);
        }

        public ArrayBuilder(JsonArray array) {
            this.array = new ArrayStructure();
            if (array != null && !array.isEmpty()) {
                this.array.addAll(array);
            }
        }

        public ArrayBuilder(Collection<?> collection) {
            this.array = new ArrayStructure();
            if (collection != null && !collection.isEmpty()) {
                for (Object jobject : collection) {
                    array.add(convert(jobject));
                }
            }
        }

        @Override
        public JsonArrayBuilder add(JsonValue value) {
            Objects.requireNonNull(value);
            array.add(value);
            return this;
        }

        @Override
        public JsonArrayBuilder add(String value) {
            Objects.requireNonNull(value);
            array.add(new StringValue(value));
            return this;
        }

        @Override
        public JsonArrayBuilder add(BigDecimal value) {
            Objects.requireNonNull(value);
            array.add(new NumberValue(value));
            return this;
        }

        @Override
        public JsonArrayBuilder add(BigInteger value) {
            Objects.requireNonNull(value);
            array.add(new NumberValue(new BigDecimal(value)));
            return this;
        }

        @Override
        public JsonArrayBuilder add(int value) {
            array.add(new Int32Value(value));
            return this;
        }

        @Override
        public JsonArrayBuilder add(long value) {
            array.add(new Int64Value(value));
            return this;
        }

        @Override
        public JsonArrayBuilder add(double value) {
            array.add(new NumberValue(BigDecimal.valueOf(value)));
            return this;
        }

        @Override
        public JsonArrayBuilder add(boolean value) {
            if (value) {
                array.add(JsonValue.TRUE);
            } else {
                array.add(JsonValue.FALSE);
            }
            return this;
        }

        @Override
        public JsonArrayBuilder addNull() {
            array.add(JsonValue.NULL);
            return this;
        }

        @Override
        public JsonArrayBuilder add(JsonObjectBuilder builder) {
            Objects.requireNonNull(builder);
            array.add(builder.build());
            return this;
        }

        @Override
        public JsonArrayBuilder add(JsonArrayBuilder builder) {
            Objects.requireNonNull(builder);
            array.add(builder.build());
            return this;
        }

        @Override
        public JsonArray build() {
            return array.isEmpty() ? JsonValue.EMPTY_JSON_ARRAY : array;
        }
    }

    /**
     * An array is an ordered sequence of zero or more values.
     * There is no requirement that the values in an array be of the same type.
     */
    private static class ArrayStructure extends ArrayList<JsonValue> implements JsonArray {

        @Override
        public JsonObject getJsonObject(int index) {
            JsonValue value = get(index);
            return value == null ? null : value.asJsonObject();
        }

        @Override
        public JsonArray getJsonArray(int index) {
            JsonValue value = get(index);
            return value == null ? null : value.asJsonArray();
        }

        @Override
        public JsonNumber getJsonNumber(int index) {
            JsonValue value = get(index);
            return (JsonNumber) value;
        }

        @Override
        public JsonString getJsonString(int index) {
            JsonValue value = get(index);
            return (JsonString) value;
        }

        @Override
        public <T extends JsonValue> List<T> getValuesAs(Class<T> clazz) {
            return stream().map(clazz::cast).collect(Collectors.toList());
        }

        @Override
        public String getString(int index) {
            JsonValue value = get(index);
            return value == null ? null : ((JsonString) value).getString();
        }

        @Override
        public String getString(int index, String defaultValue) {
            JsonValue value = get(index);
            return value == null ? defaultValue : ((JsonString) value).getString();
        }

        @Override
        public int getInt(int index) {
            JsonValue value = get(index);
            return ((JsonNumber) value).intValue();
        }

        @Override
        public int getInt(int index, int defaultValue) {
            JsonValue value = get(index);
            return value == null ? defaultValue : ((JsonNumber) value).intValue();
        }

        @Override
        public boolean getBoolean(int index) {
            JsonValue value = get(index);
            if (value == JsonValue.TRUE) {
                return true;
            } else if (value == JsonValue.FALSE) {
                return false;
            } else {
                throw new ClassCastException("expected true or false but [" + value + "]");
            }
        }

        @Override
        public boolean getBoolean(int index, boolean defaultValue) {
            JsonValue value = get(index);
            if (value == JsonValue.TRUE) {
                return true;
            } else if (value == JsonValue.FALSE) {
                return false;
            } else {
                return defaultValue;
            }
        }

        @Override
        public boolean isNull(int index) {
            JsonValue value = get(index);
            return value == JsonValue.NULL;
        }

        @Override
        public ValueType getValueType() {
            return ValueType.ARRAY;
        }

        @Override
        public JsonArray asJsonArray() {
            return this;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(LEFT_SQUARE_BRACKET);
            for (JsonValue next : this) {
                sb.append(next.toString());
                sb.append(',');
            }
            sb.setCharAt(sb.length() - 1, RIGHT_SQUARE_BRACKET);
            return sb.toString();
        }
    }

    private static class Int32Value implements JsonNumber {

        private final int value;

        Int32Value(int value) {
            this.value = value;
        }

        @Override
        public boolean isIntegral() {
            return true;
        }

        @Override
        public int intValue() {
            return value;
        }

        @Override
        public int intValueExact() {
            return value;
        }

        @Override
        public long longValue() {
            return value;
        }

        @Override
        public long longValueExact() {
            return value;
        }

        @Override
        public BigInteger bigIntegerValue() {
            return BigInteger.valueOf(value);
        }

        @Override
        public BigInteger bigIntegerValueExact() {
            return BigInteger.valueOf(value);
        }

        @Override
        public double doubleValue() {
            return value;
        }

        @Override
        public BigDecimal bigDecimalValue() {
            return BigDecimal.valueOf(value);
        }

        @Override
        public ValueType getValueType() {
            return ValueType.NUMBER;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof Int32Value) {
                return value == ((Int32Value) other).value;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }
    }

    private static class Int64Value implements JsonNumber {

        private final long value;

        Int64Value(long value) {
            this.value = value;
        }

        @Override
        public boolean isIntegral() {
            return true;
        }

        @Override
        public int intValue() {
            return (int) value;
        }

        @Override
        public int intValueExact() {
            return Math.toIntExact(value);
        }

        @Override
        public long longValue() {
            return value;
        }

        @Override
        public long longValueExact() {
            return value;
        }

        @Override
        public BigInteger bigIntegerValue() {
            return BigInteger.valueOf(value);
        }

        @Override
        public BigInteger bigIntegerValueExact() {
            return BigInteger.valueOf(value);
        }

        @Override
        public double doubleValue() {
            return value;
        }

        @Override
        public BigDecimal bigDecimalValue() {
            return BigDecimal.valueOf(value);
        }

        @Override
        public ValueType getValueType() {
            return ValueType.NUMBER;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof Int64Value) {
                return value == ((Int64Value) other).value;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (int) value;
        }

        @Override
        public String toString() {
            if (value <= JS_MAX_SAFE_INTEGER && value >= JS_MIN_SAFE_INTEGER) {
                return Long.toString(value);
            }
            StringBuilder sb = new StringBuilder();
            return sb.append(QUOTATION_MARK).append(value).append(QUOTATION_MARK).toString();
        }
    }

    private static class NumberValue implements JsonNumber {

        private final BigDecimal value;

        NumberValue(BigDecimal value) {
            this.value = value;
        }

        @Override
        public boolean isIntegral() {
            return value.scale() == 0;
        }

        @Override
        public int intValue() {
            return value.intValue();
        }

        @Override
        public int intValueExact() {
            return value.intValueExact();
        }

        @Override
        public long longValue() {
            return value.longValue();
        }

        @Override
        public long longValueExact() {
            return value.longValueExact();
        }

        @Override
        public BigInteger bigIntegerValue() {
            return value.toBigInteger();
        }

        @Override
        public BigInteger bigIntegerValueExact() {
            return value.toBigIntegerExact();
        }

        @Override
        public double doubleValue() {
            return value.doubleValue();
        }

        @Override
        public BigDecimal bigDecimalValue() {
            return value;
        }

        @Override
        public ValueType getValueType() {
            return ValueType.NUMBER;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof NumberValue) {
                return value.equals(((NumberValue) other).value);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public String toString() {
            return QUOTATION_MARK + value.toString() + QUOTATION_MARK;
        }
    }

    private static class StringValue implements JsonString {

        private final CharSequence cs;

        public StringValue(CharSequence cs) {
            this.cs = cs;
        }

        @Override
        public String getString() {
            return cs.toString();
        }

        @Override
        public CharSequence getChars() {
            return cs;
        }

        @Override
        public ValueType getValueType() {
            return ValueType.STRING;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof StringValue) {
                return cs.equals(((StringValue) other).cs);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return cs.hashCode();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(QUOTATION_MARK);
            for (int i = 0; i < cs.length(); i++) {
                char c = cs.charAt(i);
                switch (c) {
                    //escape
                    case QUOTATION_MARK: // pass through
//                    case SOLIDUS: // pass through
                    case REVERSE_SOLIDUS:
                        sb.append(REVERSE_SOLIDUS);
                        sb.append(c);
                        break;
                    case BACKSPACE: sb.append(REVERSE_SOLIDUS).append('b'); break;
                    case FORM_FEED: sb.append(REVERSE_SOLIDUS).append('f'); break;
                    case LINE_FEED: sb.append(REVERSE_SOLIDUS).append('n'); break;
                    case CARRIAGE_RETURN: sb.append(REVERSE_SOLIDUS).append('r'); break;
                    case HORIZONTAL_TAB: sb.append(REVERSE_SOLIDUS).append('t'); break;
                    default:
                        assert c >= 0x20; // 0x9, 0xA, 0xD, 0x20 took as white space
//                        if (c <= 0x7F) {
                            sb.append(c);
//                        } else {
//                            String hex = "0000" + Integer.toHexString(c);
//                            sb.append(REVERSE_SOLIDUS).append('u').append(hex.substring(hex.length() - 4));
//                        }
                }
            }
            sb.append(QUOTATION_MARK);
            return sb.toString();
        }
    }

    static final long JS_MAX_SAFE_INTEGER = (1L << 53) - 1;

    static final long JS_MIN_SAFE_INTEGER = -JS_MAX_SAFE_INTEGER;

    static final BigDecimal JS_MAX_SAFE_DECIMAL = new BigDecimal(JS_MAX_SAFE_INTEGER);

    static final BigDecimal JS_MIN_SAFE_DECIMAL = JS_MAX_SAFE_DECIMAL.negate();

    static final BigDecimal JS_MAX_NUMBER = new BigDecimal("1.7976931348623157E+308"); // 2 ^ 1024 - 2 ^971

    static final BigDecimal JS_MIN_NUMBER = new BigDecimal("5e-324"); // 2 ^ -1074

    static final BigInteger JS_MAX_SAFE_NUMBER = JS_MAX_SAFE_DECIMAL.toBigInteger();

    static final BigInteger JS_MIN_SAFE_NUMBER = JS_MIN_SAFE_DECIMAL.toBigInteger();
}
