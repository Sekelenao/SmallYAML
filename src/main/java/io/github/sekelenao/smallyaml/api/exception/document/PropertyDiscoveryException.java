package io.github.sekelenao.smallyaml.api.exception.document;

import io.github.sekelenao.smallyaml.api.exception.SmallYAMLException;

import java.lang.reflect.Field;

public class PropertyDiscoveryException extends SmallYAMLException {

    private PropertyDiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public static PropertyDiscoveryException forFollowing(Field field, Throwable cause){
        return new PropertyDiscoveryException("Could not access following field: '" + field.getName() + "'", cause);
    }
}
