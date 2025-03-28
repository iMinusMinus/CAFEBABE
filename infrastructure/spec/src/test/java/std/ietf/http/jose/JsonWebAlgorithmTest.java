package std.ietf.http.jose;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class JsonWebAlgorithmTest {

    @BeforeAll
    protected static void setUp() {
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            for (Provider.Service service : provider.getServices()) {
                System.out.println(service.getType() +"\t\t\t\t" + service.getAlgorithm() + "\t\t\t\t" + provider.getName());
            }
        }
    }

    @Test
    public void testHS256() throws GeneralSecurityException {
        SecretKeySpec s = new SecretKeySpec("talk is cheap, show me the code".getBytes(StandardCharsets.UTF_8), JsonWebAlgorithm.AES_ALGORITHM);
        byte[] h = JsonWebAlgorithm.HS256.sign("Hello World".getBytes(StandardCharsets.UTF_8), s);
        Assertions.assertEquals("pkC9J+GjD7XFy5Lbt6rrkVt/fELD0vBHD3SDE2cl+8U=", Base64.getEncoder().encodeToString(h));

        byte[] concatenation  = {101, 121, 74, 104, 98, 71, 99, 105, 79, 105, 74, 66, 77, 84, 73, 52,
                83, 49, 99, 105, 76, 67, 74, 108, 98, 109, 77, 105, 79, 105, 74, 66,
                77, 84, 73, 52, 81, 48, 74, 68, 76, 85, 104, 84, 77, 106, 85, 50, 73,
                110, 48, 3, 22, 60, 12, 43, 67, 104, 105, 108, 108, 105, 99, 111,
                116, 104, 101, 40, 57, 83, (byte) 181, 119, 33, (byte) 133, (byte) 148, (byte) 198, (byte) 185, (byte) 243, 24,
                (byte) 152, (byte) 230, 6, 75, (byte) 129, (byte) 223, 127, 19, (byte) 210, 82, (byte) 183, (byte) 230, (byte) 168, 33, (byte) 215,
                104, (byte) 143, 112, 56, 102, 0, 0, 0, 0, 0, 0, 1, (byte) 152};
        byte[] macKey= {4, (byte) 211, 31, (byte) 197, 84, (byte) 157, (byte) 252, (byte) 254, 11, 100, (byte) 157, (byte) 250, 63, (byte) 170, 106, (byte) 206};
        SecretKeySpec key = new SecretKeySpec(macKey, JsonWebAlgorithm.AES_ALGORITHM);
        byte[] m = JsonWebAlgorithm.HS256.sign(concatenation, key);
        byte[] t = Arrays.copyOf(m, macKey.length);
        byte[] expectT = {83, 73, (byte) 191, 98, 104, (byte) 205, (byte) 211, (byte) 128, (byte) 201, (byte) 189, (byte) 199, (byte) 133, 32, 38, (byte) 194, 85};
        Assertions.assertArrayEquals(expectT, t);
    }

    @Test
    public void testHS512() throws Exception {
        SecretKey sk = JsonWebAlgorithm.HS512.generateKey();
        System.out.println(sk);
        Assertions.assertTrue(sk.getEncoded().length == 512 / 8);
    }

    private boolean bcAvailable() {
        return Security.getProvider("BC") != null;
    }

    @Test
    @EnabledIf(value = "bcAvailable", disabledReason = "JCA built in provider does not support SHA256withRSAandMGF1, add BouncyCastle to enable this test case")
    public void testPS384() throws Exception {
        String pk = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC2CEAmvaY6wlL/\n" +
                "7vgXfyI0iuTQWXWzPsNZatUe0RT81iHGMIyqA0HLU1Wj9tqDgF26OuLK675UTBWw\n" +
                "Hl/tjLcz0pvsWbRksDS52YljuuZ4biB6IslCgenvFPTv0FgyOU43/kv29BoCjaqF\n" +
                "WmWVLOVR+vr/o7E+mZX1O3Us0z5rKZW/Th67ZlypRe7q4hE2YH7A7qRR67qufowk\n" +
                "+IGlS2q89HfqNXM8lJxq4vn2+fBOUmNhfx/JTenj4kt7GInXLE08UrG1l0ul7ayl\n" +
                "7qE6QDIDNiDlB+5317HTQs54Gor7di/6MXo+jmFlIVGEKZaKFRSD3V2D5m4Zo5sr\n" +
                "WiMvJ3W/AgMBAAECggEAFWZN6GAGQUUkDctxpg1/n26urKG/uQVJYOMyz88jqP+h\n" +
                "c9O0Ta5ZV0ZiPIbiNHBsVF7k15c0RmO1bwF7JImU5gY952C6WNXwNOMSiENqIcFM\n" +
                "lx8syuFL95vemazEGY9sFyARnSQgRKmT4N56L7nZ8bPQOypqxWG3498MDOt+nIlR\n" +
                "D7sfYwA2EvdusTKYC/o02N2+RiShumRzDebP44Ee0ZW30vwNjAXaRFGme7/8lght\n" +
                "GwG69fi0jKDXyChADXnOG+0Ur2pCMTZVHmUGiFMD4tRl39kZln8IqM5q9mqA8emN\n" +
                "w4TDR7cKm0bbECaETrJ9Gd243zT5uHTl1V26FXvzDQKBgQD+C0r7z/YhHEYZEkla\n" +
                "pW6VHehgKZfKzJN1VbGx6exdMDir1Eq/IZoJPdIBCJN8jkHzrRI41jGlG/T0krkG\n" +
                "ei54BXTuy16j+RO9JXbiJ4UvzNnoKkK0k5Oahdxj5NP/GUd1L6jnvhFz24N/VVty\n" +
                "t5NXAXlOepoPbzykKtSj3+ymSwKBgQC3bwa0VcM7ZTWbxuj7dfB5HCsYFdLJY0h1\n" +
                "rbzxDGVWueNoP1J4sUC1S3jY/IoYCsrFSLU0tiY8dopTM+5VeAKU4Oac3qj59rK/\n" +
                "4SeN/Bnh3uV+WWpI+UL5BVxSqDn9uSFVS7yyL1YOzd3fPbA8mk89zg4svnE0Zxin\n" +
                "rfuWws9V3QKBgGe5KKZjpHSkXKKD6ttNzDap/WS11roAiPxRProLbmwl8h2GEfaz\n" +
                "u3amfQXbAIzMcHUTZus52Q82uO7asRzJ6gsq/zE4a627VqnVkBKKXEDF/PpjttSP\n" +
                "pKZknTVUCpKPObPI/8eOvQV2zNvCeBIAjfHqMiMuv5QpsLK+og8+sBq9AoGBAIzg\n" +
                "81g66ddLEtISAUtnOAn7zUkRMbND0hm3Fn5W09m4qGOfBlr1X0odrbGQuwpd939v\n" +
                "KORT5KfZLsFaB/iSVAE9fUALbeGUS5I8fSF59fLYOo9AYvWqonoYWOCOnQM1VNlq\n" +
                "coCzBDgYegeDHEBfDu7bu1034j+p0nCtmaUe9xftAoGAEzaV5+T4mJdxmsL1tsLY\n" +
                "LznMFsS5PekQ7tiEhL7AAf0VWNRWUlLLQHUWCInE97x8bwwxK3TQkDUsep21wHmN\n" +
                "6PuDudozLTRC7zhzMr6CdMHhfe8vW2DIrnIH5kXmPdgP8jrlEw7CMKZEoB9XDrQe\n" +
                "bB5DfsqBBmJLAC05WPTh7E8=\n";
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getMimeDecoder().decode(pk));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        String sign = "UH0ujwYgW3gQcEAp3lOBclPbFymXbcO5eE4rAzZoXIzCxzdfF4vRqg-5_nu5iKpxsxf33c5mGg8IMhbRhgLwVT3IYBm0WuZC7GB67WPJFQswwxwfs5C8xpGf4fFAo1DT5N-wgquebeoIniqz2LlQUdbZ02hmP3rfE7-6IVm4_b9rrOxxUkbEqixxOuvITJC29k0A8jFNRLoMgO1TY4aWOaOK1KBgJ5wJq6WWVTjutg2e1u-2sMimrgfSH6oYUsCf-Ki0geyFrRV1BGhXqnwxD0chqbAK9e-HcLSsicbl4BbPjROvlaaRtrQ2Gf1ScehAyICsFRt0XOjSysmd4jze0Q";
        byte[] signature = JsonWebAlgorithm.PS384.sign("eyJhbGciOiJQUzM4NCIsInR5cCI6IkpXVCJ9.eyJpc3MiOiIzcmQgd29ybGQifQ".getBytes(StandardCharsets.UTF_8), privateKey);
        Assertions.assertEquals(sign, JsonWebToken.ENCODER.encodeToString(signature));
    }

