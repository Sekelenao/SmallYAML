package io.github.sekelenao.smallyaml.api.exception.parsing;

import io.github.sekelenao.smallyaml.api.exception.SmallYAMLException;

import java.util.Objects;

public class ParsingException extends SmallYAMLException {

    private ParsingException(String message) {
        super(message);
    }

    public static ParsingException wrongValue(String details, String value){
        Objects.requireNonNull(details);
        Objects.requireNonNull(value);
        return new ParsingException("Invalid value: " + details + " for: '" + value + "'");
    }

    public static ParsingException wrongKey(String details, String key){
        Objects.requireNonNull(details);
        Objects.requireNonNull(key);
        return new ParsingException("Invalid key: " + details + " for: '" + key + "'");
    }

}
