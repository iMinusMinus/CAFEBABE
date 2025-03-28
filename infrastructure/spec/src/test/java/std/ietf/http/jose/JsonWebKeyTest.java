package std.ietf.http.jose;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Base64;

public class JsonWebKeyTest {

    public static JsonWebKeySet.RsaJwk newRsaPublicKey() {
        JsonWebKeySet.RsaJwk publicKey = new JsonWebKeySet.RsaJwk();
        publicKey.setN("vrjOfz9Ccdgx5nQudyhdoR17V-IubWMeOZCwX_jj0hgAsz2J_pqYW08PLbK_PdiVGKPrqzmDIsLI7sA25VEnHU1uCLNwBuUiCO11_-7dYbsr4iJmG0Qu2j8DsVyT1azpJC_NG84Ty5KKthuCaPod7iI7w0LK9orSMhBEwwZDCxTWq4aYWAchc8t-emd9qOvWtVMDC2BXksRngh6X5bUYLy6AyHKvj-nUy1wgzjYQDwHMTplCoLtU-o-8SNnZ1tmRoGE9uJkBLdh5gFENabWnU5m1ZqZPdwS-qo-meMvVfJb6jJVWRpl2SUtCnYG2C32qvbWbjZ_jBPD5eunqsIo1vQ");
        publicKey.setE("AQAB");
        String[] x5c = {"MIIDQjCCAiqgAwIBAgIGATz/FuLiMA0GCSqGSIb3DQEBBQUAMGIxCzAJBgNVBAYTAlVTMQswCQYDVQQIEwJDTzEPMA0GA1UEBxMGRGVudmVyMRwwGgYDVQQKExNQaW5nIElkZW50aXR5IENvcnAuMRcwFQYDVQQDEw5CcmlhbiBDYW1wYmVsbDAeFw0xMzAyMjEyMzI5MTVaFw0xODA4MTQyMjI5MTVaMGIxCzAJBgNVBAYTAlVTMQswCQYDVQQIEwJDTzEPMA0GA1UEBxMGRGVudmVyMRwwGgYDVQQKExNQaW5nIElkZW50aXR5IENvcnAuMRcwFQYDVQQDEw5CcmlhbiBDYW1wYmVsbDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAL64zn8/QnHYMeZ0LncoXaEde1fiLm1jHjmQsF/449IYALM9if6amFtPDy2yvz3YlRij66s5gyLCyO7ANuVRJx1NbgizcAblIgjtdf/u3WG7K+IiZhtELto/A7Fck9Ws6SQvzRvOE8uSirYbgmj6He4iO8NCyvaK0jIQRMMGQwsU1quGmFgHIXPLfnpnfajr1rVTAwtgV5LEZ4Iel+W1GC8ugMhyr4/p1MtcIM42EA8BzE6ZQqC7VPqPvEjZ2dbZkaBhPbiZAS3YeYBRDWm1p1OZtWamT3cEvqqPpnjL1XyW+oyVVkaZdklLQp2Btgt9qr21m42f4wTw+Xrp6rCKNb0CAwEAATANBgkqhkiG9w0BAQUFAAOCAQEAh8zGlfSlcI0o3rYDPBB07aXNswb4ECNIKG0CETTUxmXl9KUL+9gGlqCz5iWLOgWsnrcKcY0vXPG9J1r9AqBNTqNgHq2G03X09266X5CpOe1zFo+Owb1zxtp3PehFdfQJ610CDLEaS9V9Rqp17hCyybEpOGVwe8fnk+fbEL2Bo3UPGrpsHzUoaGpDftmWssZkhpBJKVMJyf/RuP2SmmaIzmnw9JiSlYhzo4tpzd5rFXhjRbg4zW9C+2qok+2+qDM1iJ684gPHMIY8aLWrdgQTxkumGmTqgawR+N5MDtdPTEQ0XfIBc2cJEUyMTY5MPvACWpkA6SdS4xSvdXK3IVfOWA=="};
        publicKey.setX5c(x5c);
        return publicKey;
    }

