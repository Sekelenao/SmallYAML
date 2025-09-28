package io.github.sekelenao.smallyaml.test.util.stringparser;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.internal.parsing.parser.StringParser;
import io.github.sekelenao.smallyaml.test.util.Exceptions;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class StringParserTester {

    private final StringParser parser;

    public StringParserTester(StringParser parser) {
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
        Exceptions.isThrownAndContains(
            ParsingException.class,
            () -> parser.parse(rawLine),
            expectedExceptionDetails
        );
    }

}
