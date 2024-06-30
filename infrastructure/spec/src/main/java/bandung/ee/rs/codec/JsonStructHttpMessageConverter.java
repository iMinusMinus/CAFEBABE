package bandung.ee.rs.codec;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import javax.json.stream.JsonParsingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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

    public JsonStructHttpMessageConverter() {
        this(StandardCharsets.UTF_8, 64);
    }

    public JsonStructHttpMessageConverter(Charset charset, int bufferSize) {
        super(charset, bufferSize);
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isCompatible(type) && isCompatible(mediaType);
    }

    @Override
    public JsonStructure readFrom(Class<JsonStructure> type, Type genericType, Annotation[] annotations,
                                  MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                                  InputStream entityStream) throws IOException, WebApplicationException {
        try (JsonReader reader = Json.createReader(entityStream)) {
            return reader.read();
        } catch (JsonParsingException jpe) {
            throw new WebApplicationException(jpe.getMessage(), jpe, Response.Status.BAD_REQUEST);
        } catch (JsonException je) {
            throw new IOException(je.getMessage(), je);
        } catch (IllegalStateException ise) {
            throw new WebApplicationException(ise.getMessage(), ise, Response.Status.INTERNAL_SERVER_ERROR);
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
        try (JsonWriter writer = Json.createWriter(entityStream)) {
            writer.write(jsonStructure);
        } catch (JsonException je) {
            throw new IOException(je.getMessage(), je);
        } catch (IllegalStateException ise) {
            throw new WebApplicationException(ise.getMessage(), ise, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isCompatible(Class<?> type) {
        return JsonStructure.class.isAssignableFrom(type) ||
                JsonObject.class.isAssignableFrom(type) ||
                JsonArray.class.isAssignableFrom(type);
    }

    private boolean isCompatible(MediaType mediaType) {
        return mediaType == null ||
                MediaType.APPLICATION_JSON_TYPE.getSubtype().equals(mediaType.getSubtype()) ||
                mediaType.getSubtype().endsWith(ANY_JSON);
    }
}
