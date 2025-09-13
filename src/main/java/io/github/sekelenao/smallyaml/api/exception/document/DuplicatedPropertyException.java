package io.github.sekelenao.smallyaml.api.exception.document;

public class DuplicatedPropertyException extends RuntimeException {

    private DuplicatedPropertyException(String message) {
        super(message);
    }

    public static DuplicatedPropertyException forFollowing(String key){
        return new DuplicatedPropertyException("Duplicated property '" + key + "'");
    }

}
