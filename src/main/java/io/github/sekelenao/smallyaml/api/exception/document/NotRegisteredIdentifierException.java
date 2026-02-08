package io.github.sekelenao.smallyaml.api.exception.document;

import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;

public class NotRegisteredIdentifierException extends RuntimeException {

    private NotRegisteredIdentifierException(String message) {
        super(message);
    }

    public static NotRegisteredIdentifierException forFollowing(PropertyIdentifier identifier){
        return new NotRegisteredIdentifierException("Not registered identifier: '" + identifier + "'");
    }

}
