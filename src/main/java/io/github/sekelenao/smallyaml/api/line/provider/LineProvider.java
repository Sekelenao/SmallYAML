package io.github.sekelenao.smallyaml.api.line.provider;

import io.github.sekelenao.smallyaml.internal.parsing.line.Line;

import java.io.IOException;
import java.util.Optional;

/**
 * Defines a provider of lines, allowing for sequential access to parsed {@link Line} objects.
 * <p>
 * Implementations provide functionality to retrieve the next line from a source, such as an
 * input stream, buffered reader, or string, and return it as an {@link Optional} of {@link Line}.
 * When no more lines are available, an empty {@link Optional} is returned.
 * <p>
 * This interface is designed to abstract the process of line parsing, enabling
 * different implementations to define the source and method of parsing.
 */
public interface LineProvider {

    /**
     * Retrieves the next parsed line as an {@link Optional} of {@link Line}.
     * The method sequentially accesses and parses lines from the underlying source.
     * If no more lines are available, it returns an empty {@link Optional}.
     *
     * @return an {@link Optional} containing the next parsed {@link Line}, or an empty {@link Optional} if there are no more lines available
     * @throws IOException if an I/O error occurs while retrieving or parsing the next line
     */
    Optional<Line> nextLine() throws IOException;

}