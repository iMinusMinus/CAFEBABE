package std.ietf.http.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc6749">The OAuth 2.0 Authorization Framework</a>定义了四种角色：
 * <ul>
 *     <li>资源所有者（resource owner）是一个能授权访问受保护资源的实体，通常是一个终端用户。</li>
 *     <li>资源服务器（resource server）是一个托管受保护资源的服务器，能够接受请求并进行响应。</li>
 *     <li>客户端（client）是一个代表资源所有者获取授权访问受保护资源的应用，它可以是设备、也可以是服务器。</li>
 *     <li>授权服务器（authorization server）是一个在认证资源所有者成功并获取授权后给客户端发布access token的服务。</li>
 * </ul>
 * 协议流程如下：
 * <pre>
 +--------+                               +---------------+
 |        |--(A)- Authorization Request ->|   Resource    |
 |        |                               |     Owner     |
 |        |<-(B)-- Authorization Grant ---|               |
 |        |                               +---------------+
 |        |
 |        |                               +---------------+
 |        |--(C)-- Authorization Grant -->| Authorization |
 | Client |                               |     Server    |
 |        |<-(D)----- Access Token -------|               |
 |        |                               +---------------+
 |        |
 |        |                               +---------------+
 |        |--(E)----- Access Token ------>|    Resource   |
 |        |                               |     Server    |
 |        |<-(F)--- Protected Resource ---|               |
 +--------+                               +---------------+
 * </pre>
 * 规范定义了四种权限的授予方式（注意HTTP头设置为不缓存： "Cache-Control: no-store", "Pragma: no-cache"）：
 * <ul>
 *     <li>授权码模式（authorization code）使用授权服务器作为资源所有者和客户端的中介（资源所有者的凭证不被客户端知悉）。
 *     客户端将资源所有者重定向到授权服务器，授权服务器对资源所有者进行认证，认证通过后授权服务器携带授权码将资源所有者重定向回客户端。
 *     客户端使用授权码向授权服务器获取访问令牌，请求类型为"application/x-www-form-urlencoded"，返回类型为"application/json"。
 *     <pre>
 +----------+
 | Resource |
 |   Owner  |
 |          |
 +----------+
 ^
 |
 (B)
 +----|-----+          Client Identifier      +---------------+
 |         -+----(A)-- & Redirection URI ---->|               |
 |  User-   |                                 | Authorization |
 |  Agent  -+----(B)-- User authenticates --->|     Server    |
 |          |                                 |               |
 |         -+----(C)-- Authorization Code ---<|               |
 +-|----|---+                                 +---------------+
 |    |                                         ^      v
 (A)  (C)                                        |      |
 |    |                                         |      |
 ^    v                                         |      |
 +---------+                                      |      |
 |         |>---(D)-- Authorization Code ---------'      |
 |  Client |          & Redirection URI                  |
 |         |                                             |
 |         |<---(E)----- Access Token -------------------'
 +---------+       (w/ Optional Refresh Token)
 *     </pre>
 *     </li>
 *     <li>隐含模式（implicit）专为浏览器简化使用而优化流程：不提供授权码，而是直接提供访问令牌给客户端。
 *     320重定向，返回类型为"application/x-www-form-urlencoded"。
 *     <pre>
 +----------+
 | Resource |
 |  Owner   |
 |          |
 +----------+
 ^
 |
 (B)
 +----|-----+          Client Identifier     +---------------+
 |         -+----(A)-- & Redirection URI --->|               |
 |  User-   |                                | Authorization |
 |  Agent  -|----(B)-- User authenticates -->|     Server    |
 |          |                                |               |
 |          |<---(C)--- Redirection URI ----<|               |
 |          |          with Access Token     +---------------+
 |          |            in Fragment
 |          |                                +---------------+
 |          |----(D)--- Redirection URI ---->|   Web-Hosted  |
 |          |          without Fragment      |     Client    |
 |          |                                |    Resource   |
 |     (F)  |<---(E)------- Script ---------<|               |
 |          |                                +---------------+
 +-|--------+
 |    |
 (A)  (G) Access Token
 |    |
 ^    v
 +---------+
 |         |
 |  Client |
 |         |
 +---------+
 *     </pre>
 *     </li>
 *     <li>密码模式(resource owner password credentials)用于资源所有者高度信任客户端时使用（如客户都安为操作系统）。
 *     客户端直接对资源所有者提供的用户名、密码信息认证（不在此规范内），通过后请求认证服务器获取访问令牌。
 *     请求类型为"application/x-www-form-urlencoded"，返回类型为"application/json"
 *     <pre>
 +----------+
 | Resource |
 |  Owner   |
 |          |
 +----------+
 v
 |    Resource Owner
 (A) Password Credentials
 |
 v
 +---------+                                  +---------------+
 |         |>--(B)---- Resource Owner ------->|               |
 |         |         Password Credentials     | Authorization |
 | Client  |                                  |     Server    |
 |         |<--(C)---- Access Token ---------<|               |
 |         |    (w/ Optional Refresh Token)   |               |
 +---------+                                  +---------------+
 *     </pre>
 *     </li>
 *     <li>客户端模式（client credentials）用于以下场景：客户端是资源所有者；所访问的受保护资源已授权给客户端。
 *     此时，客户端直接向授权服务器申请访问令牌。请求类型为"application/x-www-form-urlencoded"，返回类型为"application/json"
 *     <pre>
 +---------+                                  +---------------+
 |         |                                  |               |
 |         |>--(A)- Client Authentication --->| Authorization |
 | Client  |                                  |     Server    |
 |         |<--(B)---- Access Token ---------<|               |
 |         |                                  |               |
 +---------+                                  +---------------+
 *     </pre>
 *     </li>
 * </ul>
 * 规范将客户但分为两类：
 * <ul>
 *     <li>机密（confidential）客户端能够维护客户凭证机密性，如运行在web服务器的web应用（web application）。
 *     通常要求资源所有者使用用户名、密码认证，或公私钥认证。</li>
 *     <li>公开（public）客户端无法保证客户凭证机密性，如安装运行在资源所有者设备上的本地应用（native application）、
 *     web服务器下载后在资源所有者设备运行的浏览器应用（user-agent-based application）</li>
 * </ul>
 * 如果授权服务器在颁发访问令牌时，同时也提供了刷新令牌，则客户端可以通过刷新令牌请求访问令牌接口。
 * 请求类型为"application/x-www-form-urlencoded"。
 * <br>
 * 对于没有浏览器的设备，可以参考<a href="https://www.rfc-editor.org/rfc/rfc8628">OAuth 2.0 Device Authorization Grant</a>
 * 完成授权，流程如下：
 * <pre>
 +----------+                                +----------------+
 |          |>---(A)-- Client Identifier --->|                |
 |          |                                |                |
 |          |<---(B)-- Device Code,      ---<|                |
 |          |          User Code,            |                |
 |  Device  |          & Verification URI    |                |
 |  Client  |                                |                |
 |          |  [polling]                     |                |
 |          |>---(E)-- Device Code       --->|                |
 |          |          & Client Identifier   |                |
 |          |                                |  Authorization |
 |          |<---(F)-- Access Token      ---<|     Server     |
 +----------+   (& Optional Refresh Token)   |                |
 v                                     |                |
 :                                     |                |
 (C) User Code & Verification URI       |                |
 :                                     |                |
 v                                     |                |
 +----------+                                |                |
 | End User |                                |                |
 |    at    |<---(D)-- End user reviews  --->|                |
 |  Browser |          authorization request |                |
 +----------+                                +----------------+
 * </pre>
 * 授权请求媒体类型仍为"application/x-www-form-urlencoded"，正确响应状态码为200，媒体类型为"application/json"。
 * 在用户输入收到的验证地址和验证码期间，设备会轮询令牌许可接口，请求媒体类型为"application/x-www-form-urlencoded"。
 */
