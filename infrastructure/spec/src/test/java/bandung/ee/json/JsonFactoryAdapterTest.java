package bandung.ee.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JsonFactoryAdapterTest {

    private static Class<? extends RuntimeException> jsonGenerationException;

    @BeforeAll
//    @EnabledOnJre(value = {JRE.JAVA_8}) // not work
    protected static void setUp() throws Exception {
        System.out.println(JRE.JAVA_8.isCurrentVersion());
    }

    @Test
    public void testGenerate() {
        Map<String, Object> map = new HashMap<>();
        map.put("javax.json.stream.JsonGenerator.prettyPrinting", 4);
        map.put("jakarta.json.stream.JsonGenerator.prettyPrinting", 4);
        JsonFactoryAdapter adapter = new JsonFactoryAdapter(map);
        StringWriter sw = new StringWriter();
        adapter.createGenerator(sw)
                .writeStartObject()
                    .write("str", "BMP string")
                    .write("number", Long.MAX_VALUE - 100L)
                    .write("bool", true)
                    .writeStartArray("aio")
                        .write(1L << 52)
                        .write(1L << 54)
                        .write(3.1415926)
                        .write(BigInteger.TEN)
                        .write(BigDecimal.ZERO)
                    .writeEnd()
                    .writeStartArray("aia")
                        .writeStartArray()
                            .write(-1)
                            .write(1964)
                            .write(65537)
                        .writeEnd()
                        .writeStartArray()
                            .write(true)
                            .write(false)
                        .writeEnd()
                    .writeEnd()
                    .writeNull("na")
                    .writeStartObject("on")
                        .write("bName", true)
                        .write("sName", "plane0字符")
                    .writeEnd()
                .writeEnd();
        System.out.println(sw);
    }

    private static boolean jsonpClassExist() {
        try {
            jsonGenerationException = (Class<? extends RuntimeException>) Class.forName("javax.json.stream.JsonGenerationException");
            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError notJre8) {
            try {
                jsonGenerationException = (Class<? extends RuntimeException>) Class.forName("jakarta.json.stream.JsonGenerationException");
                return true;
            } catch (ClassNotFoundException | NoClassDefFoundError missingJar) {
                return false;
            }
        }
    }

    @Test
    public void testCharacterBeyondPlane0() {
        JsonFactoryAdapter adapter = new JsonFactoryAdapter(Collections.emptyMap());
        StringWriter sw = new StringWriter();
        adapter.createGenerator(sw)
                .writeStartArray()
                    .writeStartObject()
                        .writeKey("oia")
                        .writeStartObject()
                            .writeStartArray("oio")
                                .write("Āƀ\u0ffd") // latin extend, Tibetan
                                .write("\uf337\u4DBF\u9FFF\uFDFF") // CJK, Arabic
                            .writeEnd()
                        .writeEnd()
                    .writeEnd()
                    .writeStartArray()
                        .write(new String(Character.toChars(0x1fa60))) // SMP plane 1
                        .write(new String(Character.toChars(0x2ee5d))) // SIP plane 2
                        .write(new String(Character.toChars(0x3232b))) // TIP plane 3
                    .writeEnd()
                .writeEnd();
        System.out.println(sw);
    }

    @Test
    @EnabledIf(value = "jsonpClassExist")
    public void testGenerationException() {
        JsonFactoryAdapter adapter = new JsonFactoryAdapter(Collections.emptyMap());
        StringWriter sw1 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw1).writeStartObject().writeEnd().writeStartObject().writeEnd());
        StringWriter sw2 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw2).writeStartObject().writeStartObject().writeEnd().writeEnd());

        StringWriter sw3 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw3).writeStartArray().writeStartObject("key").writeNull().writeEnd().writeEnd());
        StringWriter sw4 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw4).writeStartObject().writeStartObject("key").writeNull().writeEnd().writeEnd());
        StringWriter sw5 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw5).writeStartObject().writeKey("key").writeStartObject("name").writeNull().writeEnd());

        StringWriter sw6 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw6).writeKey("name").write(BigDecimal.TEN));
        StringWriter sw7 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw7).writeStartArray().writeKey("key").writeNull().writeEnd());
        StringWriter sw8 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw8).writeStartObject().writeKey("key").writeKey("name").writeEnd());
        StringWriter sw9 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw9).writeStartObject("name").writeKey("key").writeNull().writeEnd());

        StringWriter sw10 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw10).writeStartObject().writeEnd().writeStartArray().writeEnd());
        StringWriter sw11 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw11).writeStartArray().writeEnd().writeStartObject().writeEnd());
        StringWriter sw12 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw12).writeStartArray().writeEnd().writeStartArray().writeEnd());

        StringWriter sw13 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw13).writeStartArray("name").writeEnd());
        StringWriter sw14 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw14).writeStartObject("key").writeStartArray("name").writeEnd().writeEnd());

        StringWriter sw15 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw15).write("name", "value").writeEnd());
        StringWriter sw16 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw16).writeStartObject("key").write("name", "value").writeEnd());
        StringWriter sw17 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw17).writeStartArray().write("name", "value").writeEnd());

        StringWriter sw18 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw18).writeStartArray().writeEnd().writeEnd());
        StringWriter sw19 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw19).writeStartObject().writeEnd().writeEnd());
        StringWriter sw20 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw20).writeStartObject().writeKey("name").writeEnd().writeEnd());
        StringWriter sw21 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw21).writeStartObject().writeKey("name").writeStartArray().writeEnd().writeEnd().writeEnd());

        StringWriter sw22 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw22).writeStartObject().write(false).writeEnd());

        StringWriter sw23 = new StringWriter();
        Assertions.assertThrows(jsonGenerationException, () -> adapter.createGenerator(sw23).writeStartObject().writeKey("name").write(1984).close());
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", " false ", "  null  ", " 3.14159265358 ", " -273   ", "     11111111111 ", "\"letter字符\""})
    @EnabledOnJre(value = {JRE.JAVA_8})
    public void testParseValue(String in) {
        JsonFactoryAdapter adapter = new JsonFactoryAdapter(Collections.emptyMap());
        javax.json.stream.JsonParser parser = adapter.createParser(new ByteArrayInputStream(in.getBytes(StandardCharsets.UTF_8)));
        Assertions.assertTrue(parser.hasNext());
        System.out.println(parser.next());
        System.out.println(parser.getValue());
    }

    @Test
