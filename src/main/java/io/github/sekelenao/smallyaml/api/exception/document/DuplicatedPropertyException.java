package io.github.sekelenao.smallyaml.api.exception.document;

import io.github.sekelenao.smallyaml.api.exception.SmallYAMLException;

public class DuplicatedPropertyException extends SmallYAMLException {

    private DuplicatedPropertyException(String message) {
        super(message);
    }

    public static DuplicatedPropertyException forFollowing(String key){
        return new DuplicatedPropertyException("Duplicated property '" + key + "'");
    }

}