public interface Authorization {

    /**
     * 用于客户端从资源所有者获取授权
     */
    String AUTHORIZATION_ENDPOINT = "/authorize";

    String DEVICE_AUTHORIZATION_ENDPOINT = "/device_authorization"; // 设备授权请求参数仅含有client_id、scope

    /**
     * 用于客户端使用获取的授权交换access token，通常用于认证
     */
    String TOKEN_ENDPOINT = "/token";

    // param name
    String PARAM_CLIENT_ID = "client_id";
    String PARAM_REDIRECT_URI = "redirect_uri";
    String PARAM_SCOPE = "scope";
    String PARAM_STATE = "state";
    String PARAM_RESOURCE = "resource";
    // --authorization-- param name
    String PARAM_RESPONSE_TYPE = "response_type";
    String PARAM_CODE_CHALLENGE_METHOD = "code_challenge_method";
    String PARAM_CODE_CHALLENGE = "code_challenge";
    // --token-- param name
    String PARAM_CODE = "code";
    String PARAM_CLIENT_SECRET = "client_secret";
    String PARAM_GRANT_TYPE = "grant_type";
    String PARAM_USERNAME = "username";
    String PARAM_PASSWORD = "password";
    String PARAM_REFRESH_TOKEN = "refresh_token";
    String PARAM_CODE_VERIFIER = "code_verifier";
    // --device token-- param name
    String PARAM_DEVICE_CODE = "device_code";

