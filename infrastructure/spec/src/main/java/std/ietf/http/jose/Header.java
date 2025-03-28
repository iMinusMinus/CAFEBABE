package std.ietf.http.jose;

import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import java.net.URL;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Header {

    static final String PARAM_ALG = "alg";
    static final String PARAM_ENC = "enc";
    static final String PARAM_ZIP = "zip";
    static final String PARAM_JKU = "jku";
    static final String PARAM_JWK = "jwk";
    private static final String PARAM_KID = "kid";
    static final String PARAM_X5U = "x5u";
    private static final String PARAM_X5C = "x5c";
    private static final String PARAM_X5T = "x5t";
    private static final String PARAM_X5T_S256 = "x5t#S256";
    private static final String PARAM_TYP = "type";
    private static final String PARAM_CTY = "cty";
    private static final String PARAM_CRIT = "crit";
    static final String PARAM_EPK = "epk";
    static final String PARAM_APU = "apu";
    static final String PARAM_APV = "apv";
    static final String PARAM_IV = "iv";
    static final String PARAM_TAG = "tag";
    static final String PARAM_P2S = "p2s";
    static final String PARAM_P2C = "p2c";

    private final transient Map<String, Object> mix;

    public Header() {
        mix = new HashMap<>();
    }

    /**
     * 在JWS为签名/MAC算法，在JWE为加密算法或决定CEK的算法
     * @see std.ietf.http.jose.JsonWebAlgorithm
     */
    public String getAlg() {
        return (String) mix.get(PARAM_ALG);
    }
    public void setAlg(String algorithm) {
        mix.put(PARAM_ALG, algorithm);
    }

    /**
     * JWE用于加密payload的算法
     * @see std.ietf.http.jose.JsonWebAlgorithm
     */
    public String getEnc() {
        return (String) mix.get(PARAM_ENC);
    }
    public void setEnc(String encryptAlgorithm) {
        mix.put(PARAM_ENC, encryptAlgorithm);
    }

    /**
     * （可选），JWE中明文加密前使用的压缩算法，如DEF代表DEFLATE
     */
    public String getZip() {
        return (String) mix.get(PARAM_ZIP);
    }
    public void setZip(String zip) {
        mix.put(PARAM_ZIP, zip);
    }

    /**
     * （可选），JWK URL，在JWS/JWE中该链接内容为JSON格式的一些列公钥
     * @see std.ietf.http.jose.JsonWebKeySet
     */
    public URL getJku() {
        return (URL) mix.get(PARAM_JKU);
    }
    public void setJku(URL jsonWebKeyUrl) {
        mix.put(PARAM_JKU, jsonWebKeyUrl);
    }

    /**
     * （可选），在JWS为签名用的公钥，在JWE为加密用的公钥
     */
    public JsonWebKey<?> getJwk() {
        return (JsonWebKey<?>) mix.get(PARAM_JWK);
    }
    public void setJwk(JsonWebKey<?> jsonWebKey) {
        mix.put(PARAM_JWK, jsonWebKey);
    }

    /**
     * （可选），密钥id，如密钥轮换时，从多个密钥中选择一个。在JWE中还可以用于选择解密用的私钥
     */
    public String getKid() {
        return (String) mix.get(PARAM_KID);
    }
    public void setKid(String keyId) {
        mix.put(PARAM_KID, keyId);
    }

    /**
     * （可选），指向包含前述公钥的X.509 证书链的链接
     */
    public URL getX5u() {
        return (URL) mix.get(PARAM_X5U);
    }
    public void setX5u(URL x5u) {
        mix.put(PARAM_X5U, x5u);
    }

    /**
     * （可选），包含前述公钥，base64url编码的DER格式X.509 证书或证书链
     */
    public String[] getX5c() {
        return (String[]) mix.get(PARAM_X5C);
    }
    public void setX5c(String[] x5c) {
        mix.put(PARAM_X5C, x5c);
    }

    /**
     * （可选），包含前述公钥的X.509 证书SHA-1 指纹
     */
    public String getX5t() {
        return (String) mix.get(PARAM_X5T);
    }
    public void setX5t(String x5t) {
        mix.put(PARAM_X5T, x5t);
    }

    /**
     * （可选），包含前述公钥的X.509 证书SHA-256 指纹
     */
    public String getX5tS256() {
        return (String) mix.get(PARAM_X5T_S256);
    }
    public void setX5tS256(String x5ts256) {
        mix.put(PARAM_X5T_S256, x5ts256);
    }

    /**
     * （可选），媒体类型，如jwt、jose。
     * 在不含有参数信息时建议省略"application/"前缀，如"application/jose"可简写为"jose"，而"application/jose;charset=utf-8"不能简写。
     * 说明：JOSE表示内容形式为"Header.Payload.Signature"，JOSE+JSON表示内容形式为JSON
     */
    public String getTyp() {
        return (String) mix.get(PARAM_TYP);
    }
    public void setTyp(String type) {
        mix.put(PARAM_TYP, type);
    }

    /**
     * （可选），payload媒体类型，在不含有参数信息时建议省略"application/"前缀。JWS中出现该参数可忽略。
     */
    public String getCty() {
        return (String) mix.get(PARAM_CTY);
    }
    public void setCty(String contentType) {
        mix.put(PARAM_CTY, contentType);
    }

    /**
     * （可选），JOSE包含的扩展字段，接收者无法处理其中任意字段时视为无效JWS/JWE
     */
    public String[] getCrit() { // JWS/JWE
        return (String[]) mix.get(PARAM_CRIT);
    }
    public void setCrit(String[] crit) {
        mix.put(PARAM_CRIT, crit);
    }

    /**
     * 临时公钥，ECDH算法使用
     */
    public JsonWebKeySet.EllipticCurveJwk getEpk() { // JWE ECDH-ES
        return (JsonWebKeySet.EllipticCurveJwk) mix.get(PARAM_EPK);
    }
    public void setEpk(JsonWebKeySet.EllipticCurveJwk ephemeralPublicKey) {
        if (!ephemeralPublicKey.isAsymmetric()) {
            throw new IllegalArgumentException("require public key");
        }
        mix.put(PARAM_EPK, ephemeralPublicKey);
    }

    /**
     * Agreement PartyUInfo
     */
    public String getApu() { // JWE ECDH-ES
        return (String) mix.get(PARAM_APU);
    }
    public void setApu(String agreementPartyUInfo) {
        mix.put(PARAM_APU, agreementPartyUInfo);
    }

    /**
     * Agreement PartyVInfo
     */
    public String getApv() { // JWE ECDH-ES
        return (String) mix.get(PARAM_APV);
    }
    public void setApv(String agreementPartyVInfo) {
        mix.put(PARAM_APV, agreementPartyVInfo);
    }

    /**
     * Initialization Vector
     */
    public String getIv() { // JWE AES GCM
        return (String) mix.get(PARAM_IV);
    }
    public void setIv(String initializationVector) {
        mix.put(PARAM_IV, initializationVector);
    }

    /**
     * Authentication Tag
     */
    public String getTag() { // JWE AES GCM
        return (String) mix.get(PARAM_TAG);
    }
    public void setTag(String authenticationTag) {
        mix.put(PARAM_TAG, authenticationTag);
    }

    /**
     * PBES2 Salt Input
     */
    public String getP2s() { // JWE PBES2
        return (String) mix.get(PARAM_P2S);
    }
    public void setP2s(String salt) {
        mix.put(PARAM_P2S, salt);
    }

    /**
     * PBES2 Count
     */
    public Integer getP2c() { // JWE PBES2
        return (Integer) mix.get(PARAM_P2C);
    }
    public void setP2c(int iterationCount) {
        mix.put(PARAM_P2C, iterationCount);
    }

    Object get(String key) {
        return mix.get(key);
    }

    Object remove(String key) {
        return mix.remove(key);
    }

    Object put(String key, Object value) {
        return mix.put(key, value);
    }

    @Override
    public String toString() {
        return mix.toString();
    }

    AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return Optional.ofNullable(getEnc())
                .map(JsonWebAlgorithm::getJweAlgorithm)
                .map(jwa -> {
                    if (jwa instanceof JsonWebAlgorithm.EcFamily) {
                        return null;
                    } else if (getIv() != null) {
                        if (jwa.getAlgorithm().equals(JsonWebAlgorithm.A128GCM.getAlgorithm())) {
                            return new GCMParameterSpec(((JsonWebAlgorithm.AesFamily) jwa).getKeySize() / 8, Base64.getUrlDecoder().decode(getIv()));
                        } else if (jwa.getAlgorithm().equals(JsonWebAlgorithm.A128CBC_HS256.getAlgorithm())) {
                            return new IvParameterSpec(Base64.getUrlDecoder().decode(getIv()));
                        }
                    } else if (jwa instanceof JsonWebAlgorithm.Pbkdf2Family && getP2s() != null && getP2c() != null) {
                        return new PBEParameterSpec(Base64.getUrlDecoder().decode(getP2s()), getP2c());
                    }
                    return null;
                })
                .orElse(null);
    }

    void setAlgorithmParameterSpec(AlgorithmParameterSpec parameterSpec) {
        if (parameterSpec instanceof GCMParameterSpec) {
            setIv(JsonWebToken.ENCODER.encodeToString(((GCMParameterSpec) parameterSpec).getIV()));
        } else if (parameterSpec instanceof IvParameterSpec) {
            setIv(JsonWebToken.ENCODER.encodeToString(((IvParameterSpec) parameterSpec).getIV()));
        } else if (parameterSpec instanceof PBEParameterSpec) {
            PBEParameterSpec parameter = (PBEParameterSpec) parameterSpec;
            setP2s(JsonWebToken.ENCODER.encodeToString(parameter.getSalt()));
            setP2c(parameter.getIterationCount());
        }
    }
}
