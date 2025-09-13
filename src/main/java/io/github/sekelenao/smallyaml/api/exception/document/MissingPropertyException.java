package io.github.sekelenao.smallyaml.api.exception.document;

import io.github.sekelenao.smallyaml.api.exception.SmallYAMLException;

public class MissingPropertyException extends SmallYAMLException {

    private MissingPropertyException(String message) {
        super(message);
    }

    public static MissingPropertyException forFollowing(String key){
        return new MissingPropertyException("Missing property '" + key + "'");
    }

}