    String DEFAULT_TOKEN_TYPE = "bearer";
    String JWT_TOKEN_TYPE = "urn:ietf:params:oauth:token-type:jwt";

    String CHALLENGE_METHOD_NONE = "plain";
    String CHALLENGE_METHOD_S256 = "S256";

    static String getFirstValue(Map<String, List<String>> queryParameters, String key) {
        return getFirstValue(queryParameters, key, false, false);
    }

    static String getFirstValue(Map<String, List<String>> queryParameters, String key, boolean nonNull, boolean multiply) {
        List<String> values = queryParameters.get(key);
        if (values == null) {
            if (nonNull) {
                throw new NullPointerException("missing required parameter: " + key);
            }
            return null;
        }
        if (values.size() > 1 && !multiply) {
            throw new IllegalArgumentException("duplicate key: " + key);
        }
        return values.get(0);
    }

    /**
     * 授权码模式获取授权码，隐含模式获取访问令牌。
     * 此步骤仅保存client_id对应的请求信息，然后跳转到资源所有者认证/同意页面。
     * 认证成功时，重定向拼接code、state；错误时拼接error、state、error_description（可选）、error_uri（可选）
     * @param request 客户端信息
     * @return 设备授权时以"application/json"形式返回终端用户验证地址
     */
    ServerResponse authorize(Request request);

    default String toConsentPage(String consentPage, Request request) {
        StringBuilder url = new StringBuilder(consentPage);
        url.append('?').append(PARAM_CLIENT_ID).append('=').append(request.getClientId());
        if (request.getScope() != null) {
            url.append('&').append(PARAM_SCOPE).append('=').append(request.getScope());
        }
        if (request.getState() != null) {
            url.append('&').append(PARAM_STATE).append('=').append(request.getState());
        }
        return url.toString();
    }

    /**
     * 请求错误时在跳转的URI中添加错误信息
     * @param query 构建中的URI
     * @param errorResponse 错误信息
     */
    default void onError(StringBuilder query, ErrorResponse errorResponse) {
        Objects.requireNonNull(errorResponse);
        Objects.requireNonNull(errorResponse.error);
        query.append("error=").append(errorResponse.error);
        if (errorResponse.error_description != null) {
            query.append("&error_description=").append(errorResponse.error_description);
        }
        if (errorResponse.error_uri != null) {
            query.append("&error_uri=").append(errorResponse.error_uri);
        }
    }

    /**
     * 获取授权码构建响应URL
     * @param query 构建中的URI
     * @param code 一次性授权码，有效期建议为10分钟
     */
    default void onAuthorizeSuccess(StringBuilder query, String code) {
        Objects.requireNonNull(code);
        query.append(PARAM_CODE).append('=').append(code);
    }

