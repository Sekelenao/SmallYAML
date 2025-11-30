package io.github.sekelenao.smallyaml.test.internal.parsing.line.records.parser;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.internal.parsing.line.records.EmptyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.records.KeyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.records.KeyValueLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.records.Line;
import io.github.sekelenao.smallyaml.internal.parsing.line.records.ListValueLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.records.parser.LineRecordParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

final class LineRecordParserTest {

    private final LineRecordParser parser = new LineRecordParser();

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

        @Test
        @DisplayName("Wrong indent whitespace")
        void wrongIndentWhitespace() {
            assertAll(
                () -> checkException("\t-test", "unexpected whitespace character"),
                () -> checkException("   \t  -test", "unexpected whitespace character")
            );
        }

        @ParameterizedTest(name = "{displayName} ({0})")
        @MethodSource("io.github.sekelenao.smallyaml.test.util.Randoms#intStreamWithSize50")
        @DisplayName("Leading spaces")
        void blankString(int amountOfSpaces) {
            var listValue = " ".repeat(amountOfSpaces) + "-  \"test\"";
            checkValidListValueParsing(listValue, amountOfSpaces, "test");
        }

        @Test
        @DisplayName("Valid list value")
        void validListValue() {
            assertAll(
                    () -> checkValidListValueParsing("-\ttest", 0, "test"),
                    () -> checkValidListValueParsing("- test", 0, "test"),
                    () -> checkValidListValueParsing("  - test", 2, "test"),
                    () -> checkValidListValueParsing("    - \":1:2:3:\"", 4, ":1:2:3:"),
                    () -> checkValidListValueParsing("    - \"test\"", 4, "test"),
                    () -> checkValidListValueParsing("    - \"test\" \t", 4, "test"),
                    () -> checkValidListValueParsing("    - \"test\" \t \t", 4, "test")
            );
        }

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = {"-test", "-test ", "-test  \t", "   -test    ", "--- test", "-1", "--"})
        @DisplayName("List value should have a whitespace after dash")
        void listValueShouldHaveWhitespaceAfterDash(String listValue) {
            checkException(listValue, "list value should have a whitespace after dash");
        }

        @ParameterizedTest(name = "{displayName} ({0})")
        @ValueSource(strings = {"-", "- ", "-  \t", "   -    "})
        @DisplayName("Empty list value")
        void emptyListValue(String listValue) {
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

        @Test
        @DisplayName("Wrong indent whitespace")
        void wrongIndentWhitespace() {
            assertAll(
                () -> checkException("\ttest:", "unexpected whitespace character"),
                () -> checkException("   \t  test:", "unexpected whitespace character")
            );
        }

        @ParameterizedTest(name = "{displayName} ({0})")
        @MethodSource("io.github.sekelenao.smallyaml.test.util.Randoms#intStreamWithSize50")
        @DisplayName("Leading spaces")
        void blankString(int amountOfSpaces) {
            var key = " ".repeat(amountOfSpaces) + "key: ";
            checkValidKeyParsing(key, amountOfSpaces, "key");
        }

        @Test
        @DisplayName("Valid key")
        void validKey() {
            assertAll(
                    () -> checkValidKeyParsing("test:", 0, "test"),
                    () -> checkValidKeyParsing("test-2:", 0, "test-2"),
                    () -> checkValidKeyParsing("  test:", 2, "test"),
                    () -> checkValidKeyParsing("     1-2_3:", 5, "1-2_3"),
                    () -> checkValidKeyParsing("       1.2.3:", 7, "1.2.3"),
                    () -> checkValidKeyParsing("    key: \t", 4, "key"),
                    () -> checkValidKeyParsing("    double--dash:\t \t", 4, "double--dash")
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {":", " :", "  :", "   :    "})
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
        @ValueSource(strings = {"space :", "@yes:"})
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

        @Test
        @DisplayName("Wrong indent whitespace")
        void wrongIndentWhitespace() {
            assertAll(
                () -> checkException("\ttest: value", "unexpected whitespace character"),
                () -> checkException("   \t  test: value", "unexpected whitespace character")
            );
        }

        @ParameterizedTest(name = "{displayName} ({0})")
        @MethodSource("io.github.sekelenao.smallyaml.test.util.Randoms#intStreamWithSize50")
        @DisplayName("Leading spaces")
        void blankString(int amountOfSpaces) {
            var keyValue = " ".repeat(amountOfSpaces) + "key: \"test\" \t";
            checkValidKeyValueParsing(keyValue, amountOfSpaces, "key", "test");
        }

        @Test
        @DisplayName("Valid key")
        void validKey() {
            assertAll(
                    () -> checkValidKeyValueParsing("   key:   \"value \"   ", 3, "key", "value "),
                    () -> checkValidKeyValueParsing("test-2: 2", 0, "test-2", "2"),
                    () -> checkValidKeyValueParsing("  test: value", 2, "test", "value"),
                    () -> checkValidKeyValueParsing("     1-2_3: 3", 5, "1-2_3", "3"),
                    () -> checkValidKeyValueParsing("       1.2.3:  \":yes:\"", 7, "1.2.3", ":yes:"),
                    () -> checkValidKeyValueParsing("    key: \tvalue", 4, "key", "value"),
                    () -> checkValidKeyValueParsing("    double--dash:\t \tok", 4, "double--dash", "ok")
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"key:value", "  key:value", " key:value", "key::value", "key:-value", "key:\"value"})
        @DisplayName("No space after colon")
        void noSpaceAfterColon(String rawKey) {
            checkException(rawKey, "colon must be followed by whitespace character");
        }

        @Test
        @DisplayName("Value with special character")
        void valueWithSpecialCharacter() {
            assertAll(
                () -> checkValidKeyValueParsing("key: :colon", 0, "key", ":colon"),
                () -> checkValidKeyValueParsing("key: -dash", 0, "key", "-dash"),
                () -> checkValidKeyValueParsing("key: \"quote", 0, "key", "\"quote"),
                () -> checkValidKeyValueParsing("key: quote \"after\"", 0, "key", "quote \"after\"")
            );
        }

    }

    @Nested
    @DisplayName("Empty line parsing")
    final class EmptyLineParsing {

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "    \t# Comment", "#Comment", "          ", "     ##", "##", "\t \t"})
        @DisplayName("Empty line/Commented lines")
        void emptyLine(String rawLine) {
            var line = parser.parse(rawLine);
            assertInstanceOf(EmptyLine.class, line);
        }

    }

}