package std.ietf.http.jose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
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

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static final class EllipticCurveJwk extends JsonWebKey {

        /**
         * Curve
         */
        private String crv;

        /**
         * X Coordinate
         */
        private String x;

        /**
         * Y Coordinate
         */
        private String y;

        /**
         * ECC Private Key
         */
        private String d; // private key only

        public EllipticCurveJwk() {
            this(null, null);
        }

        public EllipticCurveJwk(ECPublicKey publicKey, ECPrivateKey privateKey) {
            super("EC");
            setPublicKey(publicKey);
            setPrivateKey(privateKey);
        }

        @Override
        public Key asKey() {
            ECParameterSpec parameter = obtainParameter();
            if (parameter == null) {
                return null;
            }
            return d == null ? getPublicKey(parameter) : getPrivateKey(parameter);
        }

        @Override
        public void withKey(Key key) {
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
            ECPoint point = new ECPoint(new BigInteger(1, Base64.getUrlDecoder().decode(x)), new BigInteger(1, Base64.getUrlDecoder().decode(y)));
            ECPublicKeySpec keySpec = new ECPublicKeySpec(point, parameter);
            try {
                return (ECPublicKey) KeyFactory.getInstance(kty).generatePublic(keySpec);
            } catch (NoSuchAlgorithmException impossible) {
                impossible.printStackTrace();
                return null;
            } catch (InvalidKeySpecException ike) {
                throw new IllegalArgumentException(ike.getMessage(), ike);
            }
        }

        void setPublicKey(ECPublicKey publicKey) {
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
            if (d == null) {
                return null;
            }
            ECPrivateKeySpec keySpec = new ECPrivateKeySpec(new BigInteger(1, Base64.getUrlDecoder().decode(d)), parameter);
            try {
                return (ECPrivateKey) KeyFactory.getInstance(kty).generatePrivate(keySpec);
            } catch (NoSuchAlgorithmException impossible) {
                impossible.printStackTrace();
                return null;
            } catch (InvalidKeySpecException ike) {
                throw new IllegalArgumentException(ike.getMessage(), ike);
            }
        }

        void setPrivateKey(ECPrivateKey privateKey) {
            if (privateKey == null) {
                return;
            }
            this.crv = Optional.ofNullable(Curve.getInstance(privateKey.getParams())).map(Curve::getName).orElse(null);
            int bytesToOutput = (privateKey.getParams().getCurve().getField().getFieldSize() + 7) / 8;
            this.d = encodeCoordinate(toBytesUnsigned(privateKey.getS()), bytesToOutput);
        }

    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static final class RsaJwk extends JsonWebKey {

        /**
         * Modulus
         */
        private String n;

        /**
         * Exponent
         */
        private String e;

        /**
         * Private Exponent
         */
        private String d; // private key only

        /**
         * First Prime Factor
         */
        private String p; // private key only

        /**
         * Second Prime Factor
         */
        private String q; // private key only

        /**
         * First Factor CRT Exponent
         */
        private String dp; // private key only

        /**
         * Second Factor CRT ExponentSecond Factor CRT Exponent
         */
        private String dq; // private key only

        /**
         * First CRT Coefficient
         */
        private String qi; // private key only

        /**
         * Other Primes Info
         */
        private OtherPrimesInfo[] oth;

        public RsaJwk() {
            this(null, null);
        }

        public RsaJwk(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
            super("RSA");
            setPublicKey(publicKey);
            setPrivateKey(privateKey);
        }

        @Override
        public Key asKey() {
            return d == null ? getPublicKey() : getPrivateKey();
        }

        @Override
        public void withKey(Key key) {
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
            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
            BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(e));
            try {
                return (RSAPublicKey) KeyFactory.getInstance(kty).generatePublic(new RSAPublicKeySpec(modulus, exponent));
            } catch (NoSuchAlgorithmException impossible) {
                impossible.printStackTrace();
                return null;
            } catch (InvalidKeySpecException ise) {
                throw new IllegalArgumentException(ise.getMessage(), ise);
            }
        }

        void setPublicKey(RSAPublicKey publicKey) {
            if (publicKey == null) {
                return;
            }
            this.n = Base64.getUrlEncoder().encodeToString(toBytesUnsigned(publicKey.getModulus()));
            this.e = Base64.getUrlEncoder().encodeToString(toBytesUnsigned(publicKey.getPublicExponent()));
        }

        RSAPrivateKey getPrivateKey() {
            if (d == null) {
                return null;
            }
            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
            BigInteger privateExponent = new BigInteger(1, Base64.getUrlDecoder().decode(d));
            try {
                if (p == null) {
                    return (RSAPrivateKey) KeyFactory.getInstance(kty).generatePrivate(new RSAPrivateKeySpec(modulus, privateExponent));
                }
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
                return (RSAPrivateKey) KeyFactory.getInstance(kty).generatePrivate(keySpec);
            } catch (NoSuchAlgorithmException impossible) {
                impossible.printStackTrace();
                return null;
            } catch (InvalidKeySpecException ise) {
                throw new IllegalArgumentException(ise.getMessage(), ise);
            }
        }

        void setPrivateKey(RSAPrivateKey privateKey) {
            if (privateKey == null) {
                return;
            }
            this.n = Base64.getUrlEncoder().encodeToString(toBytesUnsigned(privateKey.getModulus()));
            this.d = Base64.getUrlEncoder().encodeToString(toBytesUnsigned(privateKey.getPrivateExponent()));
            if (privateKey instanceof RSAPrivateCrtKey) {
                RSAPrivateCrtKey pk = (RSAPrivateCrtKey) privateKey;
                this.e = Base64.getUrlEncoder().encodeToString(toBytesUnsigned(pk.getPublicExponent()));
                this.p = Base64.getUrlEncoder().encodeToString(toBytesUnsigned(pk.getPrimeP()));
                this.q = Base64.getUrlEncoder().encodeToString(toBytesUnsigned(pk.getPrimeQ()));
                this.dp = Base64.getUrlEncoder().encodeToString(toBytesUnsigned(pk.getPrimeExponentP()));
                this.dq = Base64.getUrlEncoder().encodeToString(toBytesUnsigned(pk.getPrimeExponentQ()));
                this.qi = Base64.getUrlEncoder().encodeToString(toBytesUnsigned(pk.getCrtCoefficient()));
            }
            if (privateKey instanceof RSAMultiPrimePrivateCrtKey) {
                RSAMultiPrimePrivateCrtKey multiPrimeKey = (RSAMultiPrimePrivateCrtKey) privateKey;
                RSAOtherPrimeInfo[] primes = multiPrimeKey.getOtherPrimeInfo();
                if (primes == null) {
                    return;
                }
                oth = new OtherPrimesInfo[primes.length];
                for (int i = 0; i < primes.length; i++) {
                    oth[i].r = Base64.getUrlEncoder().encodeToString(toBytesUnsigned(primes[i].getPrime()));
                    oth[i].d = Base64.getUrlEncoder().encodeToString(toBytesUnsigned(primes[i].getExponent()));
                    oth[i].t = Base64.getUrlEncoder().encodeToString(toBytesUnsigned(primes[i].getCrtCoefficient()));
                }
            }
        }
    }

    @Getter
    @Setter
    @ToString
    public static class OtherPrimesInfo {

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

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class SymmetricKey extends JsonWebKey<SecretKeySpec> { // AES, DES, 3DES

        /**
         * Key Value
         */
        private String k;

        public SymmetricKey() {
            super("oct");
        }

        @Override
        public SecretKeySpec asKey() {
            String algorithm = Optional.ofNullable(JsonWebAlgorithm.getInstance(alg)).map(JsonWebAlgorithm::getAlgorithm).orElse(null);
            return new SecretKeySpec(Base64.getUrlDecoder().decode(k), algorithm);
        }

        @Override
        public void withKey(SecretKeySpec key) {
            this.alg = Optional.ofNullable(JsonWebAlgorithm.getEncryptAlgorithm(key.getAlgorithm())).map(JsonWebAlgorithm::getName).orElse(null);
            this.k = Base64.getUrlEncoder().encodeToString(key.getEncoded());
        }
    }
}
