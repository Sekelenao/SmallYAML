package io.github.sekelenao.smallyaml.api.line.provider;

import io.github.sekelenao.smallyaml.internal.parsing.line.Line;
import io.github.sekelenao.smallyaml.internal.parsing.parser.LineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

/**
 * Provides a mechanism to extract lines from an input stream as parsed {@code Line} objects.
 * This class implements the {@code LineProvider} and {@code AutoCloseable} interfaces,
 * ensuring readers can consume lines from an input stream and safely close the underlying resources.
 * <p>
 * This provider supports customization of the character encoding used to interpret the input stream.
 */
public final class InputStreamLineProvider implements LineProvider, AutoCloseable {

    private final LineParser parser = new LineParser();

    private final BufferedReader bufferedReader;

    private InputStreamLineProvider(InputStream inputStream, Charset charset) {
        var inputStreamReader = new InputStreamReader(inputStream, charset);
        this.bufferedReader = new BufferedReader(inputStreamReader);
    }

    /**
     * Creates a new instance of {@link InputStreamLineProvider} with the specified {@link InputStream},
     * using the default character encoding of UTF-8.
     *
     * @param inputStream the {@link InputStream} to read lines from; must not be null
     * @return a new {@link InputStreamLineProvider} for reading lines from the provided {@link InputStream}
     *         using the UTF-8 charset
     * @throws NullPointerException if the provided {@link InputStream} is null
     */
    public static InputStreamLineProvider with(InputStream inputStream) {
        return with(inputStream, StandardCharsets.UTF_8);
    }

    /**
     * Creates a new instance of {@link InputStreamLineProvider} with the specified {@link InputStream}
     * and {@link Charset} for reading lines.
     *
     * @param inputStream the {@link InputStream} to read lines from; must not be null
     * @param charset the {@link Charset} used to decode the {@link InputStream}; must not be null
     * @return a new {@link InputStreamLineProvider} for reading lines from the provided {@link InputStream}
     *         using the specified {@link Charset}
     * @throws NullPointerException if the provided {@link InputStream} or {@link Charset} is null
     */
    public static InputStreamLineProvider with(InputStream inputStream, Charset charset){
        Objects.requireNonNull(inputStream);
        Objects.requireNonNull(charset);
        return new InputStreamLineProvider(inputStream, charset);
    }

    /**
     * Retrieves the next line from the input stream, parses it, and returns it as an {@link Optional} of {@link Line}.
     * If no more lines are available in the input stream, an empty {@link Optional} is returned.
     *
     * @return an {@link Optional} containing the next parsed {@link Line}, or an empty {@link Optional} if the end of the input stream is reached
     * @throws IOException if an I/O error occurs while reading the input stream
     */
    @Override
    public Optional<Line> nextLine() throws IOException {
        return Optional.ofNullable(bufferedReader.readLine()).map(parser::parse);
    }

    /**
     * Closes the underlying {@link InputStream} and releases the associated resources.
     * <p>
     * This method should be called when the {@link InputStreamLineProvider} instance
     * is no longer needed, ensuring that system resources related to the wrapped
     * {@link InputStream} are properly released. Failure to close the {@link InputStream}
     * may result in resource leaks.
     *
     * @throws IOException if an I/O error occurs while closing the {@link InputStream}
     */
    @Override
    public void close() throws IOException {
        bufferedReader.close();
    }

}
