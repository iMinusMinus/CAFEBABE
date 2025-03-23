package std.ietf.http.jose;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbConfig;
import javax.json.bind.spi.JsonbProvider;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.function.Function;

public class JsonWebEncryptionTest {

    private static Function<byte[], Header> deserializer;

    private static Function<String, JsonWebEncryption> jweJson;

    private static Function<Object, byte[]> serializer;

    @BeforeAll
    protected static void setUp() {
        JsonbConfig config = new JsonbConfig();
        config.withDeserializers(new JsonWebKeyDeserializer());
        Jsonb jsonb = JsonbProvider.provider("bandung.ee.json.BindingProvider").create().withConfig(config).build();
        deserializer = b -> jsonb.fromJson(new ByteArrayInputStream(b), Header.class);
        jweJson = json -> jsonb.fromJson(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), JsonWebEncryption.class);
        serializer = bean -> jsonb.toJson(bean).getBytes(StandardCharsets.UTF_8);
    }

    @Test
    public void testJWE_using_RSAES_OAEP_and_AES_GCM() throws GeneralSecurityException {
        String jwe = "eyJhbGciOiJSU0EtT0FFUCIsImVuYyI6IkEyNTZHQ00ifQ.OKOawDo13gRp2ojaHV7LFpZcgV7T6DVZKTyKOMTYUmKoTCVJRgckCL9kiMT03JGeipsEdY3mx_etLbbWSrFr05kLzcSr4qKAq7YN7e9jwQRb23nfa6c9d-StnImGyFDbSv04uVuxIp5Zms1gNxKKK2Da14B8S4rzVRltdYwam_lDp5XnZAYpQdb76FdIKLaVmqgfwX7XWRxv2322i-vDxRfqNzo_tETKzpVLzfiwQyeyPGLBIO56YJ7eObdv0je81860ppamavo35UgoRdbYaBcoh9QcfylQr66oc6vFWXRcZ_ZT2LawVCWTIy3brGPi6UklfCpIMfIjf7iGdXKHzg.48V1_ALb6US04U3b.5eym8TW_c8SuK0ltJ3rpYIzOeDQz7TALvtu6UG9oMo4vpzs9tX_EFShS8iB7j6jiSdiwkIr3ajwQzaBtQD_A.XFBoMYUZodetZdvTiFvSkQ";
        JsonWebKeySet.RsaJwk key = new JsonWebKeySet.RsaJwk();
        key.setN("oahUIoWw0K0usKNuOR6H4wkf4oBUXHTxRvgb48E-BVvxkeDNjbC4he8rUWcJoZmds2h7M70imEVhRU5djINXtqllXI4DFqcI1DgjT9LewND8MW2Krf3Spsk_ZkoFnilakGygTwpZ3uesH-PFABNIUYpOiN15dsQRkgr0vEhxN92i2asbOenSZeyaxziK72UwxrrKoExv6kc5twXTq4h-QChLOln0_mtUZwfsRaMStPs6mS6XrgxnxbWhojf663tuEQueGC-FCMfra36C9knDFGzKsNa7LZK2djYgyD3JR_MB_4NUJW_TqOQtwHYbxevoJArm-L5StowjzGy-_bq6Gw");
        key.setE("AQAB");
        key.setD("kLdtIj6GbDks_ApCSTYQtelcNttlKiOyPzMrXHeI-yk1F7-kpDxY4-WY5NWV5KntaEeXS1j82E375xxhWMHXyvjYecPT9fpwR_M9gV8n9Hrh2anTpTD93Dt62ypW3yDsJzBnTnrYu1iwWRgBKrEYY46qAZIrA2xAwnm2X7uGR1hghkqDp0Vqj3kbSCz1XyfCs6_LehBwtxHIyh8Ripy40p24moOAbgxVw3rxT_vlt3UVe4WO3JkJOzlpUf-KTVI2Ptgm-dARxTEtE-id-4OJr0h-K-VFs3VSndVTIznSxfyrj8ILL6MG_Uv8YAu7VILSB3lOW085-4qE3DzgrTjgyQ");
        key.setP("1r52Xk46c-LsfB5P442p7atdPUrxQSy4mti_tZI3Mgf2EuFVbUoDBvaRQ-SWxkbkmoEzL7JXroSBjSrK3YIQgYdMgyAEPTPjXv_hI2_1eTSPVZfzL0lffNn03IXqWF5MDFuoUYE0hzb2vhrlN_rKrbfDIwUbTrjjgieRbwC6Cl0");
        key.setQ("wLb35x7hmQWZsWJmB_vle87ihgZ19S8lBEROLIsZG4ayZVe9Hi9gDVCOBmUDdaDYVTSNx_8Fyw1YYa9XGrGnDew00J28cRUoeBB_jKI1oma0Orv1T9aXIWxKwd4gvxFImOWr3QRL9KEBRzk2RatUBnmDZJTIAfwTs0g68UZHvtc");
        key.setDp("ZK-YwE7diUh0qR1tR7w8WHtolDx3MZ_OTowiFvgfeQ3SiresXjm9gZ5KLhMXvo-uz-KUJWDxS5pFQ_M0evdo1dKiRTjVw_x4NyqyXPM5nULPkcpU827rnpZzAJKpdhWAgqrXGKAECQH0Xt4taznjnd_zVpAmZZq60WPMBMfKcuE");
        key.setDq("Dq0gfgJ1DdFGXiLvQEZnuKEN0UUmsJBxkjydc3j4ZYdBiMRAy86x0vHCjywcMlYYg4yoC4YZa9hNVcsjqA3FeiL19rk8g6Qn29Tt0cj8qqyFpz9vNDBUfCAiJVeESOjJDZPYHdHY8v1b-o-Z2X5tvLx-TCekf7oxyeKDUqKWjis");
        key.setQi("VIMpMYbPf47dT1w_zDUXfPimsSegnMOA1zTaX7aGk_8urY6R8-ZW1FxU7AlWAyLWybqq6t16VFd7hQd0y6flUK4SlOydB61gwanOsXGOAOv82cHq0E3eL4HrtZkUuKvnPrMnsUUFlfUdybVzxyjz9JF_XyaY14ardLSjf4L_FNY");
        byte[] payload = JsonWebToken.fromCompactJWE(jwe, deserializer, key);
        Assertions.assertEquals("The true sign of intelligence is not knowledge but imagination.", new String(payload));
    }

    private static JsonWebKeySet.RsaJwk a2() {
        JsonWebKeySet.RsaJwk key = new JsonWebKeySet.RsaJwk();
        key.setN("sXchDaQebHnPiGvyDOAT4saGEUetSyo9MKLOoWFsueri23bOdgWp4Dy1WlUzewbgBHod5pcM9H95GQRV3JDXboIRROSBigeC5yjU1hGzHHyXss8UDprecbAYxknTcQkhslANGRUZmdTOQ5qTRsLAt6BTYuyvVRdhS8exSZEy_c4gs_7svlJJQ4H9_NxsiIoLwAEk7-Q3UXERGYw_75IDrGA84-lA_-Ct4eTlXHBIY2EaV7t7LjJaynVJCpkv4LKjTTAumiGUIuQhrNhZLuF_RJLqHpM2kgWFLU7-VTdL1VbC2tejvcI2BlMkEpk1BzBZI0KQB0GaDWFLN-aEAw3vRw");
        key.setE("AQAB");
        key.setD("VFCWOqXr8nvZNyaaJLXdnNPXZKRaWCjkU5Q2egQQpTBMwhprMzWzpR8Sxq1OPThh_J6MUD8Z35wky9b8eEO0pwNS8xlh1lOFRRBoNqDIKVOku0aZb-rynq8cxjDTLZQ6Fz7jSjR1Klop-YKaUHc9GsEofQqYruPhzSA-QgajZGPbE_0ZaVDJHfyd7UUBUKunFMScbflYAAOYJqVIVwaYR5zWEEceUjNnTNo_CVSj-VvXLO5VZfCUAVLgW4dpf1SrtZjSt34YLsRarSb127reG_DUwg9Ch-KyvjT1SkHgUWRVGcyly7uvVGRSDwsXypdrNinPA4jlhoNdizK2zF2CWQ");
        key.setP("9gY2w6I6S6L0juEKsbeDAwpd9WMfgqFoeA9vEyEUuk4kLwBKcoe1x4HG68ik918hdDSE9vDQSccA3xXHOAFOPJ8R9EeIAbTi1VwBYnbTp87X-xcPWlEPkrdoUKW60tgs1aNd_Nnc9LEVVPMS390zbFxt8TN_biaBgelNgbC95sM");
        key.setQ("uKlCKvKv_ZJMVcdIs5vVSU_6cPtYI1ljWytExV_skstvRSNi9r66jdd9-yBhVfuG4shsp2j7rGnIio901RBeHo6TPKWVVykPu1iYhQXw1jIABfw-MVsN-3bQ76WLdt2SDxsHs7q7zPyUyHXmps7ycZ5c72wGkUwNOjYelmkiNS0");
        key.setDp("w0kZbV63cVRvVX6yk3C8cMxo2qCM4Y8nsq1lmMSYhG4EcL6FWbX5h9yuvngs4iLEFk6eALoUS4vIWEwcL4txw9LsWH_zKI-hwoReoP77cOdSL4AVcraHawlkpyd2TWjE5evgbhWtOxnZee3cXJBkAi64Ik6jZxbvk-RR3pEhnCs");
        key.setDq("o_8V14SezckO6CNLKs_btPdFiO9_kC1DsuUTd2LAfIIVeMZ7jn1Gus_Ff7B7IVx3p5KuBGOVF8L-qifLb6nQnLysgHDh132NDioZkhH7mI7hPG-PYE_odApKdnqECHWw0J-F0JWnUd6D2B_1TvF9mXA2Qx-iGYn8OVV1Bsmp6qU");
        key.setQi("eNho5yRBEBxhGBtQRww9QirZsB66TrfFReG_CcteI1aCneT0ELGhYlRlCtUkTRclIfuEPmNsNDPbLoLqqCVznFbvdB7x-Tl-m0l_eFTj2KiqwGqE9PZB9nNTwMVvH3VRRSLWACvPnSiwP8N5Usy-WRXS-V7TbpxIhvepTfE0NNo");
        return key;
    }

    @Test
    public void testJWE_using_RSAES_PKCS1_v1_5_and_AES_128_CBC_HMAC_SHA_256() throws GeneralSecurityException {
        String jwe = "eyJhbGciOiJSU0ExXzUiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0.UGhIOguC7IuEvf_NPVaXsGMoLOmwvc1GyqlIKOK1nN94nHPoltGRhWhw7Zx0-kFm1NJn8LE9XShH59_i8J0PH5ZZyNfGy2xGdULU7sHNF6Gp2vPLgNZ__deLKxGHZ7PcHALUzoOegEI-8E66jX2E4zyJKx-YxzZIItRzC5hlRirb6Y5Cl_p-ko3YvkkysZIFNPccxRU7qve1WYPxqbb2Yw8kZqa2rMWI5ng8OtvzlV7elprCbuPhcCdZ6XDP0_F8rkXds2vE4X-ncOIM8hAYHHi29NX0mcKiRaD0-D-ljQTP-cFPgwCp6X-nZZd9OHBv-B3oWh2TbqmScqXMR4gp_A.AxY8DCtDaGlsbGljb3RoZQ.KDlTtXchhZTGufMYmOYGS4HffxPSUrfmqCHXaI9wOGY.9hH0vgRfYgPnAHOd8stkvw";
        byte[] payload = JsonWebToken.fromCompactJWE(jwe, deserializer, a2());
        Assertions.assertEquals("Live long and prosper.", new String(payload));
    }

    private static JsonWebKeySet.SymmetricKey a3() {
        JsonWebKeySet.SymmetricKey key = new JsonWebKeySet.SymmetricKey();
        key.setK("GawgguFyGrWKav7AX4VKUg");
        return key;
    }

    @Test
    public void testJWE_Using_AES_Key_Wrap_and_AES_128_CBC_HMAC_SHA_256() throws GeneralSecurityException {
        String jwe = "eyJhbGciOiJBMTI4S1ciLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0.6KB707dM9YTIgHtLvtgWQ8mKwboJW3of9locizkDTHzBC2IlrT1oOQ.AxY8DCtDaGlsbGljb3RoZQ.KDlTtXchhZTGufMYmOYGS4HffxPSUrfmqCHXaI9wOGY.U0m_YmjN04DJvceFICbCVQ";
        byte[] payload = JsonWebToken.fromCompactJWE(jwe, deserializer, a3());
        Assertions.assertEquals("Live long and prosper.", new String(payload));
    }

    @Test
    public void testJWE_Using_General_JWE_JSON_Serialization() throws GeneralSecurityException {
        String jwe = "{\"protected\":\"eyJlbmMiOiJBMTI4Q0JDLUhTMjU2In0\",\"unprotected\":{\"jku\":\"https://server.example.com/keys.jwks\"},\"recipients\":[{\"header\":{\"alg\":\"RSA1_5\",\"kid\":\"2011-04-29\"},\"encrypted_key\":\"UGhIOguC7IuEvf_NPVaXsGMoLOmwvc1GyqlIKOK1nN94nHPoltGRhWhw7Zx0-kFm1NJn8LE9XShH59_i8J0PH5ZZyNfGy2xGdULU7sHNF6Gp2vPLgNZ__deLKxGHZ7PcHALUzoOegEI-8E66jX2E4zyJKx-YxzZIItRzC5hlRirb6Y5Cl_p-ko3YvkkysZIFNPccxRU7qve1WYPxqbb2Yw8kZqa2rMWI5ng8OtvzlV7elprCbuPhcCdZ6XDP0_F8rkXds2vE4X-ncOIM8hAYHHi29NX0mcKiRaD0-D-ljQTP-cFPgwCp6X-nZZd9OHBv-B3oWh2TbqmScqXMR4gp_A\"},{\"header\":{\"alg\":\"A128KW\",\"kid\":\"7\"},\"encrypted_key\":\"6KB707dM9YTIgHtLvtgWQ8mKwboJW3of9locizkDTHzBC2IlrT1oOQ\"}],\"iv\":\"AxY8DCtDaGlsbGljb3RoZQ\",\"ciphertext\":\"KDlTtXchhZTGufMYmOYGS4HffxPSUrfmqCHXaI9wOGY\",\"tag\":\"Mz-VPPyU4RlcuYv1IwIvzw\"}";
        JsonWebEncryption json = jweJson.apply(jwe);
        JsonWebKeySet.RsaJwk k1 = a2();
        k1.setKid("2011-04-29");
        JsonWebKeySet.SymmetricKey k2 = a3();
        k2.setKid("7");
        byte[] payload = json.toJWT(deserializer, k1, k2);
        Assertions.assertEquals("Live long and prosper.", new String(payload));
    }

    @Test
    public void testJWE_Per_Recipient_Unprotected_Headers() throws GeneralSecurityException {
        String jwe = "{\"protected\":\"eyJlbmMiOiJBMTI4Q0JDLUhTMjU2In0\",\"unprotected\":{\"jku\":\"https://server.example.com/keys.jwks\"},\"recipients\":[{\"header\":{\"alg\":\"RSA1_5\",\"kid\":\"2011-04-29\"},\"encrypted_key\":\"UGhIOguC7IuEvf_NPVaXsGMoLOmwvc1GyqlIKOK1nN94nHPoltGRhWhw7Zx0-kFm1NJn8LE9XShH59_i8J0PH5ZZyNfGy2xGdULU7sHNF6Gp2vPLgNZ__deLKxGHZ7PcHALUzoOegEI-8E66jX2E4zyJKx-YxzZIItRzC5hlRirb6Y5Cl_p-ko3YvkkysZIFNPccxRU7qve1WYPxqbb2Yw8kZqa2rMWI5ng8OtvzlV7elprCbuPhcCdZ6XDP0_F8rkXds2vE4X-ncOIM8hAYHHi29NX0mcKiRaD0-D-ljQTP-cFPgwCp6X-nZZd9OHBv-B3oWh2TbqmScqXMR4gp_A\"},{\"header\":{\"alg\":\"A128KW\",\"kid\":\"7\"},\"encrypted_key\":\"6KB707dM9YTIgHtLvtgWQ8mKwboJW3of9locizkDTHzBC2IlrT1oOQ\"}],\"iv\":\"AxY8DCtDaGlsbGljb3RoZQ\",\"ciphertext\":\"KDlTtXchhZTGufMYmOYGS4HffxPSUrfmqCHXaI9wOGY\",\"tag\":\"Mz-VPPyU4RlcuYv1IwIvzw\"}";
        JsonWebEncryption json = jweJson.apply(jwe);
        JsonWebKeySet.RsaJwk k1 = a2();
        k1.setKid("2011-04-29");
        JsonWebKeySet.SymmetricKey k2 = a3();
        k2.setKid("7");
        byte[] payload = json.toJWT(deserializer, k1, k2);
        Assertions.assertEquals("Live long and prosper.", new String(payload));
    }

    @Test
    public void testNestedJwt() throws GeneralSecurityException {
        String jwt = "eyJhbGciOiJSU0ExXzUiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0.g_hEwksO1Ax8Qn7HoN-BVeBoa8FXe0kpyk_XdcSmxvcM5_P296JXXtoHISr_DD_MqewaQSH4dZOQHoUgKLeFly-9RI11TG-_Ge1bZFazBPwKC5lJ6OLANLMd0QSL4fYEb9ERe-epKYE3xb2jfY1AltHqBO-PM6j23Guj2yDKnFv6WO72tteVzm_2n17SBFvhDuR9a2nHTE67pe0XGBUS_TK7ecA-iVq5COeVdJR4U4VZGGlxRGPLRHvolVLEHx6DYyLpw30Ay9R6d68YCLi9FYTq3hIXPK_-dmPlOUlKvPr1GgJzRoeC9G5qCvdcHWsqJGTO_z3Wfo5zsqwkxruxwA.UmVkbW9uZCBXQSA5ODA1Mg.VwHERHPvCNcHHpTjkoigx3_ExK0Qc71RMEParpatm0X_qpg-w8kozSjfNIPPXiTBBLXR65CIPkFqz4l1Ae9w_uowKiwyi9acgVztAi-pSL8GQSXnaamh9kX1mdh3M_TT-FZGQFQsFhu0Z72gJKGdfGE-OE7hS1zuBD5oEUfk0Dmb0VzWEzpxxiSSBbBAzP10l56pPfAtrjEYw-7ygeMkwBl6Z_mLS6w6xUgKlvW6ULmkV-uLC4FUiyKECK4e3WZYKw1bpgIqGYsw2v_grHjszJZ-_I5uM-9RA8ycX9KqPRp9gc6pXmoU_-27ATs9XCvrZXUtK2902AUzqpeEUJYjWWxSNsS-r1TJ1I-FMJ4XyAiGrfmo9hQPcNBYxPz3GQb28Y5CLSQfNgKSGt0A4isp1hBUXBHAndgtcslt7ZoQJaKe_nNJgNliWtWpJ_ebuOpEl8jdhehdccnRMIwAmU1n7SPkmhIl1HlSOpvcvDfhUN5wuqU955vOBvfkBOh5A11UzBuo2WlgZ6hYi9-e3w29bR0C2-pp3jbqxEDw3iWaf2dc5b-LnR0FEYXvI_tYk5rd_J9N0mg0tQ6RbpxNEMNoA9QWk5lgdPvbh9BaO195abQ.AVO9iT5AV4CzvDJCdhSFlQ";
        byte[] payload = JsonWebToken.fromCompactJWE(jwt, deserializer, a2());
        System.out.println(new String(payload));
    }


    @Test
    public void testJwe() throws GeneralSecurityException {
        System.out.println("data from https://www.rfc-editor.org/rfc/rfc7516#appendix-B");
        byte[] key = {4, (byte) 211, 31, (byte) 197, 84, (byte) 157, (byte) 252, (byte) 254, 11, 100, (byte) 157, (byte) 250, 63, (byte) 170, 106,
                (byte) 206, 107, 124, (byte) 212, 45, 111, 107, 9, (byte) 219, (byte) 200, (byte) 177, 0, (byte) 240, (byte) 143, (byte) 156,
                44, (byte) 207};
        System.out.println(Base64.getEncoder().encodeToString(key));
        byte[] iv= {3, 22, 60, 12, 43, 67, 104, 105, 108, 108, 105, 99, 111, 116, 104, 101};
        System.out.println(Base64.getEncoder().encodeToString(iv));
        byte[] aad = {101, 121, 74, 104, 98, 71, 99, 105, 79, 105, 74, 66, 77, 84, 73, 52,
                83, 49, 99, 105, 76, 67, 74, 108, 98, 109, 77, 105, 79, 105, 74, 66,
                77, 84, 73, 52, 81, 48, 74, 68, 76, 85, 104, 84, 77, 106, 85, 50, 73,
                110, 48};
        System.out.println(new String(aad));
        byte[] plainText = {76, 105, 118, 101, 32, 108, 111, 110, 103, 32, 97, 110, 100, 32,
                112, 114, 111, 115, 112, 101, 114, 46};
        System.out.println(new String(plainText));
        Header header = new Header();
        header.setAlg("A128KW");
        header.setEnc("A128CBC-HS256");
        JsonWebKey<?> k = a3();
        k.setAlg(header.getAlg());
        String s = JsonWebToken.toCompactJWE(header, new String (plainText), serializer, k);
        System.out.println(s);
    }
}
