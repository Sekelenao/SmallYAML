package io.github.sekelenao.smallyaml.internal.parsing.line.parser;

import io.github.sekelenao.smallyaml.test.util.StringParsingTester;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;

@Tag(TestingTag.PARSING)
final class KeyParserTest {

    private final KeyParser parser = new KeyParser();

    private final StringParsingTester parsingTester = new StringParsingTester(parser);

    @ParameterizedTest
    @ValueSource(strings = {"key:", "    key:   ", "key: \t  ", "  \t   key:  \t  "})
    @DisplayName("Key parsing for valid values")
    void keyParsingForValidValues(String rawKey) {
        parsingTester.checkValid(rawKey, "key");
    }

    @Test
    @DisplayName("Key parsing for valid complex values")
    void keyParsingForValidComplexValues(){
        assertAll(
                () -> parsingTester.checkValid("  \tone.two.three:    \t ", "one.two.three"),
                () -> parsingTester.checkValid("under_score:", "under_score"),
                () -> parsingTester.checkValid(" da-sh: ", "da-sh")
        );
    }

    @Test
    @DisplayName("Key parsing for missing colon")
    void keyParsingForMissingColon(){
        parsingTester.checkException("NoColon", "missing colon");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "space :", "first:second:", "-DashStart:",
            "_UnderscoreStart:", ".DotStart:", "DashEnd-:",
            "UnderscoreEnd_:", "DotEnd.:"
    })
    @DisplayName("Key parsing for invalid values")
    void keyParsingForInvalidValues(String rawKey) {
        parsingTester.checkException(rawKey, "wrong format");
    }

}