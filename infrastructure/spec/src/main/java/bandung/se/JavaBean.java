package bandung.se;

import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class JavaBean<T> {

    public static final String DECLARING_HOLDER = "this$0";

    private static final String IS_PREFIX = "is";

    private static final String GET_PREFIX = "get";

    private static final String SET_PREFIX = "set";

    private static final String NOT_PROPERTY = "-";

    private static final Map<Class<?>, JavaBean<?>> CACHE = new WeakHashMap<>();

    private static final Predicate<Method> RW_PREDICATE = m -> Modifier.isPublic(m.getModifiers()) &&
            !Modifier.isStatic(m.getModifiers()) && !Modifier.isAbstract(m.getModifiers());

    private static final Predicate<Field> NOT_STATIC = f -> !Modifier.isStatic(f.getModifiers());

    private JavaBean(Class<T> type, Constructor<T>[] constructors, JavaProperty<T, ?>[] properties) {
        this.type = type;
        this.constructors = constructors;
        this.properties = properties;
    }

    private final Class<T> type;

    private final Constructor<T>[] constructors;

    private final JavaProperty<T, ?>[] properties;

    public T newInstance(Object[] args) throws InstantiationException {
        for (Constructor<T> ctor : constructors) {
            if (ctor.getParameterCount() != args.length) {
                continue;
            }
            int sum = 0;
            for (int i = 0; i < args.length; i++) {
                if (!ctor.getParameterTypes()[i].isInstance(args[i])) {
                    break;
                }
                sum++;
            }
            if (sum == args.length) {
                try {
                    return ctor.newInstance(args);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new InstantiationException(e.getMessage());
                }
            }
        }
        throw new InstantiationException("no matching constructor");
    }

    public boolean isNonStaticMemberClass() {
        return type.isMemberClass() && !Modifier.isStatic(type.getModifiers());
    }

    /**
     * “无参”构造函数
     * @return 非静态内部类返回参数为声明该类型的构造函数
     */
    public Constructor<T> getNoArgsConstructor() {
        boolean flag = isNonStaticMemberClass();
        for (Constructor<T> ctor : constructors) {
            if (flag && ctor.getParameterCount() == 1 && ctor.getParameterTypes()[0].equals(type.getDeclaringClass())) {
                return ctor;
            }
            if (ctor.getParameterCount() == 0) {
                return ctor;
            }
        }
        return null;
    }

    public static <T> JavaBean<T> introspect(Class<T> klazz) {
        return introspect(klazz, false, true);
    }

    public static <T> JavaBean<T> introspect(Class<T> klazz, boolean callSuper, boolean cache) {
        Objects.requireNonNull(klazz);
        JavaBean<T> cached = (JavaBean<T>) CACHE.get(klazz);
        if (cached != null) {
            return cached;
        }
        Constructor<?>[] ctors = klazz.getDeclaredConstructors();
        List<Constructor<?>> instantiator = new ArrayList<>(ctors.length);
        for (Constructor<?> ctor : ctors) {
            if (!Modifier.isPrivate(ctor.getModifiers())) {
                instantiator.add(ctor);
            }
        }
        boolean flag = JvmIntrospector.isRecord(klazz);
        Set<String> recordFields = flag ? Arrays.stream(ctors[0].getParameters())
                .map(Parameter::getName).collect(Collectors.toSet()) : null;
        List<JavaProperty<T, ?>> properties = new ArrayList<>();
        Class<?> current = klazz;
        do {
            Map<String, Field> fields = Arrays.stream(current.getDeclaredFields()).filter(NOT_STATIC)
                    .collect(Collectors.toMap(Field::getName, Function.identity()));
            Method[] methods = current.getDeclaredMethods();
            Map<String, Pair<Method, Method>> tmp = new HashMap<>(fields.size());
            for (Method method : methods) {
                if (!RW_PREDICATE.test(method)) {
                    continue;
                }
                if (method.getParameterCount() == 0) {
                    if (flag && recordFields.contains(method.getName())) { // record没有setter方法，但toString等无参方法不能被识别为getter
                        Field field = fields.remove(method.getName());
                        properties.add(new JavaProperty<>(method.getName(), field, method, null));
                        continue;
                    }
                    findGetter(method, properties, fields, tmp);
                } else if (method.getParameterCount() == 1) {
                    findSetter(method, properties, fields, tmp);
                }
            }
            for (Map.Entry<String, Field> entry : fields.entrySet()) {
                Pair<Method, Method> readWrite = tmp.remove(entry.getKey());
                if (readWrite != null) { // field存在，且有getter、setter其中之一
                    properties.add(new JavaProperty<>(entry.getKey(), entry.getValue(), readWrite.getLeft(), readWrite.getRight()));
                } else if (Modifier.isPublic(entry.getValue().getModifiers())) {
                    properties.add(new JavaProperty<>(entry.getKey(), entry.getValue(), null, null));
                }
            }
            for (Map.Entry<String, Pair<Method, Method>> entry : tmp.entrySet()) { // 仅有getter、setter其中之一
                if (entry.getValue().getLeft() != null) {
                    properties.add(new JavaProperty<>(entry.getKey(), null, entry.getValue().getLeft(), null));
                }
                if (entry.getValue().getRight() != null) {
                    properties.add(new JavaProperty<>(entry.getKey(), null, null, entry.getValue().getRight()));
                }
            }
            current = current.getSuperclass();
        } while (callSuper && current != Object.class);
        cached = new JavaBean<>(klazz, instantiator.toArray(new Constructor[0]), properties.toArray(new JavaProperty[0]));
        if (cache) {
            CACHE.put(klazz, cached);
        }
        return cached;
    }

    private static String asPropertyName(Method method) {
        char[] cs = method.getName().toCharArray();
        boolean boolType = method.getReturnType() == Boolean.TYPE && method.getParameterCount() == 0;
        int min = boolType ? IS_PREFIX.length() : GET_PREFIX.length();
        if (cs.length < min + 1) {
            return NOT_PROPERTY;
        }
        cs[min] = Character.toLowerCase(cs[min]);
        return new String(cs, min, cs.length - min);
    }

    private static <T> void findGetter(Method method, List<JavaProperty<T, ?>> properties,
                                   Map<String, Field> fields, Map<String, Pair<Method, Method>> tmp) {
        if (!method.getName().startsWith(GET_PREFIX) && !method.getName().startsWith(IS_PREFIX)) {
            return;
        }
        String name = asPropertyName(method);
        Field field = fields.get(name);
        if (field != null && !method.getReturnType().equals(field.getType())) {
            return;
        }
        Pair<Method, Method> pair = tmp.get(name);
        if (pair != null && pair.getRight() != null) {
            fields.remove(name);
            tmp.remove(name);
            properties.add(new JavaProperty<>(name, field, method, pair.getRight()));
        } else {
            tmp.put(name, new Pair<>(method, null));
        }
    }

    private static <T> void findSetter(Method method, List<JavaProperty<T, ?>> properties,
                                   Map<String, Field> fields, Map<String, Pair<Method, Method>> tmp) {
        if (!method.getName().startsWith(SET_PREFIX)) {
            return;
        }
        String name = asPropertyName(method);
        Field field = fields.get(name);
        if (field != null && !method.getParameterTypes()[0].equals(field.getType())) {
            return;
        }
        Pair<Method, Method> pair = tmp.get(name);
        if (pair != null && pair.getLeft() != null) {
            fields.remove(name);
            tmp.remove(name);
            properties.add(new JavaProperty<>(name, field, pair.getLeft(), method));
        } else {
            tmp.put(name, new Pair<>(null, method));
        }
    }
}
