package std.ietf.http.oauth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc7009">OAuth 2.0 Token Revocation</a>
 */
public interface Revocation {

    String ENDPOINT = "/revoke";

    String PARAM_TOKEN = "token";
    String PARAM_TOKEN_TYPE_HINT = "token_type_hint";
    String PARAM_CALLBACK = "callback";

    String TOKEN_TYPE_ACCESS_TOKEN = "access_token";
    String TOKEN_TYPE_REFRESH_TOKEN = "refresh_token";

    /**
     * 当撤销成功或令牌不存在时视为成功
     * @param credentials 可选的客户端凭证(如Basic认证信息)
     * @param request 要撤销的令牌信息，包含令牌、令牌类型（可选）
     * @return token不存在或撤销成功时返回null，否则返回ErrorResponse
     */
    ServerResponse revoke(String credentials, Request request);

    @Getter
    @Setter
    @ToString
    class Request {

        protected final String token;

        /**
         * 用于授权服务器优化查询需要解授权的token，如access_token、refresh_token
         */
        protected String tokenTypeHint;

        /**
         * JSONP场景下作为回调参数
         */
        protected String callback;

        public Request(String token) {
            this.token = token;
        }

        protected String clientId;

        protected String clientSecret;

        transient boolean illegal;

        public Request(Map<String, List<String>> multiValueMap) {
            String token = null;
            try {
                token = Authorization.getFirstValue(multiValueMap, PARAM_TOKEN);
                this.tokenTypeHint = Authorization.getFirstValue(multiValueMap, PARAM_TOKEN_TYPE_HINT);
                this.callback = Authorization.getFirstValue(multiValueMap, PARAM_CALLBACK);
                this.clientId = Authorization.getFirstValue(multiValueMap, Authorization.PARAM_CLIENT_ID);
                this.clientSecret = Authorization.getFirstValue(multiValueMap, Authorization.PARAM_CLIENT_SECRET);
            } catch (IllegalArgumentException | NullPointerException e) {
                illegal = true;
            }
            this.token = token;
        }

        public Map<String, List<String>> toMap() {
            Map<String, List<String>> map = new HashMap<>();
            map.put(PARAM_TOKEN, Collections.singletonList(token));
            if (tokenTypeHint != null) {
                map.put(PARAM_TOKEN_TYPE_HINT, Collections.singletonList(tokenTypeHint));
            }
            if (callback != null) {
                map.put(PARAM_CALLBACK, Collections.singletonList(callback));
            }
            if (clientId != null) {
                map.put(Authorization.PARAM_CLIENT_ID, Collections.singletonList(clientId));
            }
            if (clientSecret != null) {
                map.put(Authorization.PARAM_CLIENT_SECRET, Collections.singletonList(clientSecret));
            }
            return map;
        }

    }
}
