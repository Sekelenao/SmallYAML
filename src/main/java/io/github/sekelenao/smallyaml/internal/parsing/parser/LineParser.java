package io.github.sekelenao.smallyaml.internal.parsing.parser;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.internal.parsing.line.*;

import java.util.Objects;
import java.util.regex.Pattern;

public final class LineParser {

    private final Pattern commentLinePattern = Pattern.compile("^\\s*#.*$");

    private final KeyParser keyParser = new KeyParser();

    private final ValueParser valueParser = new ValueParser();

    private boolean isNotRelevant(String rawLine){
        return Objects.requireNonNull(rawLine).isBlank() || commentLinePattern.matcher(rawLine).matches();
    }

    private static int leadingSpaces(String rawLine){
        int currentCharacterIndex = 0;
        for(int i = 0; i < rawLine.length(); i++){
            if(!Character.isWhitespace(rawLine.charAt(i))){
                return currentCharacterIndex;
            }
            currentCharacterIndex++;
        }
        return currentCharacterIndex;
    }

    public Line parse(String rawLine){
        Objects.requireNonNull(rawLine);
        if(isNotRelevant(rawLine)){
            return EmptyLine.SINGLE_INSTANCE;
        }
        var leadingSpaces = leadingSpaces(rawLine);
        var line = rawLine.substring(leadingSpaces);
        if(line.startsWith("-")){
            var valueGroup = line.substring(1);
            return new ListValueLine(leadingSpaces, valueParser.parse(valueGroup));
        }
        var valueSplit = line.split(": ", 2);
        var keyPart = valueSplit[0].stripTrailing();
        if(!keyPart.endsWith(":")){
            throw ParsingException.wrongKey("missing colon", keyPart);
        }
        var key = keyParser.parse(keyPart);
        if(valueSplit.length > 1){
            var value = valueParser.parse(valueSplit[1]);
            return new KeyValueLine(leadingSpaces, key, value);
        }
        return new KeyLine(leadingSpaces, key);
    }

}
