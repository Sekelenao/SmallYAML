package io.github.sekelenao.smallyaml.internal.parsing.line.records.parser;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.internal.parsing.line.records.EmptyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.records.KeyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.records.KeyValueLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.records.Line;
import io.github.sekelenao.smallyaml.internal.parsing.line.records.ListValueLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.records.parser.string.KeyParser;
import io.github.sekelenao.smallyaml.internal.parsing.line.records.parser.string.ValueParser;
import io.github.sekelenao.smallyaml.internal.util.Assertions;

import java.util.Objects;

public final class LineRecordParser {

    private static final char COMMENT_SYMBOL = '#';

    private final KeyParser keyParser = new KeyParser();

    private final ValueParser valueParser = new ValueParser();

    private record LineStructure(int amountOfLeadingSpaces, boolean isNotRelevant) {

        private LineStructure {
            Assertions.isPositiveOrZero(amountOfLeadingSpaces);
        }

    }

    private static LineStructure computeDescription(String rawLine) {
        int index = 0;
        var encounteredWrongWhitespace = false;
        for (; index < rawLine.length(); index++) {
            var character = rawLine.charAt(index);
            if (!Character.isWhitespace(character)) {
                var isComment = character == COMMENT_SYMBOL;
                if(encounteredWrongWhitespace && !isComment){
                    throw ParsingException.wrongIndentation("unexpected whitespace character", rawLine);
                }
                return new LineStructure(index, isComment);
            }
            if (character != ' ') {
                encounteredWrongWhitespace = true;
            }
        }
        return new LineStructure(index, true);
    }

    public Line parse(String rawLine) {
        Objects.requireNonNull(rawLine);
        var lineStructure = computeDescription(rawLine);
        if (lineStructure.isNotRelevant()) {
            return EmptyLine.SINGLE_INSTANCE;
        }
        var leadingSpaces = lineStructure.amountOfLeadingSpaces();
        var line = rawLine.substring(leadingSpaces);
        if (line.startsWith("-")) {
            var valueGroup = line.substring(1);
            return new ListValueLine(leadingSpaces, valueParser.parse(valueGroup));
        }
        var indexOfColon = line.indexOf(':');
        if (indexOfColon == -1) {
            throw ParsingException.wrongKey("missing colon", line);
        }
        line = line.stripTrailing();
        var keyPart = line.substring(0, indexOfColon + 1);
        var key = keyParser.parse(keyPart);
        if (indexOfColon == line.length() - 1) {
            return new KeyLine(leadingSpaces, key);
        }
        if (line.charAt(indexOfColon + 1) != ' ' && line.charAt(indexOfColon + 1) != '\t') {
            throw ParsingException.wrongKey("forbidden character", line);
        }
        var valuePart = line.substring(indexOfColon + 1);
        var value = valueParser.parse(valuePart);
        return new KeyValueLine(leadingSpaces, key, value);
    }

}
