package io.github.sekelenao.smallyaml.internal.parsing.line.parser;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.internal.parsing.line.ValueLine;
import io.github.sekelenao.smallyaml.test.util.TestRandomizer;
import io.github.sekelenao.smallyaml.test.util.TestingTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Tag(TestingTag.PARSING)
final class LineParserTest {

    private final LineParser parser = new LineParser();

    private void checkForWrongValue(String rawLine, String expectedMessage) {
        var exception = assertThrows(ParsingException.class, () -> parser.parse(rawLine));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Nested
    @DisplayName("List value parsing")
    final class ListValueParsing {

        private static Stream<Integer> intProvider() {
            return Stream.generate(TestRandomizer::nextInt).limit(50);
        }

        @ParameterizedTest(name = "{displayName} ({0})")
        @MethodSource("intProvider")
        @DisplayName("Leading spaces")
        void blankString(int lengthOfBlankString) {
            var listValue = TestRandomizer.blankString(lengthOfBlankString) + "-  test";
            var line = parser.parse(listValue);
            if (line instanceof ValueLine(int depth, String value)) {
                assertAll(
                    () -> assertEquals(lengthOfBlankString, depth),
                    () -> assertEquals("test", value)
                );
            } else {
                fail("Unexpected line type " + line.getClass().getSimpleName());
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {"-test", "-", "- ", "-  \t"})
        @DisplayName("Wrong list value")
        void wrongListValue(String listValue) {
            checkForWrongValue(listValue, "empty value");
        }

    }

}