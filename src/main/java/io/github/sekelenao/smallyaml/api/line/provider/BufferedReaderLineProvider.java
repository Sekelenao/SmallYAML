package io.github.sekelenao.smallyaml.api.line.provider;

import io.github.sekelenao.smallyaml.internal.parsing.line.Line;
import io.github.sekelenao.smallyaml.internal.parsing.parser.LineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Provides functionality to read and parse lines from a {@link BufferedReader} and convert them
 * into {@link Line} objects.
 * <p>
 * This class implements {@link LineProvider}, which allows for iterative access to parsed lines,
 * and {@link AutoCloseable}, enabling the resource management of the underlying {@link BufferedReader}.
 * Each line read is parsed using the YAML parser, which processes and categorizes lines
 * into appropriate {@link Line} subtypes based on their structure and content.
 */
public final class BufferedReaderLineProvider implements LineProvider, AutoCloseable {

    private final LineParser parser = new LineParser();

    private final BufferedReader bufferedReader;

    private BufferedReaderLineProvider(BufferedReader bufferedReader){
        this.bufferedReader = bufferedReader;
    }

    /**
     * Creates a new instance of {@link BufferedReaderLineProvider} with the specified {@link BufferedReader}.
     *
     * @param bufferedReader the {@link BufferedReader} to read lines from; must not be null
     * @return a new {@link BufferedReaderLineProvider} for reading lines from the provided {@link BufferedReader}
     * @throws NullPointerException if the provided {@link BufferedReader} is null
     */
    public static BufferedReaderLineProvider with(BufferedReader bufferedReader){
        Objects.requireNonNull(bufferedReader);
        return new BufferedReaderLineProvider(bufferedReader);
    }

    /**
     * Retrieves the next line from the buffered reader, parses it, and returns it as an {@link Optional} of {@link Line}.
     * If no more lines are available, an empty {@link Optional} is returned.
     *
     * @return an {@link Optional} containing the next parsed {@link Line}, or an empty {@link Optional} if the end of the reader is reached
     * @throws IOException if an I/O error occurs while reading from the buffered reader
     */
    @Override
    public Optional<Line> nextLine() throws IOException {
        return Optional.ofNullable(bufferedReader.readLine()).map(parser::parse);
    }

    /**
     * Closes the underlying {@link BufferedReader} to release system resources.
     * <p>
     * This method is intended to be called when the {@link BufferedReaderLineProvider}
     * is no longer needed, ensuring that the associated resources are properly released.
     *
     * @throws IOException if an I/O error occurs while closing the {@link BufferedReader}
     */
    @Override
    public void close() throws IOException {
        bufferedReader.close();
    }

}
