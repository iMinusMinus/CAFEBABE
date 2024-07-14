package bandung.ee.rs.codec;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 处理XML
 *
 * @see org.apache.cxf.jaxrs.provider.SourceProvider
 * @see org.glassfish.jersey.message.internal.SourceProvider
 * @see org.springframework.http.converter.xml.SourceHttpMessageConverter
 *
 * @author iMinusMinus
 * @date 2024-06-29
 */
public class XmlSourceHttpMessageConverter<T> extends AbstractMessageBodyConverter implements MessageBodyReader<T>, MessageBodyWriter<T> {

    static final String ANY_XML = "+xml";

    private final DocumentBuilderFactory documentBuilderFactory;

    private final SAXParserFactory saxParserFactory;

    private final TransformerFactory transformerFactory;

    private final XMLInputFactory xmlInputFactory;

    private final XMLOutputFactory xmlOutputFactory;

    public XmlSourceHttpMessageConverter() {
        this(StandardCharsets.UTF_8, 64);
    }

    public XmlSourceHttpMessageConverter(Charset charset, int bufferSize) {
        this(charset, bufferSize,
                DocumentBuilderFactory.newInstance(), SAXParserFactory.newInstance(), TransformerFactory.newInstance(),
                XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
    }

    public XmlSourceHttpMessageConverter(Charset charset, int bufferSize,
                                         DocumentBuilderFactory documentBuilderFactory,
                                         SAXParserFactory saxParserFactory,
                                         TransformerFactory transformerFactory,
                                         XMLInputFactory xmlInputFactory,
                                         XMLOutputFactory xmlOutputFactory) {
        super(charset, bufferSize, MediaType.APPLICATION_XML_TYPE, MediaType.TEXT_XML_TYPE);
        this.documentBuilderFactory = documentBuilderFactory;
        this.saxParserFactory = saxParserFactory;
        this.transformerFactory = transformerFactory;
        this.xmlInputFactory = xmlInputFactory;
        this.xmlOutputFactory = xmlOutputFactory;
    }


    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return StreamSource.class.isAssignableFrom(type) ||
                DOMSource.class.isAssignableFrom(type) ||
                SAXSource.class.isAssignableFrom(type) ||
                StAXSource.class.isAssignableFrom(type) ||
                Document.class.isAssignableFrom(type);
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations,
                           MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                           InputStream entityStream) throws IOException, WebApplicationException {
        Object obj = null;
        try {
            if (DOMSource.class.isAssignableFrom(type) || Document.class.isAssignableFrom(type)) {
                Document document = documentBuilderFactory.newDocumentBuilder().parse(entityStream);
                obj = Document.class.isAssignableFrom(type) ? document : new DOMSource(document);
            } else if (SAXSource.class.isAssignableFrom(type)) {
                obj = new SAXSource(saxParserFactory.newSAXParser().getXMLReader(), new InputSource(entityStream));
            } else if (StreamSource.class.isAssignableFrom(type)) {
                obj = new StreamSource(entityStream);
            } else if (StAXSource.class.isAssignableFrom(type)) {
                Charset charset = determineCharset(mediaType);
                // XMLStreamReader会忽略外部命名空间元素的属性
                obj = new StAXSource(xmlInputFactory.createXMLEventReader(entityStream, charset.name()));
            }
        } catch (SAXException se) {
            throw new BadRequestException(se.getMessage(), se);
        } catch (ParserConfigurationException | XMLStreamException pe) {
            throw new InternalServerErrorException(pe.getMessage(), pe);
        }
        return type.cast(obj);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Source.class.isAssignableFrom(type) || Node.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        Object xml = t instanceof Node ? new DOMSource((Node) t) : t;
        if (xml instanceof DOMSource || xml instanceof SAXSource || xml instanceof StreamSource) {
            writeSource((Source) xml, type, genericType, annotations, mediaType, httpHeaders, entityStream);
        } else if (xml instanceof StAXSource) {
            writeStAXSource((StAXSource) t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
        }
    }

    protected void writeSource(Source source, Class<?> type, Type genericType, Annotation[] annotations,
                               MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                               OutputStream entityStream) throws IOException, WebApplicationException {
        try {
            StreamResult sr = new StreamResult(entityStream);
            if (source instanceof StreamSource) {
                StreamSource stream = (StreamSource) source;
                InputSource inputStream = new InputSource(stream.getInputStream());
                inputStream.setCharacterStream(inputStream.getCharacterStream());
                inputStream.setPublicId(stream.getPublicId());
                inputStream.setSystemId(source.getSystemId());
                source = new SAXSource(saxParserFactory.newSAXParser().getXMLReader(), inputStream);
            }
            transformerFactory.newTransformer().transform(source, sr);
        }  catch (ParserConfigurationException | SAXException | TransformerException e) {
            throw new InternalServerErrorException(e.getMessage(), e);
        }
    }

    protected void writeStAXSource(StAXSource source, Class<?> type, Type genericType, Annotation[] annotations,
                                   MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                                   OutputStream entityStream) throws IOException, WebApplicationException {
        Charset charset = determineCharset(mediaType);
        XMLStreamReader xmlStreamReader = source.getXMLStreamReader();
        XMLEventReader xmlEventReader = source.getXMLEventReader();
        if (xmlStreamReader != null) {
            XMLStreamWriter xmlStreamWriter = null;
            try {
                xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(entityStream, charset.name());
                copy(xmlStreamReader, xmlStreamWriter);
            } catch (XMLStreamException xe) {
                throw new InternalServerErrorException(xe.getMessage(), xe);
            } finally {
                close(xmlStreamReader, xmlStreamWriter);
            }
        } else if (xmlEventReader != null) {
            XMLEventWriter xmlEventWriter = null;
            try {
                xmlEventWriter = xmlOutputFactory.createXMLEventWriter(entityStream, charset.name());
                copy(xmlEventReader, xmlEventWriter);
            } catch (XMLStreamException xe) {
                throw new InternalServerErrorException(xe.getMessage(), xe);
            } finally {
                close(xmlEventReader, xmlEventWriter);
            }
        }

    }

    protected void copy(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartDocument(reader.getEncoding(), reader.getVersion()); // how about standalone
        while (reader.hasNext()) {
            int t = reader.next();
            switch (t) {
                // DTD part
                case XMLStreamConstants.DTD:
                    writer.writeDTD(reader.getText());
                    break;
                case XMLStreamConstants.ENTITY_DECLARATION: // when here
                    // do what
                    break;

                case XMLStreamConstants.NOTATION_DECLARATION: // when here, xml schema?
                    break;

                // xml content part
                case XMLStreamConstants.START_DOCUMENT: // never here
                    writer.writeStartDocument(reader.getVersion(), reader.getEncoding());
                    break;
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    writer.writeProcessingInstruction(reader.getPITarget(), reader.getPIData());
                    break;
                case XMLStreamConstants.NAMESPACE:
                    // do what
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    writer.writeStartElement(reader.getPrefix(), reader.getLocalName(), reader.getNamespaceURI());
                    break;
                case XMLStreamConstants.ATTRIBUTE:
                    writer.writeAttribute(reader.getPrefix(), reader.getNamespaceURI(), reader.getLocalName(), reader.getAttributeValue(reader.getNamespaceURI(), reader.getLocalName()));
                    break;
                case XMLStreamConstants.ENTITY_REFERENCE: // when here
                    writer.writeEntityRef(reader.getText());
                    break;
                case XMLStreamConstants.SPACE: // pass through
                case XMLStreamConstants.CHARACTERS:
                    writer.writeCharacters(reader.getText());
                    break;
                case XMLStreamConstants.CDATA:
                    writer.writeCData(reader.getText());
                    break;
                case XMLStreamConstants.COMMENT:
                    writer.writeComment(reader.getText());
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    writer.writeEndElement();
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    writer.writeEndDocument();
                    break;
            }
        }
    }

    protected void copy(XMLEventReader reader, XMLEventWriter writer) throws XMLStreamException {
        while (reader.hasNext()) {
            writer.add(reader.nextEvent());
        }
    }

    private void close(XMLStreamReader reader, XMLStreamWriter writer) {
        try {
            reader.close();
        } catch (XMLStreamException ignore) {
        }
        try {
            writer.flush();
            writer.close(); // we can close writer, but DO NOT close OutputStream
        } catch (XMLStreamException ignore) {
        }
    }

    private void close(XMLEventReader reader, XMLEventWriter writer) {
        try {
            reader.close();
        } catch (XMLStreamException ignore) {
        }
        try {
            writer.flush();
            writer.close();
        } catch (XMLStreamException ignore) {
        }
    }

}
