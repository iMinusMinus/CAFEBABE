package bandung.ee.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class Audit {

    /**
     * 审计类型
     */
    protected int type;

    /**
     * 连续失败次数
     */
    protected int failureTimes;

    /**
     * 上次失败时间
     */
    protected Instant lastFailureTime;
}
