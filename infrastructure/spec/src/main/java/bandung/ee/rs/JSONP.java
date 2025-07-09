package bandung.ee.rs;

import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JSONP {

    String DEFAULT_CALLBACK = "callback";

    String value() default DEFAULT_CALLBACK;

    final class Literal extends AnnotationLiteral<JSONP> implements JSONP {

        private final String callback;

        public Literal() {
            this(DEFAULT_CALLBACK);
        }

        public Literal(String callback) {
            this.callback = callback;
        }

        @Override
        public String value() {
            return callback;
        }
    }
}
