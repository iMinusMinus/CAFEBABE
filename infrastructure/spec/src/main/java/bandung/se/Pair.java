package bandung.se;

import lombok.Getter;

import java.util.Map;

@Getter
public class Pair<L, R> implements Map.Entry<L, R> {

    private final L left;

    private R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }
    @Override
    public L getKey() {
        return left;
    }

    @Override
    public R getValue() {
        return right;
    }

    @Override
    public R setValue(R value) {
        R old = this.right;
        this.right = value;
        return old;
    }
}
