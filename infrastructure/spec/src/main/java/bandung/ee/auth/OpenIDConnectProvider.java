package bandung.ee.auth;

import bandung.se.IdWorker;
import bandung.se.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import std.ietf.http.jose.Header;
import std.ietf.http.jose.JsonWebAlgorithm;
import std.ietf.http.jose.JsonWebKey;
import std.ietf.http.jose.JsonWebKeySet;
import std.ietf.http.jose.JsonWebToken;
import std.ietf.http.jose.KeyOperation;
import std.ietf.http.oauth.Authorization;
import std.ietf.http.oauth.ClientMetadata;
import std.ietf.http.oauth.ErrorResponse;
import std.ietf.http.oauth.ErrorValue;
import std.ietf.http.oauth.Introspection;
import std.ietf.http.oauth.Registration;
import std.ietf.http.oauth.Revocation;
import std.ietf.http.oauth.ServerResponse;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <a href="https://openid.net/specs/openid-connect-core-1_0.html">OpenID Connect Core 1.0</a>是基于OAuth 2.0之上的认证协议。
 * <pre>
 +--------+                                   +--------+
 |        |                                   |        |
 |        |---------(1) AuthN Request-------->|        |
 |        |                                   |        |
 |        |  +--------+                       |        |
 |        |  |        |                       |        |
 |        |  |  End-  |<--(2) AuthN & AuthZ-->|        |
 |        |  |  User  |                       |        |
 |   RP   |  |        |                       |   OP   |
 |        |  +--------+                       |        |
 |        |                                   |        |
 |        |<--------(3) AuthN Response--------|        |
 |        |                                   |        |
 |        |---------(4) UserInfo Request----->|        |
 |        |                                   |        |
 |        |<--------(5) UserInfo Response-----|        |
 |        |                                   |        |
 +--------+                                   +--------+

 * </pre>
 *
 */
public class OpenIDConnectProvider implements Registration, Authorization, Introspection, Revocation {

    private final Logger log = Logger.getLogger(OpenIDConnectProvider.class.getName());

    private static final String SEPARATOR = "\\s";

    private final OpenIDProviderConfig serverConfig;

    private final CacheManager cacheManager;

    private final IdWorker idWorker;

    private final Function<Object, byte[]> serializer;

    private final ClientMetadataStore clientMetadataStore;

    private final UserService<String, UserInfo> userService;

    private final Function<byte[], ClientMetadata> clientMetadataDeser;

    private final Function<byte[], OpenIDConnectAuthorizationRequest> authorizationReqDeser;

    private final Function<byte[], Header> headerDeser;

    public OpenIDConnectProvider(OpenIDProviderConfig serverConfig, CacheManager cacheManager, IdWorker idWorker,
                                 Function<Object, byte[]> serializer, BiFunction<byte[], Class, Object> deserializer,
                                 ClientMetadataStore clientMetadataStore, UserService<String, UserInfo> userService) {
        this.serverConfig = serverConfig;
        this.cacheManager = cacheManager;
        this.idWorker = idWorker;
        this.serializer = serializer;
        this.clientMetadataStore = clientMetadataStore;
        this.userService = userService;
        this.clientMetadataDeser = ba -> (ClientMetadata) deserializer.apply(ba, ClientMetadata.class);
        this.authorizationReqDeser = ba -> (OpenIDConnectAuthorizationRequest) deserializer.apply(ba, OpenIDConnectAuthorizationRequest.class);
        this.headerDeser = ba -> (Header) deserializer.apply(ba, Header.class);
    }

