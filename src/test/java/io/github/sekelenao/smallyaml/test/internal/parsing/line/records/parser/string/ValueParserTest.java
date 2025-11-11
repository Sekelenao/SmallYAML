package io.github.sekelenao.smallyaml.test.internal.parsing.line.records.parser.string;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.internal.parsing.line.records.parser.string.ValueParser;
import io.github.sekelenao.smallyaml.test.util.ExceptionsTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class ValueParserTest {

    private final ValueParser parser = new ValueParser();

    @Test
    @DisplayName("Assertions")
    void assertions() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"  value  ", "\tvalue\t", "  value  \t  ", "   \t\t value  \t"})
    @DisplayName("Values are trimmed")
    void valuesAreTrimmed(String rawValue) {
        assertEquals("value", parser.parse(rawValue));
    }

    @Test
    @DisplayName("Empty quoted value")
    void emptyQuotedValue() {
        assertEquals("", parser.parse("\"\""));
    }

    @Test
    @DisplayName("Quoted values keep their spaces")
    void quotedValuesKeepTheirSpaces() {
        assertAll(
                () -> assertEquals("   value  ", parser.parse("  \"   value  \"  ")),
                () -> assertEquals(" More spaces ", parser.parse("\" More spaces \""))
        );
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"\"va\"lue\"", "\"va\\l\"ue\"", "\"v\"a\\lue\"", "\"va\"\\lue\"", "\"value\"\""})
    @DisplayName("Unescaped quote inside quoted value")
    void unescapedQuoteInsideQuotedValue(String rawValue) {
        ExceptionsTester.assertIsThrownAndContains(
                ParsingException.class,
                () -> parser.parse(rawValue),
                "unescaped quote"
        );
    }

    @Test
    @DisplayName("Backslashes are handled correctly")
    void backslashIsHandledCorrectly() {
        assertAll(
            () -> assertEquals("va\\lue", parser.parse("\"va\\lue\"")),
            () -> assertEquals("va\\lue", parser.parse("va\\lue")),
            () -> assertEquals("value\\", parser.parse("value\\")),
            () -> assertEquals("value\\", parser.parse("\"value\\\""))
        );
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"\"value", "\"val\"ue", "\""})
    @DisplayName("Missing ending quote")
    void missingEndingQuote(String rawValue) {
        ExceptionsTester.assertIsThrownAndContains(
            ParsingException.class,
            () -> parser.parse(rawValue),
            "missing ending quote"
        );
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"value\"", "val:ue", "value:", "-value", "value-", "val-ue", ":value"})
    @DisplayName("Not permitted characters without quotes are throwing")
    void notPermittedCharactersWithoutQuotesAreThrowing(String rawValue) {
        ExceptionsTester.assertIsThrownAndContains(
                ParsingException.class,
                () -> parser.parse(rawValue),
                "invalid character without quotes"
        );
    }

    @Test
    @DisplayName("Quoted not permitted character are working")
    void quotedNotPermittedCharacterAreWorking() {
        assertAll(
            () -> assertEquals(":value", parser.parse("\":value\"")),
            () -> assertEquals("va:lue", parser.parse("\"va:lue\"")),
            () -> assertEquals("value:", parser.parse("\"value:\"")),
            () -> assertEquals("-value", parser.parse("\"-value\"")),
            () -> assertEquals("value-", parser.parse("\"value-\"")),
            () -> assertEquals("val-ue", parser.parse("\"val-ue\""))
        );
    }

    @Test
    @DisplayName("Escaped quotes are working")
    void escapedQuotesAreWorking() {
        assertAll(
            () -> assertEquals("va\"lue", parser.parse("\"va\\\"lue\"")),
            () -> assertEquals("\"", parser.parse("\"\\\"\"")),
            () -> assertEquals("\"\"", parser.parse("\"\\\"\\\"\""))
        );
    }

}