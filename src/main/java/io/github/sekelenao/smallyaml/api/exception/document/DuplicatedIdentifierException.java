package io.github.sekelenao.smallyaml.api.exception.document;

import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.exception.SmallYAMLException;

/**
 * Exception thrown to indicate that a duplicate {@link PropertyIdentifier} key was encountered
 * during registration in a {@link io.github.sekelenao.smallyaml.api.document.BoundedDocumentFactoryBuilder}.
 *
 * @since 0.2.0
 */
public class DuplicatedIdentifierException extends SmallYAMLException {

    private DuplicatedIdentifierException(String message) {
        super(message);
    }

    /**
     * Constructs a {@code DuplicatedIdentifierException} for the given identifier.
     *
     * @param identifier the property identifier that caused the exception
     * @return a new instance of {@code DuplicatedIdentifierException}
     *
     * @since 0.2.0
     */
    public static DuplicatedIdentifierException forFollowing(PropertyIdentifier identifier){
        return new DuplicatedIdentifierException("Duplicated identifier definition: '" + identifier.key() + "'");
    }

}
