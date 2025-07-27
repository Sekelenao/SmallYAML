package io.github.sekelenao.smallyaml.internal.parsing.line.parser;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.internal.parsing.line.EmptyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.Line;
import io.github.sekelenao.smallyaml.internal.parsing.line.ValueLine;

import java.util.Objects;
import java.util.regex.Pattern;

public final class LineParser {

    private final Pattern forbiddenCharactersPattern = Pattern.compile("[\"#:-]");

    private final Pattern commentLinePattern = Pattern.compile("^\\s*#.*$");

    private final Pattern valueLinePattern = Pattern.compile("^-\\s+(.+)$");

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

    private static String extractQuotedValue(String value) {
        if (value.length() < 2 || !value.startsWith("\"") || !value.endsWith("\"")) {
            throw new ParsingException("Invalid YAML value: " + value);
        }
        var content = value.substring(1, value.length() - 1);
        var builder = new StringBuilder(content.length());
        int index = 0;
        while (index < content.length()) {
            char currentCharacter = content.charAt(index);
            if (currentCharacter == '\\' && index + 1 < content.length() && content.charAt(index + 1) == '"') {
                builder.append('"');
                index += 2;
            } else if (currentCharacter == '"') {
                throw new ParsingException("Invalid YAML value: unescaped quote in " + value);
            } else {
                builder.append(currentCharacter);
                index++;
            }
        }
        return builder.toString();
    }

    private String extractRealValue(String rawValue){
        var value = rawValue.trim();
        if(value.startsWith("\"")){
            return extractQuotedValue(value);
        }
        if(forbiddenCharactersPattern.matcher(rawValue).find()){
            throw new ParsingException("Invalid YAML value: " + rawValue);
        }
        return rawValue;
    }

    public Line parse(String rawLine){
        Objects.requireNonNull(rawLine);
        if(isNotRelevant(rawLine)){
            return EmptyLine.SINGLE_INSTANCE;
        }
        var leadingSpaces = leadingSpaces(rawLine);
        var line = rawLine.substring(leadingSpaces).stripTrailing();
        var valueLineMatcher = valueLinePattern.matcher(line);
        if(valueLineMatcher.matches()){
            var value = extractRealValue(valueLineMatcher.group(1));
            return new ValueLine(leadingSpaces, value);
        }
        // TODO: Continue
        return null;
    }

}
