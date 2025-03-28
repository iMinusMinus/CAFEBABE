package std.ietf.http.jose;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum KeyOperation {

    DIGEST(null, 1), // SHA-1, SHA-256, SHA-384, SHA-512
    MAC(null, 2),
    SIGN("sign", 4), // RSASSA-PKCS1-v1_5, RSA-PSS, ECDSA, HMAC, Ed25519
    VERIFY("verify", 8),
    ENCRYPT("encrypt", 16), // RSA-OAEP, AES-CTR, AES-CBC, AES-GCM
    DECRYPT("decrypt", 32),
    GENERATE_KEY("generateKey", 64), // RSASSA-PKCS1-v1_5, RSA-PSS, RSA-OAEP;  ECDSA, ECDH; HMAC; AES-CTR, AES-CBC, AES-GCM, AES-KW; Ed25519; X25519
    DERIVE_KEY("deriveKey", 128), // ECDH, HKDF, PBKDF2, X25519
    DERIVE_BITS("deriveBits", 256), // ECDH, HKDF, PBKDF2, X25519
    WRAP_KEY("wrapKey", 512), // RSA-OAEP, AES-CTR, AES-CBC, AES-GCM; AES-KW(无需初始向量)
    UNWRAP_KEY("unwrapKey", 1024),
    IMPORT_KEY("importKey", 2048),
    EXPORT_KEY("exportKey", 4096),
    ;

    private final String operation;
    private final int value;

    private KeyOperation(String operation, int value) {
        this.operation = operation;
        this.value = value;
    }

    public boolean support(int value) {
        return (this.value & value) != 0;
    }

    public int and(KeyOperation... keyOps) {
        int value = this.value;
        for (KeyOperation keyOp : keyOps) {
            value |= keyOp.value;
        }
        return value;
    }

    public static String[] asKeyOps(int value) {
        List<String> keyOps = new ArrayList<>();
        for (KeyOperation instance : KeyOperation.values()) {
            if (instance.operation != null && (value & instance.value) != 0) {
                keyOps.add(instance.operation);
            }
        }
        return keyOps.toArray(new String[0]);
    }

}
