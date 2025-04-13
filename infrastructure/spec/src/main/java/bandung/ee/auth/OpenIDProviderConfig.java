package bandung.ee.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import std.ietf.http.jose.JsonWebKeySet;
import std.ietf.http.oauth.Authorization;
import std.ietf.http.oauth.AuthorizationServerMetadataResponse;

import java.net.URL;
import java.time.Duration;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class OpenIDProviderConfig extends AuthorizationServerMetadataResponse {

    private String consentPage;

    private Integer clientSecretTtl; // 0, never expires

    private Integer clientSecretResetNotBefore;

    private Integer registrationTokenTtl;

    private boolean forcePKCE;

    private Duration authorizationCodeTtl = Duration.ofSeconds(10 * 60); // 10m

    private URL deviceVerificationUri;

    private int deviceCodeTtl = 30 * 60; // 30m

    private String deviceAuthorizationCompleteUriFormat;

    private String tokenType = Authorization.DEFAULT_TOKEN_TYPE;

    private int accessTokenTtl = 60 * 60; // 60m

    private Integer refreshTokenTtl;

    private JsonWebKeySet keys;

    private int maxFailureTimes = 3;

    private List<String> audiences;
}
