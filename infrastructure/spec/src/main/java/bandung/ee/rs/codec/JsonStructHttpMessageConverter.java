package bandung.ee.rs.codec;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonParsingException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * JSONP读写
 *
 * @see org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider
 *
 * @author iMinusMinus
 * @date 2024-06-27
 */
public class JsonStructHttpMessageConverter extends AbstractMessageBodyConverter implements MessageBodyReader<JsonStructure>, MessageBodyWriter<JsonStructure> {

    static final String ANY_JSON = "+json";

    private final JsonReaderFactory jsonReaderFactory;

    private final JsonWriterFactory jsonWriterFactory;

    public JsonStructHttpMessageConverter() {
        this(StandardCharsets.UTF_8, 64);
    }

    public JsonStructHttpMessageConverter(Charset charset, int bufferSize) {
        this(charset, bufferSize, Json.createReaderFactory(Collections.emptyMap()), Json.createWriterFactory(Collections.emptyMap()));
    }

    public JsonStructHttpMessageConverter(Charset charset, int bufferSize,
                                          JsonReaderFactory jsonReaderFactory, JsonWriterFactory jsonWriterFactory) {
        super(charset, bufferSize);
        this.jsonReaderFactory = jsonReaderFactory;
        this.jsonWriterFactory = jsonWriterFactory;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isCompatible(type) && isCompatible(mediaType);
    }

    @Override
    public JsonStructure readFrom(Class<JsonStructure> type, Type genericType, Annotation[] annotations,
                                  MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                                  InputStream entityStream) throws IOException, WebApplicationException {
        try (JsonReader reader = jsonReaderFactory.createReader(entityStream)) {
            return reader.read();
        } catch (JsonParsingException jpe) {
            throw new BadRequestException(jpe.getMessage(), jpe);
        } catch (JsonException je) {
            throw new IOException(je.getMessage(), je);
        } catch (IllegalStateException ise) {
            throw new InternalServerErrorException(ise.getMessage(), ise);
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isCompatible(type) && isCompatible(mediaType);
    }

    @Override
    public void writeTo(JsonStructure jsonStructure, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        try (JsonWriter writer = jsonWriterFactory.createWriter(entityStream)) {
            writer.write(jsonStructure);
        } catch (JsonException je) {
            throw new IOException(je.getMessage(), je);
        } catch (IllegalStateException ise) {
            throw new InternalServerErrorException(ise.getMessage(), ise);
        }
    }

    private boolean isCompatible(Class<?> type) {
        return JsonStructure.class.isAssignableFrom(type);
    }

    private boolean isCompatible(MediaType mediaType) {
        return mediaType == null ||
                MediaType.APPLICATION_JSON_TYPE.getSubtype().equals(mediaType.getSubtype()) ||
                mediaType.getSubtype().endsWith(ANY_JSON);
    }
}
