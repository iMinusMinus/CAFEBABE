package bandung.se.io;

import lombok.Getter;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 根据BOM识别输入流的字符集
 * @see org.eclipse.parsson.UnicodeDetectingInputStream
 */
public class UnicodeDetectingInputStream extends FilterInputStream {

    private static final byte NUL = (byte)0x00;

    @Getter
    private Charset charset;

    private byte[] buffer;

    private int pos;

    public UnicodeDetectingInputStream(InputStream in) {
        super(in);
        detect();
    }

    private void detect() {
        buffer = new byte[4];
        try {
            int i = in.read(buffer);
            if (i >= 2) {
                if (buffer[0] == (byte) 0xFE && buffer[1] == (byte) 0xFF) {
                    charset = StandardCharsets.UTF_16BE;
                    pos = 2;
                } else if (buffer[0] == (byte) 0xFF && buffer[1] == (byte) 0xFE) {
                    charset = StandardCharsets.UTF_16LE;
                    pos = 2;
                }
            }
            if (i >= 3 && buffer[0] == (byte) 0xEF && buffer[1] == (byte) 0xBB && buffer[2] == (byte) 0xBF) {
                charset = StandardCharsets.UTF_8;
                pos = 3;
            }
            if (i >= 4) {
                if (buffer[0] == NUL && buffer[1] == NUL && buffer[2] == (byte) 0xFE && buffer[3] == (byte) 0xFF) {
                    charset = Charset.forName("UTF-32BE");
                    pos = 4;
                } else if (buffer[0] == (byte) 0xFF && buffer[1] == (byte) 0xFE && buffer[2] == NUL && buffer[3] == NUL) {
                    charset = Charset.forName("UTF-32LE");
                    pos = 4;
                }
            }
            if (i < buffer.length) {
                byte[] buffer = new byte[i <= 0 ? 0 : i];
                System.arraycopy(this.buffer, 0, buffer, 0, buffer.length);
                this.buffer = buffer;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
    }

    @Override
    public int read() throws IOException {
        if (pos < buffer.length) {
            return buffer[pos++];
        }
        return in.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (pos < buffer.length) {
            if (len == 0) {
                return 0;
            }
            if (off < 0 || len < 0 || len > b.length -off) {
                throw new IndexOutOfBoundsException();
            }
            int start;
            for (start = off; start < len && pos < buffer.length; start++) {
                b[start] = buffer[pos++];
            }
            return start - off;
        }
        return in.read(b, off, len);
    }
}
