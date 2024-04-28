package bandung.se;

import java.util.Objects;

/**
 * 对象包装器，用于跨线程、进程传递请求时，维护需要保留的信息
 *
 * @param <T>
 * @author iMinusMinus
 * @date 2024-04-28
 */
public class DiscriminatorObject<T> {

    /**
     * trace id
     */
    private final String traceId;

    /**
     * span id
     */
    private final String spanId;

    /**
     * tenant id
     */
    private final long tenantId;

    /**
     * flags: pressure test, gray test, etc.
     */
    private final int flags;

    /**
     * wrapped object
     */
    private final T raw;

    private static final int PRESSURE_TEST_FLAG = 1 << 31;

    private static final int GRAY_FLAG = 1 << 1;

    public static <T> DiscriminatorObject wrap(DiscriminatorObject downstream, T raw) {
        Objects.requireNonNull(downstream);
        return new DiscriminatorObject(downstream.traceId, downstream.spanId, downstream.tenantId, downstream.flags, raw);
    }

    public DiscriminatorObject(String traceId, String spanId, long tenantId, int flags, T raw) {
        this.traceId = traceId;
        this.spanId = spanId;
        this.tenantId = tenantId;
        this.flags = flags;
        this.raw = raw;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public long getTenantId() {
        return tenantId;
    }

    public T unwrap() {
        return raw;
    }

    public boolean isPressureTesting() {
        return (flags & PRESSURE_TEST_FLAG) != 0;
    }

    public boolean isGrayTesting() {
        return (flags & GRAY_FLAG) != 0;
    }
}
