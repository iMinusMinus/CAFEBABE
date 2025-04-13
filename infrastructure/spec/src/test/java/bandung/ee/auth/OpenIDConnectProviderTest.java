package bandung.ee.auth;

import bandung.ee.json.BindingProvider;
import bandung.se.IdWorker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import std.ietf.http.jose.JsonWebAlgorithm;
import std.ietf.http.jose.JsonWebKey;
import std.ietf.http.jose.JsonWebKeyDeserializer;
import std.ietf.http.jose.JsonWebKeySet;
import std.ietf.http.oauth.Authorization;
import std.ietf.http.oauth.ClientMetadata;
import std.ietf.http.oauth.ErrorResponse;
import std.ietf.http.oauth.Introspection;
import std.ietf.http.oauth.Registration;
import std.ietf.http.oauth.Revocation;
import std.ietf.http.oauth.ServerResponse;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbConfig;
import javax.json.bind.spi.JsonbProvider;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

/**
 * OP 测试类
 * @author iMinusMinus
 * @date 2024-04-06
 */
@ExtendWith({MockitoExtension.class})
public class OpenIDConnectProviderTest {

    private static OpenIDConnectProvider subject;

    private static Jsonb jsonb;

    private OpenIDProviderConfig serverConfig;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private ClientMetadataStore clientMetadataStore;

    @Mock
    private UserService<String, OpenIDConnectProvider.UserInfo> userService;

    @BeforeEach
    public void setUp() {
        serverConfig = new OpenIDProviderConfig();
        serverConfig.setResponse_types_supported(Arrays.asList("code", "token"));
        serverConfig.setForcePKCE(false);
        JsonbConfig config = new JsonbConfig();
        config.withDeserializers(new JsonWebKeyDeserializer());
        jsonb = JsonbProvider.provider(BindingProvider.class.getName()).create().withConfig(config).build();
        subject = new OpenIDConnectProvider(serverConfig, cacheManager, IdWorker.STANDALONE,
                x -> jsonb.toJson(x).getBytes(StandardCharsets.UTF_8),
                (ba, t) -> jsonb.fromJson(new ByteArrayInputStream(ba), t),
                clientMetadataStore, userService);
    }

    @Test
    public void testRegistration() throws Exception {
        ClientMetadata request = new ClientMetadata();
        request.setRedirect_uris(Arrays.asList("https://client.example.org/callback", "https://client.example.org/callback2"));
        request.setClient_name("My Example Client");
        request.setToken_endpoint_auth_method("client_secret_basic");
        request.setPolicy_uri(new URL("https://client.example.org/policy.html"));
        JsonWebKeySet keySet = new JsonWebKeySet();
        JsonWebKeySet.RsaJwk key = new JsonWebKeySet.RsaJwk();
        key.setE("AQAB");
        key.setN("nj3YJwsLUFl9BmpAbkOswCNVx17Eh9wMO-_AReZwBqfaWFcfGHrZXsIV2VMCNVNU8Tpb4obUaSXcRcQ-VMsfQPJm9IzgtRdAY8NN8Xb7PEcYyklBjvTtuPbpzIaqyiUepzUXNDFuAOOkrIol3WmflPUUgMKULBN0EUd1fpOD70pRM0rlp_gg_WNUKoW1V-3keYUJoXH9NztEDm_D2MQXj9eGOJJ8yPgGL8PAZMLe2R7jb9TxOCPDED7tY_TU4nFPlxptw59A42mldEmViXsKQt60s1SLboazxFKveqXC_jpLUt22OC6GUG63p-REw-ZOr3r845z50wMuzifQrMI9bQ");
        JsonWebKey[] keys = {key};
        keySet.setKeys(keys);
        request.setJwks(keySet);

        serverConfig.setRegistration_endpoint(URI.create("https://example.org"));
        serverConfig.setGrant_types_supported(Arrays.asList("authorization_code", "implicit"));
        serverConfig.setResponse_types_supported(Collections.singletonList("code"));
        String[] scopes = {"profile"};
        serverConfig.setScopes_supported(scopes);
        serverConfig.setSubject_types_supported(Collections.singletonList("public"));
        serverConfig.setId_token_signing_alg_values_supported(Collections.singletonList(JsonWebAlgorithm.HS256.jwaName()));
        serverConfig.setId_token_encryption_alg_values_supported(Collections.singletonList(JsonWebAlgorithm.ECDH_ES_A128KW.jwaName()));
        serverConfig.setId_token_encryption_enc_values_supported(Collections.emptyList());
        serverConfig.setAcr_values_supported(Collections.emptyList());
        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.REGISTRATION_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        Mockito.doNothing().when(cache).put(Mockito.anyString(), Mockito.any());
        Mockito.when(clientMetadataStore.rememberClientMetadata(Mockito.any())).thenReturn(null);

        ServerResponse response = subject.registering("Bearer ey23f2.adfj230.af32-developer321", request);
        Assertions.assertTrue(response instanceof Registration.InformationResponse);
        Registration.InformationResponse info = (Registration.InformationResponse) response;
        Assertions.assertNotNull(info.getClient_id());
        Assertions.assertNotNull(info.getClient_secret());
        Assertions.assertNotNull(info.getClient_secret_expires_at());
    }

