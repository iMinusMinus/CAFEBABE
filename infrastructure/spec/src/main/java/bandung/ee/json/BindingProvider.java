package bandung.ee.json;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.spi.JsonbProvider;
import javax.json.spi.JsonProvider;

import java.util.Objects;
import java.util.Optional;

/**
 * json binding provider
 *
 * @author iMinusMinus
 * @date 2024-10-20
 */
public class BindingProvider extends JsonbProvider {

    public static final String JSONB_ANNOTATION_INTROSPECTOR = "jsonb.annotation-introspector";

    public static final String JSONB_SERIALIZER_CACHE = "jsonb.serializer-cache";

    public static final String JSONB_DESERIALIZER_CACHE = "jsonb.deserializer-cache";

    @Override
    public JsonbBuilder create() {
        return new BindingBuilder();
    }

    private static class BindingBuilder implements JsonbBuilder {

        private JsonbConfig config = new JsonbConfig();

        private JsonProvider jsonpProvider;

        @Override
        public JsonbBuilder withConfig(JsonbConfig config) {
            Objects.requireNonNull(config);
            this.config = config;
            return this;
        }

        @Override
        public JsonbBuilder withProvider(JsonProvider jsonpProvider) {
            Objects.requireNonNull(jsonpProvider);
            this.jsonpProvider = jsonpProvider;
            return this;
        }

        @Override
        public Jsonb build() {
            return new ObjectJsonMapper(config, Optional.ofNullable(jsonpProvider).orElse(JsonProvider.provider()));
        }
    }
}
