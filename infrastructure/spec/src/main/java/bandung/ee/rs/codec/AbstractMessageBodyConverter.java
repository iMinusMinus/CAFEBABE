package bandung.ee.rs.codec;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

public abstract class AbstractMessageBodyConverter {

    protected final MediaType[] accepts;

    protected final Charset charset;

    /**
     * 缓冲区大小，允许修改.建议为2倍内存页大小，通常32位系统使用8K缓冲，64位系统使用16K缓冲
     */
    protected int bufferSize;

    public static final byte[] CRLF = {'\r', '\n'};

    protected AbstractMessageBodyConverter(Charset charset, int bufferSize, MediaType... accepts) {
        this.charset = charset;
        this.bufferSize = bufferSize;
        this.accepts = accepts;
    }

    protected boolean accept(MediaType mediaType) {
        for (MediaType allowed : accepts) {
            if (allowed.isCompatible(mediaType)) {
                return true;
            }
        }
        return false;
    }

    protected Charset determineCharset(MediaType mediaType) {
        Charset charset;
        String cs = mediaType.getParameters().get(MediaType.CHARSET_PARAMETER);
        try {
            if (cs != null) {
                charset = Charset.forName(cs);
            } else {
                charset = this.charset;
            }
        } catch (IllegalArgumentException iae) {
            throw new BadRequestException("unsupported charset " + cs);
        }
        return charset;
    }

    protected Reader wrapInputStream(MediaType mediaType, InputStream is) {
        Reader reader = new InputStreamReader(is, determineCharset(mediaType));
        return bufferSize <= 0 ? reader : new BufferedReader(reader, bufferSize);
    }

    protected Writer wrapOutputStream(MediaType mediaType, OutputStream os) {
        Writer writer = new OutputStreamWriter(os, determineCharset(mediaType));
        return bufferSize <= 0 ? writer : new BufferedWriter(writer);
    }

    static void copy(byte[] buffer, InputStream is, OutputStream os) throws IOException {
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
    }

    public void setBufferSize(int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("buffer size must great than zero");
        }
        this.bufferSize = bufferSize;
    }
}
