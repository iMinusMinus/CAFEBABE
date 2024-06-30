package bandung.ee.rs.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class FormHttpMessageReaderTest {

    private FormHttpMessageConverter testObject = new FormHttpMessageConverter(StandardCharsets.UTF_8, 64);

    private static Object obj;

    private static Type type;

    @BeforeAll
    protected static void setUp() {
        obj = new FormHttpMessageConverter<MultivaluedMap<String, String>>(){};
        type = ((ParameterizedType) obj.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @ParameterizedTest
    @ValueSource(strings = {"a=b", "a=","a=b&a=c", "a=哈哈", "name=kiss%20my%20ass'%20%261%3D1;%20drop%20table%20user;'"})
    public void testParseFormData(String enc) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(enc.getBytes(StandardCharsets.UTF_8));
        Object body = testObject.readFrom(Form.class, null, new Annotation[0],
                MediaType.APPLICATION_FORM_URLENCODED_TYPE, new MultivaluedHashMap<>(), bais);
        Assertions.assertEquals(1, ((Form) body).asMap().size());

        bais.reset();
        MultivaluedMap m = (MultivaluedMap) testObject.readFrom(MultivaluedMap.class, type, new Annotation[0],
                MediaType.APPLICATION_FORM_URLENCODED_TYPE, new MultivaluedHashMap<>(), bais);
        System.out.println(m);
        Assertions.assertEquals(1, m.size());
    }

    @Test
    public void testWriteSimpleFormData() throws Exception {
        Form f = new Form("a", "b");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        testObject.writeTo(f, Form.class, Form.class, new Annotation[0],
                MediaType.APPLICATION_FORM_URLENCODED_TYPE, new MultivaluedHashMap<>(), baos);
        Assertions.assertEquals("a=b", baos.toString());
    }

    @Test
    public void testWriteMultiValueFormData() throws Exception {
        Form f = new Form("key", "value");
        f.param("key", "another");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        testObject.writeTo(f.asMap(), MultivaluedMap.class, type, new Annotation[0],
                MediaType.APPLICATION_FORM_URLENCODED_TYPE, new MultivaluedHashMap<>(), baos);
        Assertions.assertEquals("key=value&key=another", baos.toString());
    }

    @Test
    public void testWriteMultiKeyFormData() throws Exception {
        Form f = new Form("key", "value");
        f.param("test", "why");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        testObject.writeTo(f, Form.class, Form.class, new Annotation[0],
                MediaType.APPLICATION_FORM_URLENCODED_TYPE, new MultivaluedHashMap<>(), baos);
        Assertions.assertEquals("key=value&test=why", baos.toString());
    }

}
