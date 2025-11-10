package io.github.sekelenao.smallyaml.test.util;

import org.junit.jupiter.api.function.Executable;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class TestExceptions {

    private TestExceptions(){
        throw new AssertionError("You cannot instantiate this class");
    }

    public static void isThrownAndContains(Class<?extends Exception> type, Executable executable, String message){
        Objects.requireNonNull(type);
        Objects.requireNonNull(message);
        var exception = assertThrows(type, executable);
        assertTrue(
            exception.getMessage().contains(message),
            "Exception message should contain '" + message + "' but is: '" + exception.getMessage() + "'"
        );
    }

}
