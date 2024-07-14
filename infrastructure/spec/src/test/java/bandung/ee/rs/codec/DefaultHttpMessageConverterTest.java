package bandung.ee.rs.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;

public class DefaultHttpMessageConverterTest {

    private final DefaultHttpMessageConverter testObject = new DefaultHttpMessageConverter();

    @ParameterizedTest
    @ValueSource(strings = {"{\"json\":[{\"key\":\"值\"},{\"key\":\"a b\"}]}", "OK"})
    public void testReadWrite(String str) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        Object data = testObject.readFrom(byte[].class, byte[].class, new Annotation[0], MediaType.WILDCARD_TYPE, new MultivaluedHashMap<>(), bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        testObject.writeTo(data, byte[].class, byte[].class, new Annotation[0], MediaType.WILDCARD_TYPE, new MultivaluedHashMap<>(), baos);
        Assertions.assertEquals(str, baos.toString());

        bais = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        data = testObject.readFrom(String.class, String.class, new Annotation[0], MediaType.WILDCARD_TYPE, new MultivaluedHashMap<>(), bais);
        baos = new ByteArrayOutputStream();
        testObject.writeTo(data, String.class, String.class, new Annotation[0], MediaType.WILDCARD_TYPE, new MultivaluedHashMap<>(), baos);
        Assertions.assertEquals(str, baos.toString());

        bais = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        data = testObject.readFrom(InputStream.class, InputStream.class, new Annotation[0], MediaType.WILDCARD_TYPE, new MultivaluedHashMap<>(), bais);
        baos = new ByteArrayOutputStream();
        testObject.writeTo(data, InputStream.class, InputStream.class, new Annotation[0], MediaType.WILDCARD_TYPE, new MultivaluedHashMap<>(), baos);
        Assertions.assertEquals(str, baos.toString());

        bais = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        data = testObject.readFrom(Reader.class, Reader.class, new Annotation[0], MediaType.WILDCARD_TYPE, new MultivaluedHashMap<>(), bais);
        baos = new ByteArrayOutputStream();
        testObject.writeTo(data, Reader.class, Reader.class, new Annotation[0], MediaType.WILDCARD_TYPE, new MultivaluedHashMap<>(), baos);
        Assertions.assertEquals(str, baos.toString());

        bais = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        data = testObject.readFrom(File.class, File.class, new Annotation[0], MediaType.WILDCARD_TYPE, new MultivaluedHashMap<>(), bais);
        baos = new ByteArrayOutputStream();
        testObject.writeTo(data, File.class, File.class, new Annotation[0], MediaType.WILDCARD_TYPE, new MultivaluedHashMap<>(), baos);
        ((File) data).delete();
        Assertions.assertEquals(str, baos.toString());

        bais = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        data = testObject.readFrom(StreamingOutput.class, StreamingOutput.class, new Annotation[0], MediaType.WILDCARD_TYPE, new MultivaluedHashMap<>(), bais);
        baos = new ByteArrayOutputStream();
        testObject.writeTo(data, StreamingOutput.class, StreamingOutput.class, new Annotation[0], MediaType.WILDCARD_TYPE, new MultivaluedHashMap<>(), baos);
        Assertions.assertEquals(str, baos.toString());
    }
}
