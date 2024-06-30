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
import java.io.ByteArrayOutputStream;
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

    @Override
    public MultivaluedMap<String, Part> readFrom(Class<MultivaluedMap<String, Part>> type, Type genericType, Annotation[] annotations,
                                                 MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                                                 InputStream entityStream) throws IOException, WebApplicationException {
        // https://www.rfc-editor.org/rfc/rfc2046
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
        byte[] boundary = boundaryParameter.getBytes(StandardCharsets.UTF_8);
        check(boundary);

        byte[] buffer = this.buffer != null && occupied.compareAndSet(false, true) ?
                this.buffer :
                new byte[bufferSize];
        Charset charset = determineCharset(mediaType);
        MultivaluedMap<String, Part> parts = new MultivaluedHashMap<>();
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        ContentDisposition contentDisposition = null;
        Map<String, List<String>> partHeaders = new HashMap<>();
        File tmpFile = null;
        File dir = location != null && location.length() > 0 ? new File(location) : null;
        boolean dashBoundary = true, header = false, body = false;
        boolean[] flags = {false, false}; // foundCR, foundCRLF
        byte[] match = {AbstractMessageBodyConverter.CRLF[0], AbstractMessageBodyConverter.CRLF[1]};
        byte[] boundaryMark = new byte[BOUNDARY_MARKS.length + boundary.length];
        System.arraycopy(BOUNDARY_MARKS, 0, boundaryMark, 0, BOUNDARY_MARKS.length);
        System.arraycopy(boundary, 0, boundaryMark, BOUNDARY_MARKS.length, boundary.length);
        int fileSize = 0;
        int len;
        while ((len = entityStream.read(buffer)) > 0) { // loop part
            int next = 0;
            while (next < len) {
                next = parse(buffer, next, len, match, flags, line);
                // part body exceed file size threshold, write to file
                if (body && !flags[1] && line.size() + buffer.length > fileSizeThreshold) {
                    tmpFile = tmpFile == null ?
                            File.createTempFile(contentDisposition.getFileName(), null, dir) :
                            tmpFile;
                    fileSize += appendToFile(tmpFile, line.toString().getBytes(charset), false);
                    line.reset();
                    continue;
                }
                // boundary/part header/part body not found
                if (!flags[1]) {
                    continue;
                }
                if (dashBoundary) { // first
                    checkDashBoundary(boundary, line.toString(StandardCharsets.US_ASCII).substring(0, line.size() - match.length).getBytes(StandardCharsets.US_ASCII));
                    dashBoundary = false;
                    header = true;
                } else if (header) { // second, multi
                    byte[] hContent = line.toByteArray();
                    if (hContent.length == AbstractMessageBodyConverter.CRLF.length &&
                            hContent[0] == AbstractMessageBodyConverter.CRLF[0] &&
                            hContent[1] == AbstractMessageBodyConverter.CRLF[1]) {
                        line.reset();
                        header = false;
                        match = boundaryMark;
                        flags = new boolean[match.length];
                        continue;
                    }
                    int hi;
                    for (hi = 0; hi < hContent.length; hi++) {
                        if (hContent[hi] == ':') {
                            break;
                        }
                    }
                    String name = new String(hContent, 0, hi, charset);
                    String rawValue = new String(hContent, hi + 1, hContent.length - hi -match.length - 1, charset);
                    if (HttpHeaders.CONTENT_DISPOSITION.equals(name)) {
                        contentDisposition = ContentDisposition.valueOf(rawValue.trim());
                    }
                    // if("_charset_".equals(contentDisposition.getParameter("name")) {}
                    partHeaders.computeIfAbsent(name, (key) -> new ArrayList<>()).add(rawValue.trim());
                    body = true;
                } else if (body){ // next
                    int realLength = line.size() - match.length -AbstractMessageBodyConverter.CRLF.length; // drop ending CRLF and boundary
                    byte[] d = new byte[realLength];
                    System.arraycopy(line.toByteArray(), 0, d, 0, d.length);
                    if (fileSize +  realLength> maxFileSize) {
                        tmpFile.delete();
                        throw new WebApplicationException("file too large", Response.Status.REQUEST_ENTITY_TOO_LARGE);
                    }
                    InputStream is;
                    if (fileSize > 0 || realLength > fileSizeThreshold) {
                        tmpFile = tmpFile == null ?
                                File.createTempFile(contentDisposition.getFileName(), null, dir) :
                                tmpFile; // fileSize > 0
                        appendToFile(tmpFile, d, true);
                        fileSize = 0;
                        is = new FileInputStream(tmpFile);
                    } else {
                        is = new ByteArrayInputStream(d);
                    }
                    Part part = contentDisposition.getFileName() != null ?
                            DefaultParts.filePart(contentDisposition.getName(), contentDisposition.getFileName(), partHeaders, is, tmpFile) :
                            DefaultParts.formPart(contentDisposition.getName(), partHeaders, is, tmpFile);
                    parts.add(contentDisposition.getName(), part);
                    flags = new boolean[2];
                    body = false;
                    tmpFile = null;
                    contentDisposition = null;
                    match = new byte[] {AbstractMessageBodyConverter.CRLF[0], AbstractMessageBodyConverter.CRLF[1]};
                } else { // separator or last
                    byte[] sep = line.toByteArray();
                    if (sep.length != 2) {
                        throw new WebApplicationException(Response.Status.BAD_REQUEST);
                    } else if (next == len && (sep[0] != BOUNDARY_MARKS[0] || sep[1] != BOUNDARY_MARKS[1])) {
                        throw new WebApplicationException("Bad end boundary", Response.Status.BAD_REQUEST);
                    } else if (next < len && (sep[0] != AbstractMessageBodyConverter.CRLF[0] || sep[1] != AbstractMessageBodyConverter.CRLF[1])) {
                        throw new WebApplicationException("Bad part body separator", Response.Status.BAD_REQUEST);
                    }
                    partHeaders = new HashMap<>();
                    dashBoundary = true;
                }

                line.reset();
                reset(flags);
            }
        }
        if (this.buffer == buffer) {
            occupied.compareAndSet(true, false);
        }
        return parts;
    }

    private int appendToFile(File tmpFile, byte[] data, boolean flush) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(tmpFile, true)) {
            fos.write(data);
            if (flush) {
                fos.flush();
            }
            return data.length;
        }
    }

    int parse(byte[] buffer, int start, int len, byte[] match, boolean[] flags, ByteArrayOutputStream line) {
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

        line.write(buffer, start, position - start);
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
        if (data.length == 0 || data.length > 70) {
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

    private void checkDashBoundary(byte[] boundary, byte[] data) {
        if (data.length != boundary.length + BOUNDARY_MARKS.length) {
            throw new WebApplicationException("body boundary length not match", Response.Status.BAD_REQUEST);
        }
        for (int i = 0; i < data.length; i++) {
            if (i < BOUNDARY_MARKS.length && data[i] != BOUNDARY_MARKS[i]) {
                throw new WebApplicationException("body boundary must start with '--", Response.Status.BAD_REQUEST);
            } else if (i >= BOUNDARY_MARKS.length && data[i] != boundary[i - BOUNDARY_MARKS.length]) {
                throw new WebApplicationException("body boundary doesn't match", Response.Status.BAD_REQUEST);
            }
        }
    }

}
