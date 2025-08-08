package io.github.sekelenao.smallyaml.api.exception.config;

import io.github.sekelenao.smallyaml.api.exception.SmallYAMLException;

public class WrongTypeException extends SmallYAMLException {

    private WrongTypeException(String message) {
        super(message);
    }

    public static WrongTypeException withExpectedInsteadOf(Class<?> expectedType, Class<?> actualType){
        return new WrongTypeException(
                "Expected type: " + expectedType.getSimpleName() + " but was: " + actualType.getSimpleName()
        );
    }

}
