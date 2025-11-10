package io.github.sekelenao.smallyaml.test.internal.parsing.parser;

import io.github.sekelenao.smallyaml.internal.parsing.line.records.parser.string.ValueParser;
import io.github.sekelenao.smallyaml.test.util.stringparser.StringParserTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class ValueParserTest {

    private final ValueParser parser = new ValueParser();

    private final StringParserTester parsingTester = new StringParserTester(parser);

    private static Stream<String> containingWrongCharValues() {
        return Stream.of("test\"", "test:", "te-st");
    }

    @Test
    @DisplayName("Assertions")
    void assertions() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  test  ", "\ttest\t", "  test  \t  ", "   \t\t test  \t"})
    @DisplayName("Values are trimmed")
    void valuesAreTrimmed(String rawValue) {
        parsingTester.checkValid(rawValue, "test");
    }

    @Test
    @DisplayName("Quoted values keep their spaces")
    void quotedValuesKeepTheirSpaces() {
        parsingTester.checkValid("  \"   test  \"  ", "   test  ");
    }

    @Test
    @DisplayName("Unescaped quote inside quoted value")
    void unescapedQuoteInsideQuotedValue() {
        parsingTester.checkException("\"te\"st\"", "unescaped quote");
    }

    @Test
    @DisplayName("Backslash is handled correctly")
    void backslashIsHandledCorrectly() {
        assertAll(
            () -> parsingTester.checkValid("\"te\\st\"", "te\\st"),
            () -> parsingTester.checkValid("te\\st", "te\\st"),
            () -> parsingTester.checkValid("test\\", "test\\"),
            () -> parsingTester.checkValid("\"test\\\"", "test\\")
        );
    }

    @Test
    @DisplayName("Missing ending quote")
    void missingEndingQuote() {
        parsingTester.checkException("\"", "missing ending quote");
    }

    @ParameterizedTest
    @MethodSource("containingWrongCharValues")
    @DisplayName("Values with escaped wrong characters parsing")
    void correctValuesParsing(String containingWrongCharValue) {
        parsingTester.checkValid("\"" + containingWrongCharValue.replace("\"", "\\\"") + "\"",  containingWrongCharValue);
    }

    @ParameterizedTest
    @MethodSource("containingWrongCharValues")
    @DisplayName("Values with wrong characters parsing")
    void wrongValuesParsing(String containingWrongCharValue) {
        parsingTester.checkException(containingWrongCharValue, "invalid character without quotes");
    }

    @Test
    @DisplayName("Missing ending quote parsing")
    void missingEndingQuoteParsing() {
        parsingTester.checkException("\"test", "missing ending quote");
    }

}