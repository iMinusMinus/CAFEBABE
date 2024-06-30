package bandung.ee.rs.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;

public class ByteArrayHttpMessageConverterTest {

    private ByteArrayHttpMessageConverter testObject = new ByteArrayHttpMessageConverter();

    @ParameterizedTest
    @ValueSource(strings = {"{\"json\":[{\"key\":\"值\"},{\"key\":\"a b\"}]}", "OK"})
    public void testReadWrite(String str) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        byte[] data = testObject.readFrom(byte[].class, byte[].class, new Annotation[0], MediaType.WILDCARD_TYPE, new MultivaluedHashMap<>(), bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        testObject.writeTo(data, byte[].class, byte[].class, new Annotation[0], MediaType.WILDCARD_TYPE, new MultivaluedHashMap<>(), baos);
        Assertions.assertEquals(str, baos.toString());
    }
}