//    @EnabledOnJre(value = {JRE.JAVA_8})
    public void testDetectEncoding() {
        JsonFactoryAdapter adapter = new JsonFactoryAdapter(Collections.emptyMap());
        byte[] raw = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF, ' ', '\"', 'a', '\\', 'b', '\\', 't', '\\', '\\', 'Z', '\\', 'f', '\\', 'u', '6', 'f', '2', '2', '\"', '\t'};
        javax.json.stream.JsonParser parser = adapter.createParser(new ByteArrayInputStream(raw));
        parser.next();
        Assertions.assertEquals("a\b\t\\Z\f漢", ((javax.json.JsonString) parser.getValue()).getString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"oio\":{\"bn\":true,\"na\":null,\"eo\":{},\"ea\":[],\"sn\":\"汉A\",\"on\":{\"nest\":2.7}}, \"aio\":[1,false,\"a😈\",{\"oia\":{\"key\":\"1.23e+45\"}}]}",
            "[ ]", "[\r\n]", "[\t    ]",
            " {    }", "\n{\t}    ", "\n{    \t    \n}\r\n",
            "true            \r\n        \t", "\t\t\tfalse             \n", "\r\n    null"
    })
//    @EnabledOnJre(value = {JRE.JAVA_8})
    public void testParseJsonStructure(String str) {
        JsonFactoryAdapter adapter = new JsonFactoryAdapter(Collections.emptyMap());
        try (javax.json.stream.JsonParser parser = adapter.createParser(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)))) {
            while (parser.hasNext()) {
                parser.next();
                System.out.println(parser.getValue());
            }
        } catch (javax.json.stream.JsonParsingException p) {
            System.out.println(p.getLocation());
            throw p;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "True", "tr ue", "true1", "true \"str\"",
            "fAlse", "fal\r\nse", "false{}",
            "null[]", "null:0",
            "true, false, null"
    })
