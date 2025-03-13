package std.ietf.http.jose;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.security.Key;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc7517">JSON Web Key (JWK)</a>用JSON结构表示加密密钥。
 * @author iMinusMinus
 * @date 2025-03-13
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public abstract class JsonWebKey<T extends Key> {

    public static final String MIME_TYPE = "application/jwk+json";

    /**
     * 密钥类型，如RSA、EC
     */
    protected final String kty;

    /**
     * 公钥用途，如sig（签名）、enc（加密）。如果key_ops存在，则两者需一致
     */
    protected String use;

    /**
     * 公钥/私钥/对称密钥用途，如sign（消息认证码生成）、verify（验证消息认证码）、encrypt（加密内容）、decrypt（加密内容）、wrapKey（加密密钥）、unwrapKey（解密密钥）、deriveKey（派生密钥）、deriveBits（派生用非密钥数据）
     */
    protected String key_ops;

    /**
     * @see std.ietf.http.jose.JsonWebAlgorithm
     */
    protected String alg;

    /**
     * 密钥id，如密钥轮换时，从多个密钥中选择一个
     */
    protected String kid;

    /**
     * 指向X.509 证书或证书链的链接
     */
    protected URL x5u;

    /**
     * base64url编码的DER格式X.509 证书链
     */
    protected String[] x5c;

    /**
     * X.509 证书指纹
     */
    protected String x5t;

//    protected String x5t#S256;

    public abstract T asKey();

    public abstract void withKey(T key);

    public X509Certificate[] asCertificates() {
        if (x5c == null) {
            return null;
        }
        X509Certificate[] certificates = new X509Certificate[x5c.length];
        CertificateFactory factory;
        try {
            factory = CertificateFactory.getInstance("X.509");
            for (int i = 0; i < x5c.length; i++) {
                certificates[i] = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(x5c[i])));
            }
            return certificates;
        } catch (CertificateException ce) {
            ce.printStackTrace();
            return null;
        }
    }

    public void withCertificates(X509Certificate[] certificates) {
        try {
            x5c = new String[certificates.length];
            for (int i = 0; i < certificates.length; i++) {
                x5c[i] = Base64.getEncoder().encodeToString(certificates[i].getEncoded());
            }
        } catch (CertificateEncodingException cee) {
            cee.printStackTrace();
            x5c = null;
        }
    }
}
