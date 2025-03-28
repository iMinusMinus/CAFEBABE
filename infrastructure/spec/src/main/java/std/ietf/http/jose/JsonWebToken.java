package std.ietf.http.jose;

import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Function;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc7519">JSON Web Token</a>定义了可选声明，JWT信息是公开的，不能包含敏感数据
 * @author iMinusMinus
 * @date 2025-03-20
 */
public interface JsonWebToken {

    String PERIOD = ".";

    Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    /**
     * 签发主体
     */
    String getIss();
    void setIss(String issuer);

    /**
     * 持有主体
     */
    String getSub();
    void setSub(String subject);

    /**
     * 目标受众
     */
    String getAud();
    void setAud(String audience);

    /**
     * 失效时间（单位：秒）
     */
    Integer getExp();
    void setExp(Integer expire);

    /**
     * 最早可用时间（单位：秒）
     */
    Integer getNbf();
    void setNbf(Integer notBefore);

    /**
     * 签发时间（单位：秒）
     */
    Integer getIat();
    void setIat(Integer issueAt);

    /**
     * JWT标识
     */
    String getJti();
    void setJti(String jwtId);

    /**
     * 生成JWS
     * @param headerParams JOSE Header
     * @param body payload
     * @param jsonb json serializer
     * @param keys MAC/signature keys
     * @return Header.Payload.Signature
     */
    static <T extends Header> String toCompactJWS(T headerParams, Object body, Function<Object, byte[]> jsonb, JsonWebKey<?>... keys) throws GeneralSecurityException {
        JsonWebSignature jws = new JsonWebSignature(headerParams, body, jsonb, keys);
        if (jws.getSignatures() != null) {
            throw new IllegalArgumentException("JWS Compact Serialization format don't support multiply signature");
        }
        return jws.getSignature() == null ?
                String.join(PERIOD, jws.getProtected(), jws.getPayload(), "") :
                String.join(PERIOD, jws.getProtected(), jws.getPayload(), jws.getSignature());
    }

    /**
     * 验证JWS
     * @param jws JWS
     * @param deserializer JSONB，用于将字节流转为java对象
     * @param keys 密钥
     * @return 验证通过明文
     * @throws GeneralSecurityException
     */
    static <T extends Header> byte[] fromCompactJWS(String jws, Function<byte[], T> deserializer, JsonWebKey<?>... keys) throws GeneralSecurityException {
        int payloadOffset = jws.indexOf(PERIOD);
        int signatureOffset = jws.lastIndexOf(PERIOD);
        if (payloadOffset < 0 || signatureOffset < 0 || payloadOffset >= signatureOffset) {
            throw new IllegalArgumentException("JWS Compact Serialization format: ProtectedHeader.Payload.Signature");
        }
        String signature = signatureOffset == jws.length() - 1 ? null : jws.substring(signatureOffset + 1);
        JsonWebSignature jwsJson = new JsonWebSignature();
        jwsJson.setProtected(jws.substring(0, payloadOffset));
        jwsJson.setPayload(jws.substring(payloadOffset + 1, signatureOffset));
        jwsJson.setSignature(signature);
        return jwsJson.toJWT(deserializer, keys);
    }

    /**
     * @param headerParams 标识加密算法和类型的受保护头部
     * @param body JWT载体
     * @param jsonb 对象序列化器
     * @param keys 密钥
     * @return JWE
     */
    static <T extends Header> String toCompactJWE(T headerParams, Object body, Function<Object, byte[]> jsonb, JsonWebKey<?>... keys) throws GeneralSecurityException {
        JsonWebEncryption jwe = new JsonWebEncryption(headerParams, body, jsonb, keys);
        if (jwe.getRecipients() != null) {
            throw new IllegalArgumentException("JWE Compact Serialization format don't support multiply recipients");
        }
        return String.join(PERIOD, jwe.getProtected(), jwe.getEncrypted_key(), jwe.getIv(), jwe.getCiphertext(), jwe.getTag());
    }

    /**
     * 解密JWE
     * @param jwe JWE
     * @param jsonb 反序列化器
     * @param keys 密钥
     * @return 解密（并解压）后的明文
     * @throws GeneralSecurityException
     */
    static <T extends Header> byte[] fromCompactJWE(String jwe, Function<byte[], T> jsonb, JsonWebKey<?>... keys) throws GeneralSecurityException {
        int cekOffset = jwe.indexOf(PERIOD);
        int ivOffset = jwe.indexOf(PERIOD, cekOffset + 1);
        int payloadOffset = jwe.indexOf(PERIOD, ivOffset + 1);
        int authTagOffset = jwe.lastIndexOf(PERIOD);
        if (ivOffset < 0 || payloadOffset < 0 || authTagOffset < 0 || payloadOffset >= authTagOffset) {
            throw new IllegalArgumentException("JWE Compact Serialization format: ProtectedHeader.EncryptedKey.InitializationVector.Ciphertext.AuthenticationTag");
        }
        JsonWebEncryption jweJson = new JsonWebEncryption();
        jweJson.setProtected(jwe.substring(0, cekOffset));
        jweJson.setEncrypted_key(jwe.substring(cekOffset + 1, ivOffset));
        jweJson.setIv(jwe.substring(ivOffset + 1, payloadOffset));
        jweJson.setCiphertext(jwe.substring(payloadOffset + 1, authTagOffset));
        jweJson.setTag(jwe.substring(authTagOffset + 1));
        return jweJson.toJWT(jsonb, keys);
    }

    static JsonWebKey<?> matchJWK(Header joseHeader, JsonWebKey<?>... keys) {
        if (joseHeader != null && joseHeader.getKid() != null) {
            for (JsonWebKey<?> k : keys) {
                if (joseHeader.getKid().equals(k.getKid())) {
                    return k;
                }
            }
        } else if (keys.length == 1) {
            return keys[0];
        }
        throw new IllegalArgumentException("missing key or no match kid: " + Optional.ofNullable(joseHeader).map(Header::getKid).orElse(null));
    }
}
