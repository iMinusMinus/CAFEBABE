package bandung.se;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Reflections {

    private static final Logger log = Logger.getLogger(Reflections.class.getName());

    public static final String GET_PREFIX = "get";
    public static final String SET_PREFIX = "set";
    public static final String IS_PREFIX = "is";

    private static final Map<Class<?>, Class<?>> primitiveClassMapping = new HashMap<>(8);

    static {
        primitiveClassMapping.put(boolean.class, Boolean.class);
        primitiveClassMapping.put(byte.class, Byte.class);
        primitiveClassMapping.put(char.class, Character.class);
        primitiveClassMapping.put(short.class, Short.class);
        primitiveClassMapping.put(int.class, Integer.class);
        primitiveClassMapping.put(long.class, Long.class);
        primitiveClassMapping.put(float.class, Float.class);
        primitiveClassMapping.put(double.class, Double.class);
    }

    public static Field findField(Class<?> klazz, Predicate<Field> predicate, boolean bottomUp) {
        Objects.requireNonNull(klazz);
        Field[] fields = klazz.getDeclaredFields();
        for (Field field : fields) {
            if (predicate.test(field)) {
                log.log(Level.FINE, "got field from class[{0}]", klazz);
                return field;
            }
        }
        if (!bottomUp) {
            return null;
        }
        Class<?> parent = klazz.getSuperclass();
        if (parent != Object.class) {
            log.log(Level.FINE, "try got field from class[{0}]", parent);
            Field fromSuper = findField(parent, predicate, true);
            if (fromSuper != null) {
                return fromSuper;
            }
        }
        Class<?>[] interfaces = klazz.getInterfaces();
        for (Class<?> intf : interfaces) {
            Field fromIntf = findField(intf, predicate, true);
            if (fromIntf != null) {
                return fromIntf;
            }
        }
        return null;
    }

    public static Method findMethod(Class<?> klazz, Predicate<Method> predicate, boolean bottomUp) {
        Objects.requireNonNull(klazz);
        Method[] methods = klazz.getDeclaredMethods();
        for (Method method : methods) {
            if (predicate.test(method)) {
                log.log(Level.FINE, "got method from class[{0}]", klazz);
                return method;
            }
        }
        if (!bottomUp) {
            return null;
        }
        Class<?> parent = klazz.getSuperclass();
        if (parent != Object.class) {
            log.log(Level.FINE, "try got method from class[{0}]", parent);
            Method fromSuper = findMethod(parent, predicate, true);
            if (fromSuper != null) {
                return fromSuper;
            }
        }
        Class<?>[] interfaces = klazz.getInterfaces();
        for (Class<?> intf : interfaces) {
            Method fromIntf = findMethod(intf, predicate, true);
            if (fromIntf != null) {
                return fromIntf;
            }
        }
        return null;
    }

    /**
     * 使用MethodHandle方式读取属性值
     *
     * @param bean java bean对象
     * @param propertyName 属性名称
     * @param propertyType 属性类型
     * @return 属性值
     * @throws java.lang.NoSuchMethodException getter方法不存在
     * @throws java.lang.IllegalAccessException getter方法非public
     */
    public static <T> T readProperty(Object bean, String propertyName, Class<T> propertyType) throws Throwable {
        Objects.requireNonNull(bean);
        Objects.requireNonNull(propertyName);
        Objects.requireNonNull(propertyType);
        String prefix = boolean.class == propertyType ? IS_PREFIX : GET_PREFIX;
        MethodHandle mh = MethodHandles.lookup()
                .findVirtual(
                        bean.getClass(),
                        propertyNameToGetterSetterName(prefix, propertyName),
                        MethodType.methodType(propertyType)
                );
        if (mh == null) {
            throw new RuntimeException("Not find getter method for " + propertyName);
        }
        return (T) mh.invoke(bean);
    }

    /**
     * 使用MethodHandle写属性
     *
     * @param bean java bean对象
     * @param propertyName 属性名称
     * @param propertyType 属性类型
     * @param value 属性值
     * @throws java.lang.NoSuchMethodException setter方法不存在
     * @throws java.lang.IllegalAccessException setter方法非public
     */
    public static void writeProperty(Object bean, String propertyName, Class<?> propertyType, Object value) throws Throwable {
        Objects.requireNonNull(bean);
        Objects.requireNonNull(propertyName);
        Objects.requireNonNull(propertyType);
        MethodHandle mh = MethodHandles.lookup()
                .findVirtual(
                        bean.getClass(),
                        propertyNameToGetterSetterName(SET_PREFIX, propertyName),
                        MethodType.methodType(void.class, propertyType)
                );
        if (mh == null) {
            throw new RuntimeException("Not find setter method for " + propertyName);
        }
        mh.invoke(bean, value);
    }

    private static String propertyNameToGetterSetterName(String prefix, String propertyName) {
        char[] array = propertyName.toCharArray();
        array[0] = Character.toUpperCase(array[0]);
        return prefix + new String(array);
    }

    public static Class<?> loadClass(ClassLoader classLoader, String name) {
        try {
            return classLoader.loadClass(name);
        } catch (ClassNotFoundException cnfe) {
            log.log(Level.INFO, "class[{0}] not found", name);
            return null;
        }
    }

    public static Class<?> asWrapperClass(Class<?> c) {
        assert c != null && c.isPrimitive();
        return primitiveClassMapping.get(c);
    }

    public static Class<?> searchComponentType(GenericArrayType fieldType, Stack<Type> declareChain) { // class Klazz<T> { T[] array;}
        Type type = fieldType.getGenericComponentType();
        Class<?> componentType = Object.class;
        if (type instanceof TypeVariable) {
            Type tmp = searchRuntimeType((TypeVariable) type, declareChain);
            componentType = typeToClass(tmp, declareChain);
        }
        return componentType;
    }

    public static Type searchRuntimeType(TypeVariable fieldType, Stack<Type> declareChain) {
        Type declaring = declareChain.pop();
        if (declaring instanceof Class) {
            declareChain.push(((Class<?>) declaring).getGenericSuperclass());
            Type result =  searchRuntimeType(fieldType, declareChain);
            declareChain.push(declaring);
            return result;
        } else if (declaring instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) declaring;
            if (typeToClass(pt, declareChain) == fieldType.getGenericDeclaration()) {
                for (int i = 0; i < pt.getActualTypeArguments().length; i++) {
                    Type tbd = pt.getActualTypeArguments()[i];
                    if (tbd instanceof TypeVariable) {
                        Type that = searchRuntimeType((TypeVariable) tbd, declareChain);
                        if (that instanceof Class) {
                            declareChain.push(declaring);
                            return that;
                        }
                    } else if (tbd instanceof Class) {
                        declareChain.push(declaring);
                        return (Class) tbd;
                    }
                }
            }
        }
        declareChain.push(declaring);
        return fieldType;
    }

    public static Class<?> typeToClass(Type type, Stack<Type> declaring) {
        if (type instanceof Class) { //  concrete parameter type
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) { // Map<String, List<String> --> rawType: Map.class, actualTypeArguments[0]: String.class, actualTypeArguments[1]: ParameterizedType(List<String>)
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof TypeVariable) {
            Type resolved = searchRuntimeType((TypeVariable) type, declaring);
            if (resolved != type) {
                return (Class<?>) resolved;
            }
            Type[] bounds = ((TypeVariable<?>) type).getBounds();
            Class<?> best = Object.class; // raw type
            for (Type bound : bounds) {
                Class<?> candidate = typeToClass(bound, declaring);
                if (candidate != Object.class && best.isAssignableFrom(candidate)) {
                    best = candidate;
                }
            }
            return best;
        } else if (type instanceof WildcardType) {
            Type[] bounds = ((WildcardType) type).getUpperBounds();
            Class<?> best = Object.class; // wildcard parameter type without bounds
            for (Type bound : bounds) { //  bounded parameter type
                best = typeToClass(bound, declaring);
                if (best != Object.class) {
                    return best;
                }
            }
            return best;
        } else { // T[] array --> genericComponentType: TypeVariable(T)
            // return Class.forName("[L" + typeToClass(componentType).getName().replace("[.]", "/") + ";");
            return Object[].class; // Array.class.isArray() --> false
        }
    }

}
