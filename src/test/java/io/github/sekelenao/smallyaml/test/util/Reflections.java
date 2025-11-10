package io.github.sekelenao.smallyaml.test.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public final class Reflections {

    private Reflections(){
        throw new AssertionError("You cannot instantiate this class");
    }

    public static void ensureIsUtilityClass(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        if(clazz.isEnum() || clazz.isInterface() || clazz.isAnnotation() || clazz.isArray() || clazz.isPrimitive()){
            throw new IllegalArgumentException("Class must not be an enum, interface or annotation");
        }
        var constructors = clazz.getDeclaredConstructors();
        if(constructors.length != 1){
            fail("Class must have only one private throwing constructor");
        }
        var constructor = constructors[0];
        constructor.setAccessible(true);
        var exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, exception.getCause());
    }

    public record ConstructorArgument<T>(Class<? super T> type, T value){

        public ConstructorArgument {
            Objects.requireNonNull(type);
            Objects.requireNonNull(value);
        }

    }

    public static <T> T instantiateByPrivateConstructor(Class<T> type, ConstructorArgument<?>... arguments) throws ReflectiveOperationException {
        Objects.requireNonNull(type);
        Objects.requireNonNull(arguments);
        var argumentsClasses = Arrays.stream(arguments).map(ConstructorArgument::type).toArray(Class<?>[]::new);
        var constructor = type.getDeclaredConstructor(argumentsClasses);
        constructor.setAccessible(true);
        var argumentsValues = Arrays.stream(arguments).map(ConstructorArgument::value).toArray();
        return constructor.newInstance(argumentsValues);
    }

    public static Object retrievePrivateFieldValue(Object instance, String fieldName) throws ReflectiveOperationException {
        Objects.requireNonNull(instance);
        Objects.requireNonNull(fieldName);
        var field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    public static void secureInvokePrivateMethod(Object instance, Method method, Object... args) {
        Objects.requireNonNull(instance);
        Objects.requireNonNull(method);
        Objects.requireNonNull(args);
        try {
            method.setAccessible(true);
            method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError();
        } catch (InvocationTargetException e) {
            var cause = e.getCause();
            if (cause instanceof RuntimeException exception) {
                throw exception;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw new UndeclaredThrowableException(e);
        }
    }

}
