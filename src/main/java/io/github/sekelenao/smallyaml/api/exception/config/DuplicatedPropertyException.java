package io.github.sekelenao.smallyaml.api.exception.config;

public class DuplicatedPropertyException extends RuntimeException {

    private DuplicatedPropertyException(String message) {
        super(message);
    }

    public static DuplicatedPropertyException forFollowing(String key){
        return new DuplicatedPropertyException("Duplicated property '" + key + "'");
    }

}
