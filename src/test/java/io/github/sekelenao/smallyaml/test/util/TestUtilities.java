package io.github.sekelenao.smallyaml.test.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public final class TestUtilities {

    private static final int MAX = 10_000;

    private static final String[] BLANK_CHARS = {" ", "\t"};

    private static final Random RANDOM = new Random();

    public static String generateBlankString(int size){
        var builder = new StringBuilder();
        for(int i = 0; i < size; i++){
            builder.append(BLANK_CHARS[RANDOM.nextInt(BLANK_CHARS.length)]);
        }
        return builder.toString();
    }

    public static Stream<Integer> intProvider() {
        return Stream.generate(() -> RANDOM.nextInt(MAX)).limit(50);
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