    public static final Pattern BEARER_PATTERN = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);
    public static final Pattern BASIC_PATTERN = Pattern.compile("^Basic (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);

    public static final String REGISTRATION_CACHE_NAME = "oauthRegistration";
    static final String REGISTRATION_ACCESS_TOKEN_FORMAT = "oauth2:registration:accessToken:%s"; // Registration Management
    public static final String AUTHORIZATION_CODE_CACHE_NAME = "oauthAuthorizationCode";
    public static final String AUTHORIZATION_FORMAT = "oauth2:authorization:code:%s";
    public static final String DEVICE_AUTHORIZATION_CODE_CACHE_NAME = "oauthDeviceAuthorization";
    public static final String SUBJECT_CACHE_NAME = "oauthSubject";
    static final String SUBJECT_FORMAT = "oauth2:authorization:subject:%s:%s"; // UserInfo
    static final String DEVICE_AUTHORIZATION_CODE_FORMAT = "oauth2:deviceAuthorization:code:%s:%s"; // 应用、设备及用户码，用户扫码或输入用户码后进行比对
    public static final String ACCESS_TOKEN_CACHE_NAME = "oauthAccessToken";
    static final String ACCESS_TOKEN_FORMAT = "oauth2:authorization:accessToken:%s";
    public static final String REFRESH_TOKEN_CACHE_NAME = "oauthRefreshToken";
    static final String REFRESH_TOKEN_FORMAT = "oauth2:authorization:refreshToken:%s";
    public static final String TOKEN_FAILURE_CACHE_NAME = "oauthToken";
    static final String TOKEN_FAILURE_TIMES_FORMAT = "oauth2:%s:%s"; // Audit
    public static final String TOKEN_CACHE_NAME = "oauthAdditional";
    public static final String TOKEN_FORMAT = "oauth2:introspection:token:%s";

    public static final String RESPONSE_TYPE_ID_TOKEN = "id_token";

    private static final String PROMPT_TYPE_NONE = "none";
    private static final String PROMPT_TYPE_LOGIN= "login";
    private static final String PROMPT_TYPE_CONSENT = "consent";
    private static final String PROMPT_TYPE_SELECT_ACCOUNT = "select_account"; // 如微信认证选择账号

    public static final String USERINFO_ENDPOINT = "/userinfo";

    public static final String SCOPE_OPENID = "openid"; // OIDC授权请求scope不为null，且必须包含openid
    public static final String SCOPE_PROFILE = "profile"; // JWT包含name, family_name, given_name, middle_name, nickname, preferred_username, profile, picture, website, gender, birthdate, zoneinfo, locale, updated_at
    public static final String SCOPE_EMAIL = "email"; // JWT包含email、email_verified
    public static final String SCOPE_ADDRESS = "address"; // JWT包含address
    public static final String SCOPE_PHONE = "phone"; // JWT包含phone_number、phone_number_verified

    /**
     * （可选的）注册认证
     * @param credentials 注册用的access_token
     * @return 认证结果
     */
    protected boolean authenticateRegistration(String credentials) {
        return true;
    }

    protected boolean shouldApprove(byte[] softwareStatement) {
        return false;
    }

    protected String negotiateItem(String client, List<String> supports) {
        if (supports == null || supports.isEmpty()) {
            return null;
        }
        return client != null && supports.contains(client) ? client : supports.get(0);
    }

    @Override
    public ServerResponse registering(String credentials, ClientMetadata request) {
        if (!authenticateRegistration(credentials)) {
            return new ErrorResponse(ErrorValue.unauthorized_client.name());
        }
        if (request.getRedirect_uris() == null || request.getRedirect_uris().isEmpty()) {
            return new ErrorResponse(ErrorValue.invalid_request.name(), "missing param: redirect_uris");
        }
        if (request.getSoftware_statement() != null) {
            byte[] softwareStatement;
            try {
                softwareStatement = JsonWebToken.fromCompactJWS(request.getSoftware_statement(), headerDeser, request.getJwks().getKeys());
            } catch (IllegalArgumentException | NullPointerException | GeneralSecurityException e) {
                return new ErrorResponse(ErrorValue.invalid_software_statement.name());
            }
            if (!shouldApprove(softwareStatement)) {
                return new ErrorResponse(ErrorValue.unapproved_software_statement.name());
            }
            // client metadata可直接作为请求体，也可以是software_statement的一部分
        }
        boolean multiHostnameInRedirectUri = false;
        String hostname = null;
        if (request.getRedirect_uris() != null) {
            for (String uri : request.getRedirect_uris()) {
                URI u;
                try {
                    u = URI.create(uri);
                } catch (IllegalArgumentException iae) {
                    return new ErrorResponse(ErrorValue.invalid_redirect_uri.name(), uri);
                }
                if (hostname == null) {
                    hostname = u.getHost();
                } else if (!hostname.equals(u.getHost())) {
                    multiHostnameInRedirectUri = true;
                }
            }
        }
        if (request.getSector_identifier_uri() == null &&
                ClientMetadata.SUBJECT_TYPE_PAIRWISE.equals(request.getSubject_type()) &&
                multiHostnameInRedirectUri) {
            return new ErrorResponse(ErrorValue.invalid_request.name(), "require sector_identifier_uri when subject_type=pairwise and multi hostnames in registered redirect_uris");
        }
        if (request.getJwks() == null || request.getJwks().getKeys() == null || request.getJwks().getKeys().length == 0) {
            return new ErrorResponse(ErrorValue.invalid_client_metadata.name(), "missing jwk");
        }

        String clientId = generateChars(true, 16, 32); // clientId不能重复
        Registration.InformationResponse negotiateResult = new InformationResponse();
        negotiateResult.setClient_id(clientId);
        negotiateResult.setClient_id_issued_at(System.currentTimeMillis() / 1000);
        if (ClientMetadata.WEB_APPLICATION_TYPE.equals(request.getApplication_type())) { // 机密性客户端才能保存客户密码、私钥
            negotiateResult.setClient_secret(generateChars(false, 8, 16));
            negotiateResult.setClient_secret_expires_at(estimateClientSecretExpires());
        }
        URI regEndpoint = serverConfig.getRegistration_endpoint();
        assert regEndpoint.getQuery() == null && regEndpoint.getFragment() == null;
        String path = regEndpoint.getPath().endsWith("/") ? clientId : "/" + clientId;
        URI  regMgmtUri = URI.create(regEndpoint.toString() + path); // 采用"/register/{client_id}"形式，而不是"/register?client_id="
        negotiateResult.setRegistration_client_uri(regMgmtUri);
        negotiateResult.setRegistration_access_token(generateRegistrationAccessToken());
        OAuth2Token token = new OAuth2Token();
        token.setToken(negotiateResult.getRegistration_access_token());
        token.setClientId(negotiateResult.getClient_id());
        token.setIssuedAt(Instant.now());
        cacheManager.getCache(REGISTRATION_CACHE_NAME, String.class, OAuth2Token.class)
                .put(String.format(REGISTRATION_ACCESS_TOKEN_FORMAT, negotiateResult.getClient_id()), token);
        negotiate(negotiateResult, request);

        clientMetadataStore.rememberClientMetadata(negotiateResult);
        return negotiateResult;
    }

    private long estimateClientSecretExpires() {
        return serverConfig.getClientSecretTtl() != null ?
                ZonedDateTime.now().plusSeconds(serverConfig.getClientSecretTtl()).toEpochSecond() :
                ZonedDateTime.now().plusMonths(3).toEpochSecond();
    }

    protected void negotiate(Registration.InformationResponse negotiateResult, ClientMetadata request) {
        List<String> grantTypes = new ArrayList<>(serverConfig.getGrant_types_supported());
        if (request.getGrant_types() != null) {
            grantTypes.retainAll(request.getGrant_types());
        }
        negotiateResult.setGrant_types(grantTypes);
        List<String> responseTypes = new ArrayList<>(serverConfig.getResponse_types_supported());
        responseTypes.retainAll(request.getResponse_types());
        negotiateResult.setResponse_types(responseTypes);
        List<String> acrs = new ArrayList<>(serverConfig.getAcr_values_supported());
        if (request.getDefault_acr_values() != null) {
            acrs.retainAll(request.getDefault_acr_values());
        }
        negotiateResult.setDefault_acr_values(acrs);
        if (request.getScope() != null) {
            String[] scopes = request.getScope().split(SEPARATOR);
            negotiateResult.setScope(String.join(SEPARATOR, Utils.retainAll(serverConfig.getScopes_supported(), scopes)));
        }
        // 使用默认值 或 返回错误：invalid_client_metadata
        negotiateResult.setToken_endpoint_auth_method(negotiateItem(request.getToken_endpoint_auth_method(), serverConfig.getSubject_types_supported()));
        negotiateResult.setId_token_signed_response_alg(negotiateItem(request.getId_token_signed_response_alg(), serverConfig.getId_token_signing_alg_values_supported()));
        negotiateResult.setId_token_encrypted_response_alg(negotiateItem(request.getId_token_encrypted_response_alg(), serverConfig.getId_token_encryption_alg_values_supported()));
        negotiateResult.setId_token_encrypted_response_enc(negotiateItem(request.getId_token_encrypted_response_enc(), serverConfig.getId_token_encryption_enc_values_supported()));
        negotiateResult.setUserinfo_signed_response_alg(negotiateItem(request.getUserinfo_signed_response_alg(), serverConfig.getUserinfo_signing_alg_values_supported()));
        negotiateResult.setUserinfo_encrypted_response_alg(negotiateItem(request.getUserinfo_encrypted_response_alg(), serverConfig.getUserinfo_encryption_alg_values_supported()));
        negotiateResult.setUserinfo_encrypted_response_enc(negotiateItem(request.getUserinfo_encrypted_response_enc(), serverConfig.getUserinfo_encryption_enc_values_supported()));
        negotiateResult.setRequest_object_signing_alg(negotiateItem(request.getRequest_object_signing_alg(), serverConfig.getRequest_object_signing_alg_values_supported()));
        negotiateResult.setRequest_object_encryption_alg(negotiateItem(request.getRequest_object_encryption_alg(), serverConfig.getRequest_object_encryption_alg_values_supported()));
        negotiateResult.setRequest_object_encryption_enc(negotiateItem(request.getRequest_object_encryption_enc(), serverConfig.getRequest_object_encryption_enc_values_supported()));
        negotiateResult.setToken_endpoint_auth_signing_alg(negotiateItem(request.getToken_endpoint_auth_signing_alg(), serverConfig.getToken_endpoint_auth_signing_alg_values_supported()));
        negotiateResult.setToken_endpoint_auth_method(negotiateItem(request.getToken_endpoint_auth_method(), serverConfig.getToken_endpoint_auth_methods_supported()));

        negotiateResult.setClient_name(request.getClient_name());
        negotiateResult.setClient_uri(request.getClient_uri());
        negotiateResult.setLogo_uri(request.getLogo_uri());
        negotiateResult.setContacts(request.getContacts());
        negotiateResult.setPolicy_uri(request.getPolicy_uri());
        negotiateResult.setTos_uri(request.getTos_uri());
        negotiateResult.setJwks_uri(request.getJwks_uri());
        negotiateResult.setJwks(request.getJwks());
        negotiateResult.setSoftware_id(request.getSoftware_id());
        negotiateResult.setSoftware_version(request.getSoftware_version());
        negotiateResult.setSoftware_statement(request.getSoftware_statement());
        negotiateResult.setApplication_type(request.getApplication_type());
        negotiateResult.setSector_identifier_uri(request.getSector_identifier_uri());
        negotiateResult.setSubject_type(request.getSubject_type());
        negotiateResult.setId_token_signed_response_alg(request.getId_token_signed_response_alg());
        negotiateResult.setId_token_encrypted_response_alg(request.getId_token_encrypted_response_alg());
        negotiateResult.setDefault_max_age(request.getDefault_max_age());
        negotiateResult.setInitiate_login_uri(request.getInitiate_login_uri());
        negotiateResult.setRedirect_uris(request.getRedirect_uris());
    }

    @Override
    public ServerResponse read(String credentials, String clientId) {
        ServerResponse client = findRegisteredClient(credentials, clientId);
        if (!client.isSuccess()) {
            return client;
        }
        Registration.InformationResponse metadata = (Registration.InformationResponse) client;
        // client_secret、registration_access_token设置了有效期的，可以认证通过后进行刷新
        int nbf = serverConfig.getClientSecretResetNotBefore() != null ? serverConfig.getClientSecretResetNotBefore() : 7 * 24 * 3600;
        if (System.currentTimeMillis() / 1000 - metadata.getClient_secret_expires_at() <= nbf) { // 提前多久允许重新发布密码
            metadata.setClient_secret(generateChars(false, 8, 16));
            metadata.setClient_secret_expires_at(estimateClientSecretExpires());
            metadata.setRegistration_access_token(generateRegistrationAccessToken());
            OAuth2Token holder = new OAuth2Token();
            holder.setToken(metadata.getRegistration_access_token());
            holder.setIssuedAt(Instant.now());
            holder.setClientId(clientId);
            cacheManager.getCache(REGISTRATION_CACHE_NAME, String.class, OAuth2Token.class)
                    .put(String.format(REGISTRATION_ACCESS_TOKEN_FORMAT, clientId), holder);
            clientMetadataStore.refreshClientMetadata(metadata);
        }
        return metadata;
    }

    @Override
    public ServerResponse update(String credentials, UpdateRequest request) {
        ServerResponse client = findRegisteredClient(credentials, request.getClient_id());
        if (!client.isSuccess()) {
            return client;
        }
        Registration.InformationResponse metadata = (Registration.InformationResponse) client;
        if (request.getClient_secret() != null && !request.getClient_secret().equals(metadata.getClient_secret())) {
            return new ErrorResponse(ErrorValue.invalid_request.name()); // xxx 规范未明确此时的error
        }
        // 替换原值，如请求的属性为null，则视为需删除已保存的属性
        negotiate(metadata, request);
        clientMetadataStore.refreshClientMetadata(metadata);
        // 禁止返回registration_access_token、registration_client_uri、client_secret_expires_at、client_id_issued_at字段
        metadata.setRegistration_access_token(null);
        metadata.setRegistration_client_uri(null);
        metadata.setClient_secret_expires_at(null);
        metadata.setClient_id_issued_at(null);
        return metadata;
    }

    @Override
    public ErrorResponse deprovision(String credentials, String clientId) {
        ServerResponse client = findRegisteredClient(credentials, clientId);
        if (!client.isSuccess()) {
            return (ErrorResponse) client;
        }
        clientMetadataStore.removeClientMetadata(clientId); // 避免此client_id继续用于授权、获取访问令牌
        cacheManager.getCache(REGISTRATION_CACHE_NAME, String.class, OAuth2Token.class)
                .remove(String.format(REGISTRATION_ACCESS_TOKEN_FORMAT, clientId));
        onDeprovision(clientId); // （可选）将此client_id对应的访问令牌等也失效
        return null;
    }

    protected void onDeprovision(String clientId) {
        // xxx 只有client_id，以access_token为缓存key时，移除access_token不便；以client_id为缓存key时撤销不方便
    }

    @Override
    public void handleUnknownResponseType(StringBuilder query, Authorization.Request request,
                                          String code, Authorization.TokenResponse response, ErrorResponse errorResponse) {
        if (response == null) {
            Authorization.super.handleUnknownResponseType(query, request, code, null, errorResponse);
        }
        String[] responseTypes = request.getResponseType().split(SEPARATOR);
        for (String responseType : responseTypes) {
            if (GrantType.AUTHORIZATION_CODE.getValue().equals(responseType)) {
                Authorization.super.onAuthorizeSuccess(query, code);
                query.append('&');
            } else if (RESPONSE_TYPE_ID_TOKEN.equals(responseType) && response instanceof IDTokenResponse) {
                query.append(responseType).append('=').append(((IDTokenResponse) response).id_token).append('&');
            } else if (GrantType.IMPLICIT.getValue().equals(responseType)) {
                Authorization.super.onImplicitSuccess(query, request, response);
                query.append('&');
            } else {
                log.log(Level.WARNING, "unknown response type: {0}", responseType);
            }
        }
        query.deleteCharAt(query.length() - 1);
    }

    private String generateToken(String token) {
        if (serverConfig.getKeys() == null || serverConfig.getKeys().getKeys().length == 0) {
            return token;
        }
        int index = ThreadLocalRandom.current().nextInt(serverConfig.getKeys().getKeys().length);
        JsonWebKey<?> key = serverConfig.getKeys().getKeys()[index];
        Header jose = generateJoseHeader(key);
        try {
            return JsonWebToken.toCompactJWS(jose, token, serializer, key);
        } catch (GeneralSecurityException gse) {
            throw new RuntimeException(gse.getMessage(), gse);
        }
    }

    private Header generateJoseHeader(JsonWebKey<?> key) {
        Header jose = new Header();
        if (key instanceof JsonWebKeySet.EllipticCurveJwk) {
            jose.setAlg(JsonWebAlgorithm.ES256.jwaName());
        } else if (key instanceof JsonWebKeySet.RsaJwk) {
            jose.setAlg(JsonWebAlgorithm.RS256.jwaName());
        } else {
            jose.setAlg(JsonWebAlgorithm.HS256.jwaName());
        }
        jose.setKid(key.getKid());
        return jose;
    }

    /**
     *  可以是JWS或普通token
     * @return 管理注册用访问令牌
     */
    protected String generateRegistrationAccessToken() {
        String registrationAccessToken = "reg." + generateChars(false, 12, 28);
        return generateToken(registrationAccessToken);
    }

    protected String generateAuthorizationCode() {
        String code = "code." + generateChars(true,10, 30);
        return generateToken(code);
    }

    protected boolean verifyAuthorizationCode(String authorizationCode) {
        if (serverConfig.getKeys() != null && serverConfig.getKeys().getKeys().length > 0) {
            return verifyToken(authorizationCode);
        }
        return true;
    }

    protected OAuth2Token generateAccessToken(String clientId, UserInfo user, Object audience, String scope) { // b64token
        Instant iat = Instant.now();
        Instant exp = Instant.now().plusSeconds(serverConfig.getAccessTokenTtl());
        String subject = Optional.ofNullable(user).map(UserInfo::getSub).orElse(null);
        String accessToken;
        if (subject != null && serverConfig.getKeys() != null && serverConfig.getKeys().getKeys() != null) {
            byte[] ubga = new byte[8];
            long id = idWorker.getId();
            for (int i = ubga.length - 1; i > 0; i--) {
                ubga[i] = (byte) (id % 256);
                id /= 256;
            }
            JwtAccessToken at = new JwtAccessToken(serverConfig.getIssuer(), exp, audience, subject, clientId, iat, JsonWebToken.ENCODER.encodeToString(ubga));
            at.setScope(scope);
//            at.setRoles();
//            at.setGroups();
//            at.setEntitlements();
            int index = ThreadLocalRandom.current().nextInt(serverConfig.getKeys().getKeys().length);
            JsonWebKey key = serverConfig.getKeys().getKeys()[index];
            Header jose = generateJoseHeader(key);
            jose.setTyp("at+JWT");
            try {
                accessToken = JsonWebToken.toCompactJWS(jose, at, serializer, key);
            } catch (GeneralSecurityException gse) {
                throw new RuntimeException(gse.getMessage(), gse);
            }
        } else {
            String mix = generateChars(true, 13, 29);
            accessToken = "at." + Base64.getEncoder().encodeToString(mix.getBytes(StandardCharsets.UTF_8));
        }
        OAuth2Token token = new OAuth2Token();
        token.setToken(accessToken);
        token.setSubject(subject);
        token.setIssuedAt(iat);
        token.setExpiresAt(exp);
        token.setTokenType(serverConfig.getTokenType());
        return token;
    }

    protected OAuth2Token generateRefreshToken() {
        String refreshToken = "rt." + generateChars(true, 13, 29);
        String jws = generateToken(refreshToken);
        OAuth2Token token = new OAuth2Token();
        token.setToken(jws);
        token.setIssuedAt(Instant.now());
        if (serverConfig.getRefreshTokenTtl() != null) {
            token.setExpiresAt(Instant.now().plusSeconds(serverConfig.getRefreshTokenTtl()));
        }
        return token;
    }

    protected boolean verifyRefreshToken(String refreshToken) {
        if (serverConfig.getKeys() != null && serverConfig.getKeys().getKeys().length > 0) {
            return verifyToken(refreshToken);
        }
        return true;
    }

    private boolean verifyToken(String token) {
        try {
            JsonWebToken.fromCompactJWS(token, headerDeser, serverConfig.getKeys().getKeys());
            return true;
        } catch (GeneralSecurityException gse) {
            return false;
        }
    }

    private ServerResponse validateAuthorizeParam(Authorization.Request request) {
        if (request.isIllegal()) {
            return new ErrorResponse(ErrorValue.invalid_request.name(), "multiply param value");
        }
        if (request.getClientId() == null) {
            return new ErrorResponse(ErrorValue.invalid_request.name(), "missing required parameter: client_id");
        }
        ServerResponse client = findIssuedClient(null, request.getClientId(), request.getClientSecret());
        if (!client.isSuccess()) {
            return client;
        }
        Registration.InformationResponse clientInfo = (Registration.InformationResponse) client;
        if (request.getScope() != null) {
            String[] scopes = request.getScope().split(SEPARATOR);
            if (clientInfo.getScope() != null && !Utils.containsAll(clientInfo.getScope().split(SEPARATOR), scopes)) {
                return new ErrorResponse(ErrorValue.invalid_scope.name());
            }
        }
        return clientInfo;
    }

    private ServerResponse deviceAuthorize(Authorization.Request request) {
        ServerResponse maybeError = validateAuthorizeParam(request);
        if (!maybeError.isSuccess()) {
            return maybeError;
        }
        String deviceCode = generateChars(true, 16, 32);
        String userCode = generateUserCode(ThreadLocalRandom.current().nextInt(3) + 3, ThreadLocalRandom.current().nextBoolean()); // 简短便于用户输入
        Authorization.DeviceAuthorizationResponse response = new Authorization.DeviceAuthorizationResponse(deviceCode, userCode, serverConfig.getDeviceVerificationUri(), serverConfig.getDeviceCodeTtl());
        if (serverConfig.getDeviceAuthorizationCompleteUriFormat() != null) {
            response.setVerification_uri_complete(URI.create(String.format(serverConfig.getDeviceAuthorizationCompleteUriFormat(), deviceCode, userCode)));
        }
        OAuth2Token token = new OAuth2Token();
        token.setToken(userCode);
        token.setClientId(request.getClientId());
        token.setIssuedAt(Instant.now());
        token.setExpiresAt(Instant.now().plusSeconds(serverConfig.getDeviceCodeTtl()));
        token.setVirgin(true);
        cacheManager.getCache(DEVICE_AUTHORIZATION_CODE_CACHE_NAME, String.class, OAuth2Token.class)
                .put(String.format(DEVICE_AUTHORIZATION_CODE_FORMAT, request.getClientId(), deviceCode), token);
        return response;
    }

    protected void mergeRequest(Registration.InformationResponse clientInfo, OpenIDConnectAuthorizationRequest req) {
        if (req.getRequest() != null && clientInfo.getJwks() != null) {
            byte[] payload;
            try {
                payload = JsonWebToken.fromCompactJWS(req.getRequest(), headerDeser, clientInfo.getJwks().getKeys());
            } catch (GeneralSecurityException gse) {
                throw new RuntimeException(gse.getMessage(), gse);
            }
            OpenIDConnectAuthorizationRequest jws = authorizationReqDeser.apply(payload);
            if (jws.getClientId() != null && req.getClientId() != null && !jws.getClientId().equals(req.getClientId())) {
                throw new IllegalArgumentException("inconsistent param: client_id");
            }
            if (jws.getResponseType() != null && req.getResponseType() != null && !jws.getResponseType().equals(req.getResponseType())) {
                throw new IllegalArgumentException("inconsistent param: response_type");
            }
            if (jws.getRedirectUri() != null && req.getRedirectUri() != null && !jws.getRedirectUri().equals(req.getRedirectUri())) {
                throw new IllegalArgumentException("inconsistent param: redirect_uri");
            }
            if (jws.getScope() != null && req.getScope() != null && !jws.getScope().equals(req.getScope())) {
                throw new IllegalArgumentException("inconsistent param: scope");
            }
            if (jws.getState() != null && req.getState() != null && !jws.getState().equals(req.getState())) {
                throw new IllegalArgumentException("inconsistent param: state");
            }
            if (jws.getResponseMode() != null && req.getResponseMode() != null && !jws.getResponseMode().equals(req.getResponseMode())) {
                throw new IllegalArgumentException("inconsistent param: response_mode");
            }
            if (jws.getMaxAge() != null) {
                req.setMaxAge(jws.getMaxAge());
            }
            if (jws.getClaims() != null) {
                req.setClaims(jws.getClaims());
            }
        }
    }

    @Override
    public ServerResponse authorize(Authorization.Request request) {
        if (request.getResponseType() == null) { // device authorization
            return deviceAuthorize(request);
        }
        try {
            ServerResponse maybeError = validateAuthorizeParam(request);
            if (!maybeError.isSuccess()) {
                return maybeError;
            }
            Registration.InformationResponse clientInfo = (Registration.InformationResponse) maybeError;
            if (request.getRedirectUri() != null) {
                try {
                    new URL(request.getRedirectUri());
                } catch (MalformedURLException e) {
                    return new ErrorResponse(ErrorValue.invalid_request.name(), "malformed redirect_uri");
                }
                if (!clientInfo.getRedirect_uris().isEmpty() && !clientInfo.getRedirect_uris().contains(request.getRedirectUri())) {
                    return new ErrorResponse(ErrorValue.invalid_redirect_uri.name(), "unregistered redirect_uri");
                }
            }
            List<String> responseTypes = Arrays.asList(request.getResponseType().split(SEPARATOR));
            if (!serverConfig.getResponse_types_supported().containsAll(responseTypes)) {
                return new ErrorResponse(ErrorValue.unsupported_response_type.name());
            }
            if (serverConfig.isForcePKCE() && request.getClientSecret() == null && request.getCodeChallenge() == null) { // 非机密客户端使用PKCE能够增强安全性
                return new ErrorResponse(ErrorValue.invalid_request.name(), "require PKCE for public client");
            }
            if (request.getCodeChallengeMethod() != null &&
                    !serverConfig.getCode_challenge_methods_supported().contains(request.getCodeChallengeMethod())) {
                return new ErrorResponse(ErrorValue.invalid_request.name(), "invalid code_challenge_method value");
            }
            if (request instanceof OpenIDConnectAuthorizationRequest) {
                OpenIDConnectAuthorizationRequest req = (OpenIDConnectAuthorizationRequest) request;
                mergeRequest(clientInfo, req);
                if (req.getRequestUri() != null) { // 暂不支持
                    return new ErrorResponse(ErrorValue.request_uri_not_supported.name());
                }
                if (req.getRegistration() != null) { // 暂不支持
                    return new ErrorResponse(ErrorValue.registration_not_supported.name());
                }
                if (req.getPrompt() != null &&
                        req.getPrompt().contains(PROMPT_TYPE_NONE) && req.getPrompt().length() > PROMPT_TYPE_NONE.length()) { // 含有none及其他类型
                    return new ErrorResponse(ErrorValue.invalid_request.name(), "prompt cannot have none and others");
                }
            }
            // xxx 将请求与session进行绑定
            return null;
        } catch (IllegalArgumentException iae) {
            return new ErrorResponse(ErrorValue.invalid_request.name());
        } catch (RuntimeException re) {
            log.log(Level.WARNING, re.getMessage(), re);
            return new ErrorResponse(ErrorValue.temporarily_unavailable.name());
        } catch (Exception e) {
            log.log(Level.WARNING, e.getMessage(), e);
            return new ErrorResponse(ErrorValue.server_error.name());
        }
    }

    public void onDeviceAuthorizeCallback(String clientId, String deviceCode, String userCode, UserInfo userInfo) { // 设备授权码模式：client_id, device_code, user_code
        Registration.InformationResponse clientInfo = clientMetadataStore.loadClientMetadata(clientId);
        if (clientInfo == null) {
            return; // may be revoked
        }
        Cache<String, OAuth2Token> cache = cacheManager.getCache(DEVICE_AUTHORIZATION_CODE_CACHE_NAME, String.class, OAuth2Token.class);
        OAuth2Token token = cache.get(String.format(DEVICE_AUTHORIZATION_CODE_FORMAT, clientId, deviceCode));
        if (token != null && token.getToken().equals(userCode) && token.virgin) {
            token.setVirgin(false);
            cache.put(String.format(DEVICE_AUTHORIZATION_CODE_FORMAT, clientId, deviceCode), token);

            cacheManager.getCache(SUBJECT_CACHE_NAME, String.class, UserInfo.class)
                    .put(String.format(SUBJECT_FORMAT, clientId, deviceCode), userInfo);
        }
    }

    /**
     * 授权回调
     * @param request 原始授权请求
     * @param authTime 授权请求时间
     * @param accessDenied 用户是否同意
     * @param userInfo 授权的用户信息
     * @return
     */
    public ServerResponse onAuthorizeCallback(Authorization.Request request, long authTime, boolean accessDenied, UserInfo userInfo) {
        boolean fragment = request instanceof OpenIDConnectAuthorizationRequest &&
                ResponseMode.FRAGMENT.name().toLowerCase().equals(((OpenIDConnectAuthorizationRequest) request).getResponseMode());
        // 用户拒绝
        if (accessDenied) {
            return new RedirectResponse(build(false, request, fragment, serverConfig, null, null, new ErrorResponse(ErrorValue.access_denied.name())));
        }
        Registration.InformationResponse clientInfo = clientMetadataStore.loadClientMetadata(request.getClientId());
        if (clientInfo == null) {
            return new ErrorResponse(ErrorValue.invalid_client_metadata.name()); // may be revoked
        }
        List<String> responseTypes = Arrays.asList(request.getResponseType().split(SEPARATOR));
        // 用户同意授权
        String location;
        String authorizationCode = null;
        if (responseTypes.contains(GrantType.AUTHORIZATION_CODE.getValue())) { // 授权码模式或OIDC混合模式
            authorizationCode = generateAuthorizationCode();
            cacheManager.getCache(AUTHORIZATION_CODE_CACHE_NAME, String.class, Authorization.Request.class)
                    .put(String.format(AUTHORIZATION_FORMAT, authorizationCode), request); // 授权码与{client_id, redirect_uri}绑定
            cacheManager.getCache(SUBJECT_CACHE_NAME, String.class, UserInfo.class)
                    .put(String.format(SUBJECT_FORMAT, request.getClientId(), authorizationCode), userInfo);
        }
        OAuth2Token accessToken;
        boolean hybridImplicit = responseTypes.equals(Arrays.asList(GrantType.IMPLICIT.getValue(), RESPONSE_TYPE_ID_TOKEN)); // id_token token
        boolean implicit = GrantType.IMPLICIT.getValue().equals(request.getResponseType()); // token
        Authorization.TokenResponse response = null;
        if (implicit || hybridImplicit) { // 隐含模式、OIDC扩展的隐含模式
            accessToken = generateAccessToken(request.getClientId(), userInfo, findAudiences(request.getScope()), request.getScope());
            cacheManager.getCache(ACCESS_TOKEN_CACHE_NAME, String.class, OAuth2Token.class)
                    .put(String.format(ACCESS_TOKEN_FORMAT, accessToken.getToken()), accessToken); // XXX 访问令牌与???绑定
            if (hybridImplicit) {
                String idToken = generateIDToken(request, authTime, userInfo, authorizationCode, accessToken, clientInfo);
                response = new IDTokenResponse(accessToken.getToken(), serverConfig.getTokenType(), idToken);
            } else {
                response = new Authorization.TokenResponse(accessToken.getToken(), serverConfig.getTokenType());
            }
            response.setExpires_in((int) serverConfig.getAuthorizationCodeTtl().getSeconds()); // 相对时间
            if (serverConfig.getRefreshTokenTtl() != null) {
                OAuth2Token refreshToken = generateRefreshToken();
                refreshToken.setClientId(request.getClientId());
                refreshToken.setVirgin(true);
                cacheManager.getCache(REFRESH_TOKEN_CACHE_NAME, String.class, OAuth2Token.class)
                        .put(String.format(REFRESH_TOKEN_FORMAT, refreshToken.getToken()), refreshToken); // XXX 刷新令牌与???绑定
                response.setRefresh_token(refreshToken.getToken());
            }
        } // 授权码模式(code)、OIDC混合模式(code id_token, code token, code token id_token)
        location = build(true, request, fragment, serverConfig, authorizationCode, response, null);
        return new RedirectResponse(location);
    }

    protected String generateIDToken(Authorization.Request request, long authTime, UserInfo userInfo,
                                     String authorizationCode, OAuth2Token accessToken, Registration.InformationResponse clientMetadata) {
        //header
        Header joseHeader = new Header();
        boolean jwt = false;
        if (clientMetadata.getId_token_encrypted_response_enc() != null) {
            jwt = true;
            joseHeader.setAlg(clientMetadata.getId_token_encrypted_response_alg());
            joseHeader.setEnc(clientMetadata.getId_token_encrypted_response_enc());
        } else {
            joseHeader.setAlg(clientMetadata.getId_token_signed_response_alg());
        }

        // payload
        long expiresAt = Optional.ofNullable(accessToken.getExpiresAt()).map(Instant::getEpochSecond).orElse(0L);
        String subject = null;
        if(ClientMetadata.DEFAULT_SUBJECT_TYPE.equals(clientMetadata.getSubject_type())) {
            subject = userInfo.getSub();
        } else { // 1. SHA-256(sector_identifier_uri + local_account_id + salt); 2. AES-128(sector_identifier_uri + local_account_id + salt); 3. GUID
            String ppid = Optional.ofNullable(clientMetadata.getSector_identifier_uri()).map(URI::toString)
                    .orElse(URI.create(clientMetadata.getRedirect_uris().get(0)).getHost()) + userInfo.getSub() + clientMetadata.getClient_id();
            try {
                byte[] data = JsonWebAlgorithm.HS256.digest(ppid.getBytes(StandardCharsets.UTF_8));
                subject = JsonWebToken.ENCODER.encodeToString(data);
            } catch (GeneralSecurityException ignore) {
            }
        }
        IDToken idToken = new IDToken(serverConfig.getIssuer(), subject, request.getClientId(), (int) expiresAt, (int) accessToken.getIssuedAt().getEpochSecond());
        if (clientMetadata.isRequire_auth_time()) {
            idToken.setAuth_time(authTime / 1000);
        }
        if (userInfo != null) {
            if (request.getScope().contains(SCOPE_PROFILE)) { // xxx 服务端是否授权???
                idToken.setName(userInfo.getName());
                idToken.setFamily_name(userInfo.getFamily_name());
                idToken.setGiven_name(userInfo.getGiven_name());
                idToken.setMiddle_name(userInfo.getMiddle_name());
                idToken.setNickname(userInfo.getNickname());
                idToken.setPreferred_username(userInfo.getPreferred_username());
                idToken.setProfile(userInfo.getProfile());
                idToken.setPicture(userInfo.getPicture());
                idToken.setWebsite(userInfo.getWebsite());
                idToken.setGender(userInfo.getGender());
                idToken.setBirthdate(userInfo.getBirthdate());
                idToken.setZoneinfo(userInfo.getZoneinfo());
                idToken.setLocale(userInfo.getLocale());
                idToken.setUpdated_at(userInfo.getUpdated_at());
            }
            if (request.getScope().contains(SCOPE_EMAIL)) {
                idToken.setEmail(userInfo.getEmail());
                idToken.setEmail_verified(userInfo.getEmail_verified());
            }
            if (request.getScope().contains(SCOPE_PHONE)) {
                idToken.setPhone_number(userInfo.getPhone_number());
                idToken.setPhone_number_verified(userInfo.getPhone_number_verified());
            }
            if (request.getScope().contains(SCOPE_ADDRESS)) {
                idToken.setAddress(userInfo.getAddress());
            }
        }
        if (request instanceof OpenIDConnectAuthorizationRequest) {
            OpenIDConnectAuthorizationRequest req = (OpenIDConnectAuthorizationRequest) request;
            if (req.getMaxAge() != null) {
                idToken.setAuth_time(authTime / 1000);
            }
            if (req.getNonce() != null) {
                idToken.setNonce(req.getNonce());
            }
        }
        try {
            JsonWebAlgorithm alg = Optional.ofNullable(clientMetadata.getId_token_signed_response_alg()).map(JsonWebAlgorithm::getJwsAlgorithm).orElse(null);
            if (alg != null) { // response_type = "code id_token token" 时必须返回at_hash
                byte[] signature = alg.digest(accessToken.getToken().getBytes(StandardCharsets.UTF_8));
                idToken.setAt_hash(JsonWebToken.ENCODER.encodeToString(ByteBuffer.wrap(signature, 0, signature.length / 2).array()));
            }
            if (authorizationCode != null && alg != null) { // response_type = "code id_token" 或 "code id_token token" 时必须返回c_hash
                byte[] signature = alg.digest(authorizationCode.getBytes(StandardCharsets.UTF_8));
                idToken.setC_hash(JsonWebToken.ENCODER.encodeToString(ByteBuffer.wrap(signature, 0, signature.length / 2).array()));
            }

            JsonWebKey<?> key = null; // ID Token的密钥使用发现/注册协商时事先保存的，而不是在JOSE请求头中指定
            JsonWebAlgorithm algorithm = jwt ? JsonWebAlgorithm.getJweAlgorithm(joseHeader.getAlg()) :
                    JsonWebAlgorithm.getJwsAlgorithm(joseHeader.getAlg());
            if (algorithm.support(KeyOperation.MAC)) { // 对称密钥使用client_secret
                JsonWebKeySet.SymmetricKey aesKey = new JsonWebKeySet.SymmetricKey();
                aesKey.importKey(new SecretKeySpec(clientMetadata.getClient_secret().getBytes(StandardCharsets.US_ASCII), JsonWebAlgorithm.AES_ALGORITHM));
                key = aesKey;
            } else if (algorithm.support(KeyOperation.SIGN)) { // 非对称密钥使用客户端公钥
                key = Arrays.stream(clientMetadata.getJwks().getKeys()).filter(JsonWebKey::isAsymmetric).findFirst().orElse(null);
            }
            return jwt ? JsonWebToken.toCompactJWE(joseHeader, idToken, serializer, key) : JsonWebToken.toCompactJWS(joseHeader, idToken, serializer, key);
        } catch (GeneralSecurityException gse) {
            throw new RuntimeException(gse.getMessage(), gse);
        }
    }

    protected ServerResponse aroundAuth(int type, String key, Supplier<Boolean> action) {
        Cache<String, Audit> cache = cacheManager.getCache(TOKEN_FAILURE_CACHE_NAME, String.class, Audit.class);
        Audit audit = cache.get(key);
        if (audit != null && audit.getFailureTimes() > serverConfig.getMaxFailureTimes()) {
            return new ErrorResponse(ErrorValue.access_denied.name());
        }
        boolean result = action.get();
        if (!result) {
            if (audit == null) {
                audit = new Audit();
                audit.setType(type);
            }
            audit.setFailureTimes(audit.getFailureTimes() + 1);
            audit.setLastFailureTime(Instant.now());
            cache.put(key, audit);
        }
        return null;
    }

    @Override
    public ServerResponse grant(String credentials, Authorization.TokenRequest request) { // 授权码模式/OIDC混合模式
        if (request.isIllegal()) {
            return new ErrorResponse(ErrorValue.invalid_request.name(), "multiply param value");
        }
        Authorization.GrantType grantType = Authorization.GrantType.getInstance(request.getGrantType());
        UserInfo userInfo;
        if (Authorization.GrantType.AUTHORIZATION_CODE == grantType) { // grant_type, code, redirect_uri, client_id
            if (request.getCode() == null || request.getRedirectUri() == null) {
                return new ErrorResponse(ErrorValue.invalid_request.name(), "missing required parameters");
            }
            if (!verifyAuthorizationCode(request.getCode())) {
                return new ErrorResponse(ErrorValue.invalid_token.name());
            }
            ServerResponse client = findIssuedClient(credentials, request.getClientId(), request.getClientSecret());
            if (!client.isSuccess()) {
                return client;
            }
            Registration.InformationResponse clientInfo = (Registration.InformationResponse) client;
            if (!clientInfo.getRedirect_uris().contains(request.getRedirectUri())) {
                return new ErrorResponse(ErrorValue.invalid_request.name(), "not register redirect_uri");
            }

            Cache<String, Authorization.Request> cache = cacheManager.getCache(AUTHORIZATION_CODE_CACHE_NAME, String.class, Authorization.Request.class);
            Authorization.Request cacheValue = cache.get(String.format(AUTHORIZATION_FORMAT, request.getCode()));
            if (cacheValue == null) {
                return new ErrorResponse(ErrorValue.access_denied.name());
            }
            if (cacheValue.getRedirectUri() != null && !cacheValue.getRedirectUri().equals(request.getRedirectUri())) {
                return new ErrorResponse(ErrorValue.invalid_request.name(), "redirect_uri MUST be identical");
            }
            if (cacheValue.getCodeChallenge() != null) {
                String verifier;
                if (Authorization.CHALLENGE_METHOD_S256.equals(cacheValue.getCodeChallengeMethod())) {
                    byte[] msg;
                    try {
                        msg = JsonWebAlgorithm.HS256.digest(request.getCodeVerifier().getBytes(StandardCharsets.US_ASCII));
                    } catch (GeneralSecurityException gse) {
                        msg = new byte[0];
                    }
                    verifier = JsonWebToken.ENCODER.encodeToString(msg);
                } else {
                    verifier = request.getCodeVerifier();
                }
                if (!verifier.equals(cacheValue.getCodeChallenge())) {
                    return new ErrorResponse(ErrorValue.access_denied.name()); // error???
                }
            }
            cache.remove(String.format(AUTHORIZATION_FORMAT, request.getCode()));
            userInfo = cacheManager.getCache(SUBJECT_CACHE_NAME, String.class, UserInfo.class)
                    .get(String.format(SUBJECT_FORMAT, request.getClientId(), request.getCode()));
        } else if (Authorization.GrantType.RESOURCE_OWNER_PASSWORD_CREDENTIALS == grantType) { // grant_type, username, password, scope
            if (request.getUsername() == null || request.getPassword() == null) {
                return new ErrorResponse(ErrorValue.invalid_request.name());
            }
            // 验证用户名、密码
            Supplier<Boolean> supplier = () -> userService.authenticate(request.getUsername(), request.getPassword());
            ServerResponse error = aroundAuth(1, String.format(TOKEN_FAILURE_TIMES_FORMAT, request.getClientId(), request.getUsername()), supplier);
            if (error != null) {
                return error;
            }
            userInfo = userService.loadUserByName(request.getUsername());
        } else if (Authorization.GrantType.CLIENT_CREDENTIALS == grantType) { // grant_type, scope
            // 验证client_id、client_secret
            ServerResponse client = findIssuedClient(credentials, request.getClientId(), request.getClientSecret());
            if (!client.isSuccess()) {
                return client;
            }
            userInfo = null; // xxx 能否获取到用户信息
        } else if (Authorization.GrantType.REFRESH_TOKEN == grantType) {
            if (request.getRefreshToken() == null) {
                return new ErrorResponse(ErrorValue.invalid_request.name(), "missing refresh_token parameter");
            }
            if (!verifyRefreshToken(request.getRefreshToken())) {
                return new ErrorResponse(ErrorValue.invalid_token.name());
            }
            if (request.getScope() != null &&
                    !Utils.containsAll(serverConfig.getScopes_supported(), request.getScope().split(SEPARATOR))) {
                return new ErrorResponse(ErrorValue.invalid_scope.name());
            }
            // verify client credentials
            ServerResponse client = findIssuedClient(credentials, request.getClientId(), request.getClientSecret());
            if (!client.isSuccess()) {
                return client;
            }
            // 刷新accessToken时仍保留原accessToken一段时间；如果使用新refreshToken，需要对旧refreshToken失效
            Cache<String, OAuth2Token> refreshTokenCache = cacheManager.getCache(REFRESH_TOKEN_CACHE_NAME, String.class, OAuth2Token.class);
            OAuth2Token token = refreshTokenCache.get(String.format(REFRESH_TOKEN_FORMAT, request.getRefreshToken()));
            if (token == null || !token.virgin || token.revoked) {
                return new ErrorResponse(ErrorValue.invalid_grant.name(), "refresh token is invalid, expired or revoked");
            }

            token.setVirgin(false);
            refreshTokenCache.put(String.format(REFRESH_TOKEN_FORMAT, request.getRefreshToken()), token);
            userInfo = userService.loadUser(token.getSubject());
        } else if (Authorization.GrantType.DEVICE_AUTHORIZATION == grantType) { // 设备授权码模式, device polling
            if (request.getDeviceCode() == null || request.getClientId() == null) {
                return new ErrorResponse(ErrorValue.invalid_request.name(), "missing required parameters");
            }
            // client credentials
            // device_code、user_code已在某个链接验证无误，该链接的所属系统通知或共享给授权服务器
            Cache<String, UserInfo> cache = cacheManager.getCache(SUBJECT_CACHE_NAME, String.class, UserInfo.class);
            userInfo = cache.getAndRemove(String.format(SUBJECT_FORMAT, request.getClientId(), request.getDeviceCode()));
            if (userInfo == null) {
                Cache<String, Audit> auditCache = cacheManager.getCache(TOKEN_FAILURE_CACHE_NAME, String.class, Audit.class);
                Audit auditHistory = auditCache.get(String.format(TOKEN_FAILURE_TIMES_FORMAT, Authorization.DEVICE_AUTHORIZATION_ENDPOINT, request.getDeviceCode()));
                if (auditHistory != null && auditHistory.getLastFailureTime().plusSeconds(5L * (long) Math.sqrt(auditHistory.getFailureTimes())).isAfter(Instant.now())) {
                    return new ErrorResponse(ErrorValue.slow_down.name());
                }
                return new ErrorResponse(ErrorValue.authorization_pending.name(), "the end user hasn't yet completed the user-interaction steps");
            }
        } else {
            return new ErrorResponse(ErrorValue.invalid_grant.name(), request.getGrantType());
        }
        List<URI> resourceUris = Collections.emptyList(); // 如果将要访问的资源超出权限，此时可提示客户端invalid_scope
        if (request.getResource() != null) {
            resourceUris = request.getResource().stream().map(URI::create).collect(Collectors.toList());
        }
        if (!isInScope(resourceUris)) {
            return new ErrorResponse(ErrorValue.invalid_scope.name(), "The requested scope is exceeds the scope granted by the resource owner");
        }
        OAuth2Token accessToken = generateAccessToken(request.getClientId(), userInfo, findAudiences(request.getScope()), request.getScope());
        accessToken.setClientId(request.getClientId());
        cacheManager.getCache(ACCESS_TOKEN_CACHE_NAME, String.class, OAuth2Token.class)
                .put(String.format(ACCESS_TOKEN_FORMAT, accessToken.getToken()), accessToken);
        Authorization.TokenResponse response = new TokenResponse(accessToken.getToken(), serverConfig.getTokenType()); // token_hint???
        response.setExpires_in(serverConfig.getAccessTokenTtl());
        if (serverConfig.getRefreshTokenTtl() != null) {
            OAuth2Token refreshToken = generateRefreshToken();
            refreshToken.setClientId(request.getClientId());
            refreshToken.setSubject(userInfo.getSub());
            refreshToken.setVirgin(true);
            cacheManager.getCache(REFRESH_TOKEN_CACHE_NAME, String.class, OAuth2Token.class)
                    .put(String.format(REFRESH_TOKEN_FORMAT, refreshToken.getToken()), refreshToken);
            response.setRefresh_token(refreshToken.getToken());
        }
        return response;
    }

    protected List<String> findAudiences(String scope) {
        return serverConfig.getAudiences();
    }

    protected boolean isInScope(List<URI> resourceUris) {
        return true;
    }

    /**
     * 获取已注册的客户端协商信息
     * @param credentials 注册访问令牌
     * @param clientId 客户端标识
     * @return 客户端协商信息
     */
    protected ServerResponse findRegisteredClient(String credentials, String clientId) {
        Matcher matcher = BEARER_PATTERN.matcher(credentials);
        if (!matcher.matches()) {
            return new ErrorResponse(ErrorValue.invalid_token.name());
        }
        String token = matcher.group("token");
        if (!verifyRegistrationAccessToken(token)) {
            return new ErrorResponse(ErrorValue.invalid_token.name());
        }
        Cache<String, OAuth2Token> regAccessTokenCache = cacheManager.getCache(REGISTRATION_CACHE_NAME, String.class, OAuth2Token.class);
        OAuth2Token regAccessToken = regAccessTokenCache.get(String.format(REGISTRATION_ACCESS_TOKEN_FORMAT, clientId));
        if (regAccessToken == null || regAccessToken.revoked) {
            return new ErrorResponse(ErrorValue.unauthorized_client.name(), "registration_access_token revoked or invalid");
        }
        Cache<String, Audit> auditCache = cacheManager.getCache(TOKEN_FAILURE_CACHE_NAME, String.class, Audit.class);
        Audit auditHistory = auditCache.get(String.format(TOKEN_FAILURE_TIMES_FORMAT, Registration.ENDPOINT, token));
        if (auditHistory != null && auditHistory.getFailureTimes() > serverConfig.getMaxFailureTimes()) {  // 防止暴力破解
            regAccessToken.revoked = true;
            regAccessTokenCache.put(String.format(REGISTRATION_ACCESS_TOKEN_FORMAT, clientId), regAccessToken);
            return new ErrorResponse(ErrorValue.access_denied.name());
        }
        if (!regAccessToken.getToken().equals(token)) {
            if (auditHistory != null) {
                auditHistory.setFailureTimes(auditHistory.getFailureTimes() + 1);
            } else {
                auditHistory = new Audit();
                auditHistory.setFailureTimes(1);
            }
            auditHistory.setLastFailureTime(Instant.now());
            auditCache.put(String.format(TOKEN_FAILURE_TIMES_FORMAT, Registration.ENDPOINT, token), auditHistory);
            return new ErrorResponse(ErrorValue.unauthorized_client.name());
        }
        Registration.InformationResponse clientMetadata = clientMetadataStore.loadClientMetadata(clientId);
        if (clientMetadata == null) {
            return new ErrorResponse(ErrorValue.invalid_client.name(), "the client does not exist on this server");
        }
        return clientMetadata;
    }

    protected ServerResponse findIssuedClient(String credentials, String clientId, String clientSecret) {
        if (clientId == null && credentials != null) {
            Matcher matcher = BASIC_PATTERN.matcher(credentials);
            if (!matcher.matches()) {
                return new ErrorResponse(ErrorValue.invalid_request.name(), "either Basic Authentication or client_id should specified");
            }
            byte[] raw = Base64.getDecoder().decode(matcher.group("token"));
            int offset = Arrays.binarySearch(raw, (byte) ':');
            if (offset <= 0 || offset == raw.length - 1) {
                return new ErrorResponse(ErrorValue.invalid_token.name(), "invalid basic token");
            }
            if (clientSecret != null) {
                return new ErrorResponse(ErrorValue.invalid_request.name(), "client_secret occurred multiply times");
            }
            clientId = new String(raw, 0, offset);
            clientSecret = new String(raw, offset + 1, raw.length - offset - 1);
        }
        Registration.InformationResponse clientMetadata = clientMetadataStore.loadClientMetadata(clientId);
        if (clientMetadata == null) {
            return new ErrorResponse(ErrorValue.invalid_client.name(), "the client does not exist on this server");
        }
        if (credentials != null && clientMetadata.getClient_secret() != null && !clientMetadata.getClient_secret().equals(clientSecret)) { // clientMetadata.getToken_endpoint_auth_method() ???
            return new ErrorResponse(ErrorValue.unauthorized_client.name());
        }
        return clientMetadata;
    }

    protected String generateChars(boolean unique, int minLength, int maxLength) {
        assert minLength > 0 && maxLength >= minLength;
        if (unique) {
            String id = longToVisibleString(idWorker.getId());
            if (id.length() < minLength) {
                return id + generateVisibleChars(ThreadLocalRandom.current().nextInt(maxLength - id.length()));
            } else if (id.length() > maxLength) {
                throw new IllegalArgumentException("max length too small");
            }
            return id;
        }
        int r = minLength == maxLength ? 0 : ThreadLocalRandom.current().nextInt(maxLength - minLength);
        return generateVisibleChars(minLength + r);
    }

    private String longToVisibleString(long id) {
        int capacity = 0x7E - 0x20 + 1;
        StringBuilder sb = new StringBuilder();

        for (; id > 0; id /= capacity) {
            int remainder = (int) (id % capacity);
            sb.append((byte) remainder);
        }
        return sb.reverse().toString();
    }

    protected String generateUserCode(int length, boolean number) {
        StringBuilder sb = new StringBuilder(length);
        if (number) {
            ThreadLocalRandom.current().ints(length, 0x30, 0x39).forEach(i -> sb.append((byte) i));
        } else {
            ThreadLocalRandom.current().ints(length, 0x41, 0x5A).forEach(i -> sb.append((byte) i));
        } // 0,o,O; 1,i,I,l
        return sb.toString();
    }

    protected String generateVisibleChars(int length) { // for client_id, client_secret(>256bit), state, code, access_token, refresh_token
        StringBuilder sb = new StringBuilder(length);
        ThreadLocalRandom.current().ints(length, 0x20, 0x7E).forEach(i -> sb.append((byte) i)); // VSCHAR %x20-7E
        return sb.toString();
    }


    @Override
    public ServerResponse introspect(String credentials, Introspection.Request request) {
//        serverConfig.getIntrospection_endpoint_auth_methods_supported()
        if (request.isIllegal()) {
            return new ErrorResponse(ErrorValue.invalid_request.name());
        }
        if (request.getToken() == null) {
            return new ErrorResponse(ErrorValue.invalid_request.name(), "missing required parameter: token");
        }
        // 如果token是签名过的，可以先进行验签；token可能过期、未生效（nbf）、被撤销
        if (!verifyRegistrationAccessToken(request.getToken())) {
            return new ErrorResponse(ErrorValue.invalid_token.name());
        }
        String clientId;
        Matcher matcher = BEARER_PATTERN.matcher(credentials);
        if (matcher.matches()) {
            String bearerToken = matcher.group("token");
            OAuth2Token token = cacheManager.getCache(TOKEN_CACHE_NAME, String.class, OAuth2Token.class)
                    .get(String.format(TOKEN_FORMAT, bearerToken));
            if (token == null || token.revoked) {
                return new ErrorResponse(ErrorValue.invalid_token.name());
            }
            clientId = token.getClientId();
        } else {
            ServerResponse maybeClient = findIssuedClient(credentials, null, null);
            if (!maybeClient.isSuccess()) {
                return maybeClient;
            }
            clientId = ((Registration.InformationResponse) maybeClient).getClient_id();
        }
        if (request.getTokenTypeHint() == null || Revocation.TOKEN_TYPE_REFRESH_TOKEN.equals(request.getTokenTypeHint())) {
            OAuth2Token refreshToken = cacheManager.getCache(REFRESH_TOKEN_CACHE_NAME, String.class, OAuth2Token.class)
                    .get(String.format(REFRESH_TOKEN_FORMAT, request.getToken()));
            if (refreshToken != null && refreshToken.getClientId().equals(clientId)) {
                Introspection.Response response = new Introspection.Response(!refreshToken.revoked); // xxx other info
                response.setIat((int) refreshToken.getIssuedAt().getEpochSecond());
                if (refreshToken.getExpiresAt() != null) {
                    response.setExp((int) refreshToken.getExpiresAt().getEpochSecond());
                }
                return response;
            }
        }
        if (request.getTokenTypeHint() == null || Revocation.TOKEN_TYPE_ACCESS_TOKEN.equals(request.getTokenTypeHint())) {
            OAuth2Token accessToken = cacheManager.getCache(ACCESS_TOKEN_CACHE_NAME, String.class, OAuth2Token.class)
                    .get(String.format(ACCESS_TOKEN_FORMAT, request.getToken()));
            if (accessToken != null && accessToken.getClientId().equals(clientId)) {
                Introspection.Response response = new Introspection.Response(!accessToken.revoked); // xxx other info
                response.setIat((int) accessToken.getIssuedAt().getEpochSecond());
                response.setClient_id(clientId);
                response.setExp((int) accessToken.getExpiresAt().getEpochSecond());
                return response;
            }
            return new Introspection.Response(false);
        }
        return new ErrorResponse(ErrorValue.unsupported_token_type.name());
    }

    @Override
    public ServerResponse revoke(String credentials, Revocation.Request request) {
        if (request.isIllegal()) {
            return new ErrorResponse(ErrorValue.invalid_request.name(), "multiply param value");
        }
//        serverConfig.getRevocation_endpoint_auth_methods_supported()
        if (request.getToken() == null) {
            return new ErrorResponse(ErrorValue.invalid_request.name(), "missing required parameter: token");
        }
        if (!verifyRegistrationAccessToken(request.getToken())) {
            return new ErrorResponse(ErrorValue.invalid_token.name());
        }
        ServerResponse maybeClient = findIssuedClient(credentials, request.getClientId(), request.getClientSecret());
        if (!maybeClient.isSuccess()) {
            return maybeClient;
        }
        Registration.InformationResponse clientInfo = (Registration.InformationResponse) maybeClient;
        if (request.getTokenTypeHint() == null || Revocation.TOKEN_TYPE_REFRESH_TOKEN.equals(request.getTokenTypeHint())) {
            evictIfMatch(REFRESH_TOKEN_CACHE_NAME, REFRESH_TOKEN_FORMAT, clientInfo.getClient_id(), request.getToken());
            return null;
        }
        if (request.getTokenTypeHint() == null || Revocation.TOKEN_TYPE_ACCESS_TOKEN.equals(request.getTokenTypeHint())) {
            evictIfMatch(ACCESS_TOKEN_CACHE_NAME, ACCESS_TOKEN_FORMAT, clientInfo.getClient_id(), request.getToken());
            return null;
        }
        return new ErrorResponse(ErrorValue.unsupported_token_type.name());
    }

    private void evictIfMatch(String cacheName, String keyFormat, String clientId, String token) {
        Cache<String, OAuth2Token> cache = cacheManager.getCache(cacheName, String.class, OAuth2Token.class);
        OAuth2Token tokenValue = cache.getAndRemove(String.format(keyFormat, token));
        if (tokenValue != null && !tokenValue.getClientId().equals(clientId)) {
            cache.put(String.format(keyFormat, token), tokenValue);
        }
    }

    /**
     * 当token是JWS形式时，可以先验签
     * @param token 注册令牌
     * @return true当JWS验签通过时
     */
    protected boolean verifyRegistrationAccessToken(String token) {
        if (serverConfig.getKeys() != null && serverConfig.getKeys().getKeys().length > 0) {
            return verifyToken(token);
        }
        return true;
    }

    /**
     * <a href="https://openid.net/specs/openid-connect-core-1_0.html#UserInfo">UserInfo Endpoint</a>
     * @param credentials
     * @return
     */
    public ServerResponse obtainUserInfo(String credentials) {
        Matcher matcher = BEARER_PATTERN.matcher(credentials);
        if (!matcher.matches()) {
            return new ErrorResponse(ErrorValue.invalid_request.name());
        }
        String accessToken = matcher.group("token");
        OAuth2Token token = cacheManager.getCache(ACCESS_TOKEN_CACHE_NAME, String.class, OAuth2Token.class)
                .get(String.format(ACCESS_TOKEN_FORMAT, accessToken));
        if (token == null || token.revoked) {
            return new ErrorResponse(ErrorValue.invalid_token.name());
        }
        UserInfo userInfo = userService.loadUser(token.getSubject());
        return new UserInfoResponse(userInfo);
    }

    @Getter
    @Setter
    @ToString
    protected static class OAuth2Token implements Serializable {

        private String token; // 令牌

        private String tokenType; // opaque or jwt

        private String subject; // 授权主体

        private String clientId; // 授权给的客户端

        private Instant issuedAt; // 令牌颁发时间

        private Instant expiresAt; // 令牌过期时间

        private boolean virgin; // 只能使用一次用的标记

        private boolean revoked; // 撤销标记
    }

    @Getter
    @Setter
    @ToString
    protected static class JwtAccessToken implements JsonWebToken {
        /**
         * 签发主体
         */
        protected final String iss;

        /**
         * 失效时间（单位：秒）
         */
        protected final Integer exp;

        /**
         * 目标受众
         */
        protected final Object aud;

        /**
         * 持有主体
         */
        protected final String sub;

        protected final String client_id;

        /**
         * 签发时间（单位：秒）
         */
        protected final Integer iat;

        /**
         * JSON Web Token标识
         */
        protected final String jti;

        /**
         * 令牌最早可用时间（单位：秒）
         */
        protected Integer nbf;

        protected String scope;

        /**
         * <a href="https://www.rfc-editor.org/rfc/rfc7643.txt">System for Cross-domain Identity Management: Core Schema</a>
         */
        protected List<Object> groups; // {value:,$ref:,display:, type:}

        private List<Object> entitlements; // {value:, display:, type:, primary:}

        protected List<Object> roles; // {value:, display:, type:, primary:}

        JwtAccessToken(URL issuer, Instant expiration, Object audience, String subject, String clientId, Instant issuedAt, String jsonWebTokenIdentifier) {
            this.iss = issuer.toString();
            this.exp = Math.toIntExact(expiration.getEpochSecond());
            this.aud = Objects.requireNonNull(audience);
            this.sub = Objects.requireNonNull(subject);
            this.client_id = Objects.requireNonNull(clientId);
            this.iat = Math.toIntExact(issuedAt.getEpochSecond());
            this.jti = Objects.requireNonNull(jsonWebTokenIdentifier);
        }
    }

    @Getter
    @Setter
    @ToString
    public static class OpenIDConnectAuthorizationRequest extends Authorization.Request {

        /**
         * 授权服务器返回参数机制，值包括query（302重定向时URL查询参数中）、fragment（302重定向时URL锚参数中）、web_message（向父窗口post message，适合popup方式）、form_post（用自动提交表单将客户端重定向到回调地址）
         */
        protected String responseMode;

        /**
         * 关联客户端会话和ID token，用于防止重放攻击，隐含模式、OIDC的混合模式不为null
         */
        protected String nonce;

        /**
         * 告知授权服务器展示同意时的页面形式，值为page、popup、touch、wap之一
         */
        protected String display;

        /**
         * 授权服务器提示终端用户再次认证/同意选项，值范围为none、login、consent、select_account，空格分隔
         */
        protected String prompt;

        /**
         * 认证/同意有效期，0等同于prompt=login。当此参数存在时，授权服务器返回的JWT需包含auth_time
         */
        protected Integer maxAge;

        /**
         * 界面偏好，OpenID provider不支持的地区时不能视为错误
         */
        protected List<String> uiLocales;

        protected String idTokenHint;

        /**
         * 终端用户登录身份标识提示信息，如邮箱地址、手机号码
         */
        protected String loginHint;

        protected List<String> acrValues;

        /**
         * 签名和/或加密的请求息
         */
        protected String request;

        /**
         * 包含签名和/或加密的请求信息链接
         */
        protected String requestUri;

        /**
         * OIDC客户端请求自签名OIDC服务方时提供自身信息
         */
        protected ClientMetadata registration;

        protected ClaimsRequest claims;

        public OpenIDConnectAuthorizationRequest() {
        }


        public OpenIDConnectAuthorizationRequest(Map<String, List<String>> multiValueMap) {
            super(multiValueMap);
            try {
                this.responseMode = Authorization.getFirstValue(multiValueMap, "response_mode");
                this.nonce = Authorization.getFirstValue(multiValueMap, "nonce");
                this.display = Authorization.getFirstValue(multiValueMap, "display");
                this.prompt = Authorization.getFirstValue(multiValueMap, "prompt");
                this.maxAge = Optional.ofNullable(Authorization.getFirstValue(multiValueMap, "max_age")).map(Integer::valueOf).orElse(null);
                this.uiLocales = multiValueMap.get("ui_locales");
                this.idTokenHint = Authorization.getFirstValue(multiValueMap, "id_token_hint");
                this.loginHint = Authorization.getFirstValue(multiValueMap, "login_hint");
                this.acrValues = multiValueMap.get("acr_values");
                // xxx claims
            } catch (IllegalArgumentException | NullPointerException e) {
                setIllegal(true);
            }
        }

        @Override
        public Map<String, Object> toMap() {
            Map<String, Object> map = super.toMap();
            map.put("response_mode", this.responseMode);
            map.put("nonce", this.nonce);
            map.put("display", this.display);
            map.put("prompt", this.prompt);
            map.put("max_age", this.maxAge);
            map.put("ui_locales", this.uiLocales);
            map.put("idTokenHint", this.idTokenHint);
            map.put("loginHint", this.loginHint);
            map.put("acr_values", this.acrValues);
            map.put("claims", this.claims);
            return map;
        }
    }

    public enum ResponseMode {
        QUERY,
        FRAGMENT,
        WEB_MESSAGE,
        FORM_POST;

        public static ResponseMode getInstance(String name) {
            for (ResponseMode instance : ResponseMode.values()) {
                if (instance.name().equalsIgnoreCase(name)) {
                    return instance;
                }
            }
            return null;
        }
    }

    @Getter
    @Setter
    @ToString
    public static class IDTokenResponse extends Authorization.TokenResponse {

        protected final String id_token;

        protected String code;

        protected String state;

        public IDTokenResponse(String accessToken, String tokenType, String idToken) {
            super(accessToken, tokenType);
            this.id_token = Objects.requireNonNull(idToken);
        }
    }

    @Getter
    @Setter
    @ToString
    public static class ClaimsRequest {

        protected Map<String, ClaimRequest> userinfo;

        protected Map<String, ClaimRequest> id_token;
    }

    @Getter
    @Setter
    @ToString
    public static class ClaimRequest {

        private Boolean essential;

        private Object value;

        private Object[] values;
    }

    public static class UserInfoResponse extends UserInfo implements ServerResponse {

        public UserInfoResponse(UserInfo userInfo) {
            super(userInfo.sub);
            this.address = userInfo.address;
            this.birthdate = userInfo.birthdate;
            this.email = userInfo.email;
            this.email_verified = userInfo.email_verified;
            this.family_name = userInfo.family_name;
            this.gender = userInfo.gender;
            this.given_name = userInfo.given_name;
            this.locale = userInfo.locale;
            this.middle_name = userInfo.middle_name;
            this.name = userInfo.name;
            this.nickname = userInfo.nickname;
            this.phone_number = userInfo.phone_number;
            this.phone_number_verified = userInfo.phone_number_verified;
            this.picture = userInfo.picture;
            this.preferred_username = userInfo.preferred_username;
            this.profile = userInfo.profile;
            this.updated_at = userInfo.updated_at;
            this.website = userInfo.website;
            this.zoneinfo = userInfo.zoneinfo;
        }
    }

    @Getter
    @Setter
    @ToString
    public static class IDToken extends UserInfo {

        /**
         * 签发主体
         */
        protected String iss;

//        /**
//         * 持有主体
//         */
//        protected String sub;

        /**
         * 目标受众
         */
        protected Object aud;

        /**
         * 失效时间（单位：秒）
         */
        protected Integer exp;

        /**
         * 签发时间（单位：秒）
         */
        protected Integer iat;

        /**
         * 终端用户认证时间（单位：秒）
         */
        protected Long auth_time;

        /**
         * 当请求参数包含时，返回必须包含
         */
        protected String nonce;

        protected String acr;

        /**
         * 认证方法引用，如OTP、password
         */
        protected String[] amr;

        /**
         * 被认证团体，包含client_id信息
         */
        protected String azp;

        /**
         * 访问令牌摘要算法后取左半部分字节，然后用base64url编码，如alg为RS256时，使用SHA-256对access token进行摘要，取左边128位，然后使用base64url编码。
         * 当response_type为"code id_token token"时，此值必须返回
         */
        protected String at_hash;

        /**
         * 授权码摘要算法后取左半部分字节，然后用base64url编码，如alg为HS512时，使用SHA-512对code进行摘要，取左边256位，然后使用base64url编码。
         * 当response_type为"code id_token token"或"code id_token"时，此值必须返回
         */
        protected String c_hash;

        public IDToken(URL issuer, String subject, String audience, int expirationTime, int issueTime) {
            super(subject);
            this.iss = issuer.toString();
            this.aud = audience;
            this.exp = expirationTime;
            this.iat = issueTime;
        }

    }

    @Getter
    @Setter
    @ToString
    public static class UserInfo {

        protected final String sub;

        protected String name;

        protected String given_name;

        protected String family_name;

        protected String middle_name;

        /**
         * 昵称，可以是given_name
         */
        protected String nickname;

        /**
         * 短名，如j.doe，需为JSON String
         */
        protected String preferred_username;

        /**
         * 用户资料URL
         */
        protected String profile;

        /**
         * 用户资料图片URL
         */
        protected String picture;

        /**
         * 用户博客或个人网站URL
         */
        protected String website;

        protected String email;

        protected Boolean email_verified;

        /**
         * 性别，如female、male
         */
        protected String gender;

        /**
         * 用户生日，格式为：YYYY-MM-DD或YYYY。如果年份为0000，则表示没有年份
         */
        protected String birthdate;

        /**
         * 用户时区，如 Europe/Paris
         */
        protected String zoneinfo;

        /**
         * 用户语言，如 en-US。注意有些程序格式为下划线，如en_US
         */
        protected String locale;

        /**
         * 电话号码，格式参考E.164，如+1 (425) 555-1212，如果有扩展信息，则如+1 (425) 555-1212;ext=5678
         */
        protected String phone_number;

        protected Boolean phone_number_verified;

        protected Address address;

        /**
         * 用户信息更新时间（unix时间戳）
         */
        protected Integer updated_at;

        public UserInfo(String subject) {
            this.sub = Objects.requireNonNull(subject);
        }

    }

    @Getter
    @Setter
    @ToString
    public static class Address {

        /**
         * 格式化的邮寄地址（可能多行）
         */
        private String formatted;

        /**
         * 可能多行的街道地址，包含街道、门牌号、邮箱号
         */
        private String street_address;

        /**
         * 城市
         */
        private String locality;

        /**
         * 省/州
         */
        private String region;

        /**
         * 邮箱号码
         */
        private String postal_code;

        /**
         * 国家
         */
        private String country;
    }
}
