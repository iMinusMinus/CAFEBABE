package std.ietf.http.jose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Function;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc7516>JSON Web Encryption (JWE)</a>用于加密和解密token，可包含敏感信息
 * @author iMinusMinus
 * @date 2025-03-23
 */
@NoArgsConstructor
@ToString
public class JsonWebEncryption {

    private static final String ZIP_ALG = "DEF";

    private transient boolean frozen;

    private String protectedHeader; // base64url
    @Getter private Header unprotected; // unencoded JSON object
    /**
     * 加密明文用的初始向量
     */
    @Getter private String iv; // base64url
    @Getter private String aad; // base64url
    /**
     * 加密明文后的密文（可能包含AAD）
     */
    @Getter private String ciphertext; // base64url
    @Getter private String tag; // base64url

    @Getter private Recipient[] recipients;
    // Flattened
    @Getter private Header header; // unencoded JSON object
    /**
     * 对密钥进行加密的密钥
     */
    @Getter private String encrypted_key; // base64url

    private void ensureUnfrozen() {
        if (frozen) {
            throw new IllegalStateException("cannot set property value when frozen");
        }
    }

    public String getProtected() {
        return protectedHeader;
    }

    public void setProtected(String protectedHeader) {
        ensureUnfrozen();
        this.protectedHeader = protectedHeader;
    }

    public void setUnprotected(Header unprotected) {
        ensureUnfrozen();
        this.unprotected = unprotected;
    }

    public void setIv(String iv) {
        ensureUnfrozen();
        this.iv = iv;
    }

    public void setAad(String aad) {
        ensureUnfrozen();
        this.aad = aad;
    }

    public void setCiphertext(String ciphertext) {
        ensureUnfrozen();
        this.ciphertext = ciphertext;
    }

    public void setTag(String tag) {
        ensureUnfrozen();
        this.tag = tag;
    }

    public void setRecipients(Recipient[] recipients) {
        ensureUnfrozen();
        this.recipients = recipients;
    }

    public void setHeader(Header header) {
        ensureUnfrozen();
        this.header = header;
    }

    public void setEncrypted_key(String encryptedKey) {
        ensureUnfrozen();
        this.encrypted_key = encryptedKey;
    }

    @Getter
    @Setter
    @ToString
    protected static class Recipient {
        private Header header; // unencoded JSON object
        private String encrypted_key; // base64url
    }

    public <T extends Header> JsonWebEncryption(T headerParams, Object body, Function<Object, byte[]> jsonb, JsonWebKey<?>... keys) throws GeneralSecurityException {
        Header joseHeader = new Header();
        joseHeader.setAlg(headerParams.getAlg());
        joseHeader.setEnc(headerParams.getEnc());
        JsonWebAlgorithm enc = JsonWebAlgorithm.getInstance(joseHeader.getEnc());
        if (enc == null || !enc.support(KeyOperation.ENCRYPT)) {
            throw new IllegalArgumentException("JWE enc should support encryption, but got " + joseHeader.getEnc());
        }
        headerParams.setAlg(null);
        if (headerParams.getZip() != null) {
            joseHeader.setZip(headerParams.getZip());
            headerParams.setZip(null);
        }
        if (headerParams.getCrit() != null && headerParams.getCrit().length > 0) {
            throw new UnsupportedOperationException();
        }
        this.protectedHeader = JsonWebToken.ENCODER.encodeToString(jsonb.apply(joseHeader));
        this.header = headerParams;
        SecureRandom sr = new SecureRandom();
        // Generate a random Content Encryption Key (CEK)
        byte[] rawCek = new byte[256 / 8];
        sr.nextBytes(rawCek);
        Key cek = new SecretKeySpec(rawCek, JsonWebAlgorithm.AES_ALGORITHM);
        // Generate a random JWE Initialization Vector
        byte[] iv = new byte[16];
        sr.nextBytes(iv);
        this.iv = JsonWebToken.ENCODER.encodeToString(iv);

        byte[] payload = body instanceof String ? ((String) body).getBytes(StandardCharsets.UTF_8) : jsonb.apply(body);
        if (ZIP_ALG.equals(joseHeader.getZip())) {
            payload = zip(payload);
        }
        byte[] aad = this.protectedHeader.getBytes(StandardCharsets.US_ASCII);

        Recipient[] recipients = new Recipient[keys.length];
        for (int i = 0; i < keys.length; i++) {
            JsonWebAlgorithm recAlg = JsonWebAlgorithm.getInstance(keys[i].getAlg());
            if (recAlg == null) {
                throw new IllegalArgumentException(keys[i].getAlg());
            }
            JsonWebKey<?> toEncrypt = keys[i];
            byte[] resolved;
            if (recAlg.support(KeyOperation.ENCRYPT)) {
                resolved = recAlg.encrypt(cek.getEncoded(), toEncrypt.isAsymmetric() ? toEncrypt.asPublicKey() : toEncrypt.exportKey(), null, null); // produce the JWE Encrypted Key
            } else if (recAlg.support(KeyOperation.WRAP_KEY)) {
                resolved = recAlg.wrapKey(keys[i].exportKey(), cek);
            } else if (recAlg == JsonWebAlgorithm.DIR) {
                resolved = null;
            } else { // derive?
                throw new IllegalArgumentException();
            }
            recipients[i] = new Recipient();
            recipients[i].header = new Header();
            recipients[i].header.setKid(keys[i].kid);
            recipients[i].encrypted_key = resolved == null ? null : JsonWebToken.ENCODER.encodeToString(resolved);
        }
        byte[] ciphertext = enc.encrypt(payload, cek, enc.newAlgorithmParameterSpec(aad.length, iv), aad);
        if (enc.support(KeyOperation.MAC)) {
            byte[] authTag = new byte[cek.getEncoded().length / 2];
            System.arraycopy(ciphertext, ciphertext.length - authTag.length, authTag, 0, authTag.length);
            this.tag = JsonWebToken.ENCODER.encodeToString(authTag);
            byte[] tmp = new byte[ciphertext.length - authTag.length];
            System.arraycopy(ciphertext, 0, tmp, 0, tmp.length);
            this.ciphertext = JsonWebToken.ENCODER.encodeToString(tmp);
        } else {
            this.ciphertext = JsonWebToken.ENCODER.encodeToString(ciphertext);
        }
        frozen = true;
    }

