package io.github.sekelenao.smallyaml.internal.parsing.line.parser;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.internal.parsing.line.ListValueLine;
import io.github.sekelenao.smallyaml.internal.parsing.parser.LineParser;
import io.github.sekelenao.smallyaml.test.util.TestRandomizer;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag(TestingTag.PARSING)
final class LineParserTest {

    private final LineParser parser = new LineParser();

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
            if (line instanceof ListValueLine(int depth, String value)) {
                assertAll(
                    () -> assertEquals(lengthOfBlankString, depth),
                    () -> assertEquals("test", value)
                );
            } else {
                fail("Unexpected line type " + line.getClass().getSimpleName());
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {"-", "- ", "-  \t"})
        @DisplayName("Wrong list value")
        void wrongListValue(String listValue) {
            var exception = assertThrows(ParsingException.class, () -> parser.parse(listValue));
            assertTrue(exception.getMessage().contains("empty value"));
        }

    }

}