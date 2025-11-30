package io.github.sekelenao.smallyaml.internal.parsing.line.records.parser.string;


import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;

import java.util.Objects;

public final class ValueParser {

    public String parse(String rawValue){
        Objects.requireNonNull(rawValue);
        var value = rawValue.trim();
        if(value.isEmpty()){
            throw ParsingException.wrongValue("empty value", rawValue);
        }
        if(value.startsWith("\"") && value.endsWith("\"")){
            if(value.length() == 1){
                return "\"";
            }
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

}