    @Test
    public void testRegistrationRequestUsingSoftwareStatement() {
        ClientMetadata request = new ClientMetadata();
        request.setRedirect_uris(Arrays.asList("https://client.example.org/callback", "https://client.example.org/callback2"));
        request.setSoftware_statement("eyJhbGciOiJSUzI1NiJ9." +
                "eyJzb2Z0d2FyZV9pZCI6IjROUkIxLTBYWkFCWkk5RTYtNVNNM1IiLCJjbGll" +
                "bnRfbmFtZSI6IkV4YW1wbGUgU3RhdGVtZW50LWJhc2VkIENsaWVudCIsImNs" +
                "aWVudF91cmkiOiJodHRwczovL2NsaWVudC5leGFtcGxlLm5ldC8ifQ." +
                "GHfL4QNIrQwL18BSRdE595T9jbzqa06R9BT8w409x9oIcKaZo_mt15riEXHa" +
                "zdISUvDIZhtiyNrSHQ8K4TvqWxH6uJgcmoodZdPwmWRIEYbQDLqPNxREtYn0" +
                "5X3AR7ia4FRjQ2ojZjk5fJqJdQ-JcfxyhK-P8BAWBd6I2LLA77IG32xtbhxY" +
                "fHX7VhuU5ProJO8uvu3Ayv4XRhLZJY4yKfmyjiiKiPNe-Ia4SMy_d_QSWxsk" +
                "U5XIQl5Sa2YRPMbDRXttm2TfnZM1xx70DoYi8g6czz-CPGRi4SW_S2RKHIJf" +
                "IjoI3zTJ0Y2oe0_EJAiXbL6OyF9S5tKxDXV8JIndSA");
        request.setScope("read write");
        ServerResponse response = subject.registering(null, request);
        Assertions.assertTrue(response instanceof ErrorResponse);
    }

