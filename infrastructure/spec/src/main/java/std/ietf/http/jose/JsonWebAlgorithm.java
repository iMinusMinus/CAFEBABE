package std.ietf.http.jose;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc7518">JSON Web Algorithms (JWA)</a>定义了一些签名、加密算法.
 * 在<a href="https://www.w3.org/TR/WebCryptoAPI/">Web Cryptography API</a>说明各算法用途：
 * <table>
 *   <thead>
 *     <tr>
 *       <th>Algorithm name</th>
 *       <th scope="col">encrypt</th>
 *       <th scope="col">decrypt</th>
 *       <th scope="col">sign</th>
 *       <th scope="col">verify</th>
 *       <th scope="col">digest</th>
 *       <th scope="col">generateKey</th>
 *       <th scope="col">deriveKey</th>
 *       <th scope="col">deriveBits</th>
 *       <th scope="col">importKey</th>
 *       <th scope="col">exportKey</th>
 *       <th scope="col">wrapKey</th>
 *       <th scope="col">unwrapKey</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><a href="#rsassa-pkcs1">RSASSA-PKCS1-v1_5</a></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td></td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *     </tr>
 *     <tr>
 *       <td><a href="#rsa-pss">RSA-PSS</a></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td></td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *     </tr>
 *     <tr>
 *       <td><a href="#rsa-oaep">RSA-OAEP</a></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td>✔</td>
 *     </tr>
 *     <tr>
 *       <td><a href="#ecdsa">ECDSA</a></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td></td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *     </tr>
 *     <tr>
 *       <td><a href="#ecdh">ECDH</a></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *     </tr>
 *     <tr>
 *       <td><a href="#aes-ctr">AES-CTR</a></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td>✔</td>
 *     </tr>
 *     <tr>
 *       <td><a href="#aes-cbc">AES-CBC</a></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td>✔</td>
 *     </tr>
 *     <tr>
 *       <td><a href="#aes-gcm">AES-GCM</a></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td>✔</td>
 *     </tr>
 *     <tr>
 *       <td><a href="#aes-kw">AES-KW</a></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td>✔</td>
 *     </tr>
 *     <tr>
 *       <td><a href="#hmac">HMAC</a></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td></td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *     </tr>
 *     <tr>
 *       <td><a href="#sha">SHA-1</a></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *     </tr>
 *     <tr>
 *       <td><a href="#sha">SHA-256</a></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *     </tr>
 *     <tr>
 *       <td><a href="#sha">SHA-384</a></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *     </tr>
 *     <tr>
 *       <td><a href="#sha">SHA-512</a></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *     </tr>
 *     <tr>
 *       <td><a href="#hkdf">HKDF</a></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *     </tr>
 *     <tr>
 *       <td><a href="#pbkdf2">PBKDF2</a></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td>✔</td>
 *       <td></td>
 *       <td></td>
 *       <td></td>
 *     </tr>
 *   </tbody>
 * </table>
 */
public interface JsonWebAlgorithm {

    String AES_WRAP_ALGORITHM = "AESWrap";

    String AES_ALGORITHM = "AES";

    // HMAC
    HmacFamily HS256 = new HmacFamily("HS256", "HmacSHA256", 256, "1.2.840.113549.2.9"); // HMAC using SHA-256
    HmacFamily HS384 = new HmacFamily("HS384", "HmacSHA384", 384, "1.2.840.113549.2.10"); // HMAC using SHA-384
    HmacFamily HS512 = new HmacFamily("HS512", "HmacSHA512", 512, "1.2.840.113549.2.11"); // HMAC using SHA-512
    // RSASSA-PKCS1-v1_5
    RsaFamily RS256 = new RsaFamily("RS256", "SHA256withRSA", KeyOperation.SIGN.and(KeyOperation.VERIFY), 256, "1.2.840.113549.1.1.11"); // RSASSA-PKCS1-v1_5 using SHA-256
    RsaFamily RS384 = new RsaFamily("RS384", "SHA384withRSA", KeyOperation.SIGN.and(KeyOperation.VERIFY), 384, "1.2.840.113549.1.1.12"); // RSASSA-PKCS1-v1_5 using SHA-384
    RsaFamily RS512 = new RsaFamily("RS512", "SHA512withRSA", KeyOperation.SIGN.and(KeyOperation.VERIFY), 512, "1.2.840.113549.1.1.13"); // RSASSA-PKCS1-v1_5 using SHA-512
    // ECDSA
    EcFamily ES256 = new EcFamily("ES256", "SHA256withECDSA", KeyOperation.GENERATE_KEY.and(KeyOperation.SIGN, KeyOperation.VERIFY), 256, "1.2.840.10045.4.3.2"); // ECDSA using P-256 and SHA-256
    EcFamily ES384 = new EcFamily("ES384", "SHA384withECDSA", KeyOperation.GENERATE_KEY.and(KeyOperation.SIGN, KeyOperation.VERIFY), 384, "1.2.840.10045.4.3.3"); // ECDSA using P-384 and SHA-384
    EcFamily ES512 = new EcFamily("ES512", "SHA512withECDSA", KeyOperation.GENERATE_KEY.and(KeyOperation.SIGN, KeyOperation.VERIFY), 512, "1.2.840.10045.4.3.4"); // ECDSA using P-521 and SHA-512
    // RSA-PSS JCA不支持，需要引入扩展，如BC
    RsaFamily PS256 = new RsaFamily("PS256", "SHA256withRSAandMGF1", KeyOperation.SIGN.and(KeyOperation.VERIFY), 256, "1.2.840.113549.1.1.10"); // RSASSA-PSS using SHA-256 and MGF1 with SHA-256
    RsaFamily PS384 = new RsaFamily("PS384", "SHA384withRSAandMGF1", KeyOperation.SIGN.and(KeyOperation.VERIFY), 384, "1.2.840.113549.1.1.10"); // RSASSA-PSS using SHA-384 and MGF1 with SHA-384
    RsaFamily PS512 = new RsaFamily("PS512", "SHA512withRSAandMGF1", KeyOperation.SIGN.and(KeyOperation.VERIFY), 512, "1.2.840.113549.1.1.10"); // RSASSA-PSS using SHA-512 and MGF1 with SHA-512
    JsonWebSignatureAlgorithm NONE = new NamedAlgorithm("none");

