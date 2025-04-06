package std.ietf.http.jose;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;

public class JsonWebKeyDeserializer implements JsonbDeserializer<JsonWebKey> {

    @Override
    public JsonWebKey deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonWebKey bean = null;
        JsonObject jsonObject = parser.getObject();
        String keyType = jsonObject.getString(JsonWebKey.PARAM_KEY_TYPE);
        if (JsonWebKeySet.EllipticCurveJwk.KEY_TYPE_EC.equals(keyType)) {
            JsonWebKeySet.EllipticCurveJwk ec = new JsonWebKeySet.EllipticCurveJwk();
            ec.setCrv(getKey(jsonObject, "crv"));
            ec.setX(getKey(jsonObject, "x"));
            ec.setY(getKey(jsonObject, "y"));
            ec.setD(getKey(jsonObject, "d"));
            bean = ec;
        } else if (JsonWebKeySet.RsaJwk.KEY_TYPE_RSA.equals(keyType)) {
            JsonWebKeySet.RsaJwk rsa = new JsonWebKeySet.RsaJwk();
            rsa.setN(getKey(jsonObject, "n"));
            rsa.setE(getKey(jsonObject, "e"));
            rsa.setD(getKey(jsonObject, "d"));
            rsa.setP(getKey(jsonObject, "p"));
            rsa.setQ(getKey(jsonObject, "q"));
            rsa.setDp(getKey(jsonObject, "dp"));
            rsa.setDq(getKey(jsonObject, "dq"));
            rsa.setQi(getKey(jsonObject, "qi"));
            JsonArray oth = jsonObject.getJsonArray("oth");
            if (oth != null && oth.size() > 0) {
                JsonWebKeySet.RsaOtherPrimesInfo[] rsaOtherPrimesInfos = new JsonWebKeySet.RsaOtherPrimesInfo[oth.size()];
                for (int i = 0; i < oth.size(); i++) {
                    JsonObject element = oth.getJsonObject(i);
                    rsaOtherPrimesInfos[i] = new JsonWebKeySet.RsaOtherPrimesInfo();
                    rsaOtherPrimesInfos[i].setR(getKey(element, "r"));
                    rsaOtherPrimesInfos[i].setD(getKey(element, "d"));
                    rsaOtherPrimesInfos[i].setT(getKey(element, "t"));
                }
                rsa.setOth(rsaOtherPrimesInfos);
            }
            bean = rsa;
        } else if (JsonWebKeySet.SymmetricKey.KEY_TYPE_OCT.equals(keyType)) {
            JsonWebKeySet.SymmetricKey symmetricKey = new JsonWebKeySet.SymmetricKey();
            symmetricKey.setK(getKey(jsonObject, "k"));
        } else {
            throw new JsonbException("unknown JsonWebKey type");
        }
        bean.setAlg(getKey(jsonObject, "alg"));
        JsonValue ext = jsonObject.get("ext");
        if (ext != null) {
            bean.setExt(ext.equals(JsonValue.TRUE));
        }
        bean.setKid(jsonObject.getString("kid", null));
        bean.setUse(jsonObject.getString("use", null));
        bean.setX5t(jsonObject.getString("x5t", null));
        bean.setX509Thumbprint(jsonObject.getString("x5t#S256", null));
        JsonArray keyOps = jsonObject.getJsonArray("key_ops");
        if (keyOps != null && keyOps.size() > 0) {
            String[] ops = new String[keyOps.size()];
            for (int j = 0; j < keyOps.size(); j++) {
                ops[j] = keyOps.getString(j);
            }
            bean.setKey_ops(ops);
        }
        JsonArray x5c = jsonObject.getJsonArray("x5c");
        if (x5c != null && x5c.size() > 0) {
            String[] chain = new String[x5c.size()];
            for (int k = 0; k < x5c.size(); k++) {
                chain[k] = x5c.getString(k);
            }
            bean.setX5c(chain);
        }
        String x5u = jsonObject.getString("x5u", null);
        if (x5u != null) {
            try {
                bean.setX5u(new URL(x5u));
            } catch (MalformedURLException mfe) {
                throw new JsonbException(mfe.getMessage(), mfe);
            }
        }
        return bean;
    }

    private String getKey(JsonObject jsonObject, String key) {
        JsonValue value = jsonObject.get(key);
        if (value == null || value == JsonValue.NULL) {
            return null;
        } else if (value instanceof JsonString) {
            return ((JsonString) value).getString();
        } else if (value instanceof JsonArray) {
            JsonArray array = ((JsonArray) value);
            byte[] data = new byte[array.size()];
            for (int i = 0; i < array.size(); i++) {
                if (array.get(i) instanceof JsonNumber) {
                    data[i] = (byte) ((JsonNumber) array.get(i)).intValue();
                } else {
                    throw new JsonbException("bad value type");
                }
            }
            return JsonWebToken.ENCODER.encodeToString(data);
        }
        throw new JsonbException("bad value type");
    }


}
