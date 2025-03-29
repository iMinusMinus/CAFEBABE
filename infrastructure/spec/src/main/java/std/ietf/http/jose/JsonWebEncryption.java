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
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc7516">JSON Web Encryption (JWE)</a>用于加密和解密token，可包含敏感信息
 * @author iMinusMinus
 * @date 2025-03-23
 */
@NoArgsConstructor
@ToString
public class JsonWebEncryption {

    private static final String ZIP_ALG = "DEF";

    private transient boolean frozen;

    private String protectedHeader; // base64url
    /**
     * 共享的、不计入校验的JOSE头
     */
    @Getter private Header unprotected; // unencoded JSON object
    /**
     * 加密明文用的初始向量
     */
    @Getter private String iv; // base64url
    /**
     * JSON格式，当protected header不存在时，需提供附加认证数据
     */
    @Getter private String aad; // base64url
    /**
     * 加密明文后的密文（可能包含AAD）
     */
    @Getter private String ciphertext; // base64url
    @Getter private String tag; // base64url

    @Getter private Recipient[] recipients;
    // Flattened
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

    protected boolean supportCompressionAlgorithm(String name) {
        return ZIP_ALG.equals(name);
    }

    protected boolean supportCriticalParams(String param) {
        return Header.PARAM_ZIP.equals(param);
    }