    @Test
    public void testRead() {
        String clientId = "s6BhdRkqt3";

        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.REGISTRATION_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        OpenIDConnectProvider.OAuth2Token token = new OpenIDConnectProvider.OAuth2Token();
        token.setToken(String.valueOf(System.currentTimeMillis()));
        Mockito.when(cache.get(Mockito.anyString())).thenReturn(token);
        Cache<String, Audit> auditCache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.TOKEN_FAILURE_CACHE_NAME, String.class, Audit.class)).thenReturn(auditCache);
        Mockito.when(auditCache.get(Mockito.anyString())).thenReturn(null);
        Mockito.doNothing().when(auditCache).put(Mockito.anyString(), Mockito.any());

        ServerResponse response = subject.read("Bearer reg-23410913-abewfq.123483", clientId);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    public void testUpdate() throws Exception {
        Registration.UpdateRequest request = new Registration.UpdateRequest();
        request.setClient_id("s6BhdRkqt3");
        request.setClient_secret("cf136dc3c1fc93f31185e5885805d");
        request.setRedirect_uris(Arrays.asList("https://client.example.org/callback", "https://client.example.org/alt"));
        request.setGrant_types(Arrays.asList("authorization_code", "refresh_token"));
        request.setToken_endpoint_auth_method("client_secret_basic");
        request.setJwks_uri(new URL("https://client.example.org/my_public_keys.jwks"));
        request.setClient_name("My New Example");
        request.setLogo_uri(new URL("https://client.example.org/newlogo.png"));

        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.REGISTRATION_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        OpenIDConnectProvider.OAuth2Token token = new OpenIDConnectProvider.OAuth2Token();
        token.setToken("reg-23410913-abewfq.123483");
        Mockito.when(cache.get(Mockito.anyString())).thenReturn(token);
        Cache<String, Audit> auditCache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.TOKEN_FAILURE_CACHE_NAME, String.class, Audit.class)).thenReturn(auditCache);
        Mockito.when(auditCache.get(Mockito.anyString())).thenReturn(null);
        Registration.InformationResponse clientInfo = new Registration.InformationResponse();
        clientInfo.setClient_id("s6BhdRkqt3");
        clientInfo.setClient_secret("cf136dc3c1fc93f31185e5885805d");
        Mockito.when(clientMetadataStore.loadClientMetadata(request.getClient_id())).thenReturn(clientInfo);
        Mockito.when(clientMetadataStore.refreshClientMetadata(Mockito.any())).thenReturn(true);
        serverConfig.setGrant_types_supported(Arrays.asList("authorization_code", "implicit", "refresh_token"));
        serverConfig.setResponse_types_supported(Collections.singletonList("code"));
        String[] scopes = {"profile"};
        serverConfig.setScopes_supported(scopes);
        serverConfig.setSubject_types_supported(Collections.singletonList("public"));
        serverConfig.setId_token_signing_alg_values_supported(Collections.singletonList(JsonWebAlgorithm.HS256.jwaName()));
        serverConfig.setId_token_encryption_alg_values_supported(Collections.singletonList(JsonWebAlgorithm.ECDH_ES_A128KW.jwaName()));
        serverConfig.setId_token_encryption_enc_values_supported(Collections.emptyList());
        serverConfig.setAcr_values_supported(Collections.emptyList());

        ServerResponse response = subject.update("Bearer reg-23410913-abewfq.123483", request);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    public void testDeprovision() {
        String clientId = "s6BhdRkqt3";

        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.REGISTRATION_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        OpenIDConnectProvider.OAuth2Token token = new OpenIDConnectProvider.OAuth2Token();
        token.setToken("reg-23410913-abewfq.123483");
        Mockito.when(cache.get(Mockito.anyString())).thenReturn(token);
        Cache<String, Audit> auditCache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.TOKEN_FAILURE_CACHE_NAME, String.class, Audit.class)).thenReturn(auditCache);
        Mockito.when(auditCache.get(Mockito.anyString())).thenReturn(null);
        Registration.InformationResponse clientInfo = new Registration.InformationResponse();
        clientInfo.setClient_id("s6BhdRkqt3");
        clientInfo.setClient_secret("cf136dc3c1fc93f31185e5885805d");
        Mockito.when(clientMetadataStore.loadClientMetadata(clientId)).thenReturn(clientInfo);
        Mockito.when(clientMetadataStore.removeClientMetadata(clientId)).thenReturn(true);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.REGISTRATION_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        Mockito.when(cache.remove(Mockito.anyString())).thenReturn(true);

        ServerResponse response = subject.deprovision("Bearer reg-23410913-abewfq.123483", clientId);
        Assertions.assertFalse(response instanceof ErrorResponse);
    }

    @Test
    public void testIntrospection_case1() {
//        String json = "{\n" +
//                "      \"active\": true,\n" +
//                "      \"client_id\": \"l238j323ds-23ij4\",\n" +
//                "      \"username\": \"jdoe\",\n" +
//                "      \"scope\": \"read write dolphin\",\n" +
//                "      \"sub\": \"Z5O3upPC88QrAjx00dis\",\n" +
//                "      \"aud\": \"https://protected.example.net/resource\",\n" +
//                "      \"iss\": \"https://server.example.com/\",\n" +
//                "      \"exp\": 1419356238,\n" +
//                "      \"iat\": 1419350238,\n" +
//                "      \"extension_field\": \"twenty-seven\"\n" +
//                "     }";
        Introspection.Request request = new Introspection.Request("2YotnFZFEjr1zCsicMWpAA");

        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        OpenIDConnectProvider.OAuth2Token token = new OpenIDConnectProvider.OAuth2Token();
        token.setClientId(String.valueOf(System.currentTimeMillis()));
        Mockito.when(cache.get(String.format(OpenIDConnectProvider.TOKEN_FORMAT, "23410913-abewfq.123483"))).thenReturn(token);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.ACCESS_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        OpenIDConnectProvider.OAuth2Token accessToken = new OpenIDConnectProvider.OAuth2Token();
        accessToken.setToken(request.getToken());
        accessToken.setClientId(token.getClientId());
        accessToken.setIssuedAt(Instant.now().minusSeconds(3600L));
        accessToken.setExpiresAt(Instant.now().plusSeconds(3600L));
        Mockito.when(cache.get(String.format(OpenIDConnectProvider.ACCESS_TOKEN_FORMAT, request.getToken()))).thenReturn(accessToken);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.REFRESH_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        Mockito.when(cache.get(String.format(OpenIDConnectProvider.REFRESH_TOKEN_FORMAT, request.getToken()))).thenReturn(null);


        ServerResponse response = subject.introspect("Bearer 23410913-abewfq.123483", request);
        Assertions.assertTrue(response instanceof Introspection.Response);
        Introspection.Response resp = (Introspection.Response) response;
        Assertions.assertTrue(resp.isActive());
    }

    @Test
    public void testIntrospection_case2() {
        Introspection.Request request = new Introspection.Request("mF_9.B5f-4.1JqM");
        request.setTokenTypeHint("access_token");

        Registration.InformationResponse clientInfo = new Registration.InformationResponse();
        clientInfo.setClient_id("s6BhdRkqt3");
        clientInfo.setClient_secret("gX1fBat3bV");
        Mockito.when(clientMetadataStore.loadClientMetadata(Mockito.anyString())).thenReturn(clientInfo);
        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.ACCESS_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        OpenIDConnectProvider.OAuth2Token accessToken = new OpenIDConnectProvider.OAuth2Token();
        accessToken.setToken(request.getToken());
        accessToken.setClientId(clientInfo.getClient_id());
        accessToken.setIssuedAt(Instant.now().minusSeconds(3600L));
        accessToken.setExpiresAt(Instant.now().plusSeconds(3600L));
        accessToken.setRevoked(true);

        ServerResponse response = subject.introspect("Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW", request);
        Assertions.assertTrue(response instanceof Introspection.Response);
        Introspection.Response resp = (Introspection.Response) response;
        Assertions.assertFalse(resp.isActive());
    }

    @Test
    public void testRevoke() {
        Revocation.Request request = new Revocation.Request("45ghiukldjahdnhzdauz");
        request.setTokenTypeHint("refresh_token");

        Registration.InformationResponse clientInfo = new Registration.InformationResponse();
        clientInfo.setClient_id("s6BhdRkqt3");
        clientInfo.setClient_secret("gX1fBat3bV");
        Mockito.when(clientMetadataStore.loadClientMetadata(Mockito.anyString())).thenReturn(clientInfo);
        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.REFRESH_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        Mockito.when(cache.getAndRemove(String.format(OpenIDConnectProvider.REFRESH_TOKEN_FORMAT, request.getToken()))).thenReturn(null);

        ServerResponse response = subject.revoke("Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW", request);
        Assertions.assertNull(response);
    }

    @Test
    public void testRevokeReturnError() {
        Revocation.Request request = new Revocation.Request("45ghiukldjahdnhzdauz");
        request.setTokenTypeHint("quirks_token");

        Registration.InformationResponse clientInfo = new Registration.InformationResponse();
        clientInfo.setClient_id("s6BhdRkqt3");
        clientInfo.setClient_secret("gX1fBat3bV");
        Mockito.when(clientMetadataStore.loadClientMetadata(Mockito.anyString())).thenReturn(clientInfo);

        ServerResponse response = subject.revoke("Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW", request);
        Assertions.assertTrue(response instanceof ErrorResponse);
    }

    @Test
    public void testAuthorizationCodeGrant_step1() {
        Authorization.Request request = new Authorization.Request();
        request.setResponseType("code");
        request.setClientId("s6BhdRkqt3");
        request.setState("xyz");
        request.setRedirectUri("https://client.example.com/cb");

        serverConfig.setConsentPage("https://iminusminus.github.io/oauth/login");
        Registration.InformationResponse regInfo = new Registration.InformationResponse();
        regInfo.setRedirect_uris(Collections.emptyList());
        Mockito.when(clientMetadataStore.loadClientMetadata(request.getClientId())).thenReturn(regInfo);
        subject.authorize(request);

        Cache<String, Authorization.Request> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.AUTHORIZATION_CODE_CACHE_NAME, String.class, Authorization.Request.class)).thenReturn(cache);
        Mockito.doNothing().when(cache).put(Mockito.anyString(), Mockito.any());
        Cache<String, OpenIDConnectProvider.UserInfo> userInfoCache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.SUBJECT_CACHE_NAME, String.class, OpenIDConnectProvider.UserInfo.class)).thenReturn(userInfoCache);
        Mockito.doNothing().when(userInfoCache).put(Mockito.anyString(), Mockito.any());
        ServerResponse response = subject.onAuthorizeCallback(request, System.currentTimeMillis() / 1000, false, new OpenIDConnectProvider.UserInfo("<subject>"));
        Assertions.assertTrue(response instanceof Authorization.RedirectResponse);
        String location = ((Authorization.RedirectResponse) response).getLocation();
        Assertions.assertNotNull(location);
        Assertions.assertTrue(location.contains("code="));
        Assertions.assertTrue(location.contains("state="));

        response = subject.onAuthorizeCallback(request, System.currentTimeMillis() / 1000, true, null);
        Assertions.assertEquals("https://client.example.com/cb?error=access_denied&state=xyz", ((Authorization.RedirectResponse) response).getLocation());
    }

    @Test
    public void testAuthorizationCodeGrant_step2() {
        Authorization.TokenRequest tokenRequest = new Authorization.TokenRequest("authorization_code");
        tokenRequest.setCode("SplxlOBeZQQYbYS6WxSbIA");
        tokenRequest.setRedirectUri("https://client.example.com/cb");

        Registration.InformationResponse regInfo = new Registration.InformationResponse();
        regInfo.setRedirect_uris(Collections.singletonList(tokenRequest.getRedirectUri()));
        Mockito.when(clientMetadataStore.loadClientMetadata(Mockito.anyString())).thenReturn(regInfo);
        Cache<String, Authorization.Request> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.AUTHORIZATION_CODE_CACHE_NAME, String.class, Authorization.Request.class)).thenReturn(cache);
        Authorization.Request request = new Authorization.Request();
        request.setResponseType("code");
        request.setClientId("s6BhdRkqt3");
        request.setState("xyz");
        request.setRedirectUri("https://client.example.com/cb");
        Mockito.when(cache.get(String.format(OpenIDConnectProvider.AUTHORIZATION_FORMAT, tokenRequest.getCode()))).thenReturn(request);
        Mockito.when(cache.remove(Mockito.anyString())).thenReturn(true);
        Cache<String, OpenIDConnectProvider.UserInfo> userInfoCache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.SUBJECT_CACHE_NAME, String.class, OpenIDConnectProvider.UserInfo.class)).thenReturn(userInfoCache);
        Mockito.when(userInfoCache.get(Mockito.anyString())).thenReturn(new OpenIDConnectProvider.UserInfo("<subject>"));
        Cache<String, OpenIDConnectProvider.OAuth2Token> atCache = Mockito.mock(Cache.class);
        OpenIDConnectProvider.OAuth2Token token = new OpenIDConnectProvider.OAuth2Token();
        token.setToken(tokenRequest.getCode());
        token.setVirgin(true);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.ACCESS_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(atCache);
        Mockito.doNothing().when(atCache).put(Mockito.anyString(), Mockito.any());

        ServerResponse response = subject.grant("Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW", tokenRequest);
        Authorization.TokenResponse tokenResponse = (Authorization.TokenResponse) response;
        Assertions.assertNotNull(tokenResponse.getAccess_token());
        Assertions.assertNotNull(tokenResponse.getToken_type());
    }

    @Test
    public void testImplicitGrant() {
        Authorization.Request request = new Authorization.Request();
        request.setResponseType("token");
        request.setClientId("s6BhdRkqt3");
        request.setState("xyz");
        request.setRedirectUri("https://client.example.com/cb");

        Registration.InformationResponse regInfo = new Registration.InformationResponse();
        regInfo.setRedirect_uris(Collections.emptyList());
        Mockito.when(clientMetadataStore.loadClientMetadata(request.getClientId())).thenReturn(regInfo);
        ServerResponse response = subject.authorize(request); // 跳转授权界面
        System.out.println(response);

        Mockito.when(clientMetadataStore.loadClientMetadata(request.getClientId())).thenReturn(regInfo);
        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.ACCESS_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        Mockito.doNothing().when(cache).put(Mockito.anyString(), Mockito.any());
        response = subject.onAuthorizeCallback(request, System.currentTimeMillis() / 1000, false, new OpenIDConnectProvider.UserInfo("<subject>")); // 用户同意授权，重定向回redirect_uri

        Assertions.assertTrue(response instanceof Authorization.RedirectResponse);
        String location = ((Authorization.RedirectResponse) response).getLocation();
        Assertions.assertTrue(location.contains("access_token="));
        Assertions.assertTrue(location.contains("expires_in="));
        Assertions.assertTrue(location.contains("state="));
        Assertions.assertTrue(location.contains("token_type="));

        serverConfig.setCode_challenge_methods_supported(Arrays.asList(Authorization.CHALLENGE_METHOD_NONE, Authorization.CHALLENGE_METHOD_S256));
        request.setCodeChallengeMethod("S512");
        response = subject.authorize(request);
        Assertions.assertTrue(response instanceof ErrorResponse);
    }

    @Test
    public void testResourceOwnerPasswordCredentialsGrant() {
        Authorization.TokenRequest request = new Authorization.TokenRequest("password");
        request.setUsername("johndoe");
        request.setPassword("A3ddj3w");

        Cache<String, Audit> auditCache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.TOKEN_FAILURE_CACHE_NAME, String.class, Audit.class)).thenReturn(auditCache);
        Mockito.when(auditCache.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(userService.authenticate(request.getUsername(), request.getPassword())).thenReturn(true);
        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.ACCESS_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        Mockito.doNothing().when(cache).put(Mockito.anyString(), Mockito.any());

        ServerResponse response = subject.grant("Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW", request);
        Authorization.TokenResponse tokenResponse = (Authorization.TokenResponse) response;
        Assertions.assertNotNull(tokenResponse.getAccess_token());
        Assertions.assertNotNull(tokenResponse.getToken_type());
    }

    @Test
    public void testClientCredentialsGrant() {
        Authorization.TokenRequest request = new Authorization.TokenRequest("client_credentials");

        Registration.InformationResponse regInfo = new Registration.InformationResponse();
        regInfo.setRedirect_uris(Collections.emptyList());
        Mockito.when(clientMetadataStore.loadClientMetadata(Mockito.anyString())).thenReturn(regInfo);
        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.ACCESS_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        Mockito.doNothing().when(cache).put(Mockito.anyString(), Mockito.any());

        ServerResponse response = subject.grant("Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW", request);
        Authorization.TokenResponse tokenResponse = (Authorization.TokenResponse) response;
        Assertions.assertNotNull(tokenResponse.getAccess_token());
        Assertions.assertNotNull(tokenResponse.getToken_type());
    }

    @Test
    public void testRefreshingAccessToken() {
        Authorization.TokenRequest request = new Authorization.TokenRequest("refresh_token");
        request.setRefreshToken("tGzv3JOkF0XG5Qx2TlKWIA");

        serverConfig.setRefreshTokenTtl(24 * 3600);
        Registration.InformationResponse regInfo = new Registration.InformationResponse();
        regInfo.setRedirect_uris(Collections.emptyList());
        Mockito.when(clientMetadataStore.loadClientMetadata(Mockito.anyString())).thenReturn(regInfo);
        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.REFRESH_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        OpenIDConnectProvider.OAuth2Token token = new OpenIDConnectProvider.OAuth2Token();
        token.setToken(request.getRefreshToken());
        token.setSubject("<subject>");
        token.setVirgin(true);
        Mockito.when(cache.get(String.format(OpenIDConnectProvider.REFRESH_TOKEN_FORMAT, request.getRefreshToken()))).thenReturn(token);
        Mockito.when(userService.loadUser(token.getSubject())).thenReturn(new OpenIDConnectProvider.UserInfo(token.getSubject()));
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.ACCESS_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.REFRESH_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        Mockito.doNothing().when(cache).put(Mockito.anyString(), Mockito.any());

        ServerResponse response = subject.grant("Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW", request);
        Authorization.TokenResponse tokenResponse = (Authorization.TokenResponse) response;
        Assertions.assertNotNull(tokenResponse.getAccess_token());
        Assertions.assertNotNull(tokenResponse.getToken_type());
        Assertions.assertNotNull(tokenResponse.getRefresh_token());
    }

    @Test
    public void testAuthorizeWithRequest() throws Exception {
        String key = "{\n" +
                "   \"kty\":\"RSA\",\n" +
                "   \"kid\":\"k2bdc\",\n" +
                "   \"n\":\"y9Lqv4fCp6Ei-u2-ZCKq83YvbFEk6JMs_pSj76eMkddWRuWX2aBKGHAtKlE5P7_vn__PCKZWePt3vGkB6ePgzAFu08NmKemwE5bQI0e6kIChtt_6KzT5OaaXDFI6qCLJmk51Cc4VYFaxgqevMncYrzaW_50mZ1yGSFIQzLYP8bijAHGVjdEFgZaZEN9lsn_GdWLaJpHrB3ROlS50E45wxrlg9xMncVb8qDPuXZarvghLL0HzOuYRadBJVoWZowDNTpKpk2RklZ7QaBO7XDv3uR7s_sf2g-bAjSYxYUGsqkNA9b3xVW53am_UZZ3tZbFTIh557JICWKHlWj5uzeJXaw\",\n" +
                "   \"e\":\"AQAB\"\n" +
                "  }";
        JsonWebKeySet.RsaJwk rsaJwk = jsonb.fromJson(key, JsonWebKeySet.RsaJwk.class);
        JsonWebKeySet keySet = new JsonWebKeySet();
        JsonWebKey[] keys = {rsaJwk};
        keySet.setKeys(keys);
//        String requestObject = "  {\n" +
//                "   \"iss\": \"s6BhdRkqt3\",\n" +
//                "   \"aud\": \"https://server.example.com\",\n" +
//                "   \"response_type\": \"code id_token\",\n" +
//                "   \"client_id\": \"s6BhdRkqt3\",\n" +
//                "   \"redirect_uri\": \"https://client.example.org/cb\",\n" +
//                "   \"scope\": \"openid\",\n" +
//                "   \"state\": \"af0ifjsldkj\",\n" +
//                "   \"nonce\": \"n-0S6_WzA2Mj\",\n" +
//                "   \"max_age\": 86400,\n" +
//                "   \"claims\":\n" +
//                "    {\n" +
//                "     \"userinfo\":\n" +
//                "      {\n" +
//                "       \"given_name\": {\"essential\": true},\n" +
//                "       \"nickname\": null,\n" +
//                "       \"email\": {\"essential\": true},\n" +
//                "       \"email_verified\": {\"essential\": true},\n" +
//                "       \"picture\": null\n" +
//                "      },\n" +
//                "     \"id_token\":\n" +
//                "      {\n" +
//                "       \"gender\": null,\n" +
//                "       \"birthdate\": {\"essential\": true},\n" +
//                "       \"acr\": {\"values\": [\"urn:mace:incommon:iap:silver\"]}\n" +
//                "      }\n" +
//                "    }\n" +
//                "  }";
        OpenIDConnectProvider.OpenIDConnectAuthorizationRequest request = new OpenIDConnectProvider.OpenIDConnectAuthorizationRequest(Collections.emptyMap());
        request.setResponseType("code id_token");
        request.setClientId("s6BhdRkqt3");
        request.setRedirectUri("https://client.example.org/cb");
        request.setScope("openid");
        request.setState("af0ifjsldkj");
        request.setNonce("n-0S6_WzA2Mj");
        request.setRequest("eyJhbGciOiJSUzI1NiIsImtpZCI6ImsyYmRjIn0.ew0KICJpc3MiOiAiczZCaGRSa3" +
                "F0MyIsDQogImF1ZCI6ICJodHRwczovL3NlcnZlci5leGFtcGxlLmNvbSIsDQogInJl" +
                "c3BvbnNlX3R5cGUiOiAiY29kZSBpZF90b2tlbiIsDQogImNsaWVudF9pZCI6ICJzNk" +
                "JoZFJrcXQzIiwNCiAicmVkaXJlY3RfdXJpIjogImh0dHBzOi8vY2xpZW50LmV4YW1w" +
                "bGUub3JnL2NiIiwNCiAic2NvcGUiOiAib3BlbmlkIiwNCiAic3RhdGUiOiAiYWYwaW" +
                "Zqc2xka2oiLA0KICJub25jZSI6ICJuLTBTNl9XekEyTWoiLA0KICJtYXhfYWdlIjog" +
                "ODY0MDAsDQogImNsYWltcyI6IA0KICB7DQogICAidXNlcmluZm8iOiANCiAgICB7DQ" +
                "ogICAgICJnaXZlbl9uYW1lIjogeyJlc3NlbnRpYWwiOiB0cnVlfSwNCiAgICAgIm5p" +
                "Y2tuYW1lIjogbnVsbCwNCiAgICAgImVtYWlsIjogeyJlc3NlbnRpYWwiOiB0cnVlfS" +
                "wNCiAgICAgImVtYWlsX3ZlcmlmaWVkIjogeyJlc3NlbnRpYWwiOiB0cnVlfSwNCiAg" +
                "ICAgInBpY3R1cmUiOiBudWxsDQogICAgfSwNCiAgICJpZF90b2tlbiI6IA0KICAgIH" +
                "sNCiAgICAgImdlbmRlciI6IG51bGwsDQogICAgICJiaXJ0aGRhdGUiOiB7ImVzc2Vu" +
                "dGlhbCI6IHRydWV9LA0KICAgICAiYWNyIjogeyJ2YWx1ZXMiOiBbInVybjptYWNlOm" +
                "luY29tbW9uOmlhcDpzaWx2ZXIiXX0NCiAgICB9DQogIH0NCn0.nwwnNsk1-Zkbmnvs" +
                "F6zTHm8CHERFMGQPhos-EJcaH4Hh-sMgk8ePrGhw_trPYs8KQxsn6R9Emo_wHwajyF" +
                "KzuMXZFSZ3p6Mb8dkxtVyjoy2GIzvuJT_u7PkY2t8QU9hjBcHs68PkgjDVTrG1uRTx" +
                "0GxFbuPbj96tVuj11pTnmFCUR6IEOXKYr7iGOCRB3btfJhM0_AKQUfqKnRlrRscc8K" +
                "ol-cSLWoYE9l5QqholImzjT_cMnNIznW9E7CDyWXTsO70xnB4SkG6pXfLSjLLlxmPG" +
                "iyon_-Te111V8uE83IlzCYIb_NMXvtTIVc1jpspnTSD7xMbpL-2QgwUsAlMGzw");

        Registration.InformationResponse regInfo = new Registration.InformationResponse();
        regInfo.setClient_id(request.getClientId());
        regInfo.setClient_secret("gX1fBat3bV");
        regInfo.setRedirect_uris(Collections.emptyList());
        regInfo.setJwks(keySet);
        regInfo.setId_token_signed_response_alg(JsonWebAlgorithm.HS256.jwaName());
        Mockito.when(clientMetadataStore.loadClientMetadata(Mockito.anyString())).thenReturn(regInfo);
        serverConfig.setConsentPage("https://iminusminus.github.io/oauth/login");
        serverConfig.setResponse_types_supported(Arrays.asList("code", "token", "id_token"));
        serverConfig.setIssuer(new URL("https://www.cafe.babe"));

        ServerResponse response = subject.authorize(request);
//        Assertions.assertTrue(response instanceof Authorization.RedirectResponse);
//        String location = ((Authorization.RedirectResponse) response).getLocation();
        System.out.println(response);
    }
}
