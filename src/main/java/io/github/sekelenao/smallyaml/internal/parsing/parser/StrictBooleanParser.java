package io.github.sekelenao.smallyaml.internal.parsing.parser;

import io.github.sekelenao.smallyaml.api.exception.parsing.BooleanFormatException;

import java.util.Objects;

public final class StrictBooleanParser {

    private StrictBooleanParser(){
        throw new AssertionError();
    }

    public static boolean parse(String booleanAsString){
        Objects.requireNonNull(booleanAsString);
        if(booleanAsString.equalsIgnoreCase("TRUE")){
            return true;
        }
        else if(booleanAsString.equalsIgnoreCase("FALSE")){
            return false;
        }
        throw new BooleanFormatException("Boolean should be case insensitive 'TRUE' or 'FALSE' but is: " + booleanAsString);
    }

}
