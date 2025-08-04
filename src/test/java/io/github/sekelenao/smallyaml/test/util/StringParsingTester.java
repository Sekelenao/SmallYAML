package io.github.sekelenao.smallyaml.test.util;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.internal.parsing.parser.StringParser;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public final class StringParsingTester {

    private final StringParser parser;

    public StringParsingTester(StringParser parser){
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
