package bandung.ee.rs.codec;

import javax.annotation.Priority;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Priorities;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

/**
 * 处理byte[]/String/InputStream/File/Reader
 *
 * @see org.apache.cxf.jaxrs.provider.BinaryDataProvider
 *
 * @see org.springframework.http.converter.ByteArrayHttpMessageConverter
 * @see org.springframework.core.codec.ByteArrayEncoder
 * @see org.springframework.core.codec.ByteArrayDecoder
 * @see org.springframework.http.converter.ObjectToStringHttpMessageConverter
 * @see org.springframework.http.converter.StringHttpMessageConverter
 * @see org.springframework.core.codec.StringDecoder
 *
 * @author iMinusMinus
 * @date 2024-06-27
 */
@Provider
@Priority(Priorities.USER - 100)
@Consumes({MediaType.APPLICATION_OCTET_STREAM, MediaType.WILDCARD})
@Produces({MediaType.APPLICATION_OCTET_STREAM, MediaType.WILDCARD})
public class DefaultHttpMessageConverter<T> extends AbstractMessageBodyConverter implements MessageBodyReader<T>, MessageBodyWriter<T> {

    private final AtomicBoolean occupied = new AtomicBoolean(false);

    private final Predicate<Constructor<?>> STREAMING_SUB_CLASS_CTOR_PREDICATE = c ->
            Modifier.isPublic(c.getModifiers()) &&
            c.getParameterCount() == 1 &&
            InputStream.class.isAssignableFrom(c.getParameterTypes()[0]);

    private final byte[] buffer;

    public DefaultHttpMessageConverter() {
        this(StandardCharsets.UTF_8, 8192);
    }

    public DefaultHttpMessageConverter(Charset charset, int bufferSize) {
        super(charset, bufferSize, MediaType.APPLICATION_OCTET_STREAM_TYPE, MediaType.WILDCARD_TYPE);
        this.buffer = bufferSize < 0 ? new byte[-bufferSize] : null;
        this.bufferSize = bufferSize < 0 ? -bufferSize : bufferSize;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == byte[].class || type == String.class || File.class.isAssignableFrom(type) ||
                InputStream.class.isAssignableFrom(type) || Reader.class.isAssignableFrom(type) ||
                constructable(type);
    }

    private boolean constructable(Class<?> type) {
        return StreamingOutput.class.isAssignableFrom(type) &&
                Arrays.stream(type.getDeclaredConstructors()).anyMatch(STREAMING_SUB_CLASS_CTOR_PREDICATE);
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations,
                           MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                           InputStream entityStream) throws IOException, WebApplicationException {
        Charset charset = determineCharset(mediaType);
        Object obj;
        if (type == byte[].class || type == String.class) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(bufferSize);
            byte[] buffer = this.buffer != null && occupied.compareAndExchange(false, true) ?
                    this.buffer :
                    new byte[bufferSize];
            try {
                copy(buffer, entityStream, baos);
            } finally {
                if (buffer == this.buffer) {
                    occupied.compareAndSet(true, false);
                }
            }
            obj = baos.toByteArray();
        } else if (Reader.class.isAssignableFrom(type)) {
            obj = new InputStreamReader(entityStream, charset);
        } else if (File.class.isAssignableFrom(type)) {
            File tmp = new File(System.getProperty("java.io.tmpdir"), System.currentTimeMillis() + ".jaxrs.tmp");
            byte[] buffer = this.buffer != null && occupied.compareAndExchange(false, true) ?
                    this.buffer :
                    new byte[bufferSize];
            try (FileOutputStream fos = new FileOutputStream(tmp)) {
                copy(buffer, entityStream, fos);
                fos.flush();
            } catch (IOException ioe) {
                tmp.delete();
                throw ioe;
            } finally {
                if (buffer == this.buffer) {
                    occupied.compareAndSet(true, false);
                }
            }
            obj = tmp;
        } else if (type == StreamingOutput.class) {
            obj = new InternalStreamingOutput(entityStream, bufferSize);
        } else if (type == InputStream.class){
            obj = entityStream;
        } else { // subclass of StreamingOutput or InputStream
            Constructor<?> ctor = Arrays.stream(type.getDeclaredConstructors()).filter(STREAMING_SUB_CLASS_CTOR_PREDICATE)
                    .findFirst()
                    .orElseThrow();
            try {
                obj = ctor.newInstance(entityStream);
            } catch (Exception e) {
                throw new InternalServerErrorException(e.getMessage(), e);
            }
        }
        if (type == String.class) {
            obj = new String((byte[]) obj, charset);
        }
        return type.cast(obj);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == byte[].class || type == String.class || File.class.isAssignableFrom(type) ||
                InputStream.class.isAssignableFrom(type) || Reader.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        if (t instanceof StreamingOutput) {
            ((StreamingOutput) t).write(entityStream);
            return;
        } else if (t instanceof Reader) {
            Writer writer = new OutputStreamWriter(entityStream, determineCharset(mediaType));
            char[] buffer = new char[bufferSize];
            int len;
            while ((len = ((Reader) t).read(buffer)) != -1) {
                writer.write(buffer, 0, len);
            }
            writer.flush();
            ((Reader) t).close();
            return;
        }

        InputStream is;
        if (t instanceof byte[]) {
            is = new ByteArrayInputStream((byte[]) t);
        } else if (t instanceof String) {
            is = new ByteArrayInputStream(((String) t).getBytes(determineCharset(mediaType)));
        } else if (t instanceof File) {
            is = new FileInputStream((File) t);
        } else {
            is = (InputStream) t;
        }
        byte[] buffer = this.buffer != null && occupied.compareAndSet(false, true) ?
                this.buffer :
                new byte[bufferSize];
        try {
            copy(buffer, is, entityStream);
            is.close();
        } finally {
            if (buffer == this.buffer) {
                occupied.compareAndSet(true, false);
            }
        }
//        Object range = httpHeaders.getFirst("Range");
    }

    @Override
    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (t instanceof byte[]) {
            return ((byte[]) t).length;
        } else if (t instanceof File) {
            return ((File) t).length();
        }
        return -1;
    }

    private static class InternalStreamingOutput implements StreamingOutput {

        private final InputStream is;

        private final int bufferSize;

        InternalStreamingOutput(InputStream is, int bufferSize) {
            this.is = is;
            this.bufferSize = bufferSize;
        }

        @Override
        public void write(OutputStream output) throws IOException, WebApplicationException {
            AbstractMessageBodyConverter.copy(new byte[bufferSize], is, output);
        }
    }
}
