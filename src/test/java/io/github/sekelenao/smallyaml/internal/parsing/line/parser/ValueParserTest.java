package io.github.sekelenao.smallyaml.internal.parsing.line.parser;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.test.util.TestingTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(TestingTag.PARSING)
final class ValueParserTest {

    private final ValueParser parser = new ValueParser();

    private void checkForRightValue(String rawLine, String expectedValue) {
        assertEquals(expectedValue, parser.parse(rawLine));
    }

    private void checkForWrongValue(String rawLine, String expectedMessage) {
        var exception = assertThrows(ParsingException.class, () -> parser.parse(rawLine));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    private static Stream<String> containingWrongCharValues() {
        return Stream.of("test\"", "test:", "#test", "te-st");
    }

    @ParameterizedTest
    @ValueSource(strings = {"  test  ", "\ttest\t", "  test  \t  ", "   \t\t test  \t"})
    @DisplayName("Values are trimmed")
    void valuesAreTrimmed(String rawValue) {
        checkForRightValue(rawValue, "test");
    }

    @Test
    @DisplayName("Quoted values keep their spaces")
    void quotedValuesKeepTheirSpaces() {
        checkForRightValue("  \"   test  \"  ", "   test  ");
    }

    @Test
    @DisplayName("Unescaped quote inside quoted value")
    void unescapedQuoteInsideQuotedValue() {
        checkForWrongValue("\"te\"st\"", "unescaped quote");
    }

    @ParameterizedTest
    @MethodSource("containingWrongCharValues")
    @DisplayName("Values with escaped wrong characters parsing")
    void correctValuesParsing(String containingWrongCharValue) {
        checkForRightValue("\"" + containingWrongCharValue.replace("\"", "\\\"") + "\"",  containingWrongCharValue);
    }

    @ParameterizedTest
    @MethodSource("containingWrongCharValues")
    @DisplayName("Values with wrong characters parsing")
    void wrongValuesParsing(String containingWrongCharValue) {
        checkForWrongValue(containingWrongCharValue, "invalid character without quotes");
    }

    @Test
    @DisplayName("Missing ending quote parsing")
    void missingEndingQuoteParsing() {
        checkForWrongValue("\"test", "missing ending quote");
    }

}