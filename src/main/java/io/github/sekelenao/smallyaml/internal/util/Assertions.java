package io.github.sekelenao.smallyaml.internal.util;

public final class Assertions {

    private Assertions(){
        throw new AssertionError("You cannot instantiate this class");
    }

    /**
     * Validates that the provided integer value is either positive or zero.
     * If the value is negative, an {@link IllegalArgumentException} is thrown.
     *
     * @param value the integer value to validate
     * @throws IllegalArgumentException if the value is negative
     */
    public static void isPositiveOrZero(int value){
        if(value < 0){
            throw new IllegalArgumentException("Value must be positive or zero: " + value);
        }
    }

}
