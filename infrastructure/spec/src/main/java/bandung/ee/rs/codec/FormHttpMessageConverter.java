package bandung.ee.rs.codec;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Priorities;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 处理POST请求，header中Content-Type为“application/x-www-form-urlencoded”的body；
 * 对于GET请求，queryString的处理方式类似。<br>
 * 如处理如下前端HTML表单代码：<br>
 * <pre>
 * &#60;form action="/login" method="POST" enctype="application/x-www-form-urlencoded"&#62;
 *     &#60;input type="text" name="username" /&#62;
 *     &#60;input type="password" name="password" /&#62;
 * &#60;/form&#62;
 * </pre>
 *
 * @see org.apache.cxf.jaxrs.provider.FormEncodingProvider
 *
 * @see org.springframework.http.converter.FormHttpMessageConverter
 * @see org.springframework.http.codec.FormHttpMessageReader
 * @see org.springframework.http.codec.FormHttpMessageWriter
 *
 * @author iMinusMinus
 * @date 2024-06-22
 */
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_FORM_URLENCODED)
@Provider
@Priority(Priorities.ENTITY_CODER)
public class FormHttpMessageConverter<T> extends AbstractMessageBodyConverter implements MessageBodyReader<T>, MessageBodyWriter<T> {

    private final AtomicBoolean occupied = new AtomicBoolean(false);

    private final char[] buffer;

    @Inject
    public FormHttpMessageConverter() {
        this(StandardCharsets.UTF_8, 64);
    }

    @Inject
    public FormHttpMessageConverter(@Context Charset charset, @Context int bufferSize) {
        super(charset, bufferSize, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        this.buffer = bufferSize < 0 ? new char[-bufferSize] : null;
        this.bufferSize = bufferSize < 0 ? -bufferSize : bufferSize;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return acceptType(type, genericType) && (mediaType == null || super.accept(mediaType));
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations,
                         MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                         InputStream entityStream) throws IOException, WebApplicationException {
        Charset charset = determineCharset(mediaType);

        char[] buffer = this.buffer != null && occupied.compareAndSet(false, true) ?
                this.buffer :
                new char[bufferSize];
        try {
            // The implementation should not close the input stream.
            Reader reader = new InputStreamReader(entityStream, charset);
            MultivaluedMap<String, String> kv = new MultivaluedHashMap<>();
            StringBuilder key = new StringBuilder();
            StringBuilder value = new StringBuilder();
            boolean[] flags = {false, false}; // keyFound, valueFound
            int len;
            while ((len = reader.read(buffer)) > 0) {
                parse(kv, buffer, len, key, value, flags, charset);
            }
            if (key.length() != 0) { // last pair
                kv.add(URLDecoder.decode(key.toString(), charset.name()), URLDecoder.decode(value.toString(), charset.name()));
            }
            return type == Form.class ? type.cast(new Form(kv)) : type.cast(kv);
        } finally {
            if (this.buffer == buffer) {
                occupied.compareAndSet(true, false);
            }
        }
    }

    void parse(MultivaluedMap<String, String> kv, char[] buffer, int len,
               StringBuilder key, StringBuilder value, boolean[] flags, Charset charset) {
        for (int position = 0; position < len; position++) {
            switch (buffer[position]) {
                case '=':
                    flags[0] = true; // key found
                    break;
                case '&':
                    flags[1] = true; // value found
                    break;
                default:
                    if (!flags[0]) {
                        key.append(buffer[position]);
                    } else  {
                        value.append(buffer[position]);
                    }
            }
            if (flags[0] && flags[1]) {
                try {
                    kv.add(URLDecoder.decode(key.toString(), charset.name()), URLDecoder.decode(value.toString(), charset.name()));
                } catch (UnsupportedEncodingException impossible) {
                }
                key.delete(0, key.length());
                value.delete(0, value.length());
                flags[0] = false;
                flags[1] = false;
            }
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return acceptType(type, genericType) && (mediaType == null || super.accept(mediaType));
    }

    @Override
    public void writeTo(T form, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        MultivaluedMap<String, String> map;
        if (form instanceof Form) {
            map = ((Form) form).asMap();
        } else {
            map = (MultivaluedMap) form;
        }
        Charset charset = determineCharset(mediaType);
        for (Iterator<Map.Entry<String, List<String>>> it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, List<String>> entry = it.next();
            String key = URLEncoder.encode(entry.getKey(), charset.name()); // ignore Encoded
            for (Iterator<String> values = entry.getValue().iterator(); values.hasNext();) {
                entityStream.write(key.getBytes(charset));
                entityStream.write('=');
                entityStream.write(URLEncoder.encode(values.next(), charset.name()).getBytes(charset)); // ignore Encoded
                if (values.hasNext()) {
                    entityStream.write('&');
                }
            }
            if (it.hasNext()) {
                entityStream.write('&');
            }
        }

    }

    private boolean acceptType(Class<?> type, Type genericType) {
        if (type == Form.class) {
            return true;
        }
        if (MultivaluedMap.class.isAssignableFrom(type)) {
            if (type == genericType) {
                return true;
            }
            if (genericType instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
                return types[0] == String.class && types[1] == String.class;
            }
        }
        return false;
    }
}