    JsonWebEncryptionAlgorithm DIR = new NamedAlgorithm("dir"); // Direct use of a shared symmetric key as the CEK
    // --RSAES-PKCS1-v1_5
    RsaFamily RSA1_5 = new RsaFamily("RSA1_5", "RSA/ECB/PKCS1Padding", KeyOperation.GENERATE_KEY.and(KeyOperation.ENCRYPT, KeyOperation.DECRYPT), 0, "1.2.840.113549.1.1.1");
    // RSA-OAEP
    RsaFamily RSA_OAEP = new RsaFamily("RSA-OAEP", "RSA/ECB/OAEPWithSHA-1AndMGF1Padding", KeyOperation.GENERATE_KEY.and(KeyOperation.ENCRYPT, KeyOperation.DECRYPT, KeyOperation.WRAP_KEY, KeyOperation.UNWRAP_KEY), 0, "1.2.840.113549.1.1.7"); // RSAES OAEP using default parameters
    RsaFamily RSA_OAEP_256 = new RsaFamily("RSA-OAEP-256", "RSA/ECB/OAEPWithSHA-256AndMGF1Padding & MGF1ParameterSpec.SHA256", KeyOperation.GENERATE_KEY.and(KeyOperation.ENCRYPT, KeyOperation.DECRYPT, KeyOperation.WRAP_KEY, KeyOperation.UNWRAP_KEY), 0, "1.2.840.113549.1.1.7"); // RSAES OAEP using SHA-256 and MGF1 with SHA-256
    // RSA-OAEP-384
    // RSA-OAEP-512
    // AES-CTR: A128CTR, A192CTR, A256CTR
    // AES-CBC: A128CBC, A192CBC, A256CBC
    // AES-GCM
    AesFamily A128GCM = new AesFamily("A128GCM", "AES/GCM/NoPadding", KeyOperation.GENERATE_KEY.and(KeyOperation.ENCRYPT, KeyOperation.DECRYPT), 128, 0, "2.16.840.1.101.3.4.1.6");
    AesFamily A192GCM = new AesFamily("A192GCM", "AES/GCM/NoPadding", KeyOperation.GENERATE_KEY.and(KeyOperation.ENCRYPT, KeyOperation.DECRYPT), 192, 0, "2.16.840.1.101.3.4.1.26");
    AesFamily A256GCM = new AesFamily("A256GCM", "AES/GCM/NoPadding", KeyOperation.GENERATE_KEY.and(KeyOperation.ENCRYPT, KeyOperation.DECRYPT), 256, 0, "2.16.840.1.101.3.4.1.46");
    AesFamily A128GCMKW = new AesFamily("A128GCMKW", "AES", KeyOperation.GENERATE_KEY.and(KeyOperation.ENCRYPT, KeyOperation.DECRYPT, KeyOperation.WRAP_KEY, KeyOperation.UNWRAP_KEY),  128, 0, null);  // Key wrapping with AES GCM using 128-bit key   iv, tag
    AesFamily A192GCMKW = new AesFamily("A192GCMKW", "AES", KeyOperation.GENERATE_KEY.and(KeyOperation.ENCRYPT, KeyOperation.DECRYPT, KeyOperation.WRAP_KEY, KeyOperation.UNWRAP_KEY), 192, 0, null);  // Key wrapping with AES GCM using 192-bit key   iv, tag
    AesFamily A256GCMKW = new AesFamily("A256GCMKW", "AES", KeyOperation.GENERATE_KEY.and(KeyOperation.ENCRYPT, KeyOperation.DECRYPT, KeyOperation.WRAP_KEY, KeyOperation.UNWRAP_KEY), 256, 0, null);  // Key wrapping with AES GCM using 256-bit key   iv, tag
    // AES-KW
    AesFamily A128KW = new AesFamily("A128KW", "AES", KeyOperation.GENERATE_KEY.and(KeyOperation.WRAP_KEY, KeyOperation.UNWRAP_KEY), 128, 0, "2.16.840.1.101.3.4.1.5");  // AES Key Wrap with default initial value using 128-bit key
    AesFamily A192KW = new AesFamily("A192KW", "AES", KeyOperation.GENERATE_KEY.and(KeyOperation.WRAP_KEY, KeyOperation.UNWRAP_KEY), 192, 0, "2.16.840.1.101.3.4.1.25");  // AES Key Wrap with default initial value using 192-bit key
    AesFamily A256KW = new AesFamily("A256KW", "AES", KeyOperation.GENERATE_KEY.and(KeyOperation.WRAP_KEY, KeyOperation.UNWRAP_KEY), 256, 0, "2.16.840.1.101.3.4.1.45");  // AES Key Wrap with default initial value using 256-bit key
    // --? PKCS5Padding是PKCS7Padding的子集，JCA原生不支持PKCS7Padding
    AesFamily A128CBC_HS256 = new AesFamily("A128CBC-HS256", "AES/CBC/PKCS5Padding", KeyOperation.MAC.and(KeyOperation.ENCRYPT, KeyOperation.DECRYPT), 128, 256, "2.16.840.1.101.3.4.1.2");
    AesFamily A128CBC_HS384 = new AesFamily("A128CBC-HS384", "AES/CBC/PKCS5Padding", KeyOperation.MAC.and(KeyOperation.ENCRYPT, KeyOperation.DECRYPT), 128, 384, "2.16.840.1.101.3.4.1.22");
    AesFamily A128CBC_HS512 = new AesFamily("A128CBC-HS512", "AES/CBC/PKCS5Padding", KeyOperation.MAC.and(KeyOperation.ENCRYPT, KeyOperation.DECRYPT), 128, 512, "2.16.840.1.101.3.4.1.42");
    // --ECDH-ES
    EcFamily ECDH_ES = new EcFamily("ECDH-ES ", "ECDH", KeyOperation.GENERATE_KEY.and(KeyOperation.DERIVE_KEY, KeyOperation.DERIVE_BITS), 0, "1.3.132.1.12"); // Elliptic Curve Diffie-Hellman Ephemeral Static key agreement         epk, apu, apv
    EcFamily ECDH_ES_A128KW = new EcFamily("ECDH-ES+A128KW", "ECDH", KeyOperation.GENERATE_KEY.and(KeyOperation.DERIVE_KEY, KeyOperation.DERIVE_BITS, KeyOperation.WRAP_KEY, KeyOperation.UNWRAP_KEY), 128, null); // Elliptic Curve Diffie-Hellman Ephemeral Static key agreement         epk, apu, apv
    EcFamily ECDH_ES_A192KW = new EcFamily("ECDH-ES+A192KW", "ECDH", KeyOperation.GENERATE_KEY.and(KeyOperation.DERIVE_KEY, KeyOperation.DERIVE_BITS, KeyOperation.WRAP_KEY, KeyOperation.UNWRAP_KEY), 192, null); // Elliptic Curve Diffie-Hellman Ephemeral Static key agreement         epk, apu, apv
    EcFamily ECDH_ES_A256KW = new EcFamily("ECDH-ES+A256KW", "ECDH", KeyOperation.GENERATE_KEY.and(KeyOperation.DERIVE_KEY, KeyOperation.DERIVE_BITS, KeyOperation.WRAP_KEY, KeyOperation.UNWRAP_KEY), 256, null); // Elliptic Curve Diffie-Hellman Ephemeral Static key agreement         epk, apu, apv
    // --PBES2
    Pbkdf2Family PBES2_HS256_A128KW = new Pbkdf2Family("PBES2-HS256+A128KW", "PBEWithHmacSHA256AndAES_128", 128, 256, null); // PBES2 with HMAC SHA-256 and "A128KW" wrapping    p2s, p2c
    Pbkdf2Family PBES2_HS384_A192KW = new Pbkdf2Family("PBES2-HS384+A192KW", "PBEWithHmacSHA384AndAES_192", 192, 384, null); // PBES2 with HMAC SHA-384 and "A192KW" wrapping    p2s, p2c
    Pbkdf2Family PBES2_HS512_A256KW = new Pbkdf2Family("PBES2-HS512+A256KW", "PBEWithHmacSHA512AndAES_256", 256, 512, null); // PBES2 with HMAC SHA-384 and "A192KW" wrapping    p2s, p2c

