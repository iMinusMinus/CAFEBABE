package bandung.se;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class Polymorphism<B> {

    private final B binding;

    private final Class<?> type;

    @Setter
    private Map<B, Polymorphism<B>> subClasses = Collections.emptyMap();

}
