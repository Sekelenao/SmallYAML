package io.github.sekelenao.smallyaml.internal.parsing.parser;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.internal.parsing.line.EmptyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.KeyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.KeyValueLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.Line;
import io.github.sekelenao.smallyaml.internal.parsing.line.ListValueLine;

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
            var character = rawLine.charAt(i);
            if(Character.isWhitespace(character)){
                if(character != ' '){
                    throw ParsingException.wrongIndentation("unexpected whitespace character", rawLine);
                }
                currentCharacterIndex++;
            } else {
                break;
            }
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
        var indexOfColon = line.indexOf(':');
        if(indexOfColon == -1){
            throw ParsingException.wrongKey("missing colon", line);
        }
        line = line.stripTrailing();
        var keyPart = line.substring(0, indexOfColon + 1);
        var key = keyParser.parse(keyPart);
        if(indexOfColon == line.length() - 1){
            return new KeyLine(leadingSpaces, key);
        }
        if(line.charAt(indexOfColon + 1) != ' ' && line.charAt(indexOfColon + 1) != '\t'){
            throw ParsingException.wrongKey("forbidden character", line);
        }
        var valuePart = line.substring(indexOfColon + 1);
        var value = valueParser.parse(valuePart);
        return new KeyValueLine(leadingSpaces, key, value);
    }

}
