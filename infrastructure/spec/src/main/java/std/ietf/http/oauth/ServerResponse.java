package std.ietf.http.oauth;

public interface ServerResponse {

    default boolean isSuccess() {
        return true;
    }
}
