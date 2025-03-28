package std.ietf.http.jose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAMultiPrimePrivateCrtKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAMultiPrimePrivateCrtKeySpec;
import java.security.spec.RSAOtherPrimeInfo;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Optional;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc7517">JSON Web Key (JWK)</a>用JSON结构表示加密密钥，多个密钥的优先级并不是按先后顺序，而是通过指定kid来匹配。
 * @author iMinusMinus
 * @date 2025-03-13
 */
@Getter
@Setter
@ToString
public class JsonWebKeySet {

    public static final String MIME_TYPE = "application/jwk-set+json";

    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding(); // JWS使用URL和文件名安全编码：省去结尾可能的"="，无换行、空白等特殊字符

    protected JsonWebKey<?>[] keys;

    private static byte[] toBytesUnsigned(BigInteger number) {
        int bitLength = number.bitLength() + 7 >> 3 << 3;
        byte[] data = number.toByteArray();
        if (number.bitLength() % 8 != 0 && number.bitLength() / 8 + 1 == bitLength / 8) {
            return data;
        }
        byte[] resized = new byte[bitLength / 8];
        int offset = number.bitLength() % 8 == 0 ? 1 : 0;
        System.arraycopy(data, offset, resized, bitLength / 8 - data.length + offset, data.length - offset);
        return resized;
    }

    @Getter
    public enum Curve {
        P_256("P-256", "secp256r1", "1.2.840.10045.3.1.7",
                new ECParameterSpec(new EllipticCurve(new ECFieldFp(new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951")), new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853948"), new BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291")), new ECPoint(new BigInteger("48439561293906451759052585252797914202762949526041747995844080717082404635286"), new BigInteger("36134250956749795798585127919587881956611106672985015071877198253568414405109")), new BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369"), 1)),
        SECP256K1("secp256k1", "secp256k1", "1.3.132.0.10",
                new ECParameterSpec(new EllipticCurve(new ECFieldFp(new BigInteger("115792089237316195423570985008687907853269984665640564039457584007908834671663")), new BigInteger("0"), new BigInteger("7")), new ECPoint(new BigInteger("55066263022277343669578718895168534326250603453777594175500187360389116729240"), new BigInteger("32670510020758816978083085130507043184471273380659243275938904335757337482424")), new BigInteger("115792089237316195423570985008687907852837564279074904382605163141518161494337"), 1)),
        P_256K("P-256K", "secp256k1", "1.3.132.0.10", null),
        P_384("P-384", "secp384r1", "1.3.132.0.34",
                new ECParameterSpec(new EllipticCurve(new ECFieldFp(new BigInteger("39402006196394479212279040100143613805079739270465446667948293404245721771496870329047266088258938001861606973112319")), new BigInteger("39402006196394479212279040100143613805079739270465446667948293404245721771496870329047266088258938001861606973112316"), new BigInteger("27580193559959705877849011840389048093056905856361568521428707301988689241309860865136260764883745107765439761230575")), new ECPoint(new BigInteger("26247035095799689268623156744566981891852923491109213387815615900925518854738050089022388053975719786650872476732087"), new BigInteger("8325710961489029985546751289520108179287853048861315594709205902480503199884419224438643760392947333078086511627871")), new BigInteger("39402006196394479212279040100143613805079739270465446667946905279627659399113263569398956308152294913554433653942643"), 1)),
        P_521("P-521", "secp521r1", "1.3.132.0.35",
                new ECParameterSpec(new EllipticCurve(new ECFieldFp(new BigInteger("6864797660130609714981900799081393217269435300143305409394463459185543183397656052122559640661454554977296311391480858037121987999716643812574028291115057151")), new BigInteger("6864797660130609714981900799081393217269435300143305409394463459185543183397656052122559640661454554977296311391480858037121987999716643812574028291115057148"), new BigInteger("1093849038073734274511112390766805569936207598951683748994586394495953116150735016013708737573759623248592132296706313309438452531591012912142327488478985984")), new ECPoint(new BigInteger("2661740802050217063228768716723360960729859168756973147706671368418802944996427808491545080627771902352094241225065558662157113545570916814161637315895999846"), new BigInteger("3757180025770020463545507224491183603594455134769762486694567779615544477440556316691234405012945539562144444537289428522585666729196580810124344277578376784")), new BigInteger("6864797660130609714981900799081393217269435300143305409394463459185543183397655394245057746333217197532963996371363321113864768612440380340372808892707005449"), 1)),
        ED25519("Ed25519", "Ed25519", null, null),
        ED448("Ed448", "Ed448", null, null),
        X25519("X25519", "X25519", null, null),
        X448("X448", "X448", null, null),
        ;

