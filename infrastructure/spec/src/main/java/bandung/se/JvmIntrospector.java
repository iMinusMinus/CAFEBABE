package bandung.se;

import java.lang.management.ManagementFactory;

public interface JvmIntrospector {

    long PID = Long.parseLong(
            System.getProperty("sun.java.launcher.pid",
                    ManagementFactory.getRuntimeMXBean().getName().split("@")[0])
    );

    static boolean isRecord(Class<?> klazz) {
        return false;
    }
}