//
//    @Test
//    public void testA256GCMKW() {
//
//    }

    @Test
    public void testEcdhesKeyAgreement() throws Exception {
        JsonWebKeySet.EllipticCurveJwk aliceKey = new JsonWebKeySet.EllipticCurveJwk();
        aliceKey.setCrv("P-256");
        aliceKey.setX("gI0GAILBdu7T53akrFmMyGcsF3n5dO7MmwNBHKW5SV0");
        aliceKey.setY("SLW_xSffzlPWrHEVI30DHM_4egVwt3NQqeUD7nMFpps");
        aliceKey.setD("0_NxaRPUMQoAJt50Gz8YiTr8gRTwyEaCumd-MToTmIo");

        JsonWebKeySet.EllipticCurveJwk bobKey = new JsonWebKeySet.EllipticCurveJwk();
        bobKey.setCrv("P-256");
        bobKey.setX("weNJy2HscCSM6AEDTDg04biOvhFhyyWvOHQfeF_PxMQ");
        bobKey.setY("e8lnCO-AlStT-NJVX-crhB7QRYhiix03illJOVAOyck");
        bobKey.setD("VEmDZpDXXK8p8N0Cndsxs924q6nS1RXFASRl6BfUqdw");

        Header joseHeader = new Header();
        joseHeader.setAlg("ECDH-ES");
        joseHeader.setEnc("A128GCM");
        joseHeader.setApu("QWxpY2U"); // Alice
        joseHeader.setApv("Qm9i"); // Bob
        joseHeader.setEpk(aliceKey);

        byte[] sk = JsonWebAlgorithm.ECDH_ES.deriveBits(aliceKey.asPublicKey(), bobKey.asPrivateKey());
        byte[] z = {(byte) 158, 86, (byte) 217, 29, (byte) 129, 113, 53, (byte) 211, 114, (byte) 131, 66, (byte) 131, (byte) 191, (byte) 132,
                38, (byte) 156, (byte) 251, 49, 110, (byte) 163, (byte) 218, (byte) 128, 106, 72, (byte) 246, (byte) 218, (byte) 167, 121,
                (byte) 140, (byte) 254, (byte) 144, (byte) 196};
        Assertions.assertArrayEquals(z, sk);
        SecretKey derived = JsonWebAlgorithm.ECDH_ES.deriveKey(new SecretKeySpec(sk, JsonWebAlgorithm.AES_ALGORITHM), JsonWebAlgorithm.A128GCM, Base64.getUrlDecoder().decode(joseHeader.getApu()), Base64.getUrlDecoder().decode(joseHeader.getApv()));
        Assertions.assertEquals("VqqN6vgjbSBcIijNcacQGg", JsonWebToken.ENCODER.encodeToString(derived.getEncoded()));
    }

}
