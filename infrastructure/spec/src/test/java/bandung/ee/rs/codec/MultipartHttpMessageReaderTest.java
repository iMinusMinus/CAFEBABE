package bandung.ee.rs.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.servlet.http.Part;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MultipartHttpMessageReaderTest {

    private MultipartHttpMessageReader testObject = new MultipartHttpMessageReader(512, System.getProperty("java.io.tmpdir"), 1024);

    @Test
    public void testRead() throws Exception {
        String boundary = "Qz8TkuAj37SPNHwEIoHSrwOIrJq3-sl9";
        String bodyHeader = "--Qz8TkuAj37SPNHwEIoHSrwOIrJq3-sl9\r\n" +
                "Content-Type: image/png\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"centos.png\"\r\n" +
                "Content-Length: 803\r\n\r\n";
        String base64 = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXBIWXMAAAFjAAABYwGNYDK3AAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAABd0RVh0VGl0bGUAVGhlIENlbnRPUyBTeW1ib2yF4TxCAAACjUlEQVQ4jZ1TSUxTURQ9/7dApRMd4JfSIoUyxA5GBUIcYkQiWxKLunLnhh0sjazYwwabGN0ZDWGBIGCMQUJi1BhAMKUMbV9bBEpbOvg76OdTi6s2NGgX3tXNveeee17euUCR4P1To7zvlb0YpsjwtJ2dvnHEvr6e4bdnn/4LR+USf3x+kOWIBQCqzpiaudiiWhTfKaMicurn73OcuPZiJLQV3gKAihqFo7ajaQgAhDkCliOWxcBIb6OqBxuRcaT4PVwR33UJfHX05+c+o6QqqmXMeiuZd+LC/Wt5BfRJOY2qHrDcNnSyy6ekaqy1SAVZNHSaCup5ArXI2hRMLUFaWo04R9Ci7s2DjF1mJPfiEFfJEHTsQGHUtBQQeD9sPg56SGXycBck/gaSEg1+cF4ole0SeX2zJLEbg5iRw7uwjvRBAqtfiGpmZvUZAFBkwWlfffnpQeXV8qTAkIzlmKu1JrFUIZ3LhBXH4f10J/n2PZHruQ9S6rcfPYqB/u4nQoqiswAQDcgy0f0KPq/7klKUTotwlolCxrECRu8qzbUCv0RCAMhmj2kKANzv10ZWXKE7j4bfaQHgtq0Vfl8EfQPWfZMuli1xvqih5UbwZAwAQKQPA37OOmWztfbRANB409xfY9KF9HplflinV2LTvZlOx5fTtFiDLOtBacM90OVatJxvDttsrX0Fv7C2EfC0tRngJQfQ6ZWYmvyafw2/PQNarEE2QSBkOkAnHO6/+mBiYhkGgxpLi75TPshEVkBL9OB9EwX1vJXn5tYHCQlZAMBsYYw7UQcjVLhEderkcTXFHKrk7eFs1OkGAIG83lGmuzV0asvJcEcnh8cd3Udjjq6MK/KfF+mOTNtJbHa0GOYP42QK/fYhovYAAAAASUVORK5CYII=";
        byte[] img = Base64.getDecoder().decode(base64);
        byte[] h = bodyHeader.getBytes(StandardCharsets.US_ASCII);
        byte[] end = ("\r\n--" + boundary + "--").getBytes(StandardCharsets.US_ASCII);
        byte[] data = new byte[h.length + img.length + end.length];
        System.arraycopy(h, 0, data, 0, h.length);
        System.arraycopy(img, 0, data, h.length, img.length);
        System.arraycopy(end, 0, data, h.length + img.length, end.length);


        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("boundary", boundary);
        MediaType mime = new MediaType(MediaType.MULTIPART_FORM_DATA_TYPE.getType(), MediaType.MULTIPART_FORM_DATA_TYPE.getSubtype(), parameters);
        Class<MultivaluedMap<String, Part>> klazz = (Class<MultivaluedMap<String, Part>>) ((ParameterizedType) ((ParameterizedType) MultipartHttpMessageReader.class.getGenericInterfaces()[0]).getActualTypeArguments()[0]).getRawType();
        MultivaluedMap<String, Part> result = testObject.readFrom(klazz, klazz, new Annotation[0], mime, new MultivaluedHashMap<>(), bais);
        Assertions.assertNotNull(result.get("file"));
        Assertions.assertEquals(803, result.get("file").get(0).getSize());
    }
}