//    @EnabledOnJre(value = {JRE.JAVA_8})
    public void testParseBadJsonValue(String badNumber) {
        JsonFactoryAdapter adapter = new JsonFactoryAdapter(Collections.emptyMap());
        javax.json.stream.JsonParsingException e = Assertions.assertThrows(javax.json.stream.JsonParsingException.class, () -> {
            javax.json.stream.JsonParser parser = adapter.createParser(new ByteArrayInputStream(badNumber.getBytes(StandardCharsets.UTF_8)));
            while(parser.hasNext()) {
                parser.next();
                System.out.println(parser.getValue());
            }
        });
        System.out.println(e.getLocation());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0.", "1.2e", "0.1E-", "1.2e+",
            "1.e23", "1.E456",
            "0.1e2E3", "1.2e3.4", "12.34.56", "1.1e2+345", "1.2E3-456",
            "1, 2, null"
    })
//    @EnabledOnJre(value = {JRE.JAVA_8})
    public void testParseBadJsonNumber(String badNumber) {
        JsonFactoryAdapter adapter = new JsonFactoryAdapter(Collections.emptyMap());
        javax.json.stream.JsonParsingException e = Assertions.assertThrows(javax.json.stream.JsonParsingException.class, () -> {
            javax.json.stream.JsonParser parser = adapter.createParser(new ByteArrayInputStream(badNumber.getBytes(StandardCharsets.UTF_8)));
            while(parser.hasNext()) {
                parser.next();
                System.out.println(parser.getValue());
            }
        });
        System.out.println(e.getLocation());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"12qa\"1", "\"Hello World\",\"C\"", "\"str\"true", "\"str\"null", "\"str\"false"})
//    @EnabledOnJre(value = {JRE.JAVA_8})
    public void testParseBadJsonString(String badString) {
        JsonFactoryAdapter adapter = new JsonFactoryAdapter(Collections.emptyMap());
        javax.json.stream.JsonParsingException e = Assertions.assertThrows(javax.json.stream.JsonParsingException.class, () -> {
            javax.json.stream.JsonParser parser = adapter.createParser(new ByteArrayInputStream(badString.getBytes(StandardCharsets.UTF_8)));
            while(parser.hasNext()) {
                parser.next();
                System.out.println(parser.getValue());
            }
        });
        System.out.println(e.getLocation());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{},{}", "{},[]","[],{}", "true,[]", "false,{}", "{},null", "{}false", "[]true", "[{}null]", "[1[]]",
            "{null}", "{,\"key\":null}", "{\"key\":[],}", "{]", "[true}", "[\"key\":\"value\\\\]\"",
            "{\"key\",\"name\":\"value\"}", "{\"key\":\"name\":\"value\"}", "{\"k\":\"v\",\"key\"}",
            "{\"key\"1:2}", "{\"key\":\"str\"1}", "{\"key\":true\r\nfalse}", "{\"key\":1",
            "[1:{}]", "[]]", "[1.2E3,]", "[,true]", "[true1]", "[null\n{}]", "[1, null, false    \r\n"
    })
//    @EnabledOnJre(value = {JRE.JAVA_8})
    public void testParseBadJsonStructure(String badStructure) {
        JsonFactoryAdapter adapter = new JsonFactoryAdapter(Collections.emptyMap());
        javax.json.stream.JsonParsingException e = Assertions.assertThrows(javax.json.stream.JsonParsingException.class, () -> {
            javax.json.stream.JsonParser parser = adapter.createParser(new ByteArrayInputStream(badStructure.getBytes(StandardCharsets.UTF_8)));
            while(parser.hasNext()) {
                parser.next();
                System.out.println(parser.getValue());
            }
        });
        System.out.println(e.getLocation());
    }
}
