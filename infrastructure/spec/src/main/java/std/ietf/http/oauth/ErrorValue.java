package std.ietf.http.oauth;

import lombok.Getter;

public enum ErrorValue {

    // <a href="https://www.rfc-editor.org/rfc/rfc6749">Authorization Framework</a>
    invalid_request(400), // 参数缺失、不支持的参数名/值、参数重复、访问令牌出现在多个不同的方式（如请求头、查询参数、请求体）、参数格式错误
    invalid_client(401), // 客户端认证失败（客户端不存在、认证信息缺失、客户端认证方式不支持），如果客户端使用Authentication请求头，服务端需回复WWW-Authenticate响应头
    invalid_grant(400), // 认证授权（授权码或资源所有者凭证）或刷新令牌无效、过期或被撤销
    unauthorized_client(400), // 未认证
    unsupported_grant_type(400), // 授权服务器不支持的授予方式
    invalid_scope(400), // 请求的权限无效、未知、格式错误，或超出资源所有者授予范围
    access_denied(302), // 资源所有者或授权服务器拒绝该请求
    unsupported_response_type(400), // 授权服务器不支持通过该方式获取授权码
    server_error(500), // 授权服务器处于非预期异常而无法处该理请求
    temporarily_unavailable(503), // 授权服务器当前因突发超负荷运转或维护而无法处理该请求
    // <a href="https://www.rfc-editor.org/rfc/rfc6750">Authorization Framework: Bearer Token</a>
    invalid_token(401), // 访问令牌过期、被撤销、格式错误或者因某种原因无效
    insufficient_scope(403), // 请求的资源需访问者有更高的权限，服务端可返回scope信息告知访问者需要的权限
    // <a href="https://www.rfc-editor.org/rfc/rfc7009">Token Revocation</a>
    unsupported_token_type(400), // 撤销的令牌类型不受支持
    // <a href="https://www.rfc-editor.org/rfc/rfc7591">Registration</a>
    invalid_redirect_uri(400),
    invalid_client_metadata(400),
    invalid_software_statement(400),
    unapproved_software_statement(400),
    // <a href="https://www.rfc-editor.org/rfc/rfc8628">Device Authorization Grant</a>
    authorization_pending(400), // 终端用户未完成交互（未输入正确用户码）
    slow_down(400), // 用户未完成交互，设备请求频率需降低
    expired_token(400), // 设备码过期
    // <a href="https://openid.net/specs/openid-connect-core-1_0.htm">OIDC</a>
    interaction_required(302), // prompt=none，而授权服务器要求终端用户有交互
    login_required(302), //  // prompt=none，而授权服务器要求用户登录
    account_selection_required(302), // 当终端用户在授权服务器有多个账号时，需终端用户选择一个账号
    consent_required(302), // prompt=none，而授权服务器要求用户同意
    invalid_request_uri(302), // request_uri包含无效数据，或授权服务器返回错误
    invalid_request_object(302), // request参数包含无效对象
    request_not_supported(302), // OP不支持request参数
    request_uri_not_supported(302), // OP不支持request_uri参数
    registration_not_supported(302), // OP不支持registration参数
    ;

    @Getter private final int code;

    private ErrorValue(int code) {
        this.code = code;
    }

    public static ErrorValue getInstance(String error) {
        for (ErrorValue instance : ErrorValue.values()) {
            if (instance.name().equals(error)) {
                return instance;
            }
        }
        return null;
    }
}