    public static JsonWebKeySet.RsaJwk newRsaPrivateKey() {
        JsonWebKeySet.RsaJwk privateKey = new JsonWebKeySet.RsaJwk();
        privateKey.setN("0vx7agoebGcQSuuPiLJXZptN9nndrQmbXEps2aiAFbWhM78LhWx4cbbfAAtVT86zwu1RK7aPFFxuhDR1L6tSoc_BJECPebWKRXjBZCiFV4n3oknjhMstn64tZ_2W-5JsGY4Hc5n9yBXArwl93lqt7_RN5w6Cf0h4QyQ5v-65YGjQR0_FDW2QvzqY368QQMicAtaSqzs8KJZgnYb9c7d0zgdAZHzu6qMQvRL5hajrn1n91CbOpbISD08qNLyrdkt-bFTWhAI4vMQFh6WeZu0fM4lFd2NcRwr3XPksINHaQ-G_xBniIqbw0Ls1jF44-csFCur-kEgU8awapJzKnqDKgw");
        privateKey.setE("AQAB");
        privateKey.setD("X4cTteJY_gn4FYPsXB8rdXix5vwsg1FLN5E3EaG6RJoVH-HLLKD9M7dx5oo7GURknchnrRweUkC7hT5fJLM0WbFAKNLWY2vv7B6NqXSzUvxT0_YSfqijwp3RTzlBaCxWp4doFk5N2o8Gy_nHNKroADIkJ46pRUohsXywbReAdYaMwFs9tv8d_cPVY3i07a3t8MN6TNwm0dSawm9v47UiCl3Sk5ZiG7xojPLu4sbg1U2jx4IBTNBznbJSzFHK66jT8bgkuqsk0GjskDJk19Z4qwjwbsnn4j2WBii3RL-Us2lGVkY8fkFzme1z0HbIkfz0Y6mqnOYtqc0X4jfcKoAC8Q");
        privateKey.setP("83i-7IvMGXoMXCskv73TKr8637FiO7Z27zv8oj6pbWUQyLPQBQxtPVnwD20R-60eTDmD2ujnMt5PoqMrm8RfmNhVWDtjjMmCMjOpSXicFHj7XOuVIYQyqVWlWEh6dN36GVZYk93N8Bc9vY41xy8B9RzzOGVQzXvNEvn7O0nVbfs");
        privateKey.setQ("3dfOR9cuYq-0S-mkFLzgItgMEfFzB2q3hWehMuG0oCuqnb3vobLyumqjVZQO1dIrdwgTnCdpYzBcOfW5r370AFXjiWft_NGEiovonizhKpo9VVS78TzFgxkIdrecRezsZ-1kYd_s1qDbxtkDEgfAITAG9LUnADun4vIcb6yelxk");
        privateKey.setDp("G4sPXkc6Ya9y8oJW9_ILj4xuppu0lzi_H7VTkS8xj5SdX3coE0oimYwxIi2emTAue0UOa5dpgFGyBJ4c8tQ2VF402XRugKDTP8akYhFo5tAA77Qe_NmtuYZc3C3m3I24G2GvR5sSDxUyAN2zq8Lfn9EUms6rY3Ob8YeiKkTiBj0");
        privateKey.setDq("s9lAH9fggBsoFR8Oac2R_E2gw282rT2kGOAhvIllETE1efrA6huUUvMfBcMpn8lqeW6vzznYY5SSQF7pMdC_agI3nG8Ibp1BUb0JUiraRNqUfLhcQb_d9GF4Dh7e74WbRsobRonujTYN1xCaP6TO61jvWrX-L18txXw494Q_cgk");
        privateKey.setQi("GyM_p6JrXySiz1toFgKbWV-JdI3jQ4ypu9rbMWx3rQJBfmt0FoYzgUIZEVFEcOqwemRN81zoDAaa-Bk0KWNGDjJHZDdDmFhW3AN7lI-puxk_mHZGJ11rxyR8O55XLSe3SPmRfKwZI6yU24ZxvQKFYItdldUKGzO6Ia6zTKhAVRU");
        return privateKey;
    }

    @Test
    public void testRsaKey() {
        JsonWebKeySet.RsaJwk rsaJwk = newRsaPublicKey();
        RSAPublicKey publicKey = rsaJwk.getPublicKey();
        Assertions.assertNotNull(publicKey);
        JsonWebKeySet.RsaJwk jwk = new JsonWebKeySet.RsaJwk();
        jwk.setPublicKey(publicKey);
        Assertions.assertNotNull(jwk.getN());
        Assertions.assertNotNull(jwk.getE());
        jwk.withCertificates(rsaJwk.asCertificates());
        Assertions.assertNotNull(jwk.getX5c());
        System.out.println(jwk);

        RSAPrivateKey privateKey = newRsaPrivateKey().getPrivateKey();
        Assertions.assertNotNull(privateKey);
        rsaJwk = new JsonWebKeySet.RsaJwk();
        rsaJwk.setPrivateKey(privateKey);
        Assertions.assertNotNull(rsaJwk.getD());
        Assertions.assertNotNull(rsaJwk.getP());
        Assertions.assertNotNull(rsaJwk.getQ());
        Assertions.assertNotNull(rsaJwk.getDp());
        Assertions.assertNotNull(rsaJwk.getDq());
        Assertions.assertNotNull(rsaJwk.getQi());
        System.out.println(rsaJwk);
    }

