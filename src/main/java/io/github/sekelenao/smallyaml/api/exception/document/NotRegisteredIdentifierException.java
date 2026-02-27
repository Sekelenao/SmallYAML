package io.github.sekelenao.smallyaml.api.exception.document;

import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;

/**
 * Exception thrown to indicate that a {@link PropertyIdentifier} was used to access a property
 * in a {@link io.github.sekelenao.smallyaml.api.document.BoundedDocument}, but the identifier
 * was not registered when the document was created.
 *
 * @since 0.2.0
 */
public class NotRegisteredIdentifierException extends RuntimeException {

    private NotRegisteredIdentifierException(String message) {
        super(message);
    }

    /**
     * Constructs a {@code NotRegisteredIdentifierException} for the given identifier.
     *
     * @param identifier the property identifier that is not registered
     * @return a new instance of {@code NotRegisteredIdentifierException}
     *
     * @since 0.2.0
     */
    public static NotRegisteredIdentifierException forFollowing(PropertyIdentifier identifier){
        return new NotRegisteredIdentifierException("Not registered identifier: '" + identifier + "'");
    }

}
