package bandung.ee.rs.codec;

import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * XML读写，处理类型如application/xml, text/xml, application/*+xml
 *
 * @param <T>
 *
 * @see org.springframework.http.codec.xml.Jaxb2XmlEncoder
 * @see org.springframework.http.codec.xml.Jaxb2XmlDecoder
 *
 * @author iMinusMinus
 * @date 2024-06-29
 */
public class XmlHttpMessageConverter<T> extends AbstractMessageBodyConverter implements MessageBodyReader<T>, MessageBodyWriter<T> {

    private final Function<Class<?>, JAXBContext> mapper = (key) -> {
        try {
            return JAXBContext.newInstance(key);
        } catch (JAXBException jaxb) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    };

    private final ConcurrentMap<Class<?>, JAXBContext> jaxbContexts;

    private final XMLInputFactory xmlInputFactory;

    @Getter
    @Setter
    private Marshaller.Listener marshallerListener;

    @Getter
    @Setter
    private Unmarshaller.Listener unmarshallerListener;

    public XmlHttpMessageConverter() {
        super(StandardCharsets.UTF_8, 64, MediaType.APPLICATION_XML_TYPE);
        this.jaxbContexts = null;
        this.xmlInputFactory = XMLInputFactory.newInstance();
        this.xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        this.xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        this.xmlInputFactory.setXMLResolver((publicID, systemID, base, ns) -> InputStream.nullInputStream());
    }

    public XmlHttpMessageConverter(Charset charset, int bufferSize, boolean cache, XMLInputFactory xmlInputFactory) {
        super(charset, bufferSize, MediaType.APPLICATION_XML_TYPE);
        this.jaxbContexts = cache ? new ConcurrentHashMap<>(64) : null;
        this.xmlInputFactory = xmlInputFactory;
    }

    private JAXBContext getJaxbContext(Class<?> clazz) throws WebApplicationException {
        return jaxbContexts == null ? mapper.apply(clazz) : jaxbContexts.computeIfAbsent(clazz, mapper);
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isCompatible(mediaType) && isCompatible(type);
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations,
                           MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                           InputStream entityStream) throws IOException, WebApplicationException {
        Unmarshaller unmarshaller = null;
        try {
            Charset charset = determineCharset(mediaType);
            XMLEventReader eventReader = xmlInputFactory.createXMLEventReader(entityStream, charset.name());
            Class<?> actualType = JAXBElement.class.isAssignableFrom(type) ?
                    (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0] :
                    type;
            JAXBContext jaxbContext = getJaxbContext(actualType);
            unmarshaller = jaxbContext.createUnmarshaller();
//            unmarshaller.setAdapter();
//            unmarshaller.setAttachmentUnmarshaller();
//            unmarshaller.setEventHandler();
            if (unmarshallerListener != null) {
                unmarshaller.setListener(unmarshallerListener);
            }
            unmarshaller.setProperty(Marshaller.JAXB_ENCODING, charset);
            JAXBElement<?> jaxbElement = unmarshaller.unmarshal(eventReader, actualType); // let unmarshaller handle jaxb annotations
            return type == actualType ? type.cast(jaxbElement.getValue()) : type.cast(jaxbElement);
        } catch (JAXBException je) {
            throw new WebApplicationException(je.getMessage(), je, Response.Status.INTERNAL_SERVER_ERROR);
        } catch (XMLStreamException xe) {
            throw new IOException(xe.getMessage(), xe);
        } finally {
            closeIfCloseable(unmarshaller);
        }

    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isCompatible(mediaType) && isCompatible(type);
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        try {
            Charset charset = determineCharset(mediaType);
            Class<?> actualType = JAXBElement.class.isAssignableFrom(type) ? ((JAXBElement) t).getDeclaredType() : type;
            JAXBContext jaxbContext = getJaxbContext(actualType);
            Marshaller marshaller = jaxbContext.createMarshaller();
//            marshaller.setAdapter();
//            marshaller.setAttachmentMarshaller();
//            marshaller.setEventHandler();
            if (marshallerListener != null) {
                marshaller.setListener(marshallerListener);
            }
            marshaller.setProperty(Marshaller.JAXB_ENCODING, charset);
            marshaller.marshal(t, entityStream);
        } catch (JAXBException je) {
            throw new WebApplicationException(je.getMessage(), je, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isCompatible(MediaType mediaType) {
        return mediaType == null ||
                MediaType.APPLICATION_XML_TYPE.getSubtype().equals(mediaType.getSubtype()) ||
                mediaType.getSubtype().endsWith(XmlSourceHttpMessageConverter.ANY_XML);
    }

    private boolean isCompatible(Class<?> type) {
        return type.isAnnotationPresent(XmlRootElement.class) ||
                type.isAnnotationPresent(XmlType.class) ||
                JAXBElement.class.isAssignableFrom(type);
    }

    private void closeIfCloseable(Object obj) {
        if (obj instanceof AutoCloseable) {
            try {
                ((AutoCloseable) obj).close();
            } catch (Exception ignore) {

            }
        }
    }
}
