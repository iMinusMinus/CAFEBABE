package std.ietf.http.jose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.function.Function;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc7515"> JSON Web Signature (JWS)</a>用于签名和验证token
 *
 * @author iMinusMinus
 * @date 2025-03-20
 */
@ToString
@NoArgsConstructor
public class JsonWebSignature {

    /**
     * 内容，使用base64url编码
     */
    @Getter @Setter private String payload;

    /**
     * 非Flattened JSON模式出现
     */
    @Getter @Setter private Signature[] signatures;
    /**
     * Flattened JSON模式出现。
     * 作为签名一部分，通常包含alg，如果要包含其他字段，则需要使用crit来列出。
     * 使用base64url编码
     */
    private String protectedHeader;
    /**
     * Flattened JSON模式出现，含有其他不参与签名字段
     */
    @Getter @Setter private Header header;
    /**
     * Flattened JSON模式出现，签名，使用base64url编码
     */
    @Getter @Setter private String signature;

    public String getProtected() {
        return protectedHeader;
    }

    public void setProtected(String protectedHeader) {
        this.protectedHeader = protectedHeader;
    }

    public <T extends Header> JsonWebSignature (T headerParams, Object body, Function<Object, byte[]> serializer, JsonWebKey<?>... keys) throws GeneralSecurityException {
        Header protectedHeader = new Header();
        protectedHeader.setAlg(headerParams.getAlg());
        headerParams.setAlg(null);
        if (headerParams.getCrit() != null && headerParams.getCrit().length > 0) {
            throw new UnsupportedOperationException("not support crit with values");
        }
        byte[] header = serializer.apply(protectedHeader);
        byte[] payload = serializer.apply(body);
        JsonWebAlgorithm alg = JsonWebAlgorithm.getInstance(protectedHeader.getAlg());
        if (alg == null) {
            throw new NoSuchAlgorithmException(protectedHeader.getAlg());
        }
        JsonWebSignature sig = new JsonWebSignature();
        sig.setPayload(JsonWebToken.ENCODER.encodeToString(payload));
        String joseHeader = JsonWebToken.ENCODER.encodeToString(header);
        if (keys.length <= 1) {
            sig.setProtected(joseHeader);
            sig.setHeader(headerParams);
            if (keys.length == 1) {
                Key key = keys[0].isAsymmetric() ? keys[0].asPrivateKey() : keys[0].exportKey();
                byte[] signature = alg.sign(String.join(JsonWebToken.PERIOD, sig.getProtected(), sig.getPayload()).getBytes(StandardCharsets.US_ASCII), key);
                sig.setSignature(JsonWebToken.ENCODER.encodeToString(signature));
            }
            return;

        }
        Signature[] signatures = new Signature[keys.length];
        for (int i = 0; i < keys.length; i++) {
            Key key = keys[i].isAsymmetric() ? keys[i].asPrivateKey() : keys[i].exportKey();
            byte[] signature = alg.sign(String.join(JsonWebToken.PERIOD, sig.getProtected(), sig.getPayload()).getBytes(StandardCharsets.US_ASCII), key);
            signatures[i] = new Signature();
            signatures[i].setSignature(JsonWebToken.ENCODER.encodeToString(signature));
            signatures[i].setProtected(joseHeader);
            Header jwsHeader = new Header();
            jwsHeader.setKid(keys[i].getKid());
            signatures[i].setHeader(jwsHeader);
        }
    }

    /**
     * 将JWS进行验签，返回解码后内容
     * @param deserializer JSONB，用于反序列化
     * @param keys 密钥
     * @return 解码后内容
     * @throws GeneralSecurityException 验签失败
     * @throws IllegalArgumentException JWS不正确
     */
    public <T extends Header> byte[] toJWT(Function<byte[], T> deserializer, JsonWebKey<?>... keys) throws GeneralSecurityException {
        byte[] body = Base64.getUrlDecoder().decode(payload);
        Signature[] signatures = this.signatures;
        if (protectedHeader != null && this.signatures == null) {
            signatures = new Signature[1];
            signatures[0] = new Signature();
            signatures[0].setProtected(protectedHeader);
            signatures[0].setSignature(signature); // may be null
            signatures[0].setHeader(header); // may be null
        }
        for (Signature sig : signatures) {
            if (!verify(sig.getProtected(), payload, sig.getSignature(), sig.getHeader(), deserializer, keys)) {
                throw new GeneralSecurityException();
            }
        }
        return body;
    }

    static <T extends Header> boolean verify(String protectedHeader, String payload, String signature, Header header,
                                             Function<byte[], T> deserializer, JsonWebKey<?>... keys) throws GeneralSecurityException {
        Header joseHeader = deserializer.apply(Base64.getUrlDecoder().decode(protectedHeader));
        JsonWebAlgorithm alg = JsonWebAlgorithm.getInstance(joseHeader.getAlg());
        if (alg == null) {
            throw new IllegalArgumentException(joseHeader.getAlg());
        } else if (alg == JsonWebAlgorithm.NONE) {
            assert signature == null;
            return true;
        }
        byte[] data = String.join(JsonWebToken.PERIOD, protectedHeader, payload).getBytes(StandardCharsets.US_ASCII);
        JsonWebKey<?> key = JsonWebToken.matchJWK(header, keys);
        Key jcaKey = key.isAsymmetric() ? key.asPublicKey() : key.exportKey();
        return alg.verify(Base64.getUrlDecoder().decode(signature), data, jcaKey);
    }

    @ToString
    protected static class Signature {
        private String protectedHeader; // base64url
        @Getter @Setter private Header header; // unencoded JSON object
        @Getter @Setter private String signature; // base64url

        public String getProtected() {
            return protectedHeader;
        }

        public void setProtected(String protectedHeader) {
            this.protectedHeader = protectedHeader;
        }
    }
}