    /**
     * 隐含模式获取授权码构建响应URL
     * @param query 构建中的URI
     * @param request 授权请求
     * @param response 含访问令牌的信息
     */
    default void onImplicitSuccess(StringBuilder query, Request request, TokenResponse response) {
        Objects.requireNonNull(response);
        query.append("access_token=").append(response.getAccess_token());
        query.append("&token_type=").append(response.getToken_type());
        if (response.getExpires_in() != null) {
            query.append("&expires_in=").append(response.getExpires_in().intValue());
        }
        if (request.getScope() != null) {
            query.append('&').append(PARAM_SCOPE).append('=').append(request.getScope());
        }
    }

    default void handleUnknownResponseType(StringBuilder query, Request request,
                                           String code, TokenResponse response, ErrorResponse errorResponse) {
        throw new IllegalArgumentException("unknown response_type: " + request.getResponseType());
    }

    /**
     *
     * @param success 授权是否成功
     * @param request 授权请求
     * @param fragment 参数位置
     * @param code 授权码
     * @param response 访问令牌信息（隐含模式用）
     * @param errorResponse 错误信息
     * @return 跳转URL
     */
    default String build(boolean success, Request request, boolean fragment,
                         String code, TokenResponse response, ErrorResponse errorResponse) {
        Objects.requireNonNull(request);
        int queryOffset = request.getRedirectUri().indexOf('?');
        int fragmentOffset = request.getRedirectUri().indexOf("#");
        StringBuilder query;
        if (!fragment) {
            String url = fragmentOffset > 0 ? request.getRedirectUri().substring(0, fragmentOffset) : request.getRedirectUri();
            query = new StringBuilder(url);
            query.append(queryOffset > 0 ? '&' : '?');
        } else {
            query = new StringBuilder(request.getRedirectUri());
            query.append(fragmentOffset > 0 ? '&' : '#');
        }
        if (!success) {
            onError(query, errorResponse);
        } else if (GrantType.IMPLICIT.getValue().equals(request.getResponseType())) {
            onImplicitSuccess(query, request, response);
        } else if (GrantType.AUTHORIZATION_CODE.getValue().equals(request.getResponseType())){
            onAuthorizeSuccess(query, code);
        } else {
            handleUnknownResponseType(query, request, code, response,errorResponse);
        }
        if (request.getState() != null) {
            query.append('&').append(PARAM_STATE).append('=').append(request.getState());
        }
        if (!fragment && fragmentOffset > 0) {
            query.append(request.getRedirectUri().substring(fragmentOffset));
        }
        return query.toString();
    }

    /**
     * 授权码模式、密码模式、客户端模式获取访问令牌
     * @param credentials 可选的客户端凭证，如Authorization请求头支持的Basic认证协议
     * @param request 授权码信息
     * @return 验证通过时，返回信息包含access_token、token_type等属性，否则包含error等属性
     */
    ServerResponse grant(String credentials, TokenRequest request);

    enum GrantType {
        AUTHORIZATION_CODE() {
            @Override public String getValue() { return PARAM_CODE; }
        },
        REFRESH_TOKEN,
        IMPLICIT() {
            @Override public String getValue() { return "token"; }
            @Override public String getInstanceName() { throw new UnsupportedOperationException(); }
        },
        RESOURCE_OWNER_PASSWORD_CREDENTIALS() {
            @Override public String getInstanceName() { return PARAM_PASSWORD; }
        },
        CLIENT_CREDENTIALS,
        SAML2_BEARER() { // https://www.rfc-editor.org/rfc/rfc7521 https://www.rfc-editor.org/rfc/rfc7522
            @Override public String getInstanceName() { return "urn:ietf:params:oauth:grant-type:saml2-bearer"; }
        },
        JWT_BEARER() { // https://www.rfc-editor.org/rfc/rfc7523
            @Override public String getInstanceName() { return "urn:ietf:params:oauth:grant-type:jwt-bearer"; }
        },
        TOKEN_EXCHANGE() { // https://www.rfc-editor.org/rfc/rfc8693
            @Override public String getInstanceName() { return "urn:ietf:params:oauth:grant-type:token-exchange"; }
        },
        DEVICE_AUTHORIZATION() {
            @Override public String getInstanceName() { return "urn:ietf:params:oauth:grant-type:device_code"; }
        },
        ;

