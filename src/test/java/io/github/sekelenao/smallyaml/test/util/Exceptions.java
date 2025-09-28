package io.github.sekelenao.smallyaml.test.util;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class Exceptions {

    private Exceptions(){
        throw new AssertionError("You cannot instantiate this class");
    }

    public static void isThrownAndContains(Class<?extends Exception> type, Runnable runnable, String message){
        Objects.requireNonNull(type);
        Objects.requireNonNull(message);
        var exception = assertThrows(type, runnable::run);
        assertTrue(
            exception.getMessage().contains(message),
            "Exception message should contain '" + message + "' but is: '" + exception.getMessage() + "'"
        );
    }

}
