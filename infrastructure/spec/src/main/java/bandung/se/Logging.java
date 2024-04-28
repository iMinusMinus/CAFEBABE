package bandung.se;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * JUL extension
 *
 * @author iMinusMinus
 * @date 2024-04-28
 */
public class Logging extends LogManager {

    @Override
    public void readConfiguration() throws IOException, SecurityException {
        // if a configuration class is specified, load it and use it.
        String cname = System.getProperty("java.util.logging.config.class");
        if (cname != null) {
            try {
                // Instantiate the named class.  It is its constructor's
                // responsibility to initialize the logging configuration, by
                // calling readConfiguration(InputStream) with a suitable stream.
                try {
                    Class<?> clz = ClassLoader.getSystemClassLoader().loadClass(cname);
                    clz.newInstance();
                    return;
                } catch (ClassNotFoundException ex) {
                    Class<?> clz = Thread.currentThread().getContextClassLoader().loadClass(cname);
                    clz.newInstance();
                    return;
                }
            } catch (Exception ex) {
                System.err.println("Logging configuration class \"" + cname + "\" failed");
                System.err.println("" + ex);
                // keep going and useful config file.
            }
        }

        String fname = System.getProperty("java.util.logging.config.file");
        if (fname == null) {
            try (InputStream is = Logging.class.getClassLoader().getResourceAsStream("logging.properties")) {
                if (is != null) {
                    readConfiguration(is);
                    return;
                }
            }
        }
        if (fname == null) {
            fname = System.getProperty("java.home");
            if (fname == null) {
                throw new Error("Can't find java.home ??");
            }
            File f = new File(fname, "lib");
            f = new File(f, "logging.properties");
            fname = f.getCanonicalPath();
        }
        try (final InputStream in = new FileInputStream(fname)) {
            final BufferedInputStream bin = new BufferedInputStream(in);
            readConfiguration(bin);
        }
    }

    public static class ConsoleHandler extends StreamHandler {
        /**
         * Create a <tt>ConsoleHandler</tt> for <tt>System.out</tt>.
         * <p>
         * The <tt>ConsoleHandler</tt> is configured based on
         * <tt>LogManager</tt> properties (or their default values).
         *
         */
        public ConsoleHandler() {
            configure();
            setOutputStream(System.out);
        }

        private void configure() {
            LogManager manager = LogManager.getLogManager();
            String cname = getClass().getName();

            String level = manager.getProperty(cname +".level");
            Level lvl = level != null && Level.parse(level) != null ? Level.parse(level) : Level.INFO;
            setLevel(lvl);
            String filterClass = manager.getProperty(cname +".filter");
            Filter filter = null;
            if (filterClass != null) {
                try {
                    Class<?> filterKlazz = ClassLoader.getSystemClassLoader().loadClass(filterClass);
                    filter = (Filter) filterKlazz.newInstance();
                } catch (Exception ignore) {

                }
            }
            setFilter(filter);
            String formatKlazz = manager.getProperty(cname +".formatter");
            Formatter formatter = null;
            if (formatKlazz != null) {
                try {
                    Class<?> formatterKlazz = ClassLoader.getSystemClassLoader().loadClass(formatKlazz);
                    formatter = (Formatter) formatterKlazz.newInstance();
                } catch (Exception ignore) {

                }
            }
            if (formatter == null) {
                ClassicFormatter classic = new ClassicFormatter();
                classic.format = "\u001b[2;39m%1$tFT%1$tT.%1$tL\u001b[0;39m %4$#7s \u001b[30m[%11$s,%12$s]\u001b[0;39m \u001b[35m%7$s\u001b[0;39m \u001b[2;39m---\u001b[0;39m \u001b[2;39m[%8$15.15s]\u001b[0;39m \u001b[36m%2$-40.40s\u001b[0;39m \u001b[2;39m:\u001b[0;39m %5$s%n%6$s";
                formatter = classic;
            }
            setFormatter(formatter);
            try {
                setEncoding(manager.getProperty(cname +".encoding"));
            } catch (Exception ex) {
                try {
                    setEncoding(null);
                } catch (Exception ex2) {
                    // doing a setEncoding with null should always work.
                    // assert false;
                }
            }
        }
    }

    public static class ClassicFormatter extends Formatter {
        private static final String DEFAULT_FORMAT = "%1$tFT%1$tT.%1$tL %4$7s [%11$s,%12$s] %7$s --- [%8$15.15s] %2$-40.40s : %5$s%n%6$s";

        private String format;

        private static String pid;

        static {
            try {
                pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            } catch (Throwable t) {
                pid = "-";
            }
        }

        private final Date dat = new Date();

        public ClassicFormatter() {
            configure();
        }

        protected void configure() {
            LogManager manager = LogManager.getLogManager();
            String cname = getClass().getName();
            String fmt = manager.getProperty(cname + ".format");
            format = fmt != null ? fmt : DEFAULT_FORMAT;
        }

        @Override
        public String format(LogRecord record) {
            Object[] parameters = record.getParameters();
            String traceId = "";
            String spanId = "";
            if (parameters != null && parameters.length > 0 && parameters[parameters.length -1] instanceof DiscriminatorObject) {
                DiscriminatorObject discriminator = (DiscriminatorObject) parameters[parameters.length -1];
                traceId = discriminator.getTraceId();
                spanId = discriminator.getSpanId();
                parameters[parameters.length -1] = discriminator.unwrap();
            }

            String message = formatMessage(record);
            dat.setTime(record.getMillis());
            Object level = format.contains("\u001b[") ? new ColoredLevel(record.getLevel()) : record.getLevel().getName();
            int lineNumber = 0; // 无法从LogRecord获取源代码行数，获取源代码行数会降低性能
            int tid = record.getThreadID(); // XXX 非线程名，不好对应dump的线程
            String throwable = "";
            if (record.getThrown() != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                pw.println();
                record.getThrown().printStackTrace(pw);
                pw.close();
                throwable = sw.toString();
            }
            return String.format(format, dat, record.getSourceClassName(), record.getLoggerName(), level, message, throwable,
                    pid, tid, record.getSourceMethodName(), lineNumber, traceId, spanId);
        }
    }

    private static class ColoredLevel implements Formattable {

        private final Level level;

        private final int[] colors;

        public ColoredLevel(Level level) {
            this(level, new int[] {32, 32, 32, 32, 32, 32, 32, 32, 32, 33 ,31});
        }

        public ColoredLevel(Level level, int[] colors) {
            this.level = level;
            this.colors = colors;
        }

        @Override
        public void formatTo(java.util.Formatter formatter, int flags, int width, int precision) {
            String lvl = level.getName();
            if (precision > 0 && lvl.length() > precision) {
                lvl = lvl.substring(0, precision);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("\u001b[").append(colors[level.intValue() / 100]).append("m");
            int position = sb.length();
            sb.append(lvl);
            for (int i = lvl.length(); i < width; i++) {
                if ((flags & FormattableFlags.LEFT_JUSTIFY) != FormattableFlags.LEFT_JUSTIFY) {
                    sb.append(' ');
                } else {
                    sb.insert(position, ' ');
                }
            }
            sb.append("\u001b[0;39m");
            formatter.format(sb.toString());
        }
    }

}
