package std.ietf.http.oauth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import std.ietf.http.jose.JsonWebToken;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc7662">OAuth 2.0 Token Introspection</a>规范定义了资源服务器向授权服务器
 * 查询令牌状态和元信息。POST请求，媒体类型为"application/x-www-form-urlencoded"，响应媒体类型为"application/json"。
 */
public interface Introspection {

    String ENDPOINT = "/introspect";

    /**
     * 获取关于令牌的元信息。
     * 请求体认证信息无效时返回401，Bearer无效返回401时需包含参数。认证通过，但token无效，或token不被允许获悉受保护资源情况时，不被视为错误，而只需呀正常返回（active为false）
     * @param credentials 客户端认证信息，如访问令牌(Bearer)、用户凭证(Basic)
     * @param request 令牌信息
     * @return 关于令牌的信息，active时才有JWT相关信息
     */
    ServerResponse introspect(String credentials, Request request);

    @Getter
    @Setter
    @ToString
    class Request {
        /**
         * token值，如access token、refresh token
         */
        protected final String token;

        /**
         * 供授权服务器优化查询用的提示
         */
        protected String tokenTypeHint;

        transient boolean illegal;

        public Request(String token) {
            this.token = token;
        }

        public Request(Map<String, List<String>> multiValueMap) {
            String token = null;
            try {
                token = Authorization.getFirstValue(multiValueMap, Revocation.PARAM_TOKEN);
                this.tokenTypeHint = Authorization.getFirstValue(multiValueMap, Revocation.PARAM_TOKEN_TYPE_HINT);
            } catch (IllegalArgumentException | NullPointerException e) {
                illegal = true;
            }
            this.token = token;
        }

        public Map<String, List<String>> toMap() {
            Map<String, List<String>> map = new HashMap<>();
            map.put(Revocation.PARAM_TOKEN, Collections.singletonList(token));
            if (tokenTypeHint != null) {
                map.put(Revocation.PARAM_TOKEN_TYPE_HINT, Collections.singletonList(tokenTypeHint));
            }
            return map;
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    class Response implements ServerResponse, JsonWebToken {
        /**
         * 签发主体
         */
        protected String iss;

        /**
         * 持有主体
         */
        protected String sub;

        /**
         * 目标受众：单个时类型为String，多个时类型为String集合/数组
         */
        protected Object aud;

        /**
         * 失效时间（单位：秒）
         */
        protected Integer exp;

        /**
         * 最早可用时间（单位：秒）
         */
        protected Integer nbf;

        /**
         * 签发时间（单位：秒）
         */
        protected Integer iat;

        /**
         * JWT标识
         */
        protected String jti;

        /**
         * token是否有效
         */
        protected final boolean active;

        protected String scope;

        protected String client_id;

        protected String username;

        protected String token_type;

        public Response(boolean active) {
            this.active = active;
        }

    }
}