    static JsonWebAlgorithm.JsonWebSignatureAlgorithm getJwsAlgorithm(String jwa) {
        switch (jwa) {
            case "HS256": return HS256;
            case "HS384": return HS384;
            case "HS512": return HS512;
            case "RS256": return RS256;
            case "RS384": return RS384;
            case "RS512": return RS512;
            case "ES256": return ES256;
            case "ES384": return ES384;
            case "ES512": return ES512;
            case "PS256": return PS256;
            case "PS384": return PS384;
            case "PS512": return PS512;
            case "none": return NONE;
            default:
                return null;
        }
    }

    static JsonWebAlgorithm.JsonWebEncryptionAlgorithm getJweAlgorithm(String jwa) {
        switch (jwa) {
            // alg
            case "RSA1_5": return RSA1_5;
            case "RSA-OAEP": return RSA_OAEP;
            case "RSA-OAEP-256": return RSA_OAEP_256;
            case "A128KW": return A128KW;
            case "A192KW": return A192KW;
            case "A256KW": return A256KW;
            case "dir": return DIR;
            case "ECDH-ES": return ECDH_ES;
            case "ECDH-ES_A128KW": return ECDH_ES_A128KW;
            case "ECDH-ES_A192KW": return ECDH_ES_A192KW;
            case "ECDH-ES_A256KW": return ECDH_ES_A256KW;
            case "A128GCMKW": return A128GCMKW;
            case "A192GCMKW": return A192GCMKW;
            case "A256GCMKW": return A256GCMKW;
            case "PBES2-HS256+A128KW": return PBES2_HS256_A128KW;
            case "PBES2-HS384+A192KW": return PBES2_HS384_A192KW;
            case "PBES2-HS512+A256KW": return PBES2_HS512_A256KW;
            // enc
            case "A128CBC-HS256": return A128CBC_HS256;
            case "A128CBC-HS384": return A128CBC_HS384;
            case "A128CBC-HS512": return A128CBC_HS512;
            case "A128GCM": return A128GCM;
            case "A192GCM": return A192GCM;
            case "A256GCM": return A256GCM;
            default:
                return null;
        }
    }

    static JsonWebAlgorithm.JsonWebSignatureAlgorithm jwsAlgorithmFrom(String jcaAlgorithm) {
        switch (jcaAlgorithm) {
            case "HmacSHA256": return HS256;
            case "HmacSHA384": return HS384;
            case "HmacSHA512": return HS512;
            case "SHA256withRSA": return RS256;
            case "SHA384withRSA": return RS384;
            case "SHA512withRSA": return RS512;
            case "SHA256withECDSA": return ES256;
            case "SHA384withECDSA": return ES384;
            case "SHA512withECDSA": return ES512;
            case "SHA256withRSAandMGF1": return PS256;
            case "SHA384withRSAandMGF1": return PS384;
            case "SHA512withRSAandMGF1": return PS512;
            default:
                return NONE;
        }
    }

