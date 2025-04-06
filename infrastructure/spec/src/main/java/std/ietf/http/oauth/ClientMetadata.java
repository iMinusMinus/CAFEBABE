package std.ietf.http.oauth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import std.ietf.http.jose.JsonWebKeySet;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc7591">Client Metadata</a>
 *
 * @author iMinusMinus
 * @date 2025-03-08
 */
@Getter
@Setter
@ToString
public class ClientMetadata implements Serializable {

    public static final transient String BASIC_AUTH_METHOD = "client_secret_basic";

    public static final String WEB_APPLICATION_TYPE = "web";

    /**
     * 重定向流程使用的URI，比如授权码模式、隐含模式。web应用协议应为https、native应用协议为自定义
     */
    protected List<String> redirect_uris; // Not Null

    /**
     * 获取访问令牌时，认证客户端的方式，值为client_secret_post、client_secret_basic、client_secret_jwt、private_key_jwt和none，默认为client_secret_basic
     */
    protected String token_endpoint_auth_method = BASIC_AUTH_METHOD;

    /**
     * 客户端调用获取访问令牌接口支持的授权方式
     */
    protected List<String> grant_types;

    /**
     * 支持的授权类型，缺失时默认为code
     */
    protected List<String> response_types = Collections.singletonList(Authorization.PARAM_CODE);

    /**
     * 展示给终端用户的名称，缺失时使用client_id
     */
    protected String client_name; // client_name#zh-CN

    /**
     * 应用主页
     */
    protected URL client_uri; // zh-TW

    /**
     * 应用logo地址
     */
    protected URL logo_uri; // zh-HK

    /**
     * 获取访问令牌时客户端可申请的权限，多个权限用空格分隔
     */
    protected String scope;

    /**
     * 负责人电子邮箱
     */
    protected List<String> contacts;

    /**
     * 为终端用户说明数据用途的政策页面
     */
    protected URL policy_uri; // policy_uri#en-GB

    /**
     * 服务条款页面
     */
    protected URL tos_uri; // tos_uri#en-US

    /**
     * JWK文档页面
     */
    protected URL jwks_uri;

    /**
     * JWK文档，不能和jwks_uri同时存在，仅无法使用jwks_uri使用，如native应用
     */
    protected JsonWebKeySet jwks;

    /**
     * 客户端开发者或软件发布者为客户端分配的唯一标识，通常是UUID
     */
    protected String software_id;

    /**
     * 客户端版本标识，当客户端升级时需变动此信息
     */
    protected String software_version;

    /**
     * JWT形式的客户端元信息，授权服务器不支持时可忽略此参数
     */
    protected String software_statement;

    /**
     * <a href="https://openid.net/specs/openid-connect-registration-1_0.html">Client Metadata</a>
     * 应用类型，值有native、web，缺失时为web
     */
    protected String application_type = WEB_APPLICATION_TYPE;

    /**
     * 包含多个redirect_uri的文档链接
     */
    protected URL sector_identifier_uri;

    /**
     * 要求OP返回的主体类型，值为pairwise和public
     */
    protected String subject_type;

    /**
     * 要求OP签名ID Token使用的算法，缺失时默认用RS256
     */
    protected String id_token_signed_response_alg;

    /**
     * 要求OP加密ID Token使用的算法，缺失时不加密
     */
    protected String id_token_encrypted_response_alg;

    /**
     * 要求OP加密ID Token使用的算法，如果id_token_encrypted_response_alg存在，默认为A128CBC-HS256；如果包含id_token_encrypted_response_enc，则也需包含id_token_encrypted_response_alg
     */
    protected String id_token_encrypted_response_enc;

    /**
     * 要求OP签名UserInfo使用的算法，缺失时默认用application/json序列化
     */
    protected String userinfo_signed_response_alg;

    /**
     * 要求OP加密ID Token使用的算法，缺失时不加密，存在时先签名后加密
     */
    protected String userinfo_encrypted_response_alg;

    /**
     * 要求OP加密UserInfo使用的算法，如果userinfo_encrypted_response_alg存在，默认为A128CBC-HS256；如果包含userinfo_encrypted_response_enc，则也需包含userinfo_encrypted_response_alg
     */
    protected String userinfo_encrypted_response_enc;

    /**
     * 请求OP前为request/request_uri使用的签名算法
     */
    protected String request_object_signing_alg;

    /**
     * 请求OP前为request/request_uri使用的加密算法
     */
    protected String request_object_encryption_alg;

    /**
     * 加密request/request_uri使用的算法，如果request_object_encryption_alg存在，默认为A128CBC-HS256；如果包含request_object_encryption_enc，则也需包含request_object_encryption_alg
     */
    protected String request_object_encryption_enc;

    /**
     * 当认证方式为private_key_jwt或client_secret_jwt时，JWT的签名方式
     */
    protected String token_endpoint_auth_signing_alg;

    /**
     * 默认最大认证生命周期，可被max_age参数覆盖，缺失时不限制
     */
    protected Integer default_max_age;

    /**
     * ID Token的auth_time是否必须，默认false
     */
    protected boolean require_auth_time;

    /**
     * acr_values和acr参数可覆盖此协商参数
     */
    protected List<String> default_acr_values;

    /**
     * 客户端初始化的登陆页面，该链接必须支持GET、POST请求
     */
    protected URL initiate_login_uri;

    /**
     * 客户端预先注册需要请求的URL
     */
    protected List<URL> request_uris;

    /**
     * 客户端用于管理注册信息的地址（支持管理功能时不为null）
     */
    protected URI registration_client_uri;

    /**
     * 客户端用于管理注册信息的访问令牌（支持管理功能时不为null）
     */
    protected String registration_access_token;
}