    public static JsonWebKeySet.EllipticCurveJwk newPublicEc() {
        JsonWebKeySet.EllipticCurveJwk ec = new JsonWebKeySet.EllipticCurveJwk();
        ec.setCrv("P-256");
        ec.setX("f83OJ3D2xF1Bg8vub9tLe1gHMzV76e8Tus9uPHvRVEU");
        ec.setY("x_FEzRu9m36HLN_tue659LNpXW6pCyStikYjKIWI5a0");
        return ec;
    }

    public static JsonWebKeySet.EllipticCurveJwk newPrivateEc() {
        JsonWebKeySet.EllipticCurveJwk ec = new JsonWebKeySet.EllipticCurveJwk();
        ec.setCrv("P-256");
        ec.setX("MKBCTNIcKUSDii11ySs3526iDZ8AiTo7Tu6KPAqv7D4");
        ec.setY("4Etl6SRW2YiLUrN5vfvVHuhp7x8PxltmWWlbbM4IFyM");
        ec.setD("870MB6gfuTJ4HtUnUvYMyJpr5eUZNP4Bk43bVdj3eAE");
        return ec;
    }

    @Test
    public void testEC() {
        JsonWebKeySet.EllipticCurveJwk ecJwk = newPublicEc();
        Assertions.assertNotNull(ecJwk.exportKey());
        JsonWebKeySet.EllipticCurveJwk jwk = new JsonWebKeySet.EllipticCurveJwk();
        jwk.setPublicKey((ECPublicKey) ecJwk.exportKey());
        Assertions.assertNotNull(jwk.getX());
        Assertions.assertNotNull(jwk.getY());
        System.out.println(jwk);

        ecJwk = newPrivateEc();
        Assertions.assertTrue(ecJwk.exportKey() instanceof ECPrivateKey);
        jwk = new JsonWebKeySet.EllipticCurveJwk();
        jwk.setPrivateKey((ECPrivateKey) ecJwk.exportKey());
        Assertions.assertNotNull(jwk.getD());
        System.out.println(jwk);
    }

    public static JsonWebKeySet.SymmetricKey newKey() {
        JsonWebKeySet.SymmetricKey aesKey = new JsonWebKeySet.SymmetricKey();
        aesKey.setK("GawgguFyGrWKav7AX4VKUg");
        aesKey.setAlg("A128KW");
        return aesKey;
    }

    @Test
    public void testAes() {
        JsonWebKeySet.SymmetricKey jwk = newKey();
        Assertions.assertNotNull(jwk.exportKey());

        JsonWebKeySet.SymmetricKey symmetricJwk = new JsonWebKeySet.SymmetricKey();
        symmetricJwk.importKey(jwk.exportKey());
        Assertions.assertNotNull(symmetricJwk.getK());
        System.out.println(symmetricJwk);
    }