        private final String name;

        private final String algorithm;

        private final String oid;

        private final ECParameterSpec parameter;

        Curve(String name, String algorithm, String oid, ECParameterSpec parameter) {
            this.name = name;
            this.algorithm = algorithm;
            this.oid = oid;
            this.parameter = parameter;
        }

        public static Curve getInstance(String name) {
            for (Curve instance : Curve.values()) {
                if (instance.name.equals(name)) {
                    return instance;
                }
            }
            return null;
        }

        public static Curve getInstance(ECParameterSpec parameter) {
            for (Curve instance : Curve.values()) {
                if (instance.parameter == null) {
                    continue;
                }
                if (instance.parameter.getCurve().getField().getFieldSize() == parameter.getCurve().getField().getFieldSize() &&
                        instance.parameter.getCurve().getA().equals(parameter.getCurve().getA()) &&
                        instance.parameter.getCurve().getB().equals(parameter.getCurve().getB()) &&
                        instance.parameter.getGenerator().getAffineX().equals(parameter.getGenerator().getAffineX()) &&
                        instance.parameter.getGenerator().getAffineY().equals(parameter.getGenerator().getAffineY()) &&
                        instance.parameter.getOrder().equals(parameter.getOrder()) &&
                        instance.parameter.getCofactor() == parameter.getCofactor()) {
                    return instance;
                }
            }
            return null;
        }
    }

    @ToString(callSuper = true)
    public static final class EllipticCurveJwk extends JsonWebKey {

        static final String KEY_TYPE_EC = "EC";

        /**
         * Curve
         */
        @Getter @Setter private String crv;

        /**
         * X Coordinate
         */
        @Getter @Setter private String x;

        /**
         * Y Coordinate
         */
        @Getter @Setter private String y;

        /**
         * ECC Private Key
         */
        @Getter @Setter private String d; // private key only

        private transient volatile ECPublicKey publicKey;

        private transient volatile ECPrivateKey privateKey;

        public EllipticCurveJwk() {
            this(null, null);
        }

        public EllipticCurveJwk(ECPublicKey publicKey, ECPrivateKey privateKey) {
            super(KEY_TYPE_EC);
            setPublicKey(publicKey);
            setPrivateKey(privateKey);
        }

        @Override
        public boolean isAsymmetric() {
            return true;
        }

        @Override
        public Key exportKey() {
            ECParameterSpec parameter = obtainParameter();
            if (parameter == null) {
                return null;
            }
            return d == null ? getPublicKey(parameter) : getPrivateKey(parameter);
        }

        @Override
        public PublicKey asPublicKey() {
            return getPublicKey();
        }

        @Override
        public PrivateKey asPrivateKey() {
            return getPrivateKey();
        }

        @Override
        public void importKey(Key key) {
            if (key instanceof ECPublicKey) {
                setPublicKey((ECPublicKey) key);
            } else if (key instanceof ECPrivateKey) {
                setPrivateKey((ECPrivateKey) key);
            }
        }

        public KeyPair asKeyPair() {
            ECParameterSpec parameter = obtainParameter();
            if (parameter == null) {
                return null;
            }
            return new KeyPair(getPublicKey(parameter), getPrivateKey(parameter));
        }

        private ECParameterSpec obtainParameter() {
            Curve curve = Curve.getInstance(crv);
            if (curve == null) {
                throw new IllegalArgumentException("bad curve: " + crv);
            }
            return curve.getParameter();
        }

