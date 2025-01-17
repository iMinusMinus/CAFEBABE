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
            "true            \r\n        \t", "\t\t\tfalse             \n", "\r\n    null",
            "{\"hi\":[2,{\"3\":[4,{\"key\":5,\"k\":[6,[7,[8,[9, [10, {\"11\":{\"12\":{\"13\":{\"14\":{\"15\":{\"16\":{\"17\":{\"18\":{\"19\":{\"20\":{\"21\":{\"22\":{\"23\":{\"24\":{\"25\":{\"26\":{\"27\":{\"28\":{\"29\":{\"30\":{\"31\":{\"32\":{\"33\":[34,[35,[36,[37,[38,[39,[40,[41,[42,[43,[44,[45,[46,[47,[48,[49,[50,[51,[52,[53,[54,[55,[56,[57,[58,[59,[60,[61,[62,[63]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]}}}}}}}}}}}}}}}}}}}}}}}]]]]]}]}]}"
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
            "null[]", "null:0", "nul",
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
    @ValueSource(strings = {"\"12qa\"1", "\"Hello World\",\"C\"", "\"str\"true", "\"str\"null", "\"str\":false"})
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

    @Test
    public void testTooManyCharacters() {
        Map<String, Integer> map = new HashMap<>();
        map.put(JsonFactoryAdapter.JSONP_CONFIG_PREFIX + "parser.maxTokenCount", 8);
        JsonFactoryAdapter adapter = new JsonFactoryAdapter(map);
        javax.json.JsonException e = Assertions.assertThrows(javax.json.JsonException.class, () -> {
            javax.json.stream.JsonParser parser = adapter.createParser(new ByteArrayInputStream("{\"key\":1}".getBytes(StandardCharsets.UTF_8)));
            while(parser.hasNext()) {
                parser.next();
                System.out.println(parser.getValue());
            }
        });
        System.out.println(e.getMessage());
    }

    @Test
    public void testTooManyNesting() {
        Map<String, Integer> map = new HashMap<>();
        map.put(JsonFactoryAdapter.JSONP_CONFIG_PREFIX + "parser.maxNestingDepth", 3);
        JsonFactoryAdapter adapter = new JsonFactoryAdapter(map);
        javax.json.JsonException e = Assertions.assertThrows(javax.json.JsonException.class, () -> {
            javax.json.stream.JsonParser parser = adapter.createParser(new ByteArrayInputStream("{\"industry\":[[\"bank\", \"insurance\"],[\"种植业\", \"林业\", [\"生猪养殖业\"]]]}".getBytes(StandardCharsets.UTF_8)));
            while(parser.hasNext()) {
                parser.next();
                System.out.println(parser.getValue());
            }
        });
        System.out.println(e.getMessage());
    }

    @Test
    public void testNameTooLong() {
        Map<String, Integer> map = new HashMap<>();
        map.put(JsonFactoryAdapter.JSONP_CONFIG_PREFIX + "parser.maxNameLength", 10);
        JsonFactoryAdapter adapter = new JsonFactoryAdapter(map);
        javax.json.JsonException e = Assertions.assertThrows(javax.json.JsonException.class, () -> {
            javax.json.stream.JsonParser parser = adapter.createParser(new ByteArrayInputStream("{\"longLongAgo\":\"羊肉串\"}".getBytes(StandardCharsets.UTF_8)));
            while(parser.hasNext()) {
                parser.next();
                System.out.println(parser.getValue());
            }
        });
        System.out.println(e.getMessage());
    }
    @Test
    public void testNameInRange() {
        Map<String, Integer> map = new HashMap<>();
        map.put(JsonFactoryAdapter.JSONP_CONFIG_PREFIX + "parser.maxNameLength", 8);
        map.put(JsonFactoryAdapter.JSONP_CONFIG_PREFIX + "parser.maxStringLength", 256);
        JsonFactoryAdapter adapter = new JsonFactoryAdapter(map);
        javax.json.JsonException e = Assertions.assertThrows(javax.json.JsonException.class, () -> {
            javax.json.stream.JsonParser parser = adapter.createParser(new ByteArrayInputStream("{\"桃花庵歌\":\"桃花坞里桃花庵，桃花庵里桃花仙。桃花仙人种桃树，又折花枝当酒钱。酒醒只在花前坐，酒醉还须花下眠。花前花后日复日，酒醉酒醒年复年。不愿鞠躬车马前，但愿老死花酒间。车尘马足贵者趣，酒盏花枝贫者缘。若将富贵比贫贱，一在平地一在天。若将贫贱比车马，他得驱驰我得闲。世人笑我忒疯颠，我咲世人看不穿。记得五陵豪杰墓，无酒无花锄作田\n桃花坞里桃花庵，桃花庵里桃花仙。桃花仙人种桃树，又摘桃花换酒钱。酒醒只在花前坐，酒醉还来花下眠。半醒半醉日复日，花落花开年复年。但愿老死花酒间，不愿鞠躬车马前。车尘马足富者趣，酒盏花枝贫者缘。若将富贵比贫者，一在平地一在天。若将贫贱比车马，他得驱驰我得闲。别人笑我忒风颠，我笑他人看不穿。不见五陵豪杰墓，无花无酒锄作田\"}".getBytes(StandardCharsets.UTF_8)));
            while(parser.hasNext()) {
                parser.next();
                System.out.println(parser.getValue());
            }
        });
        System.out.println(e.getMessage());
    }


    @Test
    public void testNumberTooLong() {
        String pi = "3.14159265358979323846264338327950288419716939937510582097494459230781640628620899862803482534211706798214808651328230664709384460955058223172535940812848111745028410270193852110555964462294895493038196442881097566593344612847564823378678316527120190914564856692346034861045432664821339360726024914127372458700660631558817488152092096282925409171536436789259036001133053054882046652138414695194151160943305727036575959195309218611738193261179310511854807446237996274956735188575272489122793818301194912";
        Map<String, Integer> map = new HashMap<>();
        map.put(JsonFactoryAdapter.JSONP_CONFIG_PREFIX + "parser.maxNumberLength", 512);
        JsonFactoryAdapter adapter = new JsonFactoryAdapter(map);
        javax.json.JsonException e = Assertions.assertThrows(javax.json.JsonException.class, () -> {
            javax.json.stream.JsonParser parser = adapter.createParser(new ByteArrayInputStream(pi.getBytes(StandardCharsets.UTF_8)));
            while(parser.hasNext()) {
                parser.next();
                System.out.println(parser.getValue());
            }
        });
        System.out.println(e.getMessage());
    }
}