        public static GrantType getInstance(String instanceName) {
            for (GrantType instance : GrantType.values()) {
                if (instance == IMPLICIT) {
                    continue;
                }
                if (instance.getInstanceName().equals(instanceName)) {
                    return instance;
                }
            }
            return null;
        }

        /**
         *
         * @return response_type
         */
        public String getValue() {
            throw new UnsupportedOperationException();
        }

        /**
         *
         * @return grant_type
         */
        public String getInstanceName() {
            return name().toLowerCase();
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    class Request {

        /**
         * 授权类型。
         * 授权码模式：code；隐含模式：token；
         * OIDC允许多值，如"code id_token"、"code token"、"code id_token token"
         */
        protected String responseType;

        /**
         * 授权服务器为注册的客户端生成的客户端标识
         */
        protected String clientId;

        protected String clientSecret;

        /**
         * 认证通过后，认证服务器通知客户端重定向到的绝对路径URL（拼接查询参数code，如果请求中含state，则也返回state）
         */
        protected String redirectUri;

        /**
         * 请求的权限
         */
        protected String scope;

        /**
         * 随机字符串，用于抵御CSRF攻击，如果返回的state和请求的state不同则说明受到了攻击
         */
        protected String state;

        /**
         * 客户端加工code_verifier的方式，如不进行处理的plain（默认值）、进行SHA-256摘要处理并Base64 URL方式编码的S256
         */
        protected String codeChallengeMethod;

        /**
         * <a href="https://www.rfc-editor.org/rfc/rfc7636">PKCE</a>方式下用于防止重放攻击，客户端生成URL安全的43-128个随机字符串，即code_verifier，然后加工成code_challenge
         */
        protected String codeChallenge;

        /**
         * <a href="https://www.rfc-editor.org/rfc/rfc8707">Resource Indicators for OAuth 2.0</a>规范中用于显示通知授权服务器将要请求的资源。
         * 值类型为绝对URI，禁止包含片段，不应包含查询。
         */
        protected List<String> resource;

        transient boolean illegal;

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put(PARAM_RESPONSE_TYPE, responseType);
            map.put(PARAM_CLIENT_ID, clientId);
            map.put(PARAM_CLIENT_SECRET, clientSecret);
            map.put(PARAM_REDIRECT_URI, redirectUri);
            map.put(PARAM_SCOPE, scope);
            map.put(PARAM_STATE, state);
            map.put(PARAM_CODE_CHALLENGE_METHOD, codeChallengeMethod);
            map.put(PARAM_CODE_CHALLENGE, codeChallenge);
            map.put(PARAM_RESOURCE, resource);
            return map;
        }

        public Request(Map<String, List<String>> multiValueMap) {
            try {
                this.responseType = getFirstValue(multiValueMap, Authorization.PARAM_RESPONSE_TYPE);
                this.clientId = getFirstValue(multiValueMap, Authorization.PARAM_CLIENT_ID);
                this.clientSecret = getFirstValue(multiValueMap, Authorization.PARAM_CLIENT_SECRET);
                this.redirectUri = getFirstValue(multiValueMap, Authorization.PARAM_REDIRECT_URI);
                this.scope = getFirstValue(multiValueMap, Authorization.PARAM_SCOPE);
                this.state = getFirstValue(multiValueMap, Authorization.PARAM_STATE);
                this.codeChallenge = getFirstValue(multiValueMap, Authorization.PARAM_CODE_CHALLENGE_METHOD);
                this.codeChallenge = getFirstValue(multiValueMap, Authorization.PARAM_CODE_CHALLENGE);
                this.resource = multiValueMap.get(Authorization.PARAM_RESOURCE);
            } catch (IllegalArgumentException | NullPointerException e) {
                illegal = true;
            }
            assert codeChallenge == null || (codeChallenge.length() >= 43 && codeChallenge.length() <= 128);
        }

    }

