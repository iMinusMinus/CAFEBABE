package bandung.ee.servlet;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class DefaultParts {

    public static Part formPart(String name, Map<String, List<String>> headers, InputStream is) {
        return new FormPart(name, headers, is);
    }

    public static Part filePart(String name, String filename, Map<String, List<String>> headers, InputStream is, File tmp) {
        return new FilePart(name, filename, headers, is, tmp);
    }

    static abstract class AbstractPart implements Part {

        protected final String name;

        protected final Map<String, List<String>> headers;

        protected final InputStream is;

        protected AbstractPart(String name, InputStream is) {
            this(name, Collections.emptyMap(), is);
        }

        protected AbstractPart(String name, Map<String, List<String>> headers, InputStream is) {
            this.name = name;
            this.headers = headers;
            this.is = is;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return is;
        }

        @Override
        public String getContentType() {
            return getHeader("Content-Type");
        }

        public String getName() {
            return name;
        }

        @Override
        public long getSize() {
            try {
                return is.available();
            } catch (IOException e) {
                return 0;
            }
        }

        @Override
        public String getHeader(String name) {
            for (String h : headers.keySet()) {
                if (h.equalsIgnoreCase(name)) {
                    List<String> values = headers.get(h);
                    return values != null && !values.isEmpty() ? values.get(0) : null;
                }
            }
            return null;
        }

        @Override
        public Collection<String> getHeaders(String name) {
            return headers.get(name.toLowerCase());
        }

        @Override
        public Collection<String> getHeaderNames() {
            return headers.keySet();
        }
    }

    static class FormPart extends AbstractPart {

        public FormPart(String name, InputStream is) {
            super(name, is);
        }

        public FormPart(String name, Map<String, List<String>> headers, InputStream is) {
            super(name, headers, is);
        }

        @Override
        public String getSubmittedFileName() {
            return null;
        }

        @Override
        public void write(String fileName) throws IOException {
        }

        @Override
        public void delete() throws IOException {
        }

    }

    static class FilePart extends AbstractPart {

        private final String filename;

        private File tmp;

        private FilePart(String name, String filename, Map<String, List<String>> headers, InputStream is, File tmp) {
            super(name, headers, is);
            this.filename = filename;
            this.tmp = tmp;
        }

        @Override
        public String getSubmittedFileName() {
            return filename;
        }

        @Override
        public void write(String fileName) throws IOException {
        }

        @Override
        public void delete() throws IOException {
            if (tmp != null) {
                tmp.delete();
            }
        }

    }

}
