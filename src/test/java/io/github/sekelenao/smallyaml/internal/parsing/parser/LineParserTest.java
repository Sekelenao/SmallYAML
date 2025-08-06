package io.github.sekelenao.smallyaml.internal.parsing.parser;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.internal.parsing.line.KeyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.Line;
import io.github.sekelenao.smallyaml.internal.parsing.line.ListValueLine;
import io.github.sekelenao.smallyaml.test.util.TestRandomizer;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag(TestingTag.PARSING)
final class LineParserTest {

    private final LineParser parser = new LineParser();

    private static void failBecauseOfWrongLineType(Line line, Class<? extends Line> expectedType) {
        fail("Expected " + expectedType.getSimpleName() + " but was " + line.getClass().getSimpleName());
    }

    private static Stream<Integer> intProvider() {
        return Stream.generate(TestRandomizer::randomInt).limit(50);
    }

    @Nested
    @DisplayName("List value parsing")
    final class ListValueParsing {

        private void checkValidListValueParsing(String listValue, int expectedDepth, String expectedValue) {
            var line = parser.parse(listValue);
            if (line instanceof ListValueLine(int depth, String value)) {
                assertAll(
                        () -> assertEquals(expectedDepth, depth),
                        () -> assertEquals(expectedValue, value)
                );
            } else {
                failBecauseOfWrongLineType(line, ListValueLine.class);
            }
        }

        @ParameterizedTest(name = "{displayName} ({0})")
        @MethodSource("io.github.sekelenao.smallyaml.internal.parsing.parser.LineParserTest#intProvider")
        @DisplayName("Leading spaces")
        void blankString(int lengthOfBlankString) {
            var listValue = TestRandomizer.blankString(lengthOfBlankString) + "-  \"test\"";
            checkValidListValueParsing(listValue, lengthOfBlankString, "test");
        }

        @Test
        @DisplayName("Valid list value")
        void validListValue() {
            assertAll(
                    () -> checkValidListValueParsing("-test", 0, "test"),
                    () -> checkValidListValueParsing("- test", 0, "test"),
                    () -> checkValidListValueParsing("\t- test", 1, "test"),
                    () -> checkValidListValueParsing("\t\t- \":1:2:3:\"", 2, ":1:2:3:"),
                    () -> checkValidListValueParsing("\t\t- \"test\"", 2, "test"),
                    () -> checkValidListValueParsing("\t\t- \"test\" \t", 2, "test"),
                    () -> checkValidListValueParsing("\t\t- \"test\" \t \t", 2, "test")
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"-", "- ", "-  \t", "   -    "})
        @DisplayName("Wrong list value")
        void wrongListValue(String listValue) {
            var exception = assertThrows(ParsingException.class, () -> parser.parse(listValue));
            assertTrue(exception.getMessage().contains("empty value"));
        }

    }

    @Nested
    @DisplayName("Key parsing")
    final class KeyParsing {

        private void checkValidKeyParsing(String rawKey, int expectedDepth, String expectedKey) {
            var line = parser.parse(rawKey);
            if (line instanceof KeyLine(int depth, String key)) {
                assertAll(
                        () -> assertEquals(expectedDepth, depth),
                        () -> assertEquals(expectedKey, key)
                );
            } else {
                failBecauseOfWrongLineType(line, KeyLine.class);
            }
        }

        @ParameterizedTest(name = "{displayName} ({0})")
        @MethodSource("io.github.sekelenao.smallyaml.internal.parsing.parser.LineParserTest#intProvider")
        @DisplayName("Leading spaces")
        void blankString(int lengthOfBlankString) {
            var key = TestRandomizer.blankString(lengthOfBlankString) + "key: ";
            checkValidKeyParsing(key, lengthOfBlankString, "key");
        }

    }

}