    static JsonWebAlgorithm.JsonWebEncryptionAlgorithm jweAlgorithmFrom(String jcaAlgorithm, int keySize, int messageDigestSize) {
        switch (jcaAlgorithm) {
            case "RSA/ECB/PKCS1Padding": return RSA1_5;
            case "RSA/ECB/OAEPWithSHA-1AndMGF1Padding": return RSA_OAEP;
            case "RSA/ECB/OAEPWithSHA-256AndMGF1Padding & MGF1ParameterSpec.SHA256": return RSA_OAEP_256;
            case "AES": // SunJCE 中等同于 AES/ECB/PKCS5Padding
            case "AES/ECB/PKCS5Padding":
                return null;
            case "AES/CBC/PKCS5Padding":
                if (messageDigestSize == 256) {
                    return A128CBC_HS256;
                } else if (messageDigestSize == 384) {
                    return A128CBC_HS384;
                } else if (messageDigestSize == 512) {
                    return A128CBC_HS512;
                }
                break;
            case "AES/GCM/NoPadding":
                if (keySize == 128) {
                    return A128GCM;
                } else if (keySize == 192) {
                    return A192GCM;
                } else if (keySize == 256) {
                    return A256GCM;
                }
            default:

        }
        return null;
    }

    String jwaName();

    /**
     * 获取JCA算法名称
     * @return JWA对应的JCA算法
     */
    default String getAlgorithm() {
        throw new UnsupportedOperationException();
    }

    @RequiredArgsConstructor
    @ToString
    class NamedAlgorithm extends JsonWebEncryptionAlgorithm implements JsonWebSignatureAlgorithm {

        private final String alg;

        @Override
        public String jwaName() {
            return alg;
        }

        @Override
        public int getMessageDigestSize() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getKeySize() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getAlgorithm() {
            throw new UnsupportedOperationException();
        }
    }

    interface JsonWebSignatureAlgorithm extends JsonWebAlgorithm {

        @Override
        default byte[] sign(byte[] data, Key key) throws GeneralSecurityException {
            assert key instanceof PrivateKey;
            Signature signature = Signature.getInstance(getAlgorithm());
            signature.initSign((PrivateKey) key);
            signature.update(data);
            return signature.sign();
        }

        @Override
        default boolean verify(byte[] signature, byte[] data, Key key) throws GeneralSecurityException {
            Signature sig = Signature.getInstance(getAlgorithm());
            sig.initVerify((PublicKey) key);
            sig.update(data);
            return sig.verify(signature);
        }

        @Override
        default byte[] digest(byte[] data) throws GeneralSecurityException {
            MessageDigest md = MessageDigest.getInstance("SHA-" + getMessageDigestSize());
            return md.digest(data);
        }

        int getMessageDigestSize();
    }

    @RequiredArgsConstructor
    @ToString
    class HmacFamily implements JsonWebSignatureAlgorithm {

        private final String alg;

        @Getter private final String algorithm;

        @Getter private final int messageDigestSize;

        @Getter private final String oid;

        @Override
        public boolean support(KeyOperation keyOps) {
            return KeyOperation.MAC.support(keyOps.getValue()) || KeyOperation.GENERATE_KEY.support(keyOps.getValue());
        }

        @Override
        public String jwaName() {
            return alg;
        }

        @Override
        public byte[] sign(byte[] data, Key key) throws GeneralSecurityException {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(key);
            return mac.doFinal(data);
        }

        @Override
        public boolean verify(byte[] signature, byte[] data, Key key) throws GeneralSecurityException {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(key);
            return Arrays.equals(signature, mac.doFinal(data));
        }

        @Override
        public SecretKey generateKey() throws GeneralSecurityException {
            return KeyGenerator.getInstance(algorithm).generateKey();
        }

    }

    @RequiredArgsConstructor
    @ToString
    class RsaFamily extends JsonWebEncryptionAlgorithm implements JsonWebSignatureAlgorithm {

        private final String alg;

        @Getter private final String algorithm;

        private final int keyOps;

        @Getter private final int messageDigestSize;

        @Getter private final String oid;

        @Override
        public String jwaName() {
            return alg;
        }

        @Override
        public boolean support(KeyOperation keyOperation) {
            return keyOperation.support(keyOps);
        }

        @Override
        public int getKeySize() { // depends key
            throw new UnsupportedOperationException();
        }

        @Override
        protected boolean isBlockCipher() {
            return true;
        }

        @Override
        protected int getBlockSize(Key key) { // 根据密钥长度、padding方式而不同
            if (key instanceof RSAPublicKey) {
                return alg.contains("OAEP") ?
                        ((RSAPublicKey) key).getModulus().bitLength() /8 - 41 :
                        ((RSAPublicKey) key).getModulus().bitLength() /8 - 11;
            } else if (key instanceof RSAPrivateKey) {
                return ((RSAPrivateKey) key).getModulus().bitLength() / 8;
            }
            throw new IllegalArgumentException();
        }

