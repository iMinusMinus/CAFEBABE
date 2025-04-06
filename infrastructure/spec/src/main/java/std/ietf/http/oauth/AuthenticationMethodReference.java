package std.ietf.http.oauth;

import lombok.Getter;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc8176">Authentication Method Reference Values</a>
 * 即JWT中amr属性可用值。
 *
 * @author iMinusMinus
 * @date 2025-03-08
 */
@Getter
public enum AuthenticationMethodReference {

    FACIAL_RECOGNITION("face"), // 人脸识别
    FINGERPRINT("fpt"), // 指纹
    GEOLOCATION("geo"), // 地理位置
    HARDWARE_SECURED_KEY("hwk"), // 硬件加密密钥，拥有证明（Proof-of-Possession (PoP)）方式
    IRIS_SCAN("iris"), // 虹膜扫描
    KNOWLEDGE_BASED_AUTHENTICATION("kba"),
    MULTIPLE_CHANNEL_AUTHENTICATION("mca"), // 如密码和电话结合
    MULTIPLE_FACTOR_AUTHENTICATION("mfa"), // 多因素认证，如密码和时间令牌
    ONE_TIME_PASSWORD("otp"), // 一次性密码，如短信验证码、邮件验证码
    PERSONAL_IDENTIFICATION_NUMBER("pin"),
    PASSWORD_BASED_AUTHENTICATION("pwd"), // 密码，PoP
    RISK_BASED_AUTHENTICATION("rba"),
    RETINA_SCAN("retina"), // 视网膜扫描
    SMART_CARD("sc"),
    SHORT_MESSAGE_SERVICE("sms"), // 短信
    SOFTWARE_SECURED_KEY("swk"), // 软件加密密钥，PoP
    TELEPHONE_CALL("tel"),
    USER_PRESENCE_TEST("user"),
    VOICEPRINT("vbm"), // 语音识别
    WINDOWS_INTEGRATED_AUTHENTICATION("wia"),
    ;
    private AuthenticationMethodReference(String value) {
        this.value = value;
    }
    private final String value;
}
