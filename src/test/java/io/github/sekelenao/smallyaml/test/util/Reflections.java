package io.github.sekelenao.smallyaml.test.util;

import java.lang.reflect.InvocationTargetException;
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

}
