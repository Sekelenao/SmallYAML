package io.github.sekelenao.smallyaml.test.util;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.internal.parsing.parser.StringParser;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class TestUtilities {

    private static final int MAX = 10_000;

    private static final String[] BLANK_CHARS = {" ", "\t"};

    private static final Random RANDOM = new Random();

    public static String blankString(int size){
        var builder = new StringBuilder();
        for(int i = 0; i < size; i++){
            builder.append(BLANK_CHARS[RANDOM.nextInt(BLANK_CHARS.length)]);
        }
        return builder.toString();
    }

    public static Stream<Integer> intProvider() {
        return Stream.generate(() -> RANDOM.nextInt(MAX)).limit(50);
    }

    public static Path findResource(String resourceName) throws URISyntaxException {
        Objects.requireNonNull(resourceName);
        var url = TestUtilities.class.getClassLoader().getResource(resourceName);
        var uri = Objects.requireNonNull(url).toURI();
        return Path.of(uri);
    }

    public static final class StringParserTester {

        private final StringParser parser;

        public StringParserTester(StringParser parser){
            this.parser = Objects.requireNonNull(parser);
        }

        public void checkValid(String rawLine, String expectedValue) {
            Objects.requireNonNull(rawLine);
            Objects.requireNonNull(expectedValue);
            assertEquals(expectedValue, parser.parse(rawLine));
        }

        public void checkException(String rawLine, String expectedExceptionDetails) {
            Objects.requireNonNull(rawLine);
            Objects.requireNonNull(expectedExceptionDetails);
            var exception = assertThrows(ParsingException.class, () -> parser.parse(rawLine));
            assertTrue(
                    exception.getMessage().contains(expectedExceptionDetails),
                    "Exception message should contain " + expectedExceptionDetails + " but is: " +exception.getMessage()
            );
        }

    }
}
