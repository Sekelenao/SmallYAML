package io.github.sekelenao.smallyaml.test.internal.parsing.parser;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.internal.parsing.line.records.parser.string.KeyParser;
import io.github.sekelenao.smallyaml.test.util.ExceptionsTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class KeyParserTest {

    private final KeyParser parser = new KeyParser();

    @Test
    @DisplayName("Assertions")
    void assertions() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"key:", "    key:   ", "key: \t  ", "  \t   key:  \t  "})
    @DisplayName("Key parsing for valid values")
    void keyParsingForValidValues(String rawKey) {
        assertEquals("key", parser.parse(rawKey));
    }

    @Test
    @DisplayName("Key parsing for valid complex values")
    void keyParsingForValidComplexValues() {
        assertAll(
                () -> assertEquals("one.two.three", parser.parse("  \tone.Two.three:    \t ")),
                () -> assertEquals("under_score", parser.parse("under_score:")),
                () -> assertEquals("da-sh", parser.parse(" da-sh: ")),
                () -> assertEquals("1__2",  parser.parse(" 1__2: ")),
                () -> assertEquals("1--2",   parser.parse(" 1--2:"))
        );
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"KEY: ", "Key: ", "KeY:", "kEy:"})
    @DisplayName("Key parsing is case-insensitive (LowerCase)")
    void keyParsingIsCaseInsensitive(String rawKey) {
        assertEquals("key", parser.parse(rawKey));
    }

    @Test
    @DisplayName("Key parsing is working with spécial characters")
    void keyParsingIsWorkingWithSpecialCharacters() {
        assertEquals("clé-français", parser.parse("clÉ-français:"));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"NoColon", "Colon:In:Middle"})
    @DisplayName("Key parsing for missing colon")
    void keyParsingForMissingColon(String rawKey) {
        ExceptionsTester.assertIsThrownAndContains(
                ParsingException.class,
                () -> parser.parse(rawKey),
                "missing colon"
        );
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"space :", "first:second:", "@yes:", "ta\tb:", "spa ce:"})
    @DisplayName("Key parsing for forbidden characters")
    void keyParsingForNotPermittedCharacters(String rawKey) {
        ExceptionsTester.assertIsThrownAndContains(
                ParsingException.class,
                () -> parser.parse(rawKey),
                "forbidden character"
        );
    }

    @Test
    @DisplayName("Key parser can parse multiple values")
    void keyParserCanParseMultipleValues() {
        var internalParser = new KeyParser();
        assertAll(
                () -> ExceptionsTester.assertIsThrownAndContains(
                        ParsingException.class,
                        () -> internalParser.parse("-dash:"),
                        "key start with special character"
                ),
                () -> assertEquals("yes", internalParser.parse("yes: ")),
                () -> ExceptionsTester.assertIsThrownAndContains(
                        ParsingException.class,
                        () -> internalParser.parse("_underscore:"),
                        "key start with special character"
                ),
                () -> assertEquals("ok",  internalParser.parse("ok:"))
        );
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"-DashStart:", "_UnderscoreStart:"})
    @DisplayName("Key parsing for special char at start")
    void keyParsingForSpecialCharAtStart(String rawKey) {
        ExceptionsTester.assertIsThrownAndContains(
                ParsingException.class,
                () -> parser.parse(rawKey),
                "key start with special character"
        );
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "DashEnd-:", "UnderscoreEnd_:",
            "forbidden.after_:", "before.dot-:"
    })
    @DisplayName("Key parsing for special char at end")
    void keyParsingForSpecialCharAtEnd(String rawKey) {
        ExceptionsTester.assertIsThrownAndContains(
                ParsingException.class,
                () -> parser.parse(rawKey),
                "key ends with special character"
        );
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            ".DotStart:", "DotEnd.:", "double..dot:",
            "double.dot..after:", "double..dot.before: ",
            "ending.with.dot.:", "..:"
    })
    @DisplayName("Key parsing for empty key part")
    void keyParsingForEmptyKeyPart(String rawKey) {
        ExceptionsTester.assertIsThrownAndContains(
                ParsingException.class,
                () -> parser.parse(rawKey),
                "empty key part"
        );
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"forbidden.-start.dot:", "start._dot:"})
    @DisplayName("Key parsing for key part starting with special char")
    void keyParsingForKeyPartStartingWithSpecialChar(String rawKey) {
        ExceptionsTester.assertIsThrownAndContains(
                ParsingException.class,
                () -> parser.parse(rawKey),
                "key part start with special character"
        );
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"forbidden-.before.dot:", "before_.dot:", "end.end-.yes:"})
    @DisplayName("Key parsing for key part ending with special char")
    void keyParsingForKeyPartEndingWithSpecialChar(String rawKey) {
        ExceptionsTester.assertIsThrownAndContains(
                ParsingException.class,
                () -> parser.parse(rawKey),
                "key part ends with special character"
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"   :  ", ":"})
    @DisplayName("Key parsing with empty key")
    void keyParsingWithEmptyKey(String rawKey) {
        ExceptionsTester.assertIsThrownAndContains(
                ParsingException.class,
                () -> parser.parse(rawKey),
                "empty key"
        );
    }

}