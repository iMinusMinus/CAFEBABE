package bandung.ee.json;

import bandung.se.Polymorphism;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbAnnotation;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbNumberFormat;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeInfo;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jakarta.json.bind.annotation.JsonbVisibility;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Jsob注解处理
 *
 * @see com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationIntrospector
 * @see org.eclipse.yasson.internal.AnnotationIntrospector
 */
public class AnnotationIntrospector {

    private static final Logger log = Logger.getLogger(AnnotationIntrospector.class.getName());

    protected final Function<Class<?>, Object> instantiator;

    public AnnotationIntrospector() {
        this.instantiator = klazz -> {
            Constructor[] ctors = klazz.getDeclaredConstructors();
            for (Constructor ctor : ctors) {
                if ((Modifier.isPublic(ctor.getModifiers()) || Modifier.isProtected(ctor.getModifiers())) &&
                        ctor.getParameterCount() == 0) {
                    try {
                        Object[] args = new Object[0];
                        return ctor.newInstance(args);
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new JsonbException(e.getMessage(), e);
                    }
                }
            }
            throw new JsonbException("default object instantiator only support public or protected with no-arg constructor");
        };
    }

    /**
     *
     * @param instantiator 支持通过CDI注入构造参数的对象生成器
     */
    public AnnotationIntrospector(Function<Class<?>, Object> instantiator) {
        this.instantiator = instantiator;
    }

    public boolean isAnnotationBundle(Annotation ann) {
        return ann.annotationType().isAnnotationPresent(JsonbAnnotation.class);
    }

    public <E extends Enum<E>> String[] findEnumValues(Class<E> annotatedClass, Enum<E>[] enumValues, String[] names) {
        Map<String, String> enumToPropertyMap = new LinkedHashMap<>();
        for (Field field : annotatedClass.getDeclaredFields()) {
            JsonbProperty property = field.getAnnotation(JsonbProperty.class);
            if (property != null && !property.value().isEmpty()) {
                enumToPropertyMap.put(field.getName(), property.value());
            }
        }
        for (int i = 0; i < enumValues.length; i++) {
            String name = enumValues[i].name();
            String value = enumToPropertyMap.get(name);
            if (value != null) {
                names[i] = value;
            }
        }
        return names;
    }

