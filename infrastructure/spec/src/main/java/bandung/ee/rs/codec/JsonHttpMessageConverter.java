package bandung.ee.rs.codec;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Java对象和JSON互转
 *
 * @see org.apache.cxf.jaxrs.provider.jsrjsonb.JsrJsonbProvider
 *
 * @see org.springframework.http.converter.json.JsonbHttpMessageConverter
 * @see org.springframework.http.codec.json.Jackson2JsonEncoder
 * @see org.springframework.http.codec.json.Jackson2JsonDecoder
 *
 * @author iMinusMinus
 * @date 2024-06-23
 */
public class JsonHttpMessageConverter extends AbstractMessageBodyConverter implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

    private final Jsonb converter;

    public JsonHttpMessageConverter() {
        this(StandardCharsets.UTF_8, 64, JsonbBuilder.create());
    }

    public JsonHttpMessageConverter(Charset charset, int bufferSize, Jsonb converter) {
        super(charset, bufferSize, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_PATCH_JSON_TYPE);
        this.converter = converter;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return mediaType != null &&
                (mediaType.getSubtype().equals(MediaType.APPLICATION_JSON_TYPE.getSubtype()) || mediaType.getSubtype().endsWith(JsonStructHttpMessageConverter.ANY_JSON));
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations,
                           MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                           InputStream entityStream) throws IOException, WebApplicationException {
        try {
            return converter.fromJson(wrapInputStream(mediaType, entityStream), genericType != null ? genericType : type);
        } catch (JsonbException je) {
            throw new IOException(je.getMessage(), je);
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return mediaType != null &&
                (mediaType.getSubtype().equals(MediaType.APPLICATION_JSON_TYPE.getSubtype()) || mediaType.getSubtype().endsWith(JsonStructHttpMessageConverter.ANY_JSON));
    }

    @Override
    public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        try {
            if (genericType instanceof ParameterizedType) {
                converter.toJson(object, genericType, wrapOutputStream(mediaType, entityStream));
            } else {
                converter.toJson(object, wrapOutputStream(mediaType, entityStream));
            }
        } catch (JsonbException je) {
            throw new IOException(je.getMessage(), je);
        }
    }
}
