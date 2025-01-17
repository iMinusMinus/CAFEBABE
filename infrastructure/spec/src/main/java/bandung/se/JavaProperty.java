package bandung.se;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiredArgsConstructor
@Getter
public class JavaProperty<D, T> {

    protected final String name;

    protected final Field property;

    protected final Method getter;

    protected final Method setter;

    public T get(D obj, boolean onlyGetter, boolean onlyField) {
        if (onlyGetter) {
            return invokeGetter(obj);
        }
        if (onlyField) {
            try {
                return get(obj);
            } catch (NoSuchFieldException nsfe) {
                throw new RuntimeException(nsfe.getMessage(), nsfe);
            }
        }
        return tryGet(obj);
    }

    protected T tryGet(D obj) {
        if (getter != null) {
            return invokeGetter(obj);
        }
        try {
            return get(obj);
        } catch (NoSuchFieldException nsfe) {
            throw new RuntimeException(nsfe.getMessage(), nsfe);
        }
    }

    protected T invokeGetter(D obj) {
        try {
//            getter.setAccessible(true);
            return (T) getter.invoke(obj);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected T get(D obj) throws NoSuchFieldException {
        if (property == null) {
            throw new NoSuchFieldException();
        }
        try {
//            property.setAccessible(true);
            return (T) property.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void set(D obj, T value, boolean onlySetter, boolean onlyField) {
        if (onlySetter) {
            invokeSetter(obj, value);
            return;
        }
        if (onlyField) {
            try {
                set(obj, value);
            } catch (NoSuchFieldException nsfe) {
                throw new RuntimeException(nsfe.getMessage(), nsfe);
            }
            return;
        }
        trySet(obj, value);
    }

    protected void trySet(D obj, T value) {
        if (setter != null) {
            invokeSetter(obj, value);
            return;
        }
        try {
            set(obj, value);
        } catch (NoSuchFieldException nsfe) {
            throw new RuntimeException(nsfe.getMessage(), nsfe);
        }
    }

    protected void invokeSetter(D obj, T value) {
        try {
//            setter.setAccessible(true);
            setter.invoke(obj, value);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected void set(D obj, T value) throws NoSuchFieldException {
        if (property == null) {
            throw new NoSuchFieldException();
        }
        try {
//            property.setAccessible(true);
            property.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
