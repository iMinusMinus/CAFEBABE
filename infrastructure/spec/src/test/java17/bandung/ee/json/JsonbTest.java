package bandung.ee.json;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.Date;

public class JsonbTest {

    @JsonbTypeInfo(value = {
            @JsonbSubtype(alias = "concrete", type = JsonbTest.Concrete.class),
            @JsonbSubtype(alias = "infer", type = JsonbTest.Base.class)
    })
    interface Poly {
        long getId();
    }

    @Getter
    @Setter
    @ToString
    protected static class Concrete implements Poly {

        private long id;

        private String name;

        private Date date;
    }

    @Getter
    @Setter
    @ToString
    @JsonbTypeInfo(key = "@class", value = {
            @JsonbSubtype(alias = "a", type = JsonbTest.ConcreteA.class),
    })
    protected static class Base implements Poly {

        protected long id;

        protected ZonedDateTime timestamp;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    protected static class ConcreteA extends Base {

        @JsonbCreator
        ConcreteA(@JsonbProperty(value = "signature") @JsonbTypeDeserializer(value = ByteADeserializer.class) byte[] signature) {
            this.signature = signature;
        }

        private byte[] signature;
    }

    protected static class ByteADeserializer implements JsonbDeserializer<byte[]> {
        @Override
        public byte[] deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            JsonParser.Event event = parser.next();
            if (event == JsonParser.Event.VALUE_NULL) {
                return new byte[0];
            } else if (event == JsonParser.Event.VALUE_STRING) {
                return parser.getString().getBytes(StandardCharsets.UTF_8);
            }
            throw new JsonbException("");
        }
    }

    @Getter
    @Setter
    @ToString
    protected static class Pojo {

        private Poly poly;

        private Base base;

        private boolean mark;
    }

    @Test
    public void testSubType() {
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).build();
        Pojo pojo = new Pojo();
        ConcreteA c = new ConcreteA("Hello World".getBytes(StandardCharsets.UTF_8));
        c.setId(1L);
        c.setTimestamp(ZonedDateTime.now());
        pojo.setPoly(c);
        ConcreteA b = new ConcreteA("道可道，非常道".getBytes(StandardCharsets.UTF_8));
        b.setId(3L);
        pojo.setBase(b);
        pojo.setMark(true);

        String json = jsonb.toJson(pojo, Pojo.class);
        System.out.println(json);
        Assertions.assertTrue(json.contains("@type"));
        Assertions.assertTrue(json.contains("@class"));

        pojo = jsonb.fromJson(json, Pojo.class);
        Assertions.assertTrue(pojo.getPoly() instanceof ConcreteA);
        Assertions.assertTrue(pojo.getBaseok() instanceof ConcreteA);
    }

    protected record TestRecord(long timestamp, byte[] data) {}

    @Test
    public void testRecord() {
        Constructor[] ctors = TestRecord.class.getDeclaredConstructors();
        Jsonb jsonb = JsonbBuilder.newBuilder(BindingProvider.class.getName()).build();
        TestRecord r = new TestRecord(System.currentTimeMillis(), "long long ago".getBytes(StandardCharsets.UTF_8));
        String json = jsonb.toJson(r);
        System.out.println(json);
        Assertions.assertTrue(json.contains("timestamp"));
        Assertions.assertTrue(json.contains("data"));

        TestRecord t = jsonb.fromJson(json, TestRecord.class);
        Assertions.assertEquals(r.timestamp(), t.timestamp());
        Assertions.assertArrayEquals(r.data(), t.data());
    }
}