package std.ietf.http.oauth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.URI;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc7591">OAuth 2.0 Dynamic Client Registration Protocol</a>流程如下：
 * <pre>
 +--------(A)- Initial Access Token (OPTIONAL)
 |
 |   +----(B)- Software Statement (OPTIONAL)
 |   |
 v   v
 +-----------+                                      +---------------+
 |           |--(C)- Client Registration Request -->|    Client     |
 | Client or |                                      | Registration  |
 | Developer |<-(D)- Client Information Response ---|   Endpoint    |
 |           |        or Client Error Response      +---------------+
 +-----------+

 * </pre>
 * 此规范定义客户端向授权服务器动态注册（POST方式，请求类型为"application/json"，正常响应状态码为201，错误响应状态码为400，类型为"application/json"）。
 * <br>
 * <a href="https://www.rfc-editor.org/rfc/rfc7592">OAuth 2.0 Dynamic Client Registration Management Protocol</a>
 * 用于客户端在生命周期内修改注册信息，扩展后流程如下：
 * <pre>
 +--------(A)- Initial Access Token (OPTIONAL)
 |
 |   +----(B)- Software Statement (OPTIONAL)
 |   |
 v   v
 +-----------+                                      +---------------+
 |           |--(C)- Client Registration Request -->|    Client     |
 |           |                                      | Registration  |
 |           |<-(D)- Client Information Response ---|   Endpoint    |
 |           |                                      +---------------+
 |           |
 |           |                                      +---------------+
 | Client or |--(E)- Read or Update Request ------->|               |
 | Developer |                                      |               |
 |           |<-(F)- Client Information Response ---|    Client     |
 |           |                                      | Configuration |
 |           |                                      |   Endpoint    |
 |           |                                      |               |
 |           |--(G)- Delete Request --------------->|               |
 |           |                                      |               |
 |           |<-(H)- Delete Confirmation -----------|               |
 +-----------+                                      +---------------+
 * </pre>
 * 管理地址遵循RESTful风格（读取为GET，更新为PUT，删除为DELETE，媒体类型为"application/json"），
 * 路径格式通常为"/register/{client_id}"或"/register?client_id="。
 * 成功获取/更新配置时返回200，信息同注册；
 * 成功取消注册时返回204，响应体为空；
 * 配置访问令牌无效时，返回401，响应头"WWW-Authenticate"应包含error参数表示原因，及可选的error_description、error_uri；
 * client_id不存在时返回401，同时撤销该请求的registration access token；
 * 权限不足或不被允许时，返回403；
 * 授权服务器不支持删除操作时返回405.
 *
 * @date 2025-03-02
 * @author iMinusMinus
 */
public interface Registration {

    String ENDPOINT = "/register";

    /**
     * 动态注册客户端
     * @param credentials 可选的初始化凭证
     * @param request 注册请求
     * @return 客户端元信息
     */
    ServerResponse registering(String credentials, ClientMetadata request);

    /**
     * 获取客户端注册信息
     * @param credentials 授权服务器返回的配置访问令牌(如Bearer令牌)
     * @param clientId 授权服务器颁发的客户端标识
     * @return 客户端元信息（可能返回新的client_secret和registration_access_token，此时客户端需立即丢弃旧值）
     */
    ServerResponse read(String credentials, String clientId);

    /**
     * 更新注册信息
     * @param credentials 授权服务器返回的配置访问令牌(如Bearer令牌)
     * @param request 新信息(client_id、client_secret需为授权服务器在注册时返回的信息，其他信息如果为null，则授权服务器将对应信息删除）,请求体的client_id必须和路径/查询参数的client_id值相同
     * @return 客户端元信息（可能返回新的client_secret和registration_access_token，此时客户端需立即丢弃旧值）
     */
    ServerResponse update(String credentials, UpdateRequest request);

    /**
     * 取消注册，授权服务器将client_id对应的信息失效（如访问令牌、刷新令牌、授权许可）
     * @param credentials 授权服务器返回的配置访问令牌(如Bearer令牌)
     * @param clientId 授权服务器颁发的客户端标识
     * @return 错误信息（如果有）
     */
    ErrorResponse deprovision(String credentials, String clientId);

    @Getter
    @Setter
    @ToString
    class UpdateRequest extends ClientMetadata {

        protected String client_id;

        protected String client_secret;

    }

    @Getter
    @Setter
    @ToString
    class InformationResponse extends ClientMetadata implements ServerResponse {

        private String opId; // OP标识

        /**
         * 授权服务器颁发给客户端的标识（可颁发一个client_id给多个客户端）
         */
        protected String client_id;

        /**
         * 如果颁发此信息，不同客户端的值不相同（即便client_id相同）
         */
        protected String client_secret;

        /**
         * 颁发时间距unix时间（单位：秒）
         */
        protected Long client_id_issued_at;

        /**
         * 失效时间距unix时间（单位：秒），当client_secret不为null时，此字段需赋值。0代表不失效。
         */
        protected Long client_secret_expires_at;

    }
}
