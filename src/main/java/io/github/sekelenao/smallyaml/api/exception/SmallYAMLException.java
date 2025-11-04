package io.github.sekelenao.smallyaml.api.exception;

/**
 * Base exception class for errors encountered in the SmallYAML framework.
 * <p>
 * This class serves as the root for all exceptions related to YAML parsing, processing,
 * or operations within the SmallYAML library. It provides a common type for catching
 * and handling YAML-specific exceptions.
 * <p>
 * Subclasses of {@code SmallYAMLException} are used to represent specific types
 * of errors, such as duplicate property keys, parsing issues, or type mismatches.
 *
 * @since 1.0.0
 */
public class SmallYAMLException extends RuntimeException {

    /**
     * Constructs a new {@code SmallYAMLException} with the specified detail message.
     * This exception serves as the base for all exceptions encountered in the SmallYAML framework.
     *
     * @param message the detail message that provides additional context about the error
     *
     * @since 1.0.0
     */
    public SmallYAMLException(String message) {
        super(message);
    }

}
