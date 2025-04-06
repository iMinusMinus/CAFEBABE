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
import std.ietf.http.oauth.Registration;
import std.ietf.http.oauth.ServerResponse;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbConfig;
import javax.json.bind.spi.JsonbProvider;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
    public void testAuthorizationCodeGrant_step1() {
        Authorization.Request request = new Authorization.Request();
        request.setResponseType("code");
        request.setClientId("s6BhdRkqt3");
        request.setState("xyz");
        request.setRedirectUri("https://client.example.com/cb");

        Registration.InformationResponse regInfo = new Registration.InformationResponse();
        regInfo.setRedirect_uris(Collections.emptyList());
        Mockito.when(clientMetadataStore.loadClientMetadata(request.getClientId())).thenReturn(regInfo);
        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.AUTHORIZATION_CODE_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        Mockito.doNothing().when(cache).put(Mockito.anyString(), Mockito.any());
        ServerResponse response = subject.authorize(request);
        Assertions.assertTrue(response instanceof Authorization.RedirectResponse);
        String location = ((Authorization.RedirectResponse) response).getLocation();
        Assertions.assertTrue(location.contains("code="));
        Assertions.assertTrue(location.contains("state="));

//        response = subject.authorize(request);
//        Assertions.assertEquals("https://client.example.com/cb?error=access_denied&state=xyz", ((Authorization.RedirectResponse) response).getLocation());
    }

    @Test
    public void testAuthorizationCodeGrant_step2() {
        Authorization.TokenRequest request = new Authorization.TokenRequest("authorization_code");
        request.setCode("SplxlOBeZQQYbYS6WxSbIA");
        request.setRedirectUri("https://client.example.com/cb");

        Registration.InformationResponse regInfo = new Registration.InformationResponse();
        regInfo.setRedirect_uris(Arrays.asList(request.getRedirectUri()));
        Mockito.when(clientMetadataStore.loadClientMetadata(Mockito.anyString())).thenReturn(regInfo);
        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.AUTHORIZATION_CODE_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        OpenIDConnectProvider.OAuth2Token token = new OpenIDConnectProvider.OAuth2Token();
        token.setToken(request.getCode());;
        token.setRedirectUri(request.getRedirectUri());
        token.setVirgin(true);
        Mockito.when(cache.get(String.format(OpenIDConnectProvider.AUTHORIZATION_FORMAT, request.getCode()))).thenReturn(token);

        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.ACCESS_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        Mockito.doNothing().when(cache).put(Mockito.anyString(), Mockito.any());

        ServerResponse response = subject.grant("Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW", request);
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
        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.ACCESS_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        Mockito.doNothing().when(cache).put(Mockito.anyString(), Mockito.any());

        ServerResponse response = subject.authorize(request);
        Assertions.assertTrue(response instanceof Authorization.RedirectResponse);
        String location = ((Authorization.RedirectResponse) response).getLocation();
        Assertions.assertTrue(location.contains("access_token="));
        Assertions.assertTrue(location.contains("expires_in="));
        Assertions.assertTrue(location.contains("state="));
        Assertions.assertTrue(location.contains("token_type="));

        serverConfig.setCode_challenge_methods_supported(Arrays.asList(Authorization.CHALLENGE_METHOD_NONE, Authorization.CHALLENGE_METHOD_S256));
        request.setCodeChallengeMethod("S512");
        response = subject.authorize(request);
        Assertions.assertTrue(response instanceof Authorization.RedirectResponse);
        location = ((Authorization.RedirectResponse) response).getLocation();
        Assertions.assertTrue(location.contains("error="));
    }

    @Test
    public void testResourceOwnerPasswordCredentialsGrant() {
        Authorization.TokenRequest request = new Authorization.TokenRequest("password");
        request.setUsername("johndoe");
        request.setPassword("A3ddj3w");

        Registration.InformationResponse regInfo = new Registration.InformationResponse();
        regInfo.setRedirect_uris(Collections.emptyList());
        Mockito.when(clientMetadataStore.loadClientMetadata(Mockito.anyString())).thenReturn(regInfo);
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

        Registration.InformationResponse regInfo = new Registration.InformationResponse();
        regInfo.setRedirect_uris(Collections.emptyList());
        Mockito.when(clientMetadataStore.loadClientMetadata(Mockito.anyString())).thenReturn(regInfo);
        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.REFRESH_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        OpenIDConnectProvider.OAuth2Token token = new OpenIDConnectProvider.OAuth2Token();
        token.setToken(request.getRefreshToken());
        token.setVirgin(true);
        Mockito.when(cache.get(String.format(OpenIDConnectProvider.REFRESH_TOKEN_FORMAT, request.getRefreshToken()))).thenReturn(token);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.ACCESS_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        Mockito.doNothing().when(cache).put(Mockito.anyString(), Mockito.any());

        ServerResponse response = subject.grant("Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW", request);
        Authorization.TokenResponse tokenResponse = (Authorization.TokenResponse) response;
        Assertions.assertNotNull(tokenResponse.getAccess_token());
        Assertions.assertNotNull(tokenResponse.getToken_type());
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
        serverConfig.setResponse_types_supported(Arrays.asList("code", "token", "id_token"));
        serverConfig.setIssuer(new URL("https://www.fafe.babe"));
        Cache<String, OpenIDConnectProvider.OAuth2Token> cache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.AUTHORIZATION_CODE_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        Mockito.when(cacheManager.getCache(OpenIDConnectProvider.ACCESS_TOKEN_CACHE_NAME, String.class, OpenIDConnectProvider.OAuth2Token.class)).thenReturn(cache);
        Mockito.doNothing().when(cache).put(Mockito.anyString(), Mockito.any());
        Mockito.when(userService.loadUser(Mockito.anyString())).thenReturn(new OpenIDConnectProvider.UserInfo("<any>"));

        ServerResponse response = subject.authorize(request);
        Assertions.assertTrue(response instanceof Authorization.RedirectResponse);
        String location = ((Authorization.RedirectResponse) response).getLocation();
        Assertions.assertTrue(location.contains("code="));
        Assertions.assertTrue(location.contains("id_token="));
    }
}
