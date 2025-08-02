package io.github.sekelenao.smallyaml.internal.parsing.line.parser;


import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;

import java.util.Objects;
import java.util.regex.Pattern;

public final class ValueParser {

    private final Pattern forbiddenCharactersPattern = Pattern.compile("[\"#:-]");

    private static String extractQuotedValue(String value) {
        if (value.length() < 2 || !value.endsWith("\"")) {
            throw new ParsingException("Invalid YAML value: missing ending quote in " + value);
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

    public String parse(String rawValue){
        Objects.requireNonNull(rawValue);
        var value = rawValue.trim();
        if(value.isEmpty()){
            throw new ParsingException("Invalid YAML value: empty value in " + rawValue);
        }
        if(value.startsWith("\"")){
            return extractQuotedValue(value);
        }
        if(forbiddenCharactersPattern.matcher(value).find()){
            throw new ParsingException("Invalid YAML value: invalid character without quotes in " + rawValue);
        }
        return value;
    }

}