    @RequiredArgsConstructor
    @Getter
    @ToString
    class RedirectResponse implements ServerResponse {

        private final String location;
    }

    @Getter
    @Setter
    @ToString
    class DeviceAuthorizationResponse implements ServerResponse {

        /**
         * 设备验证码
         */
        protected final String device_code;

        /**
         * 终端用户验证码
         */
        protected final String user_code;

        /**
         * 终端用户验证地址（需尽量短小便于记忆）
         */
        protected final URL verification_uri;

        /**
         * 包含user_code的verification_uri，用于非字符终端，常见场景如扫码登录
         */
        protected URI verification_uri_complete;

        /**
         * device_code、user_code存活时间（单位：秒）
         */
        protected final int expires_in;

        /**
         * 客户端轮询token等待间隔（单位：秒）
         */
        protected int interval = 5;

        public DeviceAuthorizationResponse(String deviceCode, String userCode, URL verificationUri, int expiresIn) {
            this.device_code = Objects.requireNonNull(deviceCode);
            this.user_code = Objects.requireNonNull(userCode);
            this.verification_uri = Objects.requireNonNull(verificationUri);
            this.expires_in = expiresIn;
        }
    }

    @Getter
    @Setter
    @ToString
    class TokenRequest {

        protected final String grantType;

        /**
         * <a href="https://www.rfc-editor.org/rfc/rfc8707">Resource Indicators for OAuth 2.0</a>在获取令牌时告知授权服务器将要访问的资源信息
         */
        protected List<String> resource;

        protected String scope;

        public TokenRequest(String grantType) {
            this.grantType = grantType;
        }

        /**
         * 从认证服务器获取的授权码
         */
        protected String code;

        protected String redirectUri;

        protected String clientId;

        protected String clientSecret;

        /**
         * PKCE方式下将code_verifier进行处理，与获取授权码步骤的code_challenge进行比对
         */
        protected String codeVerifier;

        public static TokenRequest withCode(String clientId, String redirectUri, String code) {
            TokenRequest request = new TokenRequest(GrantType.AUTHORIZATION_CODE.getInstanceName());
            request.code = Objects.requireNonNull(code);
            request.redirectUri = Objects.requireNonNull(redirectUri);
            request.clientId = Objects.requireNonNull(clientId);
            return request;
        }

        /**
         * 资源所有者用户名
         */
        protected String username;

        /**
         * 资源所有者密码
         */
        protected String password;

        public static TokenRequest withResourceOwnerCredentials(String username, String password) {
            TokenRequest request = new TokenRequest(GrantType.RESOURCE_OWNER_PASSWORD_CREDENTIALS.getInstanceName());
            request.username = Objects.requireNonNull(username);
            request.password = Objects.requireNonNull(password);
            return request;
        }

        public static TokenRequest withClientCredentials() {
            return new TokenRequest(GrantType.CLIENT_CREDENTIALS.getInstanceName());
        }

        /**
         * 获取访问令牌时附带的刷新令牌
         */
        protected String refreshToken;

        public static TokenRequest renew(String refreshToken) {
            TokenRequest request = new TokenRequest(GrantType.REFRESH_TOKEN.getInstanceName());
            request.refreshToken = Objects.requireNonNull(refreshToken);
            return request;
        }

        protected String deviceCode;

        public static TokenRequest withDeviceCode(String clientId, String deviceCode) {
            TokenRequest request = new TokenRequest(GrantType.DEVICE_AUTHORIZATION.getInstanceName());
            request.deviceCode = Objects.requireNonNull(deviceCode);
            request.clientId = Objects.requireNonNull(clientId);
            return request;
        }

        transient boolean illegal;

