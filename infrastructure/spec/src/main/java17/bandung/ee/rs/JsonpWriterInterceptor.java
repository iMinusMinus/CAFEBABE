package bandung.ee.rs;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;

/**
 * JSONP处理
 * @see org.apache.cxf.jaxrs.provider.jsonp.JsonpJaxrsWriterInterceptor
 * @date 2025-03-02
 * @author iMinusMinus
 */
public class JsonpWriterInterceptor implements WriterInterceptor {

    public static final String JAVASCRIPT_SUB_TYPE = "x-javascript";

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        MediaType mediaType = context.getMediaType();
        String callback = findJsonpCallback(context.getAnnotations());
        boolean jsonpMediaType = MediaType.APPLICATION_JSON_TYPE.getType().equals(mediaType.getType()) &&
                JAVASCRIPT_SUB_TYPE.equals(mediaType.getSubtype());
        if (jsonpMediaType && callback != null) {
//            context.setAnnotations(); // remove JSONP
            context.getOutputStream().write(callback.getBytes(StandardCharsets.UTF_8));
            context.getOutputStream().write('(');
            context.setMediaType(MediaType.APPLICATION_JSON_TYPE);
        }
        context.proceed();
        if (jsonpMediaType && callback != null) {
            context.getOutputStream().write(')');
//            context.setAnnotations(); // restore
            context.setMediaType(mediaType);
        }
    }

    protected String findJsonpCallback(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof JSONP) {
                return ((JSONP) annotation).value();
            }
        }
        return null;
    }
}
