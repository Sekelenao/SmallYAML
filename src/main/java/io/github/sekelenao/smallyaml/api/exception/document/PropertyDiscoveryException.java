package io.github.sekelenao.smallyaml.api.exception.document;

import io.github.sekelenao.smallyaml.api.exception.SmallYAMLException;

import java.lang.reflect.Field;

/**
 * Exception thrown when a property identifier cannot be discovered or accessed via reflection.
 *
 * @since 0.2.0
 */
public class PropertyDiscoveryException extends SmallYAMLException {

    private PropertyDiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a {@code PropertyDiscoveryException} for the given field.
     *
     * @param field the field that could not be accessed
     * @param cause the underlying cause of the access failure
     * @return a new instance of {@code PropertyDiscoveryException}
     *
     * @since 0.2.0
     */
    public static PropertyDiscoveryException forFollowing(Field field, Throwable cause){
        return new PropertyDiscoveryException("Could not access following field: '" + field.getName() + "'", cause);
    }
}
