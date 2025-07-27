package io.github.sekelenao.smallyaml.internal.parsing.line.parser;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.internal.parsing.line.ValueLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class LineParserTest {

    @Nested
    @DisplayName("List value parsing")
    final class ListValueParsing {

        @Test
        @DisplayName("Correct values parsing")
        void correctValuesParsing() {
            var parser = new LineParser();
            assertAll(
                () -> assertEquals(new ValueLine(2, "1, 2, 3"), parser.parse("  - 1, 2, 3")),
                () -> assertEquals(new ValueLine(0, "test:"), parser.parse("- \"test:\"")),
                () -> assertEquals(new ValueLine(0, "- test"), parser.parse("- \"- test\""))
            );
        }

        @Test
        @DisplayName("Wrong values parsing")
        void wrongValuesParsing() {
            var parser = new LineParser();
            assertAll(
                () -> assertThrows(ParsingException.class, () -> parser.parse("- test:")),
                () -> assertThrows(ParsingException.class, () -> parser.parse("- - test"))
            );
        }

    }

}