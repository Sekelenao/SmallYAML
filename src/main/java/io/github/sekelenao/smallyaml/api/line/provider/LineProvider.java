package io.github.sekelenao.smallyaml.api.line.provider;

import io.github.sekelenao.smallyaml.internal.parsing.line.provider.BufferedReaderLineProvider;
import io.github.sekelenao.smallyaml.internal.parsing.line.provider.StringLineProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Provides a simple pull-based API for reading lines from a character source.
 * <p>
 * Implementations expose two core operations: {@code hasNext()} to check if another line
 * is available and {@code next()} to retrieve it. This interface also offers factory
 * methods to build a {@code LineProvider} from common sources (in-memory text, a
 * {@code BufferedReader}, or an {@code InputStream} with an optional {@code Charset}).
 * <p>
 * Notes:
 * - The returned lines do not include trailing line break characters.
 * - Unless otherwise specified, resources passed to factory methods are not closed
 *   automatically when reading completes; callers remain responsible for closing
 *   them when appropriate. The default {@link #close()} is a no-op.
 *
 * @since 1.0.0
 */
public interface LineProvider extends AutoCloseable {

    /**
     * Creates a line provider that reads from the given in-memory text.
     * <p>
     * Line breaks are recognized as '\n' and are not included in returned lines.
     *
     * @param text the non-null input text to read from
     * @return a line provider backed by the provided text
     * @throws NullPointerException if {@code text} is null
     *
     * @since 1.0.0
     */
    static LineProvider with(String text){
        Objects.requireNonNull(text);
        return new StringLineProvider(text);
    }

    /**
     * Creates a line provider that reads from the given {@link BufferedReader}.
     * <p>
     * The caller remains responsible for closing the provided reader when appropriate.
     *
     * @param bufferedReader the non-null buffered reader to read from
     * @return a line provider backed by the provided reader
     * @throws NullPointerException if {@code bufferedReader} is null
     *
     * @since 1.0.0
     */
    static LineProvider with(BufferedReader bufferedReader){
        Objects.requireNonNull(bufferedReader);
        return new BufferedReaderLineProvider(bufferedReader);
    }

    /**
     * Creates a line provider that reads from the given {@link InputStream} using the provided {@link Charset}.
     * <p>
     * The caller remains responsible for closing the original stream when appropriate.
     *
     * @param inputStream the non-null input stream to read from
     * @param charset the non-null character set used to decode the stream
     * @return a line provider backed by the provided stream and charset
     * @throws NullPointerException if {@code inputStream} or {@code charset} is null
     *
     * @since 1.0.0
     */
    static LineProvider with(InputStream inputStream, Charset charset){
        Objects.requireNonNull(inputStream);
        Objects.requireNonNull(charset);
        var inputStreamReader = new InputStreamReader(inputStream, charset);
        return new BufferedReaderLineProvider(new BufferedReader(inputStreamReader));
    }

    /**
     * Creates a line provider that reads from the given {@link InputStream} using UTF-8.
     * <p>
     * This is a convenience overload equivalent to {@code with(inputStream, StandardCharsets.UTF_8)}.
     * The caller remains responsible for closing the provided stream when appropriate.
     *
     * @param inputStream the non-null input stream to read from
     * @return a line provider backed by the provided stream using UTF-8 decoding
     * @throws NullPointerException if {@code inputStream} is null
     *
     * @since 1.0.0
     */
    static LineProvider with(InputStream inputStream){
        Objects.requireNonNull(inputStream);
        return with(inputStream, StandardCharsets.UTF_8);
    }

    /**
     * Returns whether another line is available for reading.
     *
     * @return {@code true} if a later call to {@link #next()} would return a line; {@code false} otherwise
     * @throws IOException if an I/O error occurs while checking availability
     *
     * @since 1.0.0
     */
    boolean hasNext() throws IOException;

    /**
     * Returns the next line.
     * <p>
     * Implementations should not include the trailing line break in the returned value.
     * If no more lines are available, implementations should throw {@link java.util.NoSuchElementException}.
     *
     * @return the next available line
     * @throws IOException if an I/O error occurs while reading the next line
     * @throws java.util.NoSuchElementException if no more lines are available
     *
     * @since 1.0.0
     */
    String next() throws IOException;

    /**
     * Closes this resource.
     * <p>
     * This method is a part of the {@link AutoCloseable} interface and can be overridden
     * by implementing classes to release any system resources or perform cleanup operations.
     * <p>
     * By default, this method does nothing.
     *
     * @throws IOException if an I/O error occurs while closing the resource
     *
     * @since 1.0.0
     */
    @Override
    default void close() throws IOException {
        /* By default, this method does nothing */
    }

}