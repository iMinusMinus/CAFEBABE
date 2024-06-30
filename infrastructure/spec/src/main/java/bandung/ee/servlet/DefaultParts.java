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

    public static Part formPart(String name, Map<String, List<String>> headers, InputStream is, File tmp) {
        return new FormPart(name, headers, is, tmp);
    }

    public static Part filePart(String name, String filename, Map<String, List<String>> headers, InputStream is, File file) {
        return new FilePart(name, filename, headers, is, file);
    }

    static abstract class AbstractPart implements Part {

        protected final String name;

        protected final Map<String, List<String>> headers;

        protected final InputStream is;

        protected final File tmp;

        protected AbstractPart(String name, InputStream is) {
            this(name, Collections.emptyMap(), is, null);
        }

        protected AbstractPart(String name, Map<String, List<String>> headers, InputStream is, File tmp) {
            this.name = name;
            this.headers = headers;
            this.is = is;
            this.tmp = tmp;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return is;
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
        public void write(String fileName) throws IOException {

        }

        @Override
        public void delete() throws IOException {
            tmp.delete();
        }

        @Override
        public String getHeader(String name) {
            List<String> list = headers.get(name.toLowerCase());
            return list != null && !list.isEmpty() ? list.get(0) : null;
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

        public FormPart(String name, Map<String, List<String>> headers, InputStream is, File file) {
            super(name, headers, is, file);
        }

        @Override
        public String getContentType() {
            return "application/x-www-form-urlencoded";
        }

        @Override
        public String getSubmittedFileName() {
            return null;
        }


    }

    static class FilePart extends AbstractPart {

        private final String filename;

        private FilePart(String name, String filename, Map<String, List<String>> headers, InputStream is, File tmp) {
            super(name, headers, is, tmp);
            this.filename = filename;
        }

        @Override
        public String getContentType() {
            return "multipart/form-data";
        }

        @Override
        public String getSubmittedFileName() {
            return filename;
        }

    }

}
