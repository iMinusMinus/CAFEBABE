package bandung.ee.rs.codec;

import javax.annotation.Priority;
import javax.ws.rs.Consumes;
import javax.ws.rs.Priorities;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 原样输入/输出字节流
 *
 * @see org.springframework.http.converter.ByteArrayHttpMessageConverter
 * @see org.springframework.core.codec.ByteArrayEncoder
 * @see org.springframework.core.codec.ByteArrayDecoder
 *
 * @author iMinusMinus
 * @date 2024-06-27
 */
@Provider
@Priority(Priorities.USER - 1)
@Consumes({MediaType.APPLICATION_OCTET_STREAM, MediaType.WILDCARD})
@Produces({MediaType.APPLICATION_OCTET_STREAM, MediaType.WILDCARD})
public class ByteArrayHttpMessageConverter extends AbstractMessageBodyConverter implements MessageBodyReader<byte[]>, MessageBodyWriter<byte[]> {

    public ByteArrayHttpMessageConverter() {
        this(StandardCharsets.UTF_8, 8192);
    }

    public ByteArrayHttpMessageConverter(Charset charset, int bufferSize) {
        super(charset, bufferSize, MediaType.APPLICATION_OCTET_STREAM_TYPE, MediaType.WILDCARD_TYPE);
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == byte[].class;
    }

    @Override
    public byte[] readFrom(Class<byte[]> type, Type genericType, Annotation[] annotations,
                           MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                           InputStream entityStream) throws IOException, WebApplicationException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bufferSize);
        entityStream.transferTo(baos);
        return baos.toByteArray();
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == byte[].class;
    }

    @Override
    public void writeTo(byte[] bytes, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        entityStream.write(bytes);
    }

    @Override
    public long getSize(byte[] t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return t.length;
    }
}
