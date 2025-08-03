package io.github.sekelenao.smallyaml.internal.parsing.line.parser;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;

import java.util.Objects;
import java.util.regex.Pattern;

public final class KeyParser implements StringParser {

    private final Pattern keyPattern = Pattern.compile("^([a-zA-Z0-9](?:[a-zA-Z0-9_.-]*[a-zA-Z0-9])?)$");

    public String parse(String rawKey){
        Objects.requireNonNull(rawKey);
        var trimmedKey = rawKey.trim();
        if(!trimmedKey.endsWith(":")){
            throw ParsingException.wrongKey("missing colon", rawKey);
        }
        var keyValue = trimmedKey.substring(0, trimmedKey.length() - 1);
        if(!keyPattern.matcher(keyValue).matches()){
            throw ParsingException.wrongKey("wrong format", rawKey);
        }
        return keyValue;
    }

}
