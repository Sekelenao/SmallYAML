package io.github.sekelenao.smallyaml.test.internal.parsing.parser;

import io.github.sekelenao.smallyaml.internal.parsing.line.records.parser.string.KeyParser;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import io.github.sekelenao.smallyaml.test.util.stringparser.StringParserTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag(TestingTag.INTERNAL)
@Tag(TestingTag.PARSING)
final class KeyParserTest {

    private final KeyParser parser = new KeyParser();

    private final StringParserTester parsingTester = new StringParserTester(parser);

    @Test
    @DisplayName("Assertions")
    void assertions() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"key:", "    key:   ", "key: \t  ", "  \t   key:  \t  "})
    @DisplayName("Key parsing for valid values")
    void keyParsingForValidValues(String rawKey) {
        parsingTester.checkValid(rawKey, "key");
    }

    @Test
    @DisplayName("Key parsing for valid complex values")
    void keyParsingForValidComplexValues() {
        assertAll(
                () -> parsingTester.checkValid("  \tone.Two.three:    \t ", "one.Two.three"),
                () -> parsingTester.checkValid("under_score:", "under_score"),
                () -> parsingTester.checkValid(" da-sh: ", "da-sh"),
                () -> parsingTester.checkValid(" 1__2: ", "1__2")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"NoColon", "Colon:In:Middle"})
    @DisplayName("Key parsing for missing colon")
    void keyParsingForMissingColon(String rawKey) {
        parsingTester.checkException(rawKey, "missing colon");
    }

    @ParameterizedTest
    @ValueSource(strings = {"space :", "first:second:", "@yes:"})
    @DisplayName("Key parsing for forbidden characters")
    void keyParsingForNotPermittedCharacters(String rawKey) {
        parsingTester.checkException(rawKey, "forbidden character");
    }

    @Test
    @DisplayName("Key parser can parse multiple values")
    void keyParserCanParseMultipleValues() {
        var localParsingTester = new StringParserTester(new KeyParser());
        assertAll(
                () -> localParsingTester.checkException("-dash:", "key start with special character"),
                () -> localParsingTester.checkValid("yes: ", "yes"),
                () -> localParsingTester.checkException("_underscore:", "key start with special character"),
                () -> localParsingTester.checkValid("ok:", "ok")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"-DashStart:", "_UnderscoreStart:"})
    @DisplayName("Key parsing for special char at start")
    void keyParsingForSpecialCharAtStart(String rawKey) {
        parsingTester.checkException(rawKey, "key start with special character");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DashEnd-:", "UnderscoreEnd_:",
            "forbidden.after_:", "before.dot-:"
    })
    @DisplayName("Key parsing for special char at end")
    void keyParsingForSpecialCharAtEnd(String rawKey) {
        parsingTester.checkException(rawKey, "key ends with special character");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ".DotStart:", "DotEnd.:", "double..dot:",
            "double.dot..after:", "double..dot.before: ",
            "ending.with.dot.:", "..:"
    })
    @DisplayName("Key parsing for empty key part")
    void keyParsingForEmptyKeyPart(String rawKey) {
        parsingTester.checkException(rawKey, "empty key part");
    }

    @ParameterizedTest
    @ValueSource(strings = {"forbidden.-start.dot:", "start._dot:"})
    @DisplayName("Key parsing for key part starting with special char")
    void keyParsingForKeyPartStartingWithSpecialChar(String rawKey) {
        parsingTester.checkException(rawKey, "key part start with special character");
    }

    @ParameterizedTest
    @ValueSource(strings = {"forbidden-.before.dot:", "before_.dot:", "end.end-.yes:"})
    @DisplayName("Key parsing for key part ending with special char")
    void keyParsingForKeyPartEndingWithSpecialChar(String rawKey) {
        parsingTester.checkException(rawKey, "key part ends with special character");
    }

    @ParameterizedTest
    @ValueSource(strings = {"   :  ", ":"})
    @DisplayName("Key parsing with empty key")
    void keyParsingWithEmptyKey(String rawKey) {
        parsingTester.checkException(rawKey, "empty key");
    }

}