        ECPublicKey getPublicKey() {
            return getPublicKey(obtainParameter());
        }

        ECPublicKey getPublicKey(ECParameterSpec parameter) {
            if (publicKey == null && x != null && y != null) {
                ECPoint point = new ECPoint(new BigInteger(1, Base64.getUrlDecoder().decode(x)), new BigInteger(1, Base64.getUrlDecoder().decode(y)));
                ECPublicKeySpec keySpec = new ECPublicKeySpec(point, parameter);
                try {
                    publicKey = (ECPublicKey) KeyFactory.getInstance(kty).generatePublic(keySpec);
                } catch (NoSuchAlgorithmException impossible) {
                    impossible.printStackTrace();
                    return null;
                } catch (InvalidKeySpecException ike) {
                    throw new IllegalArgumentException(ike.getMessage(), ike);
                }
            }
            return publicKey;
        }

        void setPublicKey(ECPublicKey publicKey) {
            this.publicKey = publicKey;
            if (publicKey == null) {
                return;
            }
            this.crv = Optional.ofNullable(Curve.getInstance(publicKey.getParams())).map(Curve::getName).orElse(null);
            byte[] unpadX = toBytesUnsigned(publicKey.getW().getAffineX());
            byte[] unpadY = toBytesUnsigned(publicKey.getW().getAffineY());
            int bytesToOutput = (publicKey.getParams().getCurve().getField().getFieldSize() + 7) / 8;
            this.x = encodeCoordinate(unpadX, bytesToOutput);
            this.y = encodeCoordinate(unpadY, bytesToOutput);
        }

        private String encodeCoordinate(byte[] unpad, int bytesToOutput) {
            if (unpad.length >= bytesToOutput) {
                return Base64.getUrlEncoder().encodeToString(unpad);
            } else {
                byte[] data = new byte[bytesToOutput];
                System.arraycopy(unpad, 0, data, bytesToOutput - unpad.length, unpad.length);
                return Base64.getUrlEncoder().encodeToString(data);
            }
        }

        ECPrivateKey getPrivateKey() {
            return getPrivateKey(obtainParameter());
        }

        ECPrivateKey getPrivateKey(ECParameterSpec parameter) {
            if (privateKey == null && d != null) {
                ECPrivateKeySpec keySpec = new ECPrivateKeySpec(new BigInteger(1, Base64.getUrlDecoder().decode(d)), parameter);
                try {
                    privateKey = (ECPrivateKey) KeyFactory.getInstance(kty).generatePrivate(keySpec);
                } catch (NoSuchAlgorithmException impossible) {
                    impossible.printStackTrace();
                    return null;
                } catch (InvalidKeySpecException ike) {
                    throw new IllegalArgumentException(ike.getMessage(), ike);
                }
            }
            return privateKey;
        }

        void setPrivateKey(ECPrivateKey privateKey) {
            this.privateKey = privateKey;
            if (privateKey == null) {
                return;
            }
            this.crv = Optional.ofNullable(Curve.getInstance(privateKey.getParams())).map(Curve::getName).orElse(null);
            int bytesToOutput = (privateKey.getParams().getCurve().getField().getFieldSize() + 7) / 8;
            this.d = encodeCoordinate(toBytesUnsigned(privateKey.getS()), bytesToOutput);
        }

    }

    @ToString(callSuper = true)
    public static final class RsaJwk extends JsonWebKey {

        static final String KEY_TYPE_RSA = "RSA";

        /**
         * Modulus
         */
        @Getter @Setter private String n;

        /**
         * Exponent
         */
        @Getter @Setter private String e;

        /**
         * Private Exponent
         */
        @Getter @Setter private String d; // private key only

        /**
         * First Prime Factor
         */
        @Getter @Setter private String p; // private key only

        /**
         * Second Prime Factor
         */
        @Getter @Setter private String q; // private key only

        /**
         * First Factor CRT Exponent
         */
        @Getter @Setter private String dp; // private key only

        /**
         * Second Factor CRT ExponentSecond Factor CRT Exponent
         */
        @Getter @Setter private String dq; // private key only

        /**
         * First CRT Coefficient
         */
        @Getter @Setter private String qi; // private key only

        /**
         * Other Primes Info
         */
        @Getter @Setter private RsaOtherPrimesInfo[] oth;

        private transient volatile RSAPublicKey publicKey;

        private transient volatile RSAPrivateKey privateKey;

        public RsaJwk() {
            this(null, null);
        }

        public RsaJwk(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
            super(KEY_TYPE_RSA);
            setPublicKey(publicKey);
            setPrivateKey(privateKey);
        }

        @Override
        public boolean isAsymmetric() {
            return true;
        }

        @Override
        public Key exportKey() {
            return d == null ? getPublicKey() : getPrivateKey();
        }

        @Override
        public PublicKey asPublicKey() {
            return getPublicKey();
        }

        @Override
        public PrivateKey asPrivateKey() {
            return getPrivateKey();
        }

        @Override
        public void importKey(Key key) {
            if (key instanceof RSAPublicKey) {
                setPublicKey((RSAPublicKey) key);
            } else if (key instanceof RSAPrivateKey) {
                setPrivateKey((RSAPrivateKey) key);
            }
        }

        public KeyPair asKeyPair() {
            return new KeyPair(getPublicKey(), getPrivateKey());
        }

        RSAPublicKey getPublicKey() {
            if (publicKey == null && n != null && e != null) {
                BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
                BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(e));
                try {
                    publicKey = (RSAPublicKey) KeyFactory.getInstance(kty).generatePublic(new RSAPublicKeySpec(modulus, exponent));
                } catch (NoSuchAlgorithmException impossible) {
                    impossible.printStackTrace();
                    return null;
                } catch (InvalidKeySpecException ise) {
                    throw new IllegalArgumentException(ise.getMessage(), ise);
                }
            }
            return publicKey;
        }

        void setPublicKey(RSAPublicKey publicKey) {
            this.publicKey = publicKey;
            if (publicKey == null) {
                return;
            }
            this.n = encoder.encodeToString(toBytesUnsigned(publicKey.getModulus()));
            this.e = encoder.encodeToString(toBytesUnsigned(publicKey.getPublicExponent()));
        }

        RSAPrivateKey getPrivateKey() {
            if (privateKey != null) {
                return privateKey;
            }
            if (n == null || d == null) {
                return null;
            }
            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
            BigInteger privateExponent = new BigInteger(1, Base64.getUrlDecoder().decode(d));
            try {
                if (p == null) {
                    privateKey = (RSAPrivateKey) KeyFactory.getInstance(kty).generatePrivate(new RSAPrivateKeySpec(modulus, privateExponent));
                    return privateKey;
                }
                assert e != null && q != null && dp != null && dq != null && qi != null;
                BigInteger publicExponent = new BigInteger(1, Base64.getUrlDecoder().decode(e));
                BigInteger primeP = new BigInteger(1, Base64.getUrlDecoder().decode(p));
                BigInteger primeQ = new BigInteger(1, Base64.getUrlDecoder().decode(q));
                BigInteger primeExponentP = new BigInteger(1, Base64.getUrlDecoder().decode(dp));
                BigInteger primeExponentQ = new BigInteger(1, Base64.getUrlDecoder().decode(dq));
                BigInteger crtCoefficient = new BigInteger(1, Base64.getUrlDecoder().decode(qi));
                RSAPrivateKeySpec keySpec;
                if (oth != null) {
                    RSAOtherPrimeInfo[] primes = new RSAOtherPrimeInfo[oth.length];
                    for (int i = 0; i < oth.length; i++) {
                        primes[i] = new RSAOtherPrimeInfo(new BigInteger(1, Base64.getUrlDecoder().decode(oth[i].r)),
                                new BigInteger(1, Base64.getUrlDecoder().decode(oth[i].d)),
                                new BigInteger(1, Base64.getUrlDecoder().decode(oth[i].t)));
                    }
                    keySpec = new RSAMultiPrimePrivateCrtKeySpec(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient, primes);
                } else {
                    keySpec = new RSAPrivateCrtKeySpec(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient);
                }
                privateKey = (RSAPrivateKey) KeyFactory.getInstance(kty).generatePrivate(keySpec);
            } catch (NoSuchAlgorithmException impossible) {
                impossible.printStackTrace();
                return null;
            } catch (InvalidKeySpecException ise) {
                throw new IllegalArgumentException(ise.getMessage(), ise);
            }
            return privateKey;
        }

