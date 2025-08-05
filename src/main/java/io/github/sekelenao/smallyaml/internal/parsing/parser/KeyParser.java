package io.github.sekelenao.smallyaml.internal.parsing.parser;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;

import java.util.Objects;

public final class KeyParser implements StringParser {

    private enum State { START, ENCOUNTERED_NORMAL_CHAR, ENCOUNTERED_DOT, ENCOUNTERED_SPECIAL_CHAR, END }

    private State state = State.START;

    private void treatDotCharacter(String rawKey){
        switch (state){
            case START, ENCOUNTERED_DOT, END -> throw ParsingException.wrongKey("empty key part", rawKey);
            case ENCOUNTERED_SPECIAL_CHAR -> throw ParsingException.wrongKey("key part ends with special character", rawKey);
            case ENCOUNTERED_NORMAL_CHAR -> state = State.ENCOUNTERED_DOT;
        }
    }

    private void treatSpecialCharacter(String rawKey){
        switch (state){
            case START -> throw ParsingException.wrongKey("key start with special character", rawKey);
            case ENCOUNTERED_DOT -> throw ParsingException.wrongKey("key part start with special character", rawKey);
            case ENCOUNTERED_SPECIAL_CHAR, ENCOUNTERED_NORMAL_CHAR -> state = State.ENCOUNTERED_SPECIAL_CHAR;
            case END -> throw ParsingException.wrongKey("key ends with special character", rawKey);
        }
    }

    public String parse(String rawKey){
        Objects.requireNonNull(rawKey);
        var trimmedKey = rawKey.trim();
        if(!trimmedKey.endsWith(":")){
            throw ParsingException.wrongKey("missing colon", rawKey);
        }
        if(trimmedKey.length() == 1){
            throw ParsingException.wrongKey("empty key", rawKey);
        }
        var keyValue = trimmedKey.substring(0, trimmedKey.length() - 1);
        for (int i = 0; i < keyValue.length(); i++) {
            var currentCharacter = keyValue.charAt(i);
            if(Character.isLetterOrDigit(currentCharacter)){
                state = State.ENCOUNTERED_NORMAL_CHAR;
                continue;
            }
            if (i == keyValue.length() - 1){
                state = State.END;
            }
            switch (currentCharacter){
                case '.' -> treatDotCharacter(rawKey);
                case '-', '_' -> treatSpecialCharacter(rawKey);
                default -> throw ParsingException.wrongKey("not permitted character", rawKey);
            }
        }
        state = State.START;
        return keyValue;
    }

}
