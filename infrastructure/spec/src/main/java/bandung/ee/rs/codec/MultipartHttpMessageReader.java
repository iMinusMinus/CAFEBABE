package bandung.ee.rs.codec;

import bandung.ee.servlet.ContentDisposition;
import bandung.ee.servlet.DefaultParts;

import javax.servlet.http.Part;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 处理POST请求，header中Content-Type为“multipart/form-data”的body。<br>
 * 如处理如下前端HTML表单代码：<br>
 * <pre>
 * &#60;form action="/upload" method="POST" enctype="multipart/form-data"&#62;
 *     &#60;input type="text" name="tags" /&#62;
 *     &#60;input type="file" name="file" /&#62;
 * &#60;/form&#62;
 * </pre>
 *
 * @see org.springframework.http.converter.FormHttpMessageConverter
 * @see org.springframework.http.codec.multipart.DefaultPartHttpMessageReader
 * @see org.springframework.http.codec.multipart.MultipartHttpMessageReader
 * @see org.springframework.http.codec.multipart.MultipartHttpMessageWriter
 *
 * @author iMinusMinus
 * @date 2024-06-23
 */
public class MultipartHttpMessageReader extends AbstractMessageBodyConverter implements MessageBodyReader<MultivaluedMap<String, Part>> {

    private final int fileSizeThreshold;

    private final String location;

    private final int maxFileSize;

    private final AtomicBoolean occupied = new AtomicBoolean(false);

    private final byte[] buffer;

    private static final byte[] BOUNDARY_MARKS = {'-', '-'};

