package io.github.sekelenao.smallyaml.test.internal.parsing.parser;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.internal.parsing.line.EmptyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.KeyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.KeyValueLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.Line;
import io.github.sekelenao.smallyaml.internal.parsing.line.ListValueLine;
import io.github.sekelenao.smallyaml.internal.parsing.parser.LineParser;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import io.github.sekelenao.smallyaml.test.util.Randoms;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Tag(TestingTag.INTERNAL)
@Tag(TestingTag.PARSING)
final class LineParserTest {

    private final LineParser parser = new LineParser();

    private static void failBecauseOfWrongLineType(Line line, Class<? extends Line> expectedType) {
        fail("Expected " + expectedType.getSimpleName() + " but was " + line.getClass().getSimpleName());
    }

    private void checkException(String rawLine, String expectedExceptionDetails) {
        Objects.requireNonNull(rawLine);
        Objects.requireNonNull(expectedExceptionDetails);
        var exception = assertThrows(ParsingException.class, () -> parser.parse(rawLine));
        assertTrue(
                exception.getMessage().contains(expectedExceptionDetails),
                "Exception message should contain " + expectedExceptionDetails + " but is: " +exception.getMessage()
        );
    }

    @Test
    @DisplayName("Assertions")
    void assertions() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
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
        @MethodSource("io.github.sekelenao.smallyaml.test.util.Randoms#intStreamWithSize50")
        @DisplayName("Leading spaces")
        void blankString(int lengthOfBlankString) {
            var listValue = Randoms.blankString(lengthOfBlankString) + "-  \"test\"";
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
            checkException(listValue, "empty value");
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
        @MethodSource("io.github.sekelenao.smallyaml.test.util.Randoms#intStreamWithSize50")
        @DisplayName("Leading spaces")
        void blankString(int lengthOfBlankString) {
            var key = Randoms.blankString(lengthOfBlankString) + "key: ";
            checkValidKeyParsing(key, lengthOfBlankString, "key");
        }

        @Test
        @DisplayName("Valid key")
        void validKey() {
            assertAll(
                    () -> checkValidKeyParsing("test:", 0, "test"),
                    () -> checkValidKeyParsing("test-2:", 0, "test-2"),
                    () -> checkValidKeyParsing("\ttest:", 1, "test"),
                    () -> checkValidKeyParsing("\t\t 1-2_3:", 3, "1-2_3"),
                    () -> checkValidKeyParsing("\t\t   1.2.3:", 5, "1.2.3"),
                    () -> checkValidKeyParsing("\t\tkey: \t", 2, "key"),
                    () -> checkValidKeyParsing("\t\tdouble--dash:\t \t", 2, "double--dash")
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {":", " :", "\t:", "   :    "})
        @DisplayName("Empty key")
        void wrongListValue(String listValue) {
            checkException(listValue, "empty key");
        }

        @ParameterizedTest
        @ValueSource(strings = {"NoColon", "Not-A-Key"})
        @DisplayName("Missing colon")
        void missingColon(String rawKey) {
            checkException(rawKey, "missing colon");
        }

        @ParameterizedTest
        @ValueSource(strings = {"space :", "first:second:", "@yes:"})
        @DisplayName("forbidden characters")
        void notPermittedCharacters(String rawKey) {
            checkException(rawKey, "forbidden character");
        }

    }

    @Nested
    @DisplayName("Key with value parsing")
    final class KeyValueParsing {

        private void checkValidKeyValueParsing(String rawKey, int expectedDepth, String expectedKey, String expectedValue) {
            var line = parser.parse(rawKey);
            if (line instanceof KeyValueLine(int depth, String key, String value)) {
                assertAll(
                        () -> assertEquals(expectedDepth, depth),
                        () -> assertEquals(expectedKey, key),
                        () -> assertEquals(expectedValue, value)
                );
            } else {
                failBecauseOfWrongLineType(line, KeyValueLine.class);
            }
        }

        @ParameterizedTest(name = "{displayName} ({0})")
        @MethodSource("io.github.sekelenao.smallyaml.test.util.Randoms#intStreamWithSize50")
        @DisplayName("Leading spaces")
        void blankString(int lengthOfBlankString) {
            var keyValue = Randoms.blankString(lengthOfBlankString) + "key: \"test\" \t";
            checkValidKeyValueParsing(keyValue, lengthOfBlankString, "key", "test");
        }

        @Test
        @DisplayName("Valid key")
        void validKey() {
            assertAll(
                    () -> checkValidKeyValueParsing("   key:   \"value \"   ", 3, "key", "value "),
                    () -> checkValidKeyValueParsing("test-2: 2", 0, "test-2", "2"),
                    () -> checkValidKeyValueParsing("\ttest: value", 1, "test", "value"),
                    () -> checkValidKeyValueParsing("\t\t 1-2_3: 3", 3, "1-2_3", "3"),
                    () -> checkValidKeyValueParsing("\t\t   1.2.3:  \":yes:\"", 5, "1.2.3", ":yes:"),
                    () -> checkValidKeyValueParsing("\t\tkey: \tvalue", 2, "key", "value"),
                    () -> checkValidKeyValueParsing("\t\tdouble--dash:\t \tok", 2, "double--dash", "ok")
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"key: e:e", "key: e-e", "key:  e.e"})
        @DisplayName("Invalid character without quotes")
        void missingValue(String rawKey) {
            checkException(rawKey, "invalid character without quotes");
        }

        @ParameterizedTest
        @ValueSource(strings = {"key:value", "\tkey:value", " key:value"})
        @DisplayName("No space after key")
        void missingEndingQuote(String rawKey) {
            checkException(rawKey, "forbidden character");
        }

    }

    @Nested
    @DisplayName("Empty line parsing")
    final class EmptyLineParsing {

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "    # Comment", "#Comment", "  \t    \t", "   \t##", "##"})
        @DisplayName("Empty line")
        void emptyLine(String rawLine) {
            var line = parser.parse(rawLine);
            assertInstanceOf(EmptyLine.class, line);
        }

    }

}