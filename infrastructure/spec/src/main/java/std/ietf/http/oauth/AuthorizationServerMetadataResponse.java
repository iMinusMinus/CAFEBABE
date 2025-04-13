package std.ietf.http.oauth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc8414">OAuth 2.0 Authorization Server Metadata</a>
 *
 * @date 2025-03-02
 * @author iMinusMinus
 */
@Getter
@Setter
@ToString
public class AuthorizationServerMetadataResponse implements ServerResponse, Serializable {

    protected URL issuer; // NotNull

    protected URI authorization_endpoint; // NotNull

    protected URI token_endpoint; // NotNull，仅支持隐含模式例外

    /**
     * 授权访问令牌接口支持的认证方式，默认为client_secret_basic，还有none、client_secret_post、client_secret_jwt、private_key_jwt等方式
     */
    protected List<String> token_endpoint_auth_methods_supported;

    /**
     * 认证客户端支持的签名算法，需包含RS256，禁止含none
     */
    protected List<String> token_endpoint_auth_signing_alg_values_supported;

    protected URI userinfo_endpoint; // NotNull

    protected URL jwks_uri; // NotNull

    protected URI registration_endpoint;

    protected String[] scopes_supported;

    /**
     * 授权响应类型，如code、token等
     */
    protected List<String> response_types_supported; // NotNull

    /**
     * 支持的响应模式，默认为query、fragment，还有form_post等方式
     */
    protected List<String> response_modes_supported;

    /**
     * 支持的授权模式，默认为authorization_code、implicit，还有password、client_credentials、refresh_token、urn:ietf:params:oauth:grant-type:jwt-bearer、urn:ietf:params:oauth:grant-type:saml2-bearer等方式
     */
    protected List<String> grant_types_supported;

    protected URL service_documentation;

    protected List<Locale> ui_locales_supported;

    protected URL op_policy_uri;

    protected URL op_tos_uri;

    protected URI revocation_endpoint;

    protected List<String> revocation_endpoint_auth_methods_supported;

    protected List<String> revocation_endpoint_auth_signing_alg_values_supported;

    protected URI introspection_endpoint;

    protected List<String> introspection_endpoint_auth_methods_supported;

    protected List<String> introspection_endpoint_auth_signing_alg_values_supported;

    protected List<String> code_challenge_methods_supported;

    protected String signed_metadata;

    /**
     * <a href="https://www.rfc-editor.org/rfc/rfc8628">OAuth 2.0 Device Authorization Grant</a>
     */
    protected URL device_authorization_endpoint;

    /**
     * <a href="https://openid.net/specs/openid-connect-discovery-1_0.html">OpenID Provider Metadata</a>
     */
    protected List<String> acr_values_supported;

    /**
     * OIDC 支持的主体标识，值为pairwise(相同用户给不同客户端不同标识)、public(给所有客户端相同的标识).
     */
    protected List<String> subject_types_supported; // NotNull

    /**
     * OIDC 签名算法(alg)，必须包含RS256，可包含none（用于支持不含id_token的返回）
     */
    protected List<String> id_token_signing_alg_values_supported; // NotNull

    /**
     * OIDC 加密算法(alg)
     */
    protected List<String> id_token_encryption_alg_values_supported;

    /**
     * OIDC 加密算法(enc)
     */
    protected List<String> id_token_encryption_enc_values_supported;

    /**
     * OIDC 签名算法(alg)
     */
    protected List<String> userinfo_signing_alg_values_supported;

    /**
     * OIDC 加密算法(alg)
     */
    protected List<String> userinfo_encryption_alg_values_supported;

    /**
     * OIDC 加密算法(enc)
     */
    protected List<String> userinfo_encryption_enc_values_supported;

    /**
     * OIDC 签名算法(alg)
     */
    protected List<String> request_object_signing_alg_values_supported;

    /**
     * OIDC 加密算法(alg)
     */
    protected List<String> request_object_encryption_alg_values_supported;

    /**
     * OIDC 加密算法(enc)
     */
    protected List<String> request_object_encryption_enc_values_supported;

    /**
     * OIDC 支持的显示方式
     */
    protected List<String> display_values_supported;

    /**
     * OIDC 值有normal、aggregated、distributed
     */
    protected List<String> claim_types_supported;

    /**
     * OIDC 支持的JWT属性
     */
    protected List<String> claims_supported;

    /**
     * OIDC 支持的语言
     */
    protected List<String> claims_locales_supported;

    /**
     * OIDC 是否支持claims参数
     */
    protected boolean claims_parameter_supported;

    /**
     * OIDC 是否支持parameter参数
     */
    protected boolean request_parameter_supported;

    /**
     * OIDC 是否支持request_uri参数
     */
    protected boolean request_uri_parameter_supported = true;

    /**
     * OIDC 是否需预注册request_uri参数才能使用
     */
    protected boolean require_request_uri_registration;

    /**
     * <a href="https://www.rfc-editor.org/rfc/rfc9207">OAuth 2.0 Authorization Server Issuer Identification</a>授权码重定向URL是否包含iss参数
     */
    protected boolean authorization_response_iss_parameter_supported;

    /**
     * <a href="https://datatracker.ietf.org/doc/html/draft-parecki-oauth-client-id-scheme">OAuth 2.0 Client ID Scheme</a>定义了生成client_id的语义化：
     * redirect_uri代表client_id包含客户端重定向URI，openid_federation代表该client_id是一个openid联邦定义的实体id，
     * decentralized-identifier代表私钥签名的去中心化client_id，client_attestation允许客户端使用公钥验证JWT，
     * x509_san_dns代表client_id是域名，x509_san_uri代表client_id是URI。
     */
    protected List<String> client_id_schemes_supported;
}