    private static final byte[] BOUNDARY_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // DIGIT
                    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', // alpha
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', // ALPHA
                    '\'', '(', ')', '+', '_', ',', '-', '.', '/', ':', '=', '?', ' '};

    private static final int MAX_BOUNDARY_LENGTH = 70;

    /**
     * <a href="https://datatracker.ietf.org/doc/html/rfc2822#section-2.1.1">Line Length Limits</a>
     */
    private static final int LINE_LENGTH_LIMIT = 998;

    public MultipartHttpMessageReader() {
        this(8192/* nginx client_body_buffer_size */, System.getProperty("java.io.tmpdir"), 1024 * 1024/* nginx client_max_body_size 1m */);
    }

    public MultipartHttpMessageReader(int fileSizeThreshold, String location, int maxFileSize) {
        this(StandardCharsets.UTF_8, 8192, fileSizeThreshold, location, maxFileSize);
    }

    /**
     * 处理multipart/form-data请求
     *
     * @param charset 字符集
     * @param bufferSize 缓冲大小
     * @param fileSizeThreshold 留存内存最大值，超出则写到磁盘
     */
    public MultipartHttpMessageReader(Charset charset, int bufferSize, int fileSizeThreshold, String location, int maxFileSize) {
        super(charset, bufferSize, MediaType.MULTIPART_FORM_DATA_TYPE);
        if (bufferSize < 2 * LINE_LENGTH_LIMIT && -bufferSize < 2 * LINE_LENGTH_LIMIT) { // make sure dash-boundary/delimiter, header name in one buffer
            throw new IllegalArgumentException("buffer size too small");
        }
        assert fileSizeThreshold <= maxFileSize;
        this.fileSizeThreshold = fileSizeThreshold;
        this.location = location;
        this.maxFileSize = maxFileSize;
        this.buffer = bufferSize < 0 ? new byte[-bufferSize] : null;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return super.accept(mediaType);
    }

    // https://www.rfc-editor.org/rfc/rfc2046
    protected byte[] extractBoundaryParam(MediaType mediaType) throws WebApplicationException {
        String boundaryParameter = mediaType.getParameters().get("boundary");
        if (boundaryParameter == null || boundaryParameter.length() < 1) {
            throw new WebApplicationException("Content-Type 'multipart/form-data' require parameter 'boundary'", Response.Status.BAD_REQUEST);
        }
        if (boundaryParameter.startsWith("\"") && (boundaryParameter.length() <= 2 || !boundaryParameter.endsWith("\""))) {
            throw new WebApplicationException("Bad boundary", Response.Status.BAD_REQUEST);
        }
        boundaryParameter = boundaryParameter.startsWith("\"") && boundaryParameter.endsWith("\"") ?
                boundaryParameter.substring(1, boundaryParameter.length() - 1) : // eg, "boundary=\" text-has-space\""
                boundaryParameter;
        byte[] boundary = boundaryParameter.getBytes(StandardCharsets.US_ASCII);
        check(boundary);
        return boundary;
    }

    private byte[] merge(byte[]... old) {
        int len = 0;
        for (byte[] a : old) {
            len += a.length;
        }
        byte[] data = new byte[len];
        int offset = 0;
        for (byte[] a : old) {
            System.arraycopy(a, 0, data, offset, a.length);
            offset += a.length;
        }
        return data;
    }

    @Override
    public MultivaluedMap<String, Part> readFrom(Class<MultivaluedMap<String, Part>> type, Type genericType, Annotation[] annotations,
                                                 MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                                                 InputStream entityStream) throws IOException, WebApplicationException {
        byte[] boundary = extractBoundaryParam(mediaType);

        byte[] buffer = this.buffer != null && occupied.compareAndSet(false, true) ?
                this.buffer :
                new byte[bufferSize];
        Charset charset = determineCharset(mediaType);
        MultivaluedMap<String, Part> parts = new MultivaluedHashMap<>();
        Map<String, List<String>> partHeaders = new HashMap<>();

        ContentDisposition contentDisposition = null;
        byte[] retain = new byte[fileSizeThreshold];
        int writeAhead = 0;
        File tmp = null;

        byte[] dashBoundary = merge(BOUNDARY_MARKS, boundary, AbstractMessageBodyConverter.CRLF); // --boundary
        byte[] delimiter = merge(AbstractMessageBodyConverter.CRLF, BOUNDARY_MARKS, boundary); // \r\n--boundary

        byte[] toMatch = dashBoundary;
        boolean[] flags = new boolean[toMatch.length];

        boolean processDashBoundary = true, processHeader = false, processBody = false;
        int len;

        loopBody: while ((len = entityStream.read(buffer)) > 0) { // loop part
            int next = 0;

            loopBuffer: while (next < len) {
                // --boundary\r\n
                if (processDashBoundary) { // first
                    next = parse(buffer, next, len, toMatch, flags); // already checked, ignore content before dash boundary
                    processDashBoundary = !flags[flags.length - 1];
                    processHeader = flags[flags.length - 1];
                }
                if (processDashBoundary) {
                    continue;
                }
                // http: header; parameter\r\n
                // \r\n
                // body\r\n
                // --boundary--
                if (processHeader) { // second, multi
                    int hi;
                    for(hi = next; hi < len; hi++) {
                        if (buffer[hi] == ':') {
                            break;
                        }
                        if (buffer[hi] == AbstractMessageBodyConverter.CRLF[1] &&
                                hi == (next + 1) &&
                                buffer[next] == AbstractMessageBodyConverter.CRLF[0]) {
                            processHeader = false;
                            toMatch = delimiter;
                            flags = new boolean[toMatch.length];
                            next = hi + 1;
                            continue loopBuffer;
                        }
                    }
                    String headerName = new String(buffer, next, hi - next, StandardCharsets.US_ASCII);
                    toMatch = AbstractMessageBodyConverter.CRLF;
                    flags = new boolean[toMatch.length];

                    next = parse(buffer, hi + 1, len, toMatch, flags); // no CRLF in value, https://datatracker.ietf.org/doc/html/rfc2822#section-2.3
                    String headerValue = new String(buffer, hi + 1, next - hi - 1 - toMatch.length);
                    if (HttpHeaders.CONTENT_DISPOSITION.equals(headerName)) {
                        contentDisposition = ContentDisposition.valueOf(headerValue.trim());
                    }
                    // if("_charset_".equals(contentDisposition.getParameter("name")) { charset = Charset.forName("");}
                    partHeaders.computeIfAbsent(headerName, (key) -> new ArrayList<>()).add(headerValue.trim());
                    processBody = true;
                } else if (processBody){ // next
                    toMatch = delimiter;
                    flags = new boolean[toMatch.length];
                    int pos = parse(buffer, next, len, toMatch, flags);

                    if (writeAhead + pos - next > maxFileSize) {
                        throw new WebApplicationException("file too large", Response.Status.BAD_REQUEST);
                    }
                    int toCut = flags[flags.length - 1] ? toMatch.length : 0;
                    if (writeAhead + pos - next <= fileSizeThreshold) {
                        System.arraycopy(buffer, next, retain, writeAhead, pos - next);
                    } else if (tmp == null){ // exceed, once wrote
                        tmp = new File(location, contentDisposition.getFileName());
                        appendToFile(tmp, retain, 0, writeAhead, false);
                        appendToFile(tmp, buffer, next, pos- next - toCut, flags[flags.length - 1]);
                    } else {
                        appendToFile(tmp, buffer, next, pos - next - toCut, flags[flags.length - 1]);
                    }
                    writeAhead += pos - next;
                    next = pos;
                    if (!flags[flags.length - 1]) {
                        continue;
                    }
                    InputStream is = writeAhead > fileSizeThreshold ? new FileInputStream(tmp) : new ByteArrayInputStream(retain, 0, writeAhead);
                    Part part = contentDisposition.getFileName() != null ?
                            DefaultParts.filePart(contentDisposition.getName(), contentDisposition.getFileName(), partHeaders, is, tmp) :
                            DefaultParts.formPart(contentDisposition.getName(), partHeaders, is);
                    parts.add(contentDisposition.getName(), part);
                    writeAhead = 0;
                    processBody = false;
                    contentDisposition = null;
                } else { // separator or last
                    if (buffer[next] == AbstractMessageBodyConverter.CRLF[0] &&
                            next + 1 < len &&
                            buffer[next + 1] == AbstractMessageBodyConverter.CRLF[1]) {
                        partHeaders = new HashMap<>();
                        processDashBoundary = true;
                    } else if (buffer[next] == BOUNDARY_MARKS[0] &&
                            next + 1 < len &&
                            buffer[next + 1] == BOUNDARY_MARKS[1]) {
                        break loopBody; // ignore epilogue
                    } else {
                        throw new WebApplicationException(Response.Status.BAD_REQUEST);
                    }
                }
                reset(flags);
            }
        }
        if (this.buffer == buffer) {
            occupied.compareAndSet(true, false);
        }
        return parts;
    }

    private void appendToFile(File tmpFile, byte[] data, int offset, int len, boolean flush) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(tmpFile, true)) {
            fos.write(data, offset, len);
            if (flush) {
                fos.flush();
            }
        }
    }

    int parse(byte[] buffer, int start, int len, byte[] match, boolean[] flags) {
        int position;

        loop:
        for (position = start; position < len;) {
            for (int i = 0; i < match.length && position < len;) {
                flags[i] = (match[i] == buffer[position]);
                position++;
                if ((i ==0 && !flags[i]) || (i > 0 && !flags[i - 1])) {
                    reset(flags, i);
                    break;
                }
                i++;
                if (i == match.length) {
                    break loop;
                }
            }
        }
        return position;
    }

    void reset(boolean[] flags) {
        reset(flags, flags.length);
    }

    private void reset(boolean[] flags, int stop) {
        for (int i = 0; i < stop; i++) {
            flags[i] = false;
        }
    }

    private void check(byte[] data) {
        if (data.length == 0 || data.length > MAX_BOUNDARY_LENGTH) {
            throw new WebApplicationException("Illegal boundary length", Response.Status.BAD_REQUEST);
        }
        for (byte c : data) {
            if (c >= '0' && c <= '9') {
                continue;
            }
            if (c >= 'a' && c <= 'z') {
                continue;
            }
            if (c >= 'A' && c <= 'Z') {
                continue;
            }
            boolean found = false;
            for (int i = 52; i < BOUNDARY_CHARS.length; i++) {
                if (c == BOUNDARY_CHARS[i]) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new WebApplicationException("Illegal boundary character", Response.Status.BAD_REQUEST);
            }
        }
    }

}
