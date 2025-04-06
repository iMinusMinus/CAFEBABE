package std.ietf.http.oauth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
@ToString
public class ErrorResponse implements ServerResponse {

    /**
     * 简要错误信息，字符在 %x21 / %x23-5B / %x5D-7E 范围内
     */
    protected final String error;

    /**
     * 易于阅读的错误描述
     */
    protected String error_description;

    /**
     * 易于阅读的错误页面
     */
    protected String error_uri;

    public ErrorResponse(String error) {
        this(error, null, null);
    }

    public ErrorResponse(String error, String errorDescription) {
        this(error, errorDescription, null);
    }

    public ErrorResponse(String error, String errorDescription, URI errorUri) {
        this.error = Objects.requireNonNull(error);
        this.error_description = errorDescription;
        this.error_uri = Optional.ofNullable(errorUri).map(Object::toString).orElse(null);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}
