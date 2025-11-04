package io.github.sekelenao.smallyaml.api.exception.document;

import io.github.sekelenao.smallyaml.api.exception.SmallYAMLException;

/**
 * Exception thrown to indicate that a duplicate property key was encountered
 * in the document being parsed or processed.
 * <p>
 * This exception is used to signal violations of the requirement that each
 * property key within a YAML document must be unique. It is typically thrown
 * when attempting to add or update a property with a key that already exists
 * in the collection of properties.
 * <p>
 * The associated message contains the duplicated property key to facilitate
 * debugging and error handling.
 *
 * @since 1.0.0
 */
public class DuplicatedPropertyException extends SmallYAMLException {

    private DuplicatedPropertyException(String message) {
        super(message);
    }

    /**
     * Constructs a {@code DuplicatedPropertyException} for the given key,
     * indicating that a duplicate property with the specified key was encountered.
     *
     * @param key the property key that caused the exception
     * @return a new instance of {@code DuplicatedPropertyException} containing
     *         a message with the duplicated property key
     *
     * @since 1.0.0
     */
    public static DuplicatedPropertyException forFollowing(String key){
        return new DuplicatedPropertyException("Duplicated property '" + key + "'");
    }

}