    public <T extends Header> byte[] toJWT(Function<byte[], T> jsonb, JsonWebKey<?>... keys) throws GeneralSecurityException {
        boolean frozen = this.frozen;
        this.frozen = true;
        T joseHeader = jsonb.apply(Base64.getUrlDecoder().decode(protectedHeader));
        assert header == null || header.getEnc() == null; // 不受保护的JOSE头不能包含受保护JOSE头内容，包括crit指定的字段不能同时出现
        if (joseHeader.getZip() != null && !ZIP_ALG.equals(joseHeader.getZip())) {
            throw new UnsupportedOperationException("supported zip: DEF");
        }
        Recipient[] recipients = this.recipients;
        if (this.recipients == null || this.recipients.length == 0) {
            recipients = new Recipient[1];
            recipients[0] = new Recipient();
            recipients[0].encrypted_key = encrypted_key;
            recipients[0].header = Optional.ofNullable(header).orElse(joseHeader);
        }
        byte[] data = null;
        for (Recipient recipient : recipients) {
            if (recipient.header.getCrit() != null && recipient.header.getCrit().length != 0) {
                if (!Header.PARAM_ZIP.equals(recipient.header.getCrit()[0])) {
                    throw new UnsupportedOperationException("supported crit: " + Header.PARAM_ZIP);
                }
            }
            if (recipient.header.getAlg() == null) {
                recipient.header.setAlg(joseHeader.getAlg());
            }
            JsonWebAlgorithm alg = JsonWebAlgorithm.getInstance(recipient.header.getAlg());
            if (alg == null) {
                throw new IllegalArgumentException(recipient.header.getAlg());
            }
            // 密钥协商：RSA加解密/ECDH加解密、PSK密钥协商（预先共享多个密钥，通讯时通过密钥id协商使用的密钥）
            JsonWebKey<?> kek = JsonWebToken.matchJWK(recipient.header, keys);
            byte[] encryptedKey = Base64.getUrlDecoder().decode(recipient.encrypted_key);
            byte[] contentEncryptKey;
            if (alg.support(KeyOperation.DECRYPT)) {
                byte[] cekAAd = aad == null ? null : Base64.getUrlDecoder().decode(aad);
                byte[] authTag = recipient.header.getTag() == null ? null : Base64.getUrlDecoder().decode(recipient.header.getTag());
                contentEncryptKey = alg.decrypt(encryptedKey, kek.isAsymmetric() ? kek.asPrivateKey() : kek.exportKey(), recipient.header.getAlgorithmParameterSpec(), cekAAd, authTag);
            } else if (alg.support(KeyOperation.UNWRAP_KEY)) {
                contentEncryptKey = alg.unwrapKey(kek.exportKey(), encryptedKey).getEncoded();
            } else {
                // alg == JsonWebAlgorithm.DIR
                contentEncryptKey = encryptedKey;
            }
            JsonWebAlgorithm enc = JsonWebAlgorithm.getInstance(joseHeader.getEnc());
            if (enc == null) {
                throw new IllegalArgumentException(joseHeader.getEnc());
            }
            byte[] aad = protectedHeader.getBytes(StandardCharsets.US_ASCII);
            byte[] rawIv = Base64.getUrlDecoder().decode(iv);
            byte[] raw = Base64.getUrlDecoder().decode(ciphertext);
            byte[] authTag = Base64.getUrlDecoder().decode(tag);
            Key cek = new SecretKeySpec(contentEncryptKey, JsonWebAlgorithm.AES_ALGORITHM); // JWE内容只支持AES加解密
            // JWE内容加密目前只有AES/CBC和AES/GCM两种方式
            AlgorithmParameterSpec spec = enc.newAlgorithmParameterSpec(authTag.length, rawIv);
            try {
                data = enc.decrypt(raw, cek, spec, aad, authTag);
                break;
            } catch (GeneralSecurityException ignore) {

            }
        }
        this.frozen = frozen;
        if (data != null && ZIP_ALG.equals(joseHeader.getZip())) {
            return unzip(data);
        }
        return data;
    }

    private byte[] zip(byte[] data) {
        byte[] zipped = new byte[data.length];
        Deflater zip = new Deflater();
        zip.setInput(data);
        int length = zip.deflate(zipped);
        zip.end();
        byte[] raw = new byte[length];
        System.arraycopy(zipped, 0, raw, 0, length);
        return raw;
    }

    private byte[] unzip(byte[] zipped) {
        int approximateLength = 1 << (Integer.SIZE - Integer.numberOfLeadingZeros(zipped.length));
        byte[] buffer = new byte[Math.min(1024, approximateLength) << 2];
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(Math.max(approximateLength, approximateLength << 2)); // 压缩率大概4倍
            Inflater unzip = new Inflater();
            unzip.setInput(zipped);
            int length;
            while ((length = unzip.inflate(buffer)) > 0) {
                baos.write(buffer, 0, length);
            }
            unzip.end();
            return baos.toByteArray();
        } catch (DataFormatException dfe) {
            throw new IllegalArgumentException("not compressed or data corrupt", dfe);
        }
    }

}