    public <T extends Header> JsonWebEncryption(T headerParams, Object body, Function<Object, byte[]> jsonb, JsonWebKey<?>... keys) throws GeneralSecurityException {
        Header joseHeader = new Header();
        JsonWebAlgorithm.JsonWebEncryptionAlgorithm enc = JsonWebAlgorithm.getJweAlgorithm(headerParams.getEnc());
        if (enc == null || !enc.support(KeyOperation.ENCRYPT)) {
            throw new IllegalArgumentException("JWE enc should support encryption, but got " + headerParams.getEnc());
        }
        SecureRandom sr = new SecureRandom();
        // Key Management Mode: Key Encryption, Key Wrapping, Direct Key Agreement, Key Agreement with Key Wrapping, and Direct Encryption
        Key cek;
        ECPublicKey myPublicKey = null;
        List<JsonWebKey<?>> agreementKeys = new ArrayList<>();
        for (JsonWebKey<?> k : keys) {
            JsonWebAlgorithm.JsonWebEncryptionAlgorithm cekAlg = JsonWebAlgorithm.getJweAlgorithm(k.getAlg());
            if (cekAlg == null) {
                throw new IllegalArgumentException("no such JWE alg");
            }
            if (cekAlg == JsonWebAlgorithm.ECDH_ES) {
                agreementKeys.add(k);
            }
        }
        if (agreementKeys.size() > 1) { // cek仅一个
            throw new IllegalArgumentException("multiply key agreement found");
        }
        byte[] apv = headerParams.getApv() == null ? null : Base64.getUrlDecoder().decode(headerParams.getApv());
        if (!agreementKeys.isEmpty()) { // Direct Key Agreement(如ECDH-ES) or Key Agreement with Key Wrapping（如ECDH-ES+A28KW）: compute the value of the agreed upon key
            assert headerParams.getApv() != null;
            KeyPair keyPair = JsonWebAlgorithm.ECDH_ES.generateKeyPair();
            myPublicKey = (ECPublicKey) keyPair.getPublic();
            headerParams.setEpk(new JsonWebKeySet.EllipticCurveJwk(myPublicKey, null));
            byte[] apu = Optional.ofNullable(headerParams.getApu()).map(x -> Base64.getUrlDecoder().decode(x)).orElse(agreementKeys.get(0).kid.getBytes(StandardCharsets.UTF_8));
            apv = Base64.getUrlDecoder().decode(headerParams.getApv());
            Key derived = new SecretKeySpec(JsonWebAlgorithm.ECDH_ES.deriveBits(agreementKeys.get(0).asPublicKey(), keyPair.getPrivate()), JsonWebAlgorithm.AES_ALGORITHM);
            cek = JsonWebAlgorithm.ECDH_ES.deriveKey(derived, enc, apu, apv);
        } else { // Key Wrapping, Key Encryption, or Key Agreement with Key Wrapping： Generate a random Content Encryption Key (CEK)
            byte[] rawCek = new byte[enc.support(KeyOperation.MAC) ? enc.getKeySize() * 2 / 8 : enc.getKeySize() / 8]; // 如果需要拆成mac_key和enc_key，则此处不只是密钥
            sr.nextBytes(rawCek);
            cek = new SecretKeySpec(rawCek, JsonWebAlgorithm.AES_ALGORITHM);
        }

        Recipient[] recipients = new Recipient[keys.length];
        for (int i = 0; i < keys.length; i++) {
            JsonWebAlgorithm.JsonWebEncryptionAlgorithm cekAlg = JsonWebAlgorithm.getJweAlgorithm(keys[i].getAlg());
            recipients[i] = new Recipient();
            recipients[i].header = new Header();
            recipients[i].header.setKid(keys[i].getKid());
            recipients[i].header.setAlg(keys[i].getAlg());
            byte[] resolved; // 密钥长度需要和加密算法一致
            byte[] apu = null;
            Key kek = keys[i].isAsymmetric() ? keys[i].asPublicKey() : keys[i].exportKey();
            if (cekAlg.support(KeyOperation.DERIVE_KEY)) {
                if (cekAlg instanceof JsonWebAlgorithm.EcFamily) {
                    apu = Optional.ofNullable(headerParams.getApu()).map(x -> Base64.getUrlDecoder().decode(x))
                            .orElse(keys[i].getKid().getBytes(StandardCharsets.UTF_8));
                    kek = ((JsonWebAlgorithm.EcFamily) cekAlg).deriveKey(cek, enc, apu, apv);
                } else {
                    kek = ((JsonWebAlgorithm.Pbkdf2Family) cekAlg).deriveKey(Base64.getUrlDecoder().decode(headerParams.getP2s()), headerParams.getP2c());
                }
            }
            if (cekAlg == JsonWebAlgorithm.DIR || cekAlg == JsonWebAlgorithm.ECDH_ES) { // When Direct Encryption is employed, let the CEK be the shared symmetric key; When Direct Key Agreement is employed, let the CEK be the agreed upon key
                resolved = null; // When Direct Key Agreement or Direct Encryption are employed, let the JWE Encrypted Key be the empty octet sequence
            } else if (cekAlg.support(KeyOperation.ENCRYPT)) { // Key Wrapping, Key Encryption, or Key Agreement with Key Wrapping are employed, encrypt the CEK to the recipient and let the result be the JWE Encrypted Key
                byte[] cekAuthTag = jsonb.apply(recipients[i].header);
                recipients[i].header.setTag(JsonWebToken.ENCODER.encodeToString(cekAuthTag));
                AlgorithmParameterSpec parameterSpec = cekAlg.newRandomParameter();
                recipients[i].header.setAlgorithmParameterSpec(parameterSpec);
                resolved = cekAlg.encrypt(cek.getEncoded(), kek, parameterSpec, cekAuthTag); // produce the JWE Encrypted Key
            } else if (cekAlg.support(KeyOperation.WRAP_KEY)) { // Key Agreement with Key Wrapping, wrap the CEK with the agreed upon key
                resolved = cekAlg.wrapKey(kek, cek);
            } else {
                throw new IllegalArgumentException();
            }
            if (myPublicKey != null) { // ECDH*
                recipients[i].header.setEpk(new JsonWebKeySet.EllipticCurveJwk(myPublicKey, null));
                recipients[i].header.setApu(apu != null ? JsonWebToken.ENCODER.encodeToString(apu) : null);
                recipients[i].header.setApv(headerParams.getApv());
            }
            recipients[i].encrypted_key = resolved == null ? null : JsonWebToken.ENCODER.encodeToString(resolved);
        }
        if (recipients.length == 1) {
            this.encrypted_key = recipients[0].encrypted_key;
            joseHeader.put(Header.PARAM_ALG, headerParams.remove(Header.PARAM_ALG));
            joseHeader.put(Header.PARAM_ENC, headerParams.remove(Header.PARAM_ENC));
            joseHeader.put(Header.PARAM_EPK, headerParams.remove(Header.PARAM_EPK));
            joseHeader.put(Header.PARAM_APU, headerParams.remove(Header.PARAM_APU));
            joseHeader.put(Header.PARAM_APV, headerParams.remove(Header.PARAM_APV));
            joseHeader.put(Header.PARAM_P2S, headerParams.remove(Header.PARAM_P2S));
            joseHeader.put(Header.PARAM_P2C, headerParams.remove(Header.PARAM_P2C));
            if (headerParams.getCrit() != null && headerParams.getCrit().length > 0) {
                for (String param : headerParams.getCrit()) {
                    if (!supportCriticalParams(param)) {
                        throw new UnsupportedOperationException();
                    }
                    joseHeader.put(param, headerParams.remove(param));
                }
            }
        } else {
            this.recipients = recipients;
        }
        // Generate a random JWE Initialization Vector
        byte[] iv = new byte[16]; // AES向量长度固定为128 bit
        sr.nextBytes(iv);
        this.iv = JsonWebToken.ENCODER.encodeToString(iv);
        // (compress) plaintext
        byte[] payload = body instanceof String ? ((String) body).getBytes(StandardCharsets.UTF_8) : jsonb.apply(body);
        if (supportCompressionAlgorithm(joseHeader.getZip())) {
            payload = compress(joseHeader.getZip(), payload);
        }
        this.unprotected = headerParams;
        this.unprotected.setTyp("jwt");
        this.protectedHeader = JsonWebToken.ENCODER.encodeToString(jsonb.apply(joseHeader)); // may be empty
        byte[] aad = this.protectedHeader.getBytes(StandardCharsets.US_ASCII);
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
        assert unprotected == null || unprotected.getEnc() == null; // 不受保护的JOSE头不能包含受保护JOSE头内容，包括crit指定的字段不能同时出现
        Recipient[] recipients = this.recipients;
        if (this.recipients == null || this.recipients.length == 0) {
            recipients = new Recipient[1];
            recipients[0] = new Recipient();
            recipients[0].encrypted_key = encrypted_key;
            recipients[0].header = Optional.ofNullable(unprotected).orElse(joseHeader);
        }
        JsonWebAlgorithm.JsonWebEncryptionAlgorithm enc = JsonWebAlgorithm.getJweAlgorithm(joseHeader.getEnc());
        if (enc == null) {
            throw new IllegalArgumentException(joseHeader.getEnc());
        }

        byte[] data = null;
        for (Recipient recipient : recipients) {
            if (recipient.header.getCrit() != null && recipient.header.getCrit().length != 0) {
                for (String param : recipient.getHeader().getCrit()) {
                    if (!supportCriticalParams(param)) {
                        throw new UnsupportedOperationException("supported crit: " + Header.PARAM_ZIP);
                    }
                }
            }
            if (recipient.header.getAlg() == null) {
                recipient.header.setAlg(joseHeader.getAlg());
            }
            JsonWebAlgorithm alg = JsonWebAlgorithm.getJweAlgorithm(recipient.header.getAlg());
            if (alg == null) {
                throw new IllegalArgumentException(recipient.header.getAlg());
            }

            // 密钥协商：非对称密钥加解密、对称密钥封装、直接密钥协商、协商对称密钥用于加密和封装、共享密钥
            JsonWebKey<?> kek = JsonWebToken.matchJWK(recipient.header, keys);
            byte[] encryptedKey = recipient.encrypted_key != null ? Base64.getUrlDecoder().decode(recipient.encrypted_key) : null;
            Key cek;
            Key agreedUponKey = null;
            if (alg instanceof JsonWebAlgorithm.EcFamily) {
                if (recipient.header.getEpk() == null || recipient.header.getApu() == null || recipient.header.getApv() == null) {
                    throw new IllegalArgumentException("epk, apu, apv must not null when alg is ECDH");
                }
                byte[] sharedSecret = ((JsonWebAlgorithm.EcFamily) alg).deriveBits(recipient.header.getEpk().asPublicKey(), kek.asPrivateKey());
                Key derived = new SecretKeySpec(sharedSecret, JsonWebAlgorithm.AES_ALGORITHM);
                agreedUponKey = ((JsonWebAlgorithm.EcFamily) alg).deriveKey(derived, enc, Base64.getUrlDecoder().decode(recipient.header.getApu()), Base64.getUrlDecoder().decode(recipient.header.getApv()));
            }
            Key keyEncryptionKey = kek.isAsymmetric() ? kek.asPrivateKey() : kek.exportKey();
            if (alg == JsonWebAlgorithm.DIR) { // Direct Encryption
                assert recipient.encrypted_key == null || recipient.encrypted_key.length() == 0;
                assert !kek.isAsymmetric();
                cek = keyEncryptionKey;
            } else if (alg == JsonWebAlgorithm.ECDH_ES) { // Direct Key Agreement
                assert recipient.encrypted_key == null || recipient.encrypted_key.length() == 0;
                cek = agreedUponKey; // When Direct Key Agreement is employed, let the CEK be the agreed upon key
            } else if (alg.support(KeyOperation.DECRYPT)) { // Key Wrapping, Key Encryption, or Key Agreement with Key Wrapping
                byte[] cekAAd = aad == null ? null : Base64.getUrlDecoder().decode(aad);
                byte[] authTag = recipient.header.getTag() == null ? null : Base64.getUrlDecoder().decode(recipient.header.getTag());
                byte[] contentEncryptKey = alg.decrypt(encryptedKey, keyEncryptionKey, recipient.header.getAlgorithmParameterSpec(), cekAAd, authTag);
                cek = new SecretKeySpec(contentEncryptKey, JsonWebAlgorithm.AES_ALGORITHM); // JWE内容只支持AES加解密
            } else if (alg.support(KeyOperation.UNWRAP_KEY)) { // Direct Key Agreement or Key Agreement with Key Wrapping
                cek = alg.unwrapKey(agreedUponKey != null ? agreedUponKey : keyEncryptionKey, encryptedKey); // When Key Agreement with Key Wrapping is employed, the agreed upon key will be used to decrypt the JWE Encrypted Key
            } else {
                throw new UnsupportedOperationException();
            }

            byte[] aad = protectedHeader.getBytes(StandardCharsets.US_ASCII);
            byte[] rawIv = Base64.getUrlDecoder().decode(iv);
            byte[] raw = Base64.getUrlDecoder().decode(ciphertext);
            byte[] authTag = Base64.getUrlDecoder().decode(tag);
            // JWE内容加密目前只有AES/CBC和AES/GCM两种方式
            AlgorithmParameterSpec spec = enc.newAlgorithmParameterSpec(authTag.length, rawIv);
            try {
                data = enc.decrypt(raw, cek, spec, aad, authTag);
                break;
            } catch (GeneralSecurityException ignore) {

            }
        }
        this.frozen = frozen;
        if (data != null && supportCompressionAlgorithm(joseHeader.getZip())) {
            return decompress(joseHeader.getZip(), data);
        }
        return data;
    }

    protected byte[] compress(String name, byte[] data) {
        return zip(data);
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

    protected byte[] decompress(String name, byte[] zipped) {
        return unzip(zipped);
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
