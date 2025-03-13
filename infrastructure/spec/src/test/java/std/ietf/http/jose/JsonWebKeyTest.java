package std.ietf.http.jose;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

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
        Assertions.assertNotNull(ecJwk.asKey());
        JsonWebKeySet.EllipticCurveJwk jwk = new JsonWebKeySet.EllipticCurveJwk();
        jwk.setPublicKey((ECPublicKey) ecJwk.asKey());
        Assertions.assertNotNull(jwk.getX());
        Assertions.assertNotNull(jwk.getY());
        System.out.println(jwk);

        ecJwk = newPrivateEc();
        Assertions.assertTrue(ecJwk.asKey() instanceof ECPrivateKey);
        jwk = new JsonWebKeySet.EllipticCurveJwk();
        jwk.setPrivateKey((ECPrivateKey) ecJwk.asKey());
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
        Assertions.assertNotNull(jwk.asKey());

        JsonWebKeySet.SymmetricKey symmetricJwk = new JsonWebKeySet.SymmetricKey();
        symmetricJwk.withKey(jwk.asKey());
        Assertions.assertNotNull(symmetricJwk.getK());
        System.out.println(symmetricJwk);
    }
}
