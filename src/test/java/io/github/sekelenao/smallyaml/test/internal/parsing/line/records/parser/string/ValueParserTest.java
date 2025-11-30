package io.github.sekelenao.smallyaml.test.internal.parsing.line.records.parser.string;

import io.github.sekelenao.smallyaml.internal.parsing.line.records.parser.string.ValueParser;
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
    @DisplayName("Starting quote without ending quote")
    void startingQuoteWithoutEndingQuote() {
        assertAll(
            () -> assertEquals("\"", parser.parse("\"")),
            () -> assertEquals("\"starting_quote", parser.parse("\"starting_quote"))
        );
    }

    @Test
    @DisplayName("Quoted values keep their spaces")
    void quotedValuesKeepTheirSpaces() {
        assertAll(
                () -> assertEquals("   value  ", parser.parse("  \"   value  \"  ")),
                () -> assertEquals(" More spaces ", parser.parse("\" More spaces \""))
        );
    }

    @Test
    @DisplayName("Quotes inside quotes are working")
    void escapedQuotesAreWorking() {
        assertAll(
            () -> assertEquals("va\"lue", parser.parse("\"va\"lue\"")),
            () -> assertEquals("\"", parser.parse("\"\"\"")),
            () -> assertEquals("\"\"", parser.parse("\"\"\"\""))
        );
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"test:", "-test", "test-2:\""})
    @DisplayName("Special characters are well handled")
    void specialCharactersAreWellHandled(){
        assertAll(
            () -> assertEquals("test:", parser.parse("test:")),
            () -> assertEquals("-test", parser.parse("-test")),
            () -> assertEquals("test-2:\"", parser.parse("test-2:\""))
        );
    }

}