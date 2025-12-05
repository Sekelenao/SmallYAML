package io.github.sekelenao.smallyaml.api.exception.parsing;

import io.github.sekelenao.smallyaml.api.exception.SmallYAMLException;

/**
 * Exception thrown to indicate that a string value cannot be parsed into a valid boolean.
 * <p>
 * This exception is typically used when the system encounters a malformed or invalid
 * boolean representation within a YAML parsing process.
 *
 * @since 0.1.0
 */
public class BooleanFormatException extends SmallYAMLException {

    /**
     * Constructs a new {@code BooleanFormatException} with the specified detail message.
     * This exception is thrown when a provided string cannot be parsed as a valid boolean
     * representation.
     *
     * @param message the detail message to describe the error, providing context about
     *                the invalid boolean string that caused this exception to be thrown
     *
     * @since 0.1.0
     */
    public BooleanFormatException(String message) {
        super(message);
    }

}
