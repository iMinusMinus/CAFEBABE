package bandung.ee.json;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonPointer;
import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;
import jakarta.json.JsonString;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;

/**
 * JSON Streaming API Provider
 *
 * @author iMinusMinus
 * @date 2024-10-16
 */
public class ParsingProvider extends JsonProvider {

    private final JsonFactoryAdapter adapter;

    public ParsingProvider() {
        adapter = new JsonFactoryAdapter(Collections.emptyMap());
    }

    private JsonFactoryAdapter returnSingletonOrClone(Map<String, ?> config) {
        if (config == null || config.isEmpty()) {
            return adapter;
        }
        Map<String, Object> acceptedConfig = new HashMap<>();
        config.forEach( (k, v) -> {
            if (JsonGenerator.PRETTY_PRINTING.equals(k) || k.startsWith(JsonFactoryAdapter.JSONP_CONFIG_PREFIX)) {
                acceptedConfig.put(k, v);
            }
        });
        if (acceptedConfig.isEmpty()) {
            return adapter;
        }
        try {
            JsonFactoryAdapter clone = adapter.clone();
            clone.setConfig(acceptedConfig);
            return clone;
        } catch (Exception ignore) {
            return new JsonFactoryAdapter(acceptedConfig);
        }
    }

    @Override
    public JsonParser createParser(Reader reader) {
        return adapter.createParser(reader);
    }

    @Override
    public JsonParser createParser(InputStream in) {
        return adapter.createParser(in);
    }

    @Override
    public JsonParserFactory createParserFactory(Map<String, ?> config) {
        return returnSingletonOrClone(config);
    }

    @Override
    public JsonGenerator createGenerator(Writer writer) {
        return adapter.createGenerator(writer);
    }

    @Override
    public JsonGenerator createGenerator(OutputStream out) {
        return adapter.createGenerator(out);
    }

    @Override
    public JsonGeneratorFactory createGeneratorFactory(Map<String, ?> config) {
        return returnSingletonOrClone(config);
    }

    @Override
    public JsonReader createReader(Reader reader) {
        return  adapter.createReader(reader);
    }

    @Override
    public JsonReader createReader(InputStream in) {
        return adapter.createReader(in);
    }

    @Override
    public JsonWriter createWriter(Writer writer) {
        return adapter.createWriter(writer);
    }

    @Override
    public JsonWriter createWriter(OutputStream out) {
        return adapter.createWriter(out);
    }

    @Override
    public JsonWriterFactory createWriterFactory(Map<String, ?> config) {
        return returnSingletonOrClone(config);
    }

    @Override
    public JsonReaderFactory createReaderFactory(Map<String, ?> config) {
        return returnSingletonOrClone(config);
    }

    @Override
    public JsonObjectBuilder createObjectBuilder() {
        return adapter.createObjectBuilder();
    }

    @Override
    public JsonObjectBuilder createObjectBuilder(JsonObject object) {
        return adapter.createObjectBuilder(object);
    }

    @Override
    public JsonObjectBuilder createObjectBuilder(Map<String, ?> map) {
        return adapter.createObjectBuilder(Collections.unmodifiableMap(map));
    }

    @Override
    public JsonArrayBuilder createArrayBuilder() {
        return adapter.createArrayBuilder();
    }

    @Override
    public JsonArrayBuilder createArrayBuilder(JsonArray array) {
        return adapter.createArrayBuilder(array);
    }

    @Override
    public JsonPointer createPointer(String jsonPointer) {
        return adapter.createPointer(jsonPointer);
    }


    @Override
    public JsonBuilderFactory createBuilderFactory(Map<String, ?> config) {
        return returnSingletonOrClone(config);
    }

    @Override
    public JsonString createValue(String value) {
        return adapter.createValue(value);
    }

    @Override
    public JsonNumber createValue(int value) {
        return adapter.createValue(value);
    }

    @Override
    public JsonNumber createValue(long value) {
        return adapter.createValue(value);
    }

    @Override
    public JsonNumber createValue(double value) {
        return adapter.createValue(value);
    }

    @Override
    public JsonNumber createValue(BigDecimal value) {
        return adapter.createValue(value);
    }

    @Override
    public JsonNumber createValue(BigInteger value) {
        return adapter.createValue(value);
    }
}