    @Test
    public void testX5c() {
        JsonWebKeySet.RsaJwk rsaJwk = new JsonWebKeySet.RsaJwk();
        rsaJwk.setUse("sig");
        rsaJwk.setKid("1b94c");
        rsaJwk.setN("vrjOfz9Ccdgx5nQudyhdoR17V-IubWMeOZCwX_jj0hgAsz2J_pqYW08PLbK_PdiVGKPrqzmDIsLI7sA25VEnHU1uCLNwBuUiCO11_-7dYbsr4iJmG0Qu2j8DsVyT1azpJC_NG84Ty5KKthuCaPod7iI7w0LK9orSMhBEwwZDCxTWq4aYWAchc8t-emd9qOvWtVMDC2BXksRngh6X5bUYLy6AyHKvj-nUy1wgzjYQDwHMTplCoLtU-o-8SNnZ1tmRoGE9uJkBLdh5gFENabWnU5m1ZqZPdwS-qo-meMvVfJb6jJVWRpl2SUtCnYG2C32qvbWbjZ_jBPD5eunqsIo1vQ");
        rsaJwk.setE("AQAB");
        String[] x5c = {"MIIDQjCCAiqgAwIBAgIGATz/FuLiMA0GCSqGSIb3DQEBBQUAMGIxCzAJB" +
                "gNVBAYTAlVTMQswCQYDVQQIEwJDTzEPMA0GA1UEBxMGRGVudmVyMRwwGgYD" +
                "VQQKExNQaW5nIElkZW50aXR5IENvcnAuMRcwFQYDVQQDEw5CcmlhbiBDYW1" +
                "wYmVsbDAeFw0xMzAyMjEyMzI5MTVaFw0xODA4MTQyMjI5MTVaMGIxCzAJBg" +
                "NVBAYTAlVTMQswCQYDVQQIEwJDTzEPMA0GA1UEBxMGRGVudmVyMRwwGgYDV" +
                "QQKExNQaW5nIElkZW50aXR5IENvcnAuMRcwFQYDVQQDEw5CcmlhbiBDYW1w" +
                "YmVsbDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAL64zn8/QnH" +
                "YMeZ0LncoXaEde1fiLm1jHjmQsF/449IYALM9if6amFtPDy2yvz3YlRij66" +
                "s5gyLCyO7ANuVRJx1NbgizcAblIgjtdf/u3WG7K+IiZhtELto/A7Fck9Ws6" +
                "SQvzRvOE8uSirYbgmj6He4iO8NCyvaK0jIQRMMGQwsU1quGmFgHIXPLfnpn" +
                "fajr1rVTAwtgV5LEZ4Iel+W1GC8ugMhyr4/p1MtcIM42EA8BzE6ZQqC7VPq" +
                "PvEjZ2dbZkaBhPbiZAS3YeYBRDWm1p1OZtWamT3cEvqqPpnjL1XyW+oyVVk" +
                "aZdklLQp2Btgt9qr21m42f4wTw+Xrp6rCKNb0CAwEAATANBgkqhkiG9w0BA" +
                "QUFAAOCAQEAh8zGlfSlcI0o3rYDPBB07aXNswb4ECNIKG0CETTUxmXl9KUL" +
                "+9gGlqCz5iWLOgWsnrcKcY0vXPG9J1r9AqBNTqNgHq2G03X09266X5CpOe1" +
                "zFo+Owb1zxtp3PehFdfQJ610CDLEaS9V9Rqp17hCyybEpOGVwe8fnk+fbEL" +
                "2Bo3UPGrpsHzUoaGpDftmWssZkhpBJKVMJyf/RuP2SmmaIzmnw9JiSlYhzo" +
                "4tpzd5rFXhjRbg4zW9C+2qok+2+qDM1iJ684gPHMIY8aLWrdgQTxkumGmTq" +
                "gawR+N5MDtdPTEQ0XfIBc2cJEUyMTY5MPvACWpkA6SdS4xSvdXK3IVfOWA=="};
        rsaJwk.setX5c(x5c);
        Assertions.assertNotNull(rsaJwk.asCertificates());
    }

