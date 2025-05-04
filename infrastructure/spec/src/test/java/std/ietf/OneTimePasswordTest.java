package std.ietf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneTimePasswordTest {

    private static final String OPT_CHALLENGE_FORMAT = "otp-%s %s %s\n";

    @ParameterizedTest
    @ValueSource(strings = {"Too_short", "1234567890123456789012345678901234567890123456789012345678901234"})
    public void testOTPWithIllegalPassPhrase(String passPhrase) {
        String seed = "iamvalid";
        int count = 99;
        String alg = "<any>";
        OneTimePassword<byte[], String> otp = new OneTimePassword.OTP(false);
        Assertions.assertThrows(IllegalArgumentException.class, () -> otp.generate(passPhrase.getBytes(StandardCharsets.UTF_8), String.format(OPT_CHALLENGE_FORMAT, alg, count, seed)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Length_Okay", "LengthOfSeventeen", "A Seed"})
    public void testOTPWithBadSeed(String seed) {
        String passPhrase = "A_Valid_Pass_Phrase";
        int count = 99;
        String alg = "<any>";
        OneTimePassword<byte[], String> otp = new OneTimePassword.OTP(false);
        Assertions.assertThrows(IllegalArgumentException.class, () -> otp.generate(passPhrase.getBytes(StandardCharsets.UTF_8), String.format(OPT_CHALLENGE_FORMAT, alg, count, seed)));
    }

    @Test
    public void testOTP_md5() {
        String passPhrase = "A_Valid_Pass_Phrase";
        String seed = "AValidSeed";
        int count = 99;
        String alg = "md5";
        OneTimePassword<byte[], String> otp = new OneTimePassword.OTP(false);
        String result = otp.generate(passPhrase.getBytes(StandardCharsets.UTF_8), String.format(OPT_CHALLENGE_FORMAT, alg, count, seed));
        String outputHex = "85c43ee03857765b";
        String outputSixWord = "FOWL KID MASH DEAD DUAL OAF";
        Assertions.assertTrue(outputHex.equalsIgnoreCase(result));
    }

    @ParameterizedTest
    @MethodSource(value = "otpProvider")
    public void testOTP(String algorithm, String passPhrase, String seed, String count, String hex, String word) {
        OneTimePassword<byte[], String> otp = new OneTimePassword.OTP(true);
        String result = otp.generate(passPhrase.getBytes(StandardCharsets.UTF_8), String.format(OPT_CHALLENGE_FORMAT, algorithm, count, seed));
        Assertions.assertEquals(word, result);
    }

    static List<Arguments> otpProvider() {
        String md4Encoding = "Pass Phrase     Seed    Cnt Hex                 Six Word Format\n" +
                "========================================================================\n" +
                "This is a test. TeSt     0  D185 4218 EBBB 0B51\n" +
                "                                           ROME MUG FRED SCAN LIVE LACE\n" +
                "This is a test. TeSt     1  6347 3EF0 1CD0 B444\n" +
                "                                           CARD SAD MINI RYE COL KIN\n" +
                "This is a test. TeSt    99  C5E6 1277 6E6C 237A\n" +
                "                                           NOTE OUT IBIS SINK NAVE MODE\n" +
                "AbCdEfGhIjK     alpha1   0  5007 6F47 EB1A DE4E\n" +
                "                                           AWAY SEN ROOK SALT LICE MAP\n" +
                "AbCdEfGhIjK     alpha1   1  65D2 0D19 49B5 F7AB\n" +
                "                                           CHEW GRIM WU HANG BUCK SAID\n" +
                "AbCdEfGhIjK     alpha1  99  D150 C82C CE6F 62D1\n" +
                "                                           ROIL FREE COG HUNK WAIT COCA\n" +
                "OTP's are good  correct  0  849C 79D4 F6F5 5388\n" +
                "                                           FOOL STEM DONE TOOL BECK NILE\n" +
                "OTP's are good  correct  1  8C09 92FB 2508 47B1\n" +
                "                                           GIST AMOS MOOT AIDS FOOD SEEM\n" +
                "OTP's are good  correct 99  3F3B F4B4 145F D74B\n" +
                "                                           TAG SLOW NOV MIN WOOL KENO";

        String md5Encoding = "Pass Phrase     Seed    Cnt Hex                 Six Word Format\n" +
                "========================================================================\n" +
                "This is a test. TeSt     0  9E87 6134 D904 99DD\n" +
                "                                           INCH SEA ANNE LONG AHEM TOUR\n" +
                "This is a test. TeSt     1  7965 E054 36F5 029F\n" +
                "                                           EASE OIL FUM CURE AWRY AVIS\n" +
                "This is a test. TeSt    99  50FE 1962 C496 5880\n" +
                "                                           BAIL TUFT BITS GANG CHEF THY\n" +
                "AbCdEfGhIjK     alpha1   0  8706 6DD9 644B F206\n" +
                "                                           FULL PEW DOWN ONCE MORT ARC\n" +
                "AbCdEfGhIjK     alpha1   1  7CD3 4C10 40AD D14B\n" +
                "                                           FACT HOOF AT FIST SITE KENT\n" +
                "AbCdEfGhIjK     alpha1  99  5AA3 7A81 F212 146C\n" +
                "                                           BODE HOP JAKE STOW JUT RAP\n" +
                "OTP's are good  correct  0  F205 7539 43DE 4CF9\n" +
                "                                           ULAN NEW ARMY FUSE SUIT EYED\n" +
                "OTP's are good  correct  1  DDCD AC95 6F23 4937\n" +
                "                                           SKIM CULT LOB SLAM POE HOWL\n" +
                "OTP's are good  correct 99  B203 E28F A525 BE47\n" +
                "                                           LONG IVY JULY AJAR BOND LEE";

        String sha1Encoding = "Pass Phrase     Seed    Cnt Hex                 Six Word Format\n" +
                "========================================================================\n" +
                "This is a test. TeSt     0  BB9E 6AE1 979D 8FF4\n" +
                "                                           MILT VARY MAST OK SEES WENT\n" +
                "This is a test. TeSt     1  63D9 3663 9734 385B\n" +
                "                                           CART OTTO HIVE ODE VAT NUT\n" +
                "This is a test. TeSt    99  87FE C776 8B73 CCF9\n" +
                "                                           GAFF WAIT SKID GIG SKY EYED\n" +
                "AbCdEfGhIjK     alpha1   0  AD85 F658 EBE3 83C9\n" +
                "                                           LEST OR HEEL SCOT ROB SUIT\n" +
                "AbCdEfGhIjK     alpha1   1  D07C E229 B5CF 119B\n" +
                "                                           RITE TAKE GELD COST TUNE RECK\n" +
                "AbCdEfGhIjK     alpha1  99  27BC 7103 5AAF 3DC6\n" +
                "                                           MAY STAR TIN LYON VEDA STAN\n" +
                "OTP's are good  correct  0  D51F 3E99 BF8E 6F0B\n" +
                "                                           RUST WELT KICK FELL TAIL FRAU\n" +
                "OTP's are good  correct  1  82AE B52D 9437 74E4\n" +
                "                                           FLIT DOSE ALSO MEW DRUM DEFY\n" +
                "OTP's are good  correct 99  4F29 6A74 FE15 67EC\n" +
                "                                           AURA ALOE HURL WING BERG WAIT";
        Map<String, String> map = new HashMap<>();
        map.put("md4", md4Encoding);
        map.put("md5", md5Encoding);
        map.put("sha1", sha1Encoding);
        List<Arguments> arguments = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String[] cases = entry.getValue().split("[\n]");
            for (int i = 2; i < cases.length; i += 2) {
                String hex = String.join("", cases[i].substring(26).trim().split("[\\s]"));
                String word = cases[i + 1].trim();
                arguments.add(Arguments.of(entry.getKey(), cases[i].substring(0, 16).trim(), cases[i].substring(16, 24).trim(), cases[i].substring(24, 26).trim(), hex, word));
            }
        }
        return arguments;
    }

    @ParameterizedTest
    @MethodSource(value = "hotpTable")
    public void testHOTP(String count, String hotp) {
        String secret = "12345678901234567890";
        OneTimePassword<byte[], Long> otp = new OneTimePassword.HOTP(6, false, -1);
        String result = otp.generate(secret.getBytes(StandardCharsets.UTF_8), Long.parseLong(count));
        Assertions.assertEquals(hotp, result);
    }

    static List<Arguments> hotpTable() {
        String truncated = "   Count    Hexadecimal    Decimal        HOTP\n" +
                "   0        4c93cf18       1284755224     755224\n" +
                "   1        41397eea       1094287082     287082\n" +
                "   2         82fef30        137359152     359152\n" +
                "   3        66ef7655       1726969429     969429\n" +
                "   4        61c5938a       1640338314     338314\n" +
                "   5        33c083d4        868254676     254676\n" +
                "   6        7256c032       1918287922     287922\n" +
                "   7         4e5b397         82162583     162583\n" +
                "   8        2823443f        673399871     399871\n" +
                "   9        2679dc69        645520489     520489";
        String[] lines = truncated.split("[\n]");
        List<Arguments> arguments = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String[] items = lines[i].split("\\s+");
            arguments.add(Arguments.of(items[1], items[4]));
        }
        return arguments;
    }

    @ParameterizedTest
    @MethodSource(value = {"totpTable"})
    public void testTOTP(String algorithm, String time, String totp) {
        String seed = "3132333435363738393031323334353637383930";
        String seed32 = "3132333435363738393031323334353637383930313233343536373839303132";
        String seed64 = "31323334353637383930313233343536373839303132333435363738393031323334353637383930313233343536373839303132333435363738393031323334";
        String sharedSecret;
        if ("HmacSHA1".equals(algorithm)) {
            sharedSecret = seed;
        } else if ("HmacSHA256".equals(algorithm)) {
            sharedSecret = seed32;
        } else {
            sharedSecret = seed64;
        }
        int step = 30;
        OneTimePassword<String, String> otp = new OneTimePassword.TOTP(algorithm, "+8");
//        System.out.println(OneTimePassword.TOTP.timeAsHex(59000, step));
        String result = otp.generate(sharedSecret, time);
        Assertions.assertEquals(totp, result);
    }

    static List<Arguments> totpTable() {
        String table = "  +-------------+--------------+------------------+----------+--------+\n" +
                "  |  Time (sec) |   UTC Time   | Value of T (hex) |   TOTP   |  Mode  |\n" +
                "  +-------------+--------------+------------------+----------+--------+\n" +
                "  |      59     |  1970-01-01  | 0000000000000001 | 94287082 |  SHA1  |\n" +
                "  |             |   00:00:59   |                  |          |        |\n" +
                "  |      59     |  1970-01-01  | 0000000000000001 | 46119246 | SHA256 |\n" +
                "  |             |   00:00:59   |                  |          |        |\n" +
                "  |      59     |  1970-01-01  | 0000000000000001 | 90693936 | SHA512 |\n" +
                "  |             |   00:00:59   |                  |          |        |\n" +
                "  |  1111111109 |  2005-03-18  | 00000000023523EC | 07081804 |  SHA1  |\n" +
                "  |             |   01:58:29   |                  |          |        |\n" +
                "  |  1111111109 |  2005-03-18  | 00000000023523EC | 68084774 | SHA256 |\n" +
                "  |             |   01:58:29   |                  |          |        |\n" +
                "  |  1111111109 |  2005-03-18  | 00000000023523EC | 25091201 | SHA512 |\n" +
                "  |             |   01:58:29   |                  |          |        |\n" +
                "  |  1111111111 |  2005-03-18  | 00000000023523ED | 14050471 |  SHA1  |\n" +
                "  |             |   01:58:31   |                  |          |        |\n" +
                "  |  1111111111 |  2005-03-18  | 00000000023523ED | 67062674 | SHA256 |\n" +
                "  |             |   01:58:31   |                  |          |        |\n" +
                "  |  1111111111 |  2005-03-18  | 00000000023523ED | 99943326 | SHA512 |\n" +
                "  |             |   01:58:31   |                  |          |        |\n" +
                "  |  1234567890 |  2009-02-13  | 000000000273EF07 | 89005924 |  SHA1  |\n" +
                "  |             |   23:31:30   |                  |          |        |\n" +
                "  |  1234567890 |  2009-02-13  | 000000000273EF07 | 91819424 | SHA256 |\n" +
                "  |             |   23:31:30   |                  |          |        |\n" +
                "  |  1234567890 |  2009-02-13  | 000000000273EF07 | 93441116 | SHA512 |\n" +
                "  |             |   23:31:30   |                  |          |        |\n" +
                "  |  2000000000 |  2033-05-18  | 0000000003F940AA | 69279037 |  SHA1  |\n" +
                "  |             |   03:33:20   |                  |          |        |\n" +
                "  |  2000000000 |  2033-05-18  | 0000000003F940AA | 90698825 | SHA256 |\n" +
                "  |             |   03:33:20   |                  |          |        |\n" +
                "  |  2000000000 |  2033-05-18  | 0000000003F940AA | 38618901 | SHA512 |\n" +
                "  |             |   03:33:20   |                  |          |        |\n" +
                "  | 20000000000 |  2603-10-11  | 0000000027BC86AA | 65353130 |  SHA1  |\n" +
                "  |             |   11:33:20   |                  |          |        |\n" +
                "  | 20000000000 |  2603-10-11  | 0000000027BC86AA | 77737706 | SHA256 |\n" +
                "  |             |   11:33:20   |                  |          |        |\n" +
                "  | 20000000000 |  2603-10-11  | 0000000027BC86AA | 47863826 | SHA512 |\n" +
                "  |             |   11:33:20   |                  |          |        |\n" +
                "  +-------------+--------------+------------------+----------+--------+";
        List<Arguments> sources = new ArrayList<>();
        String[] lines = table.split("[\n]");
        for (int i = 3; i < lines.length - 2; i += 2) {
            String[] items = lines[i].split("[|]");
            sources.add(Arguments.of("Hmac" + items[5].trim(), items[3].trim(), items[4].trim()));
        }
        return sources;
    }
}