        void setPrivateKey(RSAPrivateKey privateKey) {
            this.privateKey = privateKey;
            if (privateKey == null) {
                return;
            }
            this.n = encoder.encodeToString(toBytesUnsigned(privateKey.getModulus()));
            this.d = encoder.encodeToString(toBytesUnsigned(privateKey.getPrivateExponent()));
            if (privateKey instanceof RSAPrivateCrtKey) {
                RSAPrivateCrtKey pk = (RSAPrivateCrtKey) privateKey;
                this.e = encoder.encodeToString(toBytesUnsigned(pk.getPublicExponent()));
                this.p = encoder.encodeToString(toBytesUnsigned(pk.getPrimeP()));
                this.q = encoder.encodeToString(toBytesUnsigned(pk.getPrimeQ()));
                this.dp = encoder.encodeToString(toBytesUnsigned(pk.getPrimeExponentP()));
                this.dq = encoder.encodeToString(toBytesUnsigned(pk.getPrimeExponentQ()));
                this.qi = encoder.encodeToString(toBytesUnsigned(pk.getCrtCoefficient()));
            }
            if (privateKey instanceof RSAMultiPrimePrivateCrtKey) {
                RSAMultiPrimePrivateCrtKey multiPrimeKey = (RSAMultiPrimePrivateCrtKey) privateKey;
                RSAOtherPrimeInfo[] primes = multiPrimeKey.getOtherPrimeInfo();
                if (primes == null) {
                    return;
                }
                oth = new RsaOtherPrimesInfo[primes.length];
                for (int i = 0; i < primes.length; i++) {
                    oth[i].r = encoder.encodeToString(toBytesUnsigned(primes[i].getPrime()));
                    oth[i].d = encoder.encodeToString(toBytesUnsigned(primes[i].getExponent()));
                    oth[i].t = encoder.encodeToString(toBytesUnsigned(primes[i].getCrtCoefficient()));
                }
            }
        }
    }

    @Getter
    @Setter
    @ToString
    public static class RsaOtherPrimesInfo {

        /**
         * Prime Factor
         */
        private String r;

        /**
         * Factor CRT Exponent
         */
        private String d;

        /**
         * Factor CRT Coefficient
         */
        private String t;
    }

    @ToString(callSuper = true)
    public static class SymmetricKey extends JsonWebKey<SecretKey> { // AES, DES, 3DES

        static final String KEY_TYPE_OCT = "oct";

        /**
         * Key Value
         */
        @Getter @Setter private String k;

        private transient volatile SecretKey key;

        public SymmetricKey() {
            super(KEY_TYPE_OCT);
        }

        @Override
        public boolean isAsymmetric() {
            return false;
        }

        @Override
        public SecretKey exportKey() {
            if (key == null && k != null) {
                String algorithm = Optional.ofNullable(alg)
                        .map(JsonWebAlgorithm::getJweAlgorithm).map(JsonWebAlgorithm::getAlgorithm)
                        .orElse(JsonWebAlgorithm.AES_ALGORITHM);
                key = new SecretKeySpec(Base64.getUrlDecoder().decode(k), algorithm);
            }
            return key;
        }

        @Override
        public void importKey(SecretKey key) {
            assert key != null;
            this.key = key;
            this.alg = Optional.ofNullable(JsonWebAlgorithm.jweAlgorithmFrom(key.getAlgorithm(), key.getEncoded().length, 0))
                    .map(JsonWebAlgorithm::jwaName)
                    .orElse(null);
            this.k = encoder.encodeToString(key.getEncoded());
        }
    }
}