        @Override
        public byte[] sign(byte[] data, Key key) throws GeneralSecurityException {
            if (KeyOperation.SIGN.support(keyOps)) {
                return JsonWebSignatureAlgorithm.super.sign(data, key);
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] encrypt(byte[] data, Key key, AlgorithmParameterSpec parameterSpec, byte[] authData) throws GeneralSecurityException {
            if (KeyOperation.ENCRYPT.support(keyOps)) {
                return super.encrypt(data, key, parameterSpec, authData);
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] decrypt(byte[] data, Key key, AlgorithmParameterSpec parameterSpec, byte[] authData, byte[] authTag) throws GeneralSecurityException {
            if (KeyOperation.DECRYPT.support(keyOps)) {
                return super.decrypt(data, key, parameterSpec, authData, authTag);
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public KeyPair generateKeyPair() throws GeneralSecurityException {
            if (KeyOperation.GENERATE_KEY.support(keyOps)) {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                return keyPairGenerator.generateKeyPair();
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] wrapKey(Key kek, Key cek) throws GeneralSecurityException {
            if (KeyOperation.WRAP_KEY.support(keyOps)) {
                return super.wrapKey(kek, cek);
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public Key unwrapKey(Key kek, byte[] encryptedCEK) throws GeneralSecurityException {
            if (KeyOperation.UNWRAP_KEY.support(keyOps)) {
                return super.unwrapKey(kek, encryptedCEK);
            }
            throw new UnsupportedOperationException();
        }
    }

    @RequiredArgsConstructor
    @ToString
    class EcFamily extends JsonWebEncryptionAlgorithm implements JsonWebSignatureAlgorithm {
        private final String alg;

        @Getter private final String algorithm;

        @Getter private final int keyOps;

        @Getter private final int messageDigestSize;

        @Getter private final String oid;

        @Override
        public String jwaName() {
            return alg;
        }

        @Override
        public boolean support(KeyOperation keyOperation) {
            return keyOperation.support(keyOps);
        }

        @Override
        public int getKeySize() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected boolean isBlockCipher() { // TODO
            return false;
        }

        @Override
        public byte[] sign(byte[] data, Key key) throws GeneralSecurityException {
            if (KeyOperation.SIGN.support(keyOps)) { // ECDSA
                return fromDER(JsonWebSignatureAlgorithm.super.sign(data, key));
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean verify(byte[] signature, byte[] data, Key key) throws GeneralSecurityException {
            if (KeyOperation.VERIFY.support(keyOps)) { // ECDSA
                return JsonWebSignatureAlgorithm.super.verify(toDER(signature), data, key);
            }
            throw new UnsupportedOperationException();
        }

        protected byte[] fromDER(byte[] derSignature) {
            assert derSignature[0] == (byte) 0x30;
            int offset = derSignature[1] > 0 ? 2 : 3;
            byte rLength = derSignature[offset + 1];
            int i = rLength;
            for (; i > 0 && derSignature[(offset + 2 + rLength) - i] == 0; i--);
            byte sLength = derSignature[offset + 2 + rLength + 1];
            int j = sLength;
            for (; j > 0 && derSignature[(offset + 2 + rLength + 2 + sLength) - j] == 0; j--);
            int rawLen = Math.max(i, j);
            rawLen = Math.max(rawLen, messageDigestSize / 8);
            byte[] jwsSignature = new byte[2 * rawLen];
            System.arraycopy(derSignature, (offset + 2 + rLength) - i, jwsSignature, rawLen - i, i);
            System.arraycopy(derSignature, (offset + 2 + rLength + 2 + sLength) - j, jwsSignature, 2 * rawLen - j, j);
            return jwsSignature;
        }

        protected byte[] toDER(byte[] jwsSignature) throws GeneralSecurityException { // JWT ES256签名为R|S，JCA的SHA256withECDSA签名为ASN.1 DER格式
            int middle = jwsSignature.length / 2;
            int i = middle;
            for (; i > 0 && jwsSignature[middle - i] == 0; i--);
            int j = jwsSignature[middle - i] < 0 ? i + 1 : i;
            int k = middle;
            for (; k > 0 && jwsSignature[jwsSignature.length - k] == 0; k--);
            int l = jwsSignature[jwsSignature.length - k] < 0 ? k + 1 : k;
            int len = 2 + j + 2 + l; // 0x02, R length, R, 0x02, S length, S
            if (len > 255) {
                throw new SignatureException("Invalid ECDSA signature format");
            }
            byte[] derSignature;
            int offset = 1;
            if (len < 128) {
                derSignature = new byte[2 + len];
            } else {
                derSignature = new byte[3 + len];
                derSignature[offset++] = (byte) 0x81;
            }
            derSignature[0] = (byte) 0x30;
            derSignature[offset++] = (byte) len;
            derSignature[offset++] = (byte) 0x02;
            derSignature[offset++] = (byte) j;
            System.arraycopy(jwsSignature, middle - i, derSignature, (offset + j) - i, i);
            offset += j;
            derSignature[offset++] = (byte) 0x02;
            derSignature[offset++] = (byte) l;
            System.arraycopy(jwsSignature, 2 * middle - k, derSignature, (offset + l) - k, k);
            return derSignature;
        }

        @Override
        public KeyPair generateKeyPair() throws GeneralSecurityException {
            if (KeyOperation.GENERATE_KEY.support(keyOps)) { // ECDSA, ECDH
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
                keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"));
                return keyPairGenerator.generateKeyPair();
            }
            throw new UnsupportedOperationException();
        }

        SecretKey deriveKey(Key publicKey, JsonWebEncryptionAlgorithm enc, byte[] apu, byte[] apv) throws GeneralSecurityException {
            if (KeyOperation.DERIVE_KEY.support(keyOps)) { // ECDH
                assert apv != null;
                byte[] sharedSecretKey = publicKey instanceof PublicKey ? deriveBits(publicKey) : publicKey.getEncoded();
                MessageDigest md = MessageDigest.getInstance("SHA-256"); // JWE中，ECDH 密钥协商摘要算法使用SHA-256
                int keyDataLength = messageDigestSize <= 0 ? enc.getKeySize() : messageDigestSize; // 如果是ECDH，密钥长度为enc算法的密钥长度；ECDH-ES+A128KW等为算法指定长度
                keyDataLength = enc.support(KeyOperation.MAC) ? keyDataLength * 2 : keyDataLength;
                byte[] algorithmIdContent;
                if (support(KeyOperation.WRAP_KEY)) {
                    String[] kw = jwaName().split("[+]");
                    algorithmIdContent = kw[kw.length - 1].getBytes(StandardCharsets.US_ASCII);
                } else {
                    algorithmIdContent = enc.jwaName().getBytes(StandardCharsets.US_ASCII);
                }
                int round = (keyDataLength + md.getDigestLength() * 8 - 1) / md.getDigestLength();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for (int i = 1; i <= round; i++) {
                    md.update(asBigEndianValue(32, i)); // round number
                    md.update(sharedSecretKey); // Z
                    // otherInfo: algorithmId, partyUInfo, partyVInfo, suppPubInfo
                    md.update(asBigEndianValue(32, algorithmIdContent.length));
                    md.update(algorithmIdContent);
                    md.update(asBigEndianValue(32, apu.length));
                    md.update(apu);
                    md.update(asBigEndianValue(32, apv.length));
                    md.update(apv);
                    md.update(asBigEndianValue(32, keyDataLength));
                    byte[] output = md.digest();
                    baos.write(output, 0, output.length);
                }
                byte[] derivedKey = Arrays.copyOf(baos.toByteArray(), keyDataLength / 8);
                return new SecretKeySpec(derivedKey, AES_ALGORITHM);
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] deriveBits(Key publicKey) throws GeneralSecurityException {
            if (KeyOperation.DERIVE_BITS.support(keyOps)) { // ECDH
                return deriveBits(publicKey, generateKeyPair().getPrivate());
            }
            throw new UnsupportedOperationException();
        }

        byte[] deriveBits(Key publicKey, Key privateKey) throws GeneralSecurityException {
            KeyAgreement keyAgreement = KeyAgreement.getInstance(algorithm);
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(publicKey, true);
            return keyAgreement.generateSecret();
        }
    }

    abstract class JsonWebEncryptionAlgorithm implements JsonWebAlgorithm {

        public abstract String getAlgorithm();

        public AlgorithmParameterSpec newAlgorithmParameterSpec(int tlen, byte[] iv) {
            throw new UnsupportedOperationException();
        }

        public AlgorithmParameterSpec newRandomParameter() {
            return null;
        }

        public abstract int getKeySize();

        protected boolean isBlockCipher() {
            return false;
        }

        protected int getBlockSize(Key key) {
            throw new UnsupportedOperationException();
        }

        protected boolean supportAad() {
            return false;
        }

        protected int getOutputLength(int length) {
            return length;
        }

        protected final byte[] asBigEndianValue(int bit, int length) {
            byte[] value = new byte[bit / 8];
            int index = value.length;
            while (index > 0 && length > 0) {
                int remainder = length % 256;
                value[--index] = (byte) remainder;
                length /= 256;
            }
            return value;
        }

        @Override
        public byte[] encrypt(byte[] data, Key key, AlgorithmParameterSpec parameterSpec, byte[] authData) throws GeneralSecurityException {
            Cipher cipher = Cipher.getInstance(getAlgorithm());
            if (parameterSpec != null) {
                cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            }
            if (supportAad() && authData != null && authData.length > 0) {
                cipher.updateAAD(authData);
            }
            if (isBlockCipher()) {
                ByteArrayOutputStream output = new ByteArrayOutputStream(getOutputLength(data.length));
                int blockSize = getBlockSize(key);
                int offset = 0;
                while (offset < data.length) {
                    int left = data.length - offset;
                    byte[] ciphertext;
                    if (left <= blockSize) {
                        ciphertext = cipher.doFinal(data, offset, left);
                        output.write(ciphertext, 0, ciphertext.length);
                        offset += left;
                    } else {
                        ciphertext = cipher.update(data, offset, blockSize);
                        output.write(ciphertext, 0, ciphertext.length);
                        offset += blockSize;
                    }
                }
                return output.toByteArray();
            }
            return cipher.doFinal(data);
        }

        @Override
        public byte[] decrypt(byte[] data, Key key, AlgorithmParameterSpec parameterSpec, byte[] authData, byte[] authTag) throws GeneralSecurityException {
            Cipher cipher = Cipher.getInstance(getAlgorithm());
            if (parameterSpec != null) {
                cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key);
            }
            byte[] ciphertext = data;
            if (supportAad() && authData != null && authData.length > 0) {
                cipher.updateAAD(authData);
                ciphertext = new byte[data.length + authTag.length];
                System.arraycopy(data, 0, ciphertext, 0, data.length);
                System.arraycopy(authTag, 0, ciphertext, data.length, authTag.length);
            }
            if (isBlockCipher()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length); // 解密后原文长度一般不大于密文长度
                int blockSize = getBlockSize(key);
                int offset = 0;
                while (offset < ciphertext.length) {
                    int left = ciphertext.length - offset;
                    byte[] plain;
                    if (left <= blockSize) {
                        plain = cipher.doFinal(ciphertext, offset, left);
                        baos.write(plain, 0, plain.length);
                        offset += left;
                    } else {
                        plain = cipher.update(ciphertext, offset, blockSize);
                        baos.write(plain, 0, plain.length);
                        offset += blockSize;
                    }
                }
                return baos.toByteArray();
            }
            return cipher.doFinal(ciphertext);
        }

        @Override
        public byte[] wrapKey(Key kek, Key cek) throws GeneralSecurityException {
            Cipher cipher = Cipher.getInstance(AES_WRAP_ALGORITHM);
            cipher.init(Cipher.WRAP_MODE, kek);
            return cipher.wrap(cek);
        }

        @Override
        public Key unwrapKey(Key kek, byte[] encryptedCEK) throws GeneralSecurityException {
            assert AES_ALGORITHM.equals(kek.getAlgorithm()); // format不同，即便创建SecretKeySpec子类，也可能无法被加载
            Cipher cipher = Cipher.getInstance(AES_WRAP_ALGORITHM);
            cipher.init(Cipher.UNWRAP_MODE, kek);
            return cipher.unwrap(encryptedCEK, kek.getAlgorithm(), Cipher.SECRET_KEY);
        }

        @Override
        public KeyPair generateKeyPair() throws GeneralSecurityException {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(getAlgorithm());
            return keyPairGenerator.generateKeyPair();
        }
    }

    @RequiredArgsConstructor
    @ToString
    class AesFamily extends JsonWebEncryptionAlgorithm {

        private final String alg;

        @Getter private final String algorithm; // algorithm/mode/padding

        private final int keyOps;

        @Getter private final int keySize;

        private final int messageDigestSize;

        @Getter private final String oid;

        /**
         * AES向量长度为128 bit
         */
        static final byte[] DEFAULT_IV = {0xA, 0x6, 0xA, 0x6, 0xA, 0x6, 0xA, 0x6, 0xA, 0x6, 0xA, 0x6, 0xA, 0x6, 0xA, 0x6};

        @Override
        public String jwaName() {
            return alg;
        }

        @Override
        public boolean support(KeyOperation keyOperation) {
            return keyOperation.support(keyOps);
        }

        @Override
        protected boolean isBlockCipher() {
            String mode = algorithm.split("/")[1];
            return mode.startsWith("ECB") || mode.startsWith("CBC"); // AES/CFB8/NoPadding
        }

        @Override
        protected int getBlockSize(Key key) {
            return 128; // AES分组长度与密钥无关
        }

        @Override
        protected int getOutputLength(int length) {
            return length + 16 - length % 16;
        }

        @Override
        protected boolean supportAad() {
            String mode = algorithm.split("/")[1];
            return mode.equals("GCM");
        }

        @Override
        public AlgorithmParameterSpec newAlgorithmParameterSpec(int tlen, byte[] iv) {
            if (supportAad()) {
                return new GCMParameterSpec(tlen * 8, iv);
            } else { // 反馈模式，使用初始向量，如 CBC, CFB, OFB, PCBC
                return new IvParameterSpec(iv);
            }
        }

        @Override
        public byte[] encrypt(byte[] data, Key key, AlgorithmParameterSpec parameterSpec, byte[] authData) throws GeneralSecurityException {
            assert parameterSpec == null || parameterSpec instanceof GCMParameterSpec || parameterSpec instanceof IvParameterSpec;
            if (!KeyOperation.ENCRYPT.support(keyOps)) {
                throw new UnsupportedOperationException();
            }
            SecretKey macKey = null;
            JsonWebAlgorithm digestAlgorithm = null;
            if (KeyOperation.MAC.support(keyOps)) { // A128CBC-HS256, A128CBC-HS384, A128CBC-HS512
                digestAlgorithm = JsonWebAlgorithm.getJwsAlgorithm(alg.split("-")[1]);
                byte[] k = key.getEncoded();
                // K length 32: ENC_KEY_LEN = MAC_KEY_LEN = 16, SHA-256, T_LEN = 16;
                // K length 48: ENC_KEY_LEN = MAC_KEY_LEN = 24, SHA-384, T_LEN = 24;
                // K length 64: ENC_KEY_LEN = MAC_KEY_LEN = 32, SHA-512, T_LEN = 32
                int len = k.length / 2;
                assert len == 16 || len == 24 || len == 32;
                macKey = new SecretKeySpec(k, 0, len, digestAlgorithm.getAlgorithm()); // MAC_KEY = initial MAC_KEY_LEN octets of K
                key = new SecretKeySpec(k, len, len, JsonWebAlgorithm.AES_ALGORITHM); // ENC_KEY = final ENC_KEY_LEN octets of K
            }
            byte[] ciphertext =  super.encrypt(data, key, parameterSpec, authData); // E = CBC-PKCS7-ENC(ENC_KEY, P)
            if (KeyOperation.MAC.support(keyOps)) {
                byte[] m = computeHmacValue(digestAlgorithm, macKey, authData, parameterSpec, ciphertext); // M = MAC(MAC_KEY, A || IV || E || AL)
                byte[] t = Arrays.copyOf(m, macKey.getEncoded().length); // T = initial T_LEN octets of M
                byte[] combined = new byte[ciphertext.length + t.length];
                System.arraycopy(ciphertext, 0, combined, 0, ciphertext.length);
                System.arraycopy(t, 0, combined, ciphertext.length, t.length);
                ciphertext = combined;
            }
            return ciphertext;
        }

        private byte[] computeHmacValue(JsonWebAlgorithm mac, SecretKey macKey,
                                        byte[] aad, AlgorithmParameterSpec ivSpec, byte[] ciphertext) throws GeneralSecurityException {
            assert aad.length * 8 > 0;
            assert ivSpec instanceof IvParameterSpec;
            byte[] al = asBigEndianValue(64, aad.length * 8);
            byte[] iv = ((IvParameterSpec) ivSpec).getIV();
            int hmacInputLength = aad.length + iv.length + ciphertext.length + al.length;
            byte[] hmacInput = new byte[hmacInputLength];
            System.arraycopy(aad, 0, hmacInput, 0, aad.length);
            System.arraycopy(iv, 0, hmacInput, aad.length, iv.length);
            System.arraycopy(ciphertext, 0, hmacInput, aad.length + iv.length, ciphertext.length);
            System.arraycopy(al, 0, hmacInput, aad.length + iv.length + ciphertext.length, al.length);
            return mac.sign(hmacInput, macKey); // M = MAC(MAC_KEY, A || IV || E || AL)
        }

        @Override
        public byte[] decrypt(byte[] data, Key key, AlgorithmParameterSpec parameterSpec, byte[] authData, byte[] authTag) throws GeneralSecurityException {
            assert parameterSpec == null || parameterSpec instanceof GCMParameterSpec || parameterSpec instanceof IvParameterSpec;
            if (!KeyOperation.DECRYPT.support(keyOps)) {
                throw new UnsupportedOperationException();
            }

            if (KeyOperation.MAC.support(keyOps)) { // A128CBC-HS256, A128CBC-HS384, A128CBC-HS512
                assert parameterSpec instanceof IvParameterSpec;
                JsonWebAlgorithm digestAlgorithm = JsonWebAlgorithm.getJwsAlgorithm(alg.split("-")[1]);
                byte[] k = key.getEncoded();
                int len = k.length / 2;
                assert len == 16 || len == 24 || len == 32;
                SecretKey macKey = new SecretKeySpec(k, 0, len, digestAlgorithm.getAlgorithm()); // MAC_KEY = initial MAC_KEY_LEN octets of K
                SecretKey encKey = new SecretKeySpec(k, len, len, JsonWebAlgorithm.AES_ALGORITHM); // ENC_KEY = final ENC_KEY_LEN octets of K
                byte[] m = computeHmacValue(digestAlgorithm, macKey, authData, parameterSpec, data); // M = MAC(MAC_KEY, A || IV || E || AL)
                byte[] t = Arrays.copyOf(m, len); // T = initial T_LEN octets of M
                if (!Arrays.equals(authTag, t)) {
                    throw new GeneralSecurityException("verify failure");
                }
                key = encKey; // E = CBC-PKCS7-ENC(ENC_KEY, P)
            }
            assert !supportAad() || authTag.length == 128 / 8; // GCM算法认证码长度为128 bit
            return super.decrypt(data, key, parameterSpec, authData, authTag);
        }

        @Override
        public Key generateKey() throws GeneralSecurityException {
            if (KeyOperation.GENERATE_KEY.support(keyOps)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
                keyGenerator.init(keySize);
                return keyGenerator.generateKey();
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] wrapKey(Key kek, Key cek) throws GeneralSecurityException {
            if (KeyOperation.WRAP_KEY.support(keyOps)) {
                return super.wrapKey(kek, cek);
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public Key unwrapKey(Key kek, byte[] encryptedCEK) throws GeneralSecurityException {
            if (KeyOperation.UNWRAP_KEY.support(keyOps)) {
                return super.unwrapKey(kek, encryptedCEK);
            }
            throw new UnsupportedOperationException();
        }

    }

    @RequiredArgsConstructor
    @Getter
    @ToString
    class Pbkdf2Family extends JsonWebEncryptionAlgorithm {

        private final String alg;

        private final String algorithm;

        private final int keySize;

        private final int messageDigestSize;

        private final String oid;

        @Override
        public String jwaName() {
            return alg;
        }

        @Override
        public boolean support(KeyOperation keyOperation) {
            return keyOperation == KeyOperation.DERIVE_KEY ||
                    keyOperation == KeyOperation.WRAP_KEY || keyOperation == KeyOperation.UNWRAP_KEY;
        }

        @Override
        public byte[] encrypt(byte[] data, Key key, AlgorithmParameterSpec parameterSpec, byte[] authData) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] decrypt(byte[] data, Key key, AlgorithmParameterSpec parameterSpec, byte[] authData, byte[] authTag) {
            throw new UnsupportedOperationException();
        }

        SecretKey deriveKey(byte[] salt, int iterationCount) throws GeneralSecurityException {
            SecureRandom sr = new SecureRandom();
            byte[] password = new byte[keySize / 8];
            sr.nextBytes(password);
            return deriveKey(password, salt, iterationCount);
        }

        /**
         *
         * @param password 密码（可包含非ASCII字符），长度应不小于生成的密钥长度（如A128KW，则最少16字节），不大于128字节
         * @param salt 盐值（随机），不小于8字节
         * @param iterationCount 迭代次数，应不小于1000
         * @return 生成的共享密钥
         * @throws GeneralSecurityException
         */
        SecretKey deriveKey(byte[] password, byte[] salt, int iterationCount) throws GeneralSecurityException {
            assert iterationCount >= 1 && salt.length >= 8;
            String[] parts = jwaName().split("[+\\-]");
            Mac prf = Mac.getInstance(JsonWebAlgorithm.getJwsAlgorithm(parts[1]).getAlgorithm());
            prf.init(new SecretKeySpec(password, prf.getAlgorithm()));
            int round = (keySize / 8 - 1)  / prf.getMacLength() + 1;
            int maxBlockSize = keySize / 8 - (round - 1) * prf.getMacLength();
            byte[] jwa = jwaName().getBytes(StandardCharsets.UTF_8);
            byte[] formattedSalt = new byte[jwa.length + 1 + salt.length];
            System.arraycopy(jwa, 0, formattedSalt, 0, jwa.length);
            formattedSalt[jwa.length] = 0x00;
            System.arraycopy(salt, 0, formattedSalt, jwa.length + 1, salt.length);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (int i = 0; i < round; i++) {
                byte[] turn = asBigEndianValue(32, i + 1);
                byte[] saltValue = new byte[formattedSalt.length + turn.length];
                System.arraycopy(formattedSalt, 0, saltValue, 0, formattedSalt.length);
                System.arraycopy(turn, 0, saltValue, formattedSalt.length, turn.length);
                byte[] currentU = prf.doFinal(saltValue);
                byte[] lastU = currentU;
                byte[] xorU = currentU;
                for (int j = 2; j <= iterationCount; j++) {
                    currentU = prf.doFinal(lastU);
                    for (int k = 0; k < currentU.length; k++) {
                        xorU[k] = (byte) (currentU[k] ^ xorU[k]);
                    }
                    lastU = currentU;
                }
                if (i == round - 1) {
                    xorU = Arrays.copyOf(xorU, maxBlockSize);
                }
                baos.write(xorU, 0, xorU.length);
            }
            return new SecretKeySpec(baos.toByteArray(), AES_ALGORITHM);
        }

    }

    default boolean support(KeyOperation keyOperation) {
        return false;
    }

    default byte[] encrypt(byte[] data, Key key, AlgorithmParameterSpec parameterSpec, byte[] authData) throws GeneralSecurityException {
        throw new UnsupportedOperationException();
    }

    default byte[] decrypt(byte[] data, Key key, AlgorithmParameterSpec parameterSpec, byte[] authData, byte[] authTag) throws GeneralSecurityException {
        throw new UnsupportedOperationException();
    }

    default byte[] sign(byte[] data, Key key) throws GeneralSecurityException {
        throw new UnsupportedOperationException();
    }

    default boolean verify(byte[] signature, byte[] data, Key key) throws GeneralSecurityException {
        throw new UnsupportedOperationException();
    }

    default byte[] digest(byte[] data) throws GeneralSecurityException {
        throw new UnsupportedOperationException();
    }

    default byte[] wrapKey(Key wrapKey, Key cek) throws GeneralSecurityException {
        throw new UnsupportedOperationException();
    }

    default Key unwrapKey(Key unwrapKey, byte[] encryptedCEK) throws GeneralSecurityException {
        throw new UnsupportedOperationException();
    }

    default Key generateKey() throws GeneralSecurityException {
        throw new UnsupportedOperationException();
    }

    default KeyPair generateKeyPair() throws GeneralSecurityException {
        throw new UnsupportedOperationException();
    }

    default SecretKey deriveKey(Key baseKey, byte[] keyMaterial, int keyLength) throws GeneralSecurityException {
        throw new UnsupportedOperationException();
    }

    default byte[] deriveBits(Key baseKey) throws GeneralSecurityException {
        throw new UnsupportedOperationException();
    }
}