    <E extends Enum<E>> String[] findEnumValues(Class<E> annotatedClass) {
        Field[] fields = annotatedClass.getDeclaredFields();
        String[] names = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            JsonbProperty property = fields[i].getAnnotation(JsonbProperty.class);
            if (property != null && !property.value().isEmpty()) {
                names[i] = property.value();
            } else {
                names[i] = fields[i].getName();
            }
        }
        return names;
    }

    public boolean hasIgnoreMarker(AccessibleObject ann) {
        boolean ignore = ann.isAnnotationPresent(JsonbTransient.class);
        if (!ignore) {
            return false;
        }
        checkTransientIncompatible(ann);
        return true;
    }

    /**
     * 字段上有JsonbTransient注解，字段及其getter/setter方法不能有其他Jsonb注解；
     * getter方法上有JsonbTransient注解，字段和getter方法不能有其他Jsonb注解；
     * setter方法上有JsonbTransient注解，字段和setter方法不能有其他Jsonb注解
     * @param ann field or method
     */
    void checkTransientIncompatible(AccessibleObject ann) {
        Annotation[] annotations = ann.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (!JsonbTransient.class.equals(annotation.annotationType()) &&
                    annotation.annotationType().isAnnotationPresent(JsonbAnnotation.class)) {
                throw new JsonbException("JsonbTransient annotated class/field/method can't has other jsonb annotation");
            }
        }
    }

    protected JsonbDateFormat findDateTimeFormat(AnnotatedElement ann, boolean searchDeclaringTarget) {
        JsonbDateFormat fmt = ann.getAnnotation(JsonbDateFormat.class);
        if (fmt == null) {
            if (searchDeclaringTarget) {
                if (ann instanceof Field) {
                    return findDateTimeFormat(((Field) ann).getDeclaringClass(), true);
                } else if (ann instanceof Method) {
                    return findDateTimeFormat(((Method) ann).getDeclaringClass(), true);
                } else if (ann instanceof Class) {
                    return findDateTimeFormat(((Class<?>) ann).getPackage(), true);
                }
            }
            return null;
        }
        return fmt;
    }

    protected JsonbDateFormat findDateTimeFormat(AnnotatedElement ann) {
        return findDateTimeFormat(ann, false);
    }

    protected NumberFormat findNumberFormat(AnnotatedElement ann, boolean searchDeclaringTarget) {
        JsonbNumberFormat fmt = ann.getAnnotation(JsonbNumberFormat.class);
        if (fmt == null || fmt.value().isEmpty()) {
            if (searchDeclaringTarget) {
                if (ann instanceof Field) {
                    return findNumberFormat(((Field) ann).getDeclaringClass(), true);
                } else if (ann instanceof Method) {
                    return findNumberFormat(((Method) ann).getDeclaringClass(), true);
                } else if (ann instanceof Class) {
                    return findNumberFormat(((Class<?>) ann).getPackage(), true);
                }
            }
            return null;
        }
        Locale locale = fmt.locale().isEmpty() || JsonbNumberFormat.DEFAULT_LOCALE.equals(fmt.locale()) ?
                Locale.getDefault() :
                new Locale(fmt.locale());
        return new DecimalFormat(fmt.value(), new DecimalFormatSymbols(locale));
    }

    protected NumberFormat findNumberFormat(AnnotatedElement ann) {
        return findNumberFormat(ann, false);
    }

    public JsonbSerializer<?> findSerializer(AnnotatedElement a) {
        JsonbTypeSerializer typeSerializer = a.getAnnotation(JsonbTypeSerializer.class);
        return typeSerializer == null ? null : instantiate(a, JsonbSerializer.class, typeSerializer.value());
    }

    public String[] findSerializationPropertyOrder(AnnotatedElement ann) {
        JsonbPropertyOrder orders = ann.getAnnotation(JsonbPropertyOrder.class);
        return orders == null ? null : orders.value();
    }

    public String findNameForSerialization(AnnotatedElement ann) {
        JsonbProperty property = ann.getAnnotation(JsonbProperty.class);
        return property == null || property.value().isEmpty() ? null : property.value();
    }

    protected Boolean findNillable(AnnotatedElement ann) {
        return findNillable(ann, false);
    }

    protected Boolean findNillable(AnnotatedElement ann, boolean searchDeclaringTarget) {
        JsonbNillable nillable = ann.getDeclaredAnnotation(JsonbNillable.class);
        if (nillable == null) {
            Optional<JsonbProperty> opt = Optional.ofNullable(ann.getDeclaredAnnotation(JsonbProperty.class));
            if (opt.isPresent()) {
                return opt.get().nillable();
            }
            if (searchDeclaringTarget) {
                if (ann instanceof Field) {
                    return findNillable(((Field) ann).getDeclaringClass(), true);
                } else if (ann instanceof Method) {
                    return findNillable(((Method) ann).getDeclaringClass(), true);
                } else if (ann instanceof Class) {
                    return findNillable(((Class<?>) ann).getPackage(), true);
                }
            }
            return null;
        }
        return nillable.value();
    }

    public <T> JsonbDeserializer<T> findDeserializer(AnnotatedElement ann) {
        JsonbTypeDeserializer typeDeserializer = ann.getAnnotation(JsonbTypeDeserializer.class);
        return typeDeserializer == null ? null : instantiate(ann, JsonbDeserializer.class, typeDeserializer.value());
    }

    public String findNameForDeserialization(AnnotatedElement ann) {
        JsonbProperty property = ann.getAnnotation(JsonbProperty.class);
        return property == null || property.value().isEmpty() ? null : property.value();
    }

    public <S, T> JsonbAdapter<S, T> findAdapter(AnnotatedElement ann) {
        JsonbTypeAdapter typeAdapter = ann.getAnnotation(JsonbTypeAdapter.class);
        return typeAdapter == null ? null : instantiate(ann, JsonbAdapter.class, typeAdapter.value());
    }

    public <T> Executable findCreator(Class<T> klazz) {
        Executable candidate = null;
        Constructor[] ctors = klazz.getDeclaredConstructors();
        for (Constructor ctor : ctors) {
            if (ctor.isAnnotationPresent(JsonbCreator.class)) {
                if (candidate != null) {
                    throw new JsonbException("Too many JsonbCreator candidates at class [" + klazz.getName() + "]");
                }
                candidate = ctor;
            }
        }
        Method[] methods = klazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(JsonbCreator.class)) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    throw new JsonbException("JsonbCreator candidate factory method not static");
                }
                if (!klazz.equals(method.getReturnType())) {
                    throw new JsonbException("JsonbCreator candidate factory method with bad return type");
                }
                if (candidate != null) {
                    throw new JsonbException("Too many JsonbCreator candidates at class [" + klazz.getName() + "]");
                }
                candidate = method;
            }
        }
        // maybe we should consider ConstructorProperties as candidate Creator
        return candidate;
    }

    Map<String, Integer> resolveCreatorParametersName(Executable executable) {
        if (executable.getParameterCount() == 0) {
            return Collections.emptyMap();
        }
        Map<String, Integer> nameIndexPairs = new HashMap<>(executable.getParameterCount());
        Parameter[] parameters = executable.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            JsonbProperty alias = parameters[i].getDeclaredAnnotation(JsonbProperty.class);
            if (alias != null && !alias.value().isEmpty()) {
                nameIndexPairs.put(alias.value(), i);
            } else {
                nameIndexPairs.put(parameters[i].getName(), i);
            }
        }
        return nameIndexPairs;
    }

    protected PropertyVisibilityStrategy findVisibility(AnnotatedElement ann) {
        return findVisibility(ann, false);
    }

    protected PropertyVisibilityStrategy findVisibility(AnnotatedElement ann, boolean searchDeclareTarget) {
        JsonbVisibility visibility = ann.getAnnotation(JsonbVisibility.class);
        if (visibility != null) {
            return  instantiate(visibility.value());
        } else if (searchDeclareTarget && ann instanceof Class) {
            return findVisibility(((Class<?>) ann).getPackage(), true);
        }
        return null;
    }

    protected Polymorphism<String> findSubtypes(Class<?> ancestor) {
        return findSubtypes(ancestor, new HashMap<>());
    }

    protected Polymorphism<String> findSubtypes(Class<?> ancestor, Map<String, Class<?>> children) {
        JsonbTypeInfo annotation = ancestor.getAnnotation(JsonbTypeInfo.class);
        if (annotation == null) {
            return null;
        }
        Polymorphism<String> polymorphism = new Polymorphism(annotation.key(), ancestor);
        Map<String, Polymorphism<String>> subClasses = new HashMap<>();
        JsonbSubtype[] values = annotation.value();
        for (JsonbSubtype maybe : values) {
            if (maybe.type().equals(ancestor)) {
                throw new JsonbException("JsonbSubtype value MUST NOT declaring class"); // 会死循环/栈溢出
            }
            if (!ancestor.isAssignableFrom(maybe.type())) {
                throw new JsonbException("JsonbSubtype value MUST be sub class of JsonbTypeInfo class");
            }
            Class<?> old = children.put(maybe.alias(), maybe.type());
            if (old != null) {
                throw new JsonbException("found duplicate alias: " + old + " >> VS << " + maybe.type());
            }
            Polymorphism<String> sub = findSubtypes(maybe.type());
            if (sub != null) {
                subClasses.put(maybe.alias(), sub);
            } else {
                subClasses.put(maybe.alias(), new Polymorphism<>(maybe.alias(), maybe.type()));
            }
        }
        polymorphism.setSubClasses(subClasses);
        return polymorphism;
    }

    private <T> T instantiate(AnnotatedElement ann, Class<?> intf, Class<T> impl) {
        Class<?> declaredBy;
        if (ann instanceof Class) {
            declaredBy = (Class<?>) ann;
        } else if (ann instanceof Field) {
            declaredBy = ((Field) ann).getType();
        } else if (ann instanceof Method) {
            declaredBy = ((Method) ann).getParameterCount() == 0 ?
                    ((Method) ann).getReturnType():
                    ((Method) ann).getParameterTypes()[0];
        } else {
            throw new JsonbException(intf.getSimpleName() + " expect find on Class/Field/Method only, but actual: " + ann);
        }
        ParameterizedType type = null;
        Type source = null, ancestor = impl;
        lookup:
        for (Class<?> current = impl; current != Object.class; ancestor = current.getGenericSuperclass(), current = current.getSuperclass()) {
            Type[] interfaces = current.getGenericInterfaces();
            for (Type iface : interfaces) {
                if (iface instanceof ParameterizedType && intf.equals(((ParameterizedType) iface).getRawType())) {
                    type = (ParameterizedType) iface;
                    source = ancestor instanceof ParameterizedType ?
                            ((ParameterizedType) ancestor).getActualTypeArguments()[0] :
                            type.getActualTypeArguments()[0];
                    break lookup;
                }
            }
        }
        if (type == null) {
            throw new JsonbException("bad " + intf.getSimpleName() + " implementation class: " + impl.getName());
        }
        if (!declaredBy.equals(source)) {
            throw new JsonbException(intf.getSimpleName() + " implementation class[" + impl.getName() + "] expect type argument [" + declaredBy.getName() + "] but actual is: " + type.getActualTypeArguments()[0]);
        }
        return instantiate(impl);
    }

    private <T> T instantiate(Class<T> klazz) {
        return klazz.cast(instantiator.apply(klazz));
    }

}