        public TokenRequest(Map<String, List<String>> multiValueMap) {
            String grantType = null;
            try {
                grantType = getFirstValue(multiValueMap, PARAM_GRANT_TYPE);
                this.code = getFirstValue(multiValueMap, Authorization.PARAM_CODE);
                this.redirectUri = getFirstValue(multiValueMap, Authorization.PARAM_REDIRECT_URI);
                this.clientId = getFirstValue(multiValueMap, Authorization.PARAM_CLIENT_ID);
                this.clientSecret = getFirstValue(multiValueMap, Authorization.PARAM_CLIENT_SECRET);
                this.codeVerifier = getFirstValue(multiValueMap, Authorization.PARAM_CODE_VERIFIER);
                this.scope = getFirstValue(multiValueMap, Authorization.PARAM_SCOPE);
                this.username = getFirstValue(multiValueMap, Authorization.PARAM_USERNAME);
                this.password = getFirstValue(multiValueMap, Authorization.PARAM_PASSWORD);
                this.refreshToken = getFirstValue(multiValueMap, Authorization.PARAM_REFRESH_TOKEN);
                this.deviceCode = getFirstValue(multiValueMap, Authorization.PARAM_DEVICE_CODE);
                this.resource = multiValueMap.get(Authorization.PARAM_RESOURCE);
            } catch (IllegalArgumentException | NullPointerException e) {
                illegal = true;
            }
            this.grantType = grantType;
        }

        public Map<String, List<String>> toMap() {
            Map<String, List<String>> map = new HashMap<>();
            map.put(PARAM_GRANT_TYPE, Collections.singletonList(grantType));
            if (code != null) {
                map.put(Authorization.PARAM_CODE, Collections.singletonList(code));
            }
            if (redirectUri != null) {
                map.put(Authorization.PARAM_REDIRECT_URI, Collections.singletonList(redirectUri));
            }
            if (clientId != null) {
                map.put(Authorization.PARAM_CLIENT_ID, Collections.singletonList(clientId));
            }
            if (clientSecret != null) {
                map.put(Authorization.PARAM_CLIENT_SECRET, Collections.singletonList(clientSecret));
            }
            if (codeVerifier != null) {
                map.put(Authorization.PARAM_CODE_VERIFIER, Collections.singletonList(codeVerifier));
            }
            if (scope != null) {
                map.put(Authorization.PARAM_SCOPE, Collections.singletonList(scope));
            }
            if (username != null) {
                map.put(Authorization.PARAM_USERNAME, Collections.singletonList(username));
            }
            if (password != null) {
                map.put(Authorization.PARAM_PASSWORD, Collections.singletonList(password));
            }
            if (refreshToken != null) {
                map.put(Authorization.PARAM_REFRESH_TOKEN, Collections.singletonList(refreshToken));
            }
            if (deviceCode != null) {
                map.put(Authorization.PARAM_DEVICE_CODE, Collections.singletonList(deviceCode));
            }
            if (resource != null) {
                map.put(Authorization.PARAM_RESOURCE, resource);
            }
            return map;
        }

    }


    @Getter
    @Setter
    @ToString
    class TokenResponse implements ServerResponse {

        /**
         * 可分为使用随机字符串的opaque access token和携带用户信息的<a href="https://www.rfc-editor.org/rfc/rfc9068">JWT access token</a>
         */
        protected final String access_token;

        /**
         * 访问令牌类型，用于访问资源时携带，如
         * <ul>
         *     <li>bearer: curl -H "Authorization: Bearer {access_token}" -X POST -d '{}' https://cafe.babe/java
         *     <br>
         *     可放在请求体（key为access_token），仅当以下要求都满足时：POST请求，类型为“application/x-www-form-urlencoded”，请求体仅含有ASCII码值。
         *     </li>
         *     <li>mac: curl -H "Authorization: MAC id=\"\",nonce=\"\",mac={access_token}" -X POST -d '{}' https://cafe.babe/java</li>
         * </ul>
         */
        protected final String token_type;

        public TokenResponse(String accessToken, String tokenType) {
            this.access_token = Objects.requireNonNull(accessToken);
            this.token_type = Objects.requireNonNull(tokenType);
        }

        /**
         * access token存活时间，单位：秒
         */
        protected Integer expires_in;

        /**
         * 用于刷新access token
         */
        protected String refresh_token;

        /**
         * 权限范围，当权限授予范围和授权请求范围不一致必须返回
         */
        protected String scope;

    }

}