    @Test
    public void testAES_128_CBC_HMAC_SHA_256() throws Exception {
        byte[] k = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f};
        byte[] p = {0x41, 0x20, 0x63, 0x69, 0x70, 0x68, 0x65, 0x72, 0x20, 0x73, 0x79, 0x73, 0x74, 0x65, 0x6d, 0x20,
                0x6d, 0x75, 0x73, 0x74, 0x20, 0x6e, 0x6f, 0x74, 0x20, 0x62, 0x65, 0x20, 0x72, 0x65, 0x71, 0x75,
                0x69, 0x72, 0x65, 0x64, 0x20, 0x74, 0x6f, 0x20, 0x62, 0x65, 0x20, 0x73, 0x65, 0x63, 0x72, 0x65,
                0x74, 0x2c, 0x20, 0x61, 0x6e, 0x64, 0x20, 0x69, 0x74, 0x20, 0x6d, 0x75, 0x73, 0x74, 0x20, 0x62,
                0x65, 0x20, 0x61, 0x62, 0x6c, 0x65, 0x20, 0x74, 0x6f, 0x20, 0x66, 0x61, 0x6c, 0x6c, 0x20, 0x69,
                0x6e, 0x74, 0x6f, 0x20, 0x74, 0x68, 0x65, 0x20, 0x68, 0x61, 0x6e, 0x64, 0x73, 0x20, 0x6f, 0x66,
                0x20, 0x74, 0x68, 0x65, 0x20, 0x65, 0x6e, 0x65, 0x6d, 0x79, 0x20, 0x77, 0x69, 0x74, 0x68, 0x6f,
                0x75, 0x74, 0x20, 0x69, 0x6e, 0x63, 0x6f, 0x6e, 0x76, 0x65, 0x6e, 0x69, 0x65, 0x6e, 0x63, 0x65};
        byte[] iv = {0x1a, (byte) 0xf3, (byte) 0x8c, 0x2d, (byte) 0xc2, (byte) 0xb9, 0x6f, (byte) 0xfd, (byte) 0xd8, 0x66, (byte) 0x94, 0x09, 0x23, 0x41, (byte) 0xbc, 0x04};
        byte[] a = {0x54, 0x68, 0x65, 0x20, 0x73, 0x65, 0x63, 0x6f, 0x6e, 0x64, 0x20, 0x70, 0x72, 0x69, 0x6e, 0x63,
                0x69, 0x70, 0x6c, 0x65, 0x20, 0x6f, 0x66, 0x20, 0x41, 0x75, 0x67, 0x75, 0x73, 0x74, 0x65, 0x20,
                0x4b, 0x65, 0x72, 0x63, 0x6b, 0x68, 0x6f, 0x66, 0x66, 0x73,};
        byte[] mixed = JsonWebAlgorithm.A128CBC_HS256.encrypt(p, new SecretKeySpec(k, JsonWebAlgorithm.AES_ALGORITHM), new IvParameterSpec(iv), a);
        byte[] e = {(byte) 0xc8, 0x0e, (byte) 0xdf, (byte) 0xa3, 0x2d, (byte) 0xdf, 0x39, (byte) 0xd5, (byte) 0xef, 0x00, (byte) 0xc0, (byte) 0xb4, 0x68, (byte) 0x83, 0x42, 0x79,
                (byte) 0xa2, (byte) 0xe4, 0x6a, 0x1b, (byte) 0x80, 0x49, (byte) 0xf7, (byte) 0x92, (byte) 0xf7, 0x6b, (byte) 0xfe, 0x54, (byte) 0xb9, 0x03, (byte) 0xa9, (byte) 0xc9,
                (byte) 0xa9, 0x4a, (byte) 0xc9, (byte) 0xb4, 0x7a, (byte) 0xd2, 0x65, 0x5c, 0x5f, 0x10, (byte) 0xf9, (byte) 0xae, (byte) 0xf7, 0x14, 0x27, (byte) 0xe2,
                (byte) 0xfc, 0x6f, (byte) 0x9b, 0x3f, 0x39, (byte) 0x9a, 0x22, 0x14, (byte) 0x89, (byte) 0xf1, 0x63, 0x62, (byte) 0xc7, 0x03, 0x23, 0x36,
                0x09, (byte) 0xd4, 0x5a, (byte) 0xc6, (byte) 0x98, 0x64, (byte) 0xe3, 0x32, 0x1c, (byte) 0xf8, 0x29, 0x35, (byte) 0xac, 0x40, (byte) 0x96, (byte) 0xc8,
                0x6e, 0x13, 0x33, 0x14, (byte) 0xc5, 0x40, 0x19, (byte) 0xe8, (byte) 0xca, (byte) 0x79, (byte) 0x80, (byte) 0xdf, (byte) 0xa4, (byte) 0xb9, (byte) 0xcf, 0x1b,
                0x38, 0x4c, 0x48, 0x6f, 0x3a, 0x54, (byte) 0xc5, 0x10, 0x78, 0x15, (byte) 0x8e, (byte) 0xe5, (byte) 0xd7, (byte) 0x9d, (byte) 0xe5, (byte) 0x9f,
                (byte) 0xbd, 0x34, (byte) 0xd8, 0x48, (byte) 0xb3, (byte) 0xd6, (byte) 0x95, 0x50, (byte) 0xa6, 0x76, 0x46, 0x34, 0x44, 0x27, (byte) 0xad, (byte) 0xe5,
                0x4b, (byte) 0x88, 0x51, (byte) 0xff, (byte) 0xb5, (byte) 0x98, (byte) 0xf7, (byte) 0xf8, 0x00, 0x74, (byte) 0xb9, 0x47, 0x3c, (byte) 0x82, (byte) 0xe2, (byte) 0xdb};
        byte[] t = {0x65, 0x2c, 0x3f, (byte) 0xa3, 0x6b, 0x0a, 0x7c, 0x5b, 0x32, 0x19, (byte) 0xfa, (byte) 0xb3, (byte) 0xa3, 0x0b, (byte) 0xc1, (byte) 0xc4};
        Assertions.assertArrayEquals(e, Arrays.copyOf(mixed, mixed.length - JsonWebAlgorithm.A128CBC_HS256.getKeySize()/ 8));
        byte[] tag = new byte[JsonWebAlgorithm.A128CBC_HS256.getKeySize()/ 8];
        System.arraycopy(mixed, mixed.length - tag.length, tag, 0, tag.length);
        Assertions.assertArrayEquals(t, tag);
    }

    @Test
    public void testDeriveWith_PBES2_HS256_A128KW_AND_EncryptWith_A128CBC_HS256() throws Exception {
        byte[] plainRSAPrivateKey = new StringBuilder().append("{\"kty\":\"RSA\",\"kid\":\"juliet@capulet.lit\",\"use\":\"enc\",")
                .append("\"n\":\"t6Q8PWSi1dkJj9hTP8hNYFlvadM7DflW9mWepOJhJ66w7nyoK1gPNqFMSQRyO125Gp-TEkodhWr0iujjHVx7BcV0llS4w5ACGgPrcAd6ZcSR0-Iqom-QFcNP8Sjg086MwoqQU_LYywlAGZ21WSdS_PERyGFiNnj3QQlO8Yns5jCtLCRwLHL0Pb1fEv45AuRIuUfVcPySBWYnDyGxvjYGDSM-AqWS9zIQ2ZilgT-GqUmipg0XOC0Cc20rgLe2ymLHjpHciCKVAbY5-L32-lSeZO-Os6U15_aXrk9Gw8cPUaX1_I8sLGuSiVdt3C_Fn2PZ3Z8i744FPFGGcG1qs2Wz-Q\",")
                .append("\"e\":\"AQAB\",")
                .append("\"d\":\"GRtbIQmhOZtyszfgKdg4u_N-R_mZGU_9k7JQ_jn1DnfTuMdSNprTeaSTyWfSNkuaAwnOEbIQVy1IQbWVV25NY3ybc_IhUJtfri7bAXYEReWaCl3hdlPKXy9UvqPYGR0kIXTQRqns-dVJ7jahlI7LyckrpTmrM8dWBo4_PMaenNnPiQgO0xnuToxutRZJfJvG4Ox4ka3GORQd9CsCZ2vsUDmsXOfUENOyMqADC6p1M3h33tsurY15k9qMSpG9OX_IJAXmxzAh_tWiZOwk2K4yxH9tS3Lq1yX8C1EWmeRDkK2ahecG85-oLKQt5VEpWHKmjOi_gJSdSgqcN96X52esAQ\",")
                .append("\"p\":\"2rnSOV4hKSN8sS4CgcQHFbs08XboFDqKum3sc4h3GRxrTmQdl1ZK9uw-PIHfQP0FkxXVrx-WE-ZEbrqivH_2iCLUS7wAl6XvARt1KkIaUxPPSYB9yk31s0Q8UK96E3_OrADAYtAJs-M3JxCLfNgqh56HDnETTQhH3rCT5T3yJws\",")
                .append("\"q\":\"1u_RiFDP7LBYh3N4GXLT9OpSKYP0uQZyiaZwBtOCBNJgQxaj10RWjsZu0c6Iedis4S7B_coSKB0Kj9PaPaBzg-IySRvvcQuPamQu66riMhjVtG6TlV8CLCYKrYl52ziqK0E_ym2QnkwsUX7eYTB7LbAHRK9GqocDE5B0f808I4s\",")
                .append("\"dp\":\"KkMTWqBUefVwZ2_Dbj1pPQqyHSHjj90L5x_MOzqYAJMcLMZtbUtwKqvVDq3tbEo3ZIcohbDtt6SbfmWzggabpQxNxuBpoOOf_a_HgMXK_lhqigI4y_kqS1wY52IwjUn5rgRrJ-yYo1h41KR-vz2pYhEAeYrhttWtxVqLCRViD6c\",")
                .append("\"dq\":\"AvfS0-gRxvn0bwJoMSnFxYcK1WnuEjQFluMGfwGitQBWtfZ1Er7t1xDkbN9GQTB9yqpDoYaN06H7CFtrkxhJIBQaj6nkF5KKS3TQtQ5qCzkOkmxIe3KRbBymXxkb5qwUpX5ELD5xFc6FeiafWYY63TmmEAu_lRFCOJ3xDea-ots\",")
                .append("\"qi\":\"lSQi-w9CpyUReMErP1RsBLk7wNtOvs5EQpPqmuMvqW57NBUczScEoPwmUqqabu9V0-Py4dQ57_bapoKRu1R90bvuFnU63SHWEFglZQvJDMeAvmj4sm-Fp0oYu_neotgQ0hzbI5gry7ajdYy9-2lNx_76aBZoOUu9HCJ-UsfSOI8\"")
                .append("}").toString().getBytes(StandardCharsets.US_ASCII);
        String protectedHeader = "eyJhbGciOiJQQkVTMi1IUzI1NitBMTI4S1ciLCJwMnMiOiIyV0NUY0paMVJ2ZF9DSnVKcmlwUTF3IiwicDJjIjo0MDk2LCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiandrK2pzb24ifQ";
        byte[] contentEncryptionKey = {111, 27, 25, 52, 66, 29, 20, 78, 92, (byte) 176, 56, (byte) 240, 65, (byte) 208, 82, 112,
                (byte) 161, (byte) 131, 36, 55, (byte) 202, (byte) 236, (byte) 185, (byte) 172, (byte) 129, 23, (byte) 153, (byte) 194, (byte) 195, 48,
                (byte) 253, (byte) 182};
        String passphrase = "Thus from my lips, by yours, my sin is purged.";
        Key k = JsonWebAlgorithm.PBES2_HS256_A128KW.deriveKey(passphrase.getBytes(StandardCharsets.UTF_8), Base64.getUrlDecoder().decode("2WCTcJZ1Rvd_CJuJripQ1w"), 4096);
        byte[] derivedKey = {110, (byte) 171, (byte) 169, 92, (byte) 129, 92, 109, 117, (byte) 233, (byte) 242, 116, (byte) 233, (byte) 170, 14, 24, 75};
        Assertions.assertArrayEquals(k.getEncoded(), derivedKey);

        byte[] encryptedKey = JsonWebAlgorithm.A128KW.wrapKey(k, new SecretKeySpec(contentEncryptionKey, JsonWebAlgorithm.AES_ALGORITHM));
        Assertions.assertEquals("TrqXOwuNUfDV9VPTNbyGvEJ9JMjefAVn-TR1uIxR9p6hsRQh9Tk7BA", JsonWebToken.ENCODER.encodeToString(encryptedKey));

        byte[] iv = Base64.getUrlDecoder().decode("Ye9j1qs22DmRSAddIh-VnA");
        byte[] aad = protectedHeader.getBytes(StandardCharsets.US_ASCII);

        byte[] mine = JsonWebAlgorithm.A128CBC_HS256.encrypt(plainRSAPrivateKey, new SecretKeySpec(contentEncryptionKey, JsonWebAlgorithm.AES_ALGORITHM), new IvParameterSpec(iv), aad);
        byte[] t = new byte[JsonWebAlgorithm.A128CBC_HS256.getKeySize() / 8];
        System.arraycopy(mine, mine.length - t.length, t, 0, t.length);
        byte[] e = Arrays.copyOf(mine, mine.length - t.length);
        String ciphertext = "AwhB8lxrlKjFn02LGWEqg27H4Tg9fyZAbFv3p5ZicHpj64QyHC44qqlZ3JEmnZTgQo" +
                "wIqZJ13jbyHB8LgePiqUJ1hf6M2HPLgzw8L-mEeQ0jvDUTrE07NtOerBk8bwBQyZ6g" +
                "0kQ3DEOIglfYxV8-FJvNBYwbqN1Bck6d_i7OtjSHV-8DIrp-3JcRIe05YKy3Oi34Z_" +
                "GOiAc1EK21B11c_AE11PII_wvvtRiUiG8YofQXakWd1_O98Kap-UgmyWPfreUJ3lJP" +
                "nbD4Ve95owEfMGLOPflo2MnjaTDCwQokoJ_xplQ2vNPz8iguLcHBoKllyQFJL2mOWB" +
                "wqhBo9Oj-O800as5mmLsvQMTflIrIEbbTMzHMBZ8EFW9fWwwFu0DWQJGkMNhmBZQ-3" +
                "lvqTc-M6-gWA6D8PDhONfP2Oib2HGizwG1iEaX8GRyUpfLuljCLIe1DkGOewhKuKkZ" +
                "h04DKNM5Nbugf2atmU9OP0Ldx5peCUtRG1gMVl7Qup5ZXHTjgPDr5b2N731UooCGAU" +
                "qHdgGhg0JVJ_ObCTdjsH4CF1SJsdUhrXvYx3HJh2Xd7CwJRzU_3Y1GxYU6-s3GFPbi" +
                "rfqqEipJDBTHpcoCmyrwYjYHFgnlqBZRotRrS95g8F95bRXqsaDY7UgQGwBQBwy665" +
                "d0zpvTasvfXf_c0MWAl-neFaKOW_Px6g4EUDjG1GWSXV9cLStLw_0ovdApDIFLHYHe" +
                "PyagyHjouQUuGiq7BsYwYrwaF06tgB8hV8omLNfMEmDPJaZUzMuHw6tBDwGkzD-tS_" +
                "ub9hxrpJ4UsOWnt5rGUyoN2N_c1-TQlXxm5oto14MxnoAyBQBpwIEgSH3Y4ZhwKBhH" +
                "PjSo0cdwuNdYbGPpb-YUvF-2NZzODiQ1OvWQBRHSbPWYz_xbGkgD504LRtqRwCO7CC" +
                "_CyyURi1sEssPVsMJRX_U4LFEOc82TiDdqjKOjRUfKK5rqLi8nBE9soQ0DSaOoFQZi" +
                "GrBrqxDsNYiAYAmxxkos-i3nX4qtByVx85sCE5U_0MqG7COxZWMOPEFrDaepUV-cOy" +
                "rvoUIng8i8ljKBKxETY2BgPegKBYCxsAUcAkKamSCC9AiBxA0UOHyhTqtlvMksO7AE" +
                "hNC2-YzPyx1FkhMoS4LLe6E_pFsMlmjA6P1NSge9C5G5tETYXGAn6b1xZbHtmwrPSc" +
                "ro9LWhVmAaA7_bxYObnFUxgWtK4vzzQBjZJ36UTk4OTB-JvKWgfVWCFsaw5WCHj6Oo" +
                "4jpO7d2yN7WMfAj2hTEabz9wumQ0TMhBduZ-QON3pYObSy7TSC1vVme0NJrwF_cJRe" +
                "hKTFmdlXGVldPxZCplr7ZQqRQhF8JP-l4mEQVnCaWGn9ONHlemczGOS-A-wwtnmwjI" +
                "B1V_vgJRf4FdpV-4hUk4-QLpu3-1lWFxrtZKcggq3tWTduRo5_QebQbUUT_VSCgsFc" +
                "OmyWKoj56lbxthN19hq1XGWbLGfrrR6MWh23vk01zn8FVwi7uFwEnRYSafsnWLa1Z5" +
                "TpBj9GvAdl2H9NHwzpB5NqHpZNkQ3NMDj13Fn8fzO0JB83Etbm_tnFQfcb13X3bJ15" +
                "Cz-Ww1MGhvIpGGnMBT_ADp9xSIyAM9dQ1yeVXk-AIgWBUlN5uyWSGyCxp0cJwx7HxM" +
                "38z0UIeBu-MytL-eqndM7LxytsVzCbjOTSVRmhYEMIzUAnS1gs7uMQAGRdgRIElTJE" +
                "SGMjb_4bZq9s6Ve1LKkSi0_QDsrABaLe55UY0zF4ZSfOV5PMyPtocwV_dcNPlxLgNA" +
                "D1BFX_Z9kAdMZQW6fAmsfFle0zAoMe4l9pMESH0JB4sJGdCKtQXj1cXNydDYozF7l8" +
                "H00BV_Er7zd6VtIw0MxwkFCTatsv_R-GsBCH218RgVPsfYhwVuT8R4HarpzsDBufC4" +
                "r8_c8fc9Z278sQ081jFjOja6L2x0N_ImzFNXU6xwO-Ska-QeuvYZ3X_L31ZOX4Llp-" +
                "7QSfgDoHnOxFv1Xws-D5mDHD3zxOup2b2TppdKTZb9eW2vxUVviM8OI9atBfPKMGAO" +
                "v9omA-6vv5IxUH0-lWMiHLQ_g8vnswp-Jav0c4t6URVUzujNOoNd_CBGGVnHiJTCHl" +
                "88LQxsqLHHIu4Fz-U2SGnlxGTj0-ihit2ELGRv4vO8E1BosTmf0cx3qgG0Pq0eOLBD" +
                "IHsrdZ_CCAiTc0HVkMbyq1M6qEhM-q5P6y1QCIrwg";
        Assertions.assertEquals(ciphertext, JsonWebToken.ENCODER.encodeToString(e));
        String tag = "0HFmhOzsQ98nNWJjIHkR7A";
        Assertions.assertEquals(tag, JsonWebToken.ENCODER.encodeToString(t));
    }

}
