package std.ietf.http.jose;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.json.bind.spi.JsonbProvider;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.function.Function;

public class JsonWebSignatureTest {

    private static Function<byte[], Header> deserializer;

    private static Function<String, JsonWebSignature> jwsJson;

    @BeforeAll
    protected static void setUp() {
        deserializer = b -> JsonbProvider.provider("bandung.ee.json.BindingProvider").create().build().fromJson(new ByteArrayInputStream(b), Header.class);
        jwsJson = json -> JsonbProvider.provider("bandung.ee.json.BindingProvider").create().build().fromJson(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), JsonWebSignature.class);
    }

    @Test
    public void testJWS_Using_HMAC_SHA_256() throws Exception {
        String jws = "eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ.dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";
        JsonWebKeySet.SymmetricKey key = new JsonWebKeySet.SymmetricKey();
        key.setK("AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow");
        byte[] payload = JsonWebToken.fromCompactJWS(jws, deserializer, key);
        String s = new String(payload);
        Assertions.assertTrue(s.contains("iss"));
        Assertions.assertTrue(s.contains("exp"));
    }

    private static JsonWebKeySet.RsaJwk jwsA2() {
        JsonWebKeySet.RsaJwk key = new JsonWebKeySet.RsaJwk();
        key.setN("ofgWCuLjybRlzo0tZWJjNiuSfb4p4fAkd_wWJcyQoTbji9k0l8W26mPddxHmfHQp-Vaw-4qPCJrcS2mJPMEzP1Pt0Bm4d4QlL-yRT-SFd2lZS-pCgNMsD1W_YpRPEwOWvG6b32690r2jZ47soMZo9wGzjb_7OMg0LOL-bSf63kpaSHSXndS5z5rexMdbBYUsLA9e-KXBdQOS-UTo7WTBEMa2R2CapHg665xsmtdVMTBQY4uDZlxvb3qCo5ZwKh9kG4LT6_I5IhlJH7aGhyxXFvUK-DWNmoudF8NAco9_h9iaGNj8q2ethFkMLs91kzk2PAcDTW9gb54h4FRWyuXpoQ");
        key.setE("AQAB");
        key.setD("Eq5xpGnNCivDflJsRQBXHx1hdR1k6Ulwe2JZD50LpXyWPEAeP88vLNO97IjlA7_GQ5sLKMgvfTeXZx9SE-7YwVol2NXOoAJe46sui395IW_GO-pWJ1O0BkTGoVEn2bKVRUCgu-GjBVaYLU6f3l9kJfFNS3E0QbVdxzubSu3Mkqzjkn439X0M_V51gfpRLI9JYanrC4D4qAdGcopV_0ZHHzQlBjudU2QvXt4ehNYTCBr6XCLQUShb1juUO1ZdiYoFaFQT5Tw8bGUl_x_jTj3ccPDVZFD9pIuhLhBOneufuBiB4cS98l2SR_RQyGWSeWjnczT0QU91p1DhOVRuOopznQ");
        key.setP("4BzEEOtIpmVdVEZNCqS7baC4crd0pqnRH_5IB3jw3bcxGn6QLvnEtfdUdiYrqBdss1l58BQ3KhooKeQTa9AB0Hw_Py5PJdTJNPY8cQn7ouZ2KKDcmnPGBY5t7yLc1QlQ5xHdwW1VhvKn-nXqhJTBgIPgtldC-KDV5z-y2XDwGUc");
        key.setQ("uQPEfgmVtjL0Uyyx88GZFF1fOunH3-7cepKmtH4pxhtCoHqpWmT8YAmZxaewHgHAjLYsp1ZSe7zFYHj7C6ul7TjeLQeZD_YwD66t62wDmpe_HlB-TnBA-njbglfIsRLtXlnDzQkv5dTltRJ11BKBBypeeF6689rjcJIDEz9RWdc");
        key.setDp("BwKfV3Akq5_MFZDFZCnW-wzl-CCo83WoZvnLQwCTeDv8uzluRSnm71I3QCLdhrqE2e9YkxvuxdBfpT_PI7Yz-FOKnu1R6HsJeDCjn12Sk3vmAktV2zb34MCdy7cpdTh_YVr7tss2u6vneTwrA86rZtu5Mbr1C1XsmvkxHQAdYo0");
        key.setDq("h_96-mK1R_7glhsum81dZxjTnYynPbZpHziZjeeHcXYsXaaMwkOlODsWa7I9xXDoRwbKgB719rrmI2oKr6N3Do9U0ajaHF-NKJnwgjMd2w9cjz3_-kyNlxAr2v4IKhGNpmM5iIgOS1VZnOZ68m6_pbLBSp3nssTdlqvd0tIiTHU");
        key.setQi("IYd7DHOhrWvxkwPQsRM2tOgrjbcrfvtQJipd-DlcxyVuuM9sQLdgjVk2oy26F0EmpScGLq2MowX7fhd_QJQ3ydy5cY7YIBi87w93IKLEdfnbJtoOPLUW0ITrJReOgo1cq9SbsxYawBgfp_gh6A5603k2-ZQwVK0JKSHuLFkuQ3U");
        return key;
    }

    @Test
    public void testJWS_Using_RSASSA_PKCS1_v1_5_SHA_256() throws GeneralSecurityException {
        String jws = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ.cC4hiUPoj9Eetdgtv3hF80EGrhuB__dzERat0XF9g2VtQgr9PJbu3XOiZj5RZmh7AAuHIm4Bh-0Qc_lF5YKt_O8W2Fp5jujGbds9uJdbF9CUAr7t1dnZcAcQjbKBYNX4BAynRFdiuB--f_nZLgrnbyTyWzO75vRK5h6xBArLIARNPvkSjtQBMHlb1L07Qe7K0GarZRmB_eSN9383LcOLn6_dO--xi12jzDwusC-eOkHWEsqtFZESc6BfI7noOPqvhJ1phCnvWh6IeYI2w9QOYEUipUTI8np6LbgGY9Fs98rqVt5AXLIhWkWywlVmtVrBp0igcN_IoypGlUPQGe77Rw";
        byte[] payload = JsonWebToken.fromCompactJWS(jws, deserializer, jwsA2());
        String s = new String(payload);
        Assertions.assertTrue(s.contains("iss"));
        Assertions.assertTrue(s.contains("exp"));
    }

    private static JsonWebKeySet.EllipticCurveJwk jwsA3() {
        JsonWebKeySet.EllipticCurveJwk key = new JsonWebKeySet.EllipticCurveJwk();
        key.setCrv("P-256");
        key.setX("f83OJ3D2xF1Bg8vub9tLe1gHMzV76e8Tus9uPHvRVEU");
        key.setY("x_FEzRu9m36HLN_tue659LNpXW6pCyStikYjKIWI5a0");
        key.setD("jpsQnnGQmL-YBIffH1136cspYG6-0iY7X1fCE9-E9LI");
        return key;
    }

    @Test
    public void testJWS_Using_ECDSA_P_256_SHA_256() throws GeneralSecurityException {
        String jws = "eyJhbGciOiJFUzI1NiJ9.eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ.DtEhU3ljbEg8L38VWAfUAqOyKAM6-Xx-F4GawxaepmXFCgfTjDxw5djxLa8ISlSApmWQxfKTUJqPP3-Kg6NU1Q";

        byte[] payload = JsonWebToken.fromCompactJWS(jws, deserializer, jwsA3());
        String s = new String(payload);
        Assertions.assertTrue(s.contains("iss"));
        Assertions.assertTrue(s.contains("exp"));
    }

    @Test
    public void testJWS_Using_ECDSA_P_521_SHA_512() throws GeneralSecurityException {
        String jws = "eyJhbGciOiJFUzUxMiJ9.UGF5bG9hZA.AdwMgeerwtHoh-l192l60hp9wAHZFVJbLfD_UxMi70cwnZOYaRI1bKPWROc-mZZqwqT2SI-KGDKB34XO0aw_7XdtAG8GaSwFKdCAPZgoXD2YBJZCPEX3xKpRwcdOO8KpEHwJjyqOgzDO7iKvU8vcnwNrmxYbSW9ERBXukOXolLzeO_Jn";
        JsonWebKeySet.EllipticCurveJwk key = new JsonWebKeySet.EllipticCurveJwk();
        key.setCrv("P-521");
        key.setX("AekpBQ8ST8a8VcfVOTNl353vSrDCLLJXmPk06wTjxrrjcBpXp5EOnYG_NjFZ6OvLFV1jSfS9tsz4qUxcWceqwQGk");
        key.setY("ADSmRA43Z1DSNx_RvcLI87cdL07l6jQyyBXMoxVg_l2Th-x3S1WDhjDly79ajL4Kkd0AZMaZmh9ubmf63e3kyMj2");
        key.setD("AY5pb7A0UFiB3RELSD64fTLOSV_jazdF7fLYyuTw8lOfRhWg6Y6rUrPAxerEzgdRhajnu0ferB0d53vM9mE15j2C");
        byte[] payload = JsonWebToken.fromCompactJWS(jws, deserializer, key);
        String s = new String(payload);
        Assertions.assertTrue(s.equals("Payload"));
    }

    @Test
    public void testUnsecure_Jws() throws GeneralSecurityException {
        String jws = "eyJhbGciOiJub25lIn0.eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ.";
        byte[] payload = JsonWebToken.fromCompactJWS(jws, deserializer);
        String s = new String(payload);
        Assertions.assertTrue(s.contains("iss"));
        Assertions.assertTrue(s.contains("exp"));
    }

    @Test
    public void testJWS_Using_General_JWS_JSON_Serialization() throws GeneralSecurityException {
        String jws = "    {\n" +
                "      \"payload\":\n" +
                "       \"eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ\",\n" +
                "      \"signatures\":[\n" +
                "       {\"protected\":\"eyJhbGciOiJSUzI1NiJ9\",\n" +
                "        \"header\":\n" +
                "         {\"kid\":\"2010-12-29\"},\n" +
                "        \"signature\":\"cC4hiUPoj9Eetdgtv3hF80EGrhuB__dzERat0XF9g2VtQgr9PJbu3XOiZj5RZmh7AAuHIm4Bh-0Qc_lF5YKt_O8W2Fp5jujGbds9uJdbF9CUAr7t1dnZcAcQjbKBYNX4BAynRFdiuB--f_nZLgrnbyTyWzO75vRK5h6xBArLIARNPvkSjtQBMHlb1L07Qe7K0GarZRmB_eSN9383LcOLn6_dO--xi12jzDwusC-eOkHWEsqtFZESc6BfI7noOPqvhJ1phCnvWh6IeYI2w9QOYEUipUTI8np6LbgGY9Fs98rqVt5AXLIhWkWywlVmtVrBp0igcN_IoypGlUPQGe77Rw\"},\n" +
                "       {\"protected\":\"eyJhbGciOiJFUzI1NiJ9\",\n" +
                "        \"header\":\n" +
                "         {\"kid\":\"e9bc097a-ce51-4036-9562-d2ade882db0d\"},\n" +
                "        \"signature\":\"DtEhU3ljbEg8L38VWAfUAqOyKAM6-Xx-F4GawxaepmXFCgfTjDxw5djxLa8ISlSApmWQxfKTUJqPP3-Kg6NU1Q\"}]\n" +
                "     }";
        JsonWebSignature sig = jwsJson.apply(jws);
        JsonWebKeySet.RsaJwk key1 = jwsA2();
        key1.setKid("2010-12-29");
        JsonWebKeySet.EllipticCurveJwk key2 = jwsA3();
        key2.setKid("e9bc097a-ce51-4036-9562-d2ade882db0d");
        JsonWebKey[] keys = {key1, key2};
        String s = new String(sig.toJWT(deserializer, keys));
        Assertions.assertTrue(s.contains("iss"));
        Assertions.assertTrue(s.contains("exp"));
    }

    @Test
    public void testJWS_Using_Flattened_JWS_JSON_Serialization() throws GeneralSecurityException {
        String jws = "     {\n" +
                "      \"payload\":\n" +
                "       \"eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ\",\n" +
                "      \"protected\":\"eyJhbGciOiJFUzI1NiJ9\",\n" +
                "      \"header\":\n" +
                "       {\"kid\":\"e9bc097a-ce51-4036-9562-d2ade882db0d\"},\n" +
                "      \"signature\":\n" +
                "       \"DtEhU3ljbEg8L38VWAfUAqOyKAM6-Xx-F4GawxaepmXFCgfTjDxw5djxLa8ISlSApmWQxfKTUJqPP3-Kg6NU1Q\"\n" +
                "     }";
        JsonWebSignature sig = jwsJson.apply(jws);
        JsonWebKeySet.EllipticCurveJwk key = jwsA3();
        key.setKid("e9bc097a-ce51-4036-9562-d2ade882db0d");
        String s = new String(sig.toJWT(deserializer, key));
        Assertions.assertTrue(s.contains("iss"));
        Assertions.assertTrue(s.contains("exp"));
    }
}
