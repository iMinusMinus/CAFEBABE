package bandung.ee.rs.codec;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;

public class XmlSourceHttpMessageConverterTest {

    private XmlSourceHttpMessageConverter testObject = new XmlSourceHttpMessageConverter<>();

    private static String xml;

    @BeforeAll
    protected static void setUp() {
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<?xml-stylesheet type=\"text/css\" href=\"stylesheet.css\"?>\n" +
                "<!DOCTYPE Document [\n" +
                "<!ENTITY cite \"mean.leung@outlook.com\">\n" +
                "<!ELEMENT ele (me)>\n" +
                "<!ELEMENT me ANY>\n" +
                "<!ELEMENT nt (#PCDATA)>\n" +
                "<!ATTLIST nt manufacture CDATA #REQUIRED>\n" +
                "<!ATTLIST nt born CDATA #IMPLIED>\n" +
                "<!ATTLIST nt user CDATA #IMPLIED>\n" +
                "<!ELEMENT br EMPTY>\n" +
                "]>\n" +
                "<Document xmlns:c=\"http://java.sun.com/jsp/jstl/core\">\n" +
                "    <!-- element begin -->\n" +
                "    <ele>\n" +
                "        <me>\n" +
                "            <nt manufacture=\"Microsoft\">\n" +
                "                <![CDATA[1 + 1 > 1. 文以载道]]>\n" +
                "            </nt>\n" +
                "            <br/>\n" +
                "            <nt manufacture=\"Microsoft\">\n" +
                "                &lt;&cite;&gt;\n" +
                "            </nt>\n" +
                "            <fmt:message key=\"JAVASCRIPT_DISABLED_WARNING\" xmlns:fmt=\"http://java.sun.com/jsp/jstl/fmt\"/>\n" +
                "            <c:url value='/' />\n" +
                "        </me>\n" +
                "    </ele>\n" +
                "</Document>";
    }

    @Test
    public void testDocument() throws Exception { // not preserve DTD, line break not affect on instruction line
        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        Document source = (Document) testObject.readFrom(Document.class, Document.class, new Annotation[0], MediaType.TEXT_XML_TYPE, new MultivaluedHashMap<>(), bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        testObject.writeTo(source, Document.class, Document.class, new Annotation[0], MediaType.TEXT_XML_TYPE, new MultivaluedHashMap<>(), baos);
        System.out.println(baos);
    }

    @Test
    public void testDom() throws Exception { // not preserve DTD, line break not affect on instruction line
        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        DOMSource source = (DOMSource) testObject.readFrom(DOMSource.class, DOMSource.class, new Annotation[0], MediaType.TEXT_XML_TYPE, new MultivaluedHashMap<>(), bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        testObject.writeTo(source, DOMSource.class, DOMSource.class, new Annotation[0], MediaType.TEXT_XML_TYPE, new MultivaluedHashMap<>(), baos);
        System.out.println(baos);
    }

    @Test
    public void testSax() throws Exception { // not preserve DTD(but DOCTYPE declaration exist), line break not affect on instruction line
        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        SAXSource source = (SAXSource) testObject.readFrom(SAXSource.class, SAXSource.class, new Annotation[0], MediaType.TEXT_XML_TYPE, new MultivaluedHashMap<>(), bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        testObject.writeTo(source, SAXSource.class, SAXSource.class, new Annotation[0], MediaType.TEXT_XML_TYPE, new MultivaluedHashMap<>(), baos);
        System.out.println(baos);
    }

    @Test
    public void testStream() throws Exception { // not preserve DTD(but DOCTYPE declaration exist), line break not affect on instruction line
        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        StreamSource source = (StreamSource) testObject.readFrom(StreamSource.class, StreamSource.class, new Annotation[0], MediaType.TEXT_XML_TYPE, new MultivaluedHashMap<>(), bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        testObject.writeTo(source, StreamSource.class, StreamSource.class, new Annotation[0], MediaType.TEXT_XML_TYPE, new MultivaluedHashMap<>(), baos);
        System.out.println(baos);
    }

    @Test
    public void testStAX() throws Exception { // line break not affect on instruction line
        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        StAXSource source = (StAXSource) testObject.readFrom(StAXSource.class, StAXSource.class, new Annotation[0], MediaType.TEXT_XML_TYPE, new MultivaluedHashMap<>(), bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        testObject.writeTo(source, StAXSource.class, StAXSource.class, new Annotation[0], MediaType.TEXT_XML_TYPE, new MultivaluedHashMap<>(), baos);
        System.out.println(baos);
    }

}
