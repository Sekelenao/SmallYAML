package io.github.sekelenao.smallyaml.api.exception.document;

import io.github.sekelenao.smallyaml.api.exception.SmallYAMLException;

/**
 * Exception thrown to indicate that a mandatory property is missing from a document.
 *
 * @since 0.2.0
 */
public class MissingPropertyException extends SmallYAMLException {

    private MissingPropertyException(String message) {
        super(message);
    }

    /**
     * Constructs a {@code MissingPropertyException} for the given property key.
     *
     * @param key the property key that is missing
     * @return a new instance of {@code MissingPropertyException}
     *
     * @since 0.2.0
     */
    public static MissingPropertyException forFollowing(String key){
        return new MissingPropertyException("Missing property '" + key + "'");
    }

}
