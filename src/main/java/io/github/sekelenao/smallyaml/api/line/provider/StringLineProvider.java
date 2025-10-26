package io.github.sekelenao.smallyaml.api.line.provider;

import io.github.sekelenao.smallyaml.internal.parsing.line.Line;
import io.github.sekelenao.smallyaml.internal.parsing.parser.LineParser;

import java.util.Objects;
import java.util.Optional;

/**
 * A concrete implementation of the {@link LineProvider} interface, which provides
 * sequential access to parsed lines from a source string.
 * <p>
 * This class splits the input text into individual lines based on newline characters
 * and relies on YAML parser to parse each line into a {@link Line} object.
 * <p>
 * If the source text is empty, or no more lines are available for parsing,
 * {@code nextLine} will return an empty {@link Optional}.
 * <p>
 * Instances of this class are immutable, and the line parsing is stateful,
 * advancing progressively through the lines.
 */
public final class StringLineProvider implements LineProvider {

    private final LineParser parser = new LineParser();

    private final String[] lines; // WARNING: Nullable to avoid array allocation

    private int currentLine = 0;

    private StringLineProvider(String[] lines) {
        this.lines = lines;
    }

    /**
     * Creates a new instance of {@link StringLineProvider} from the given text.
     * If the provided text is empty, the method returns a {@link StringLineProvider}
     * with no lines to provide. Otherwise, the text is split into lines using the newline
     * character as the delimiter.
     *
     * @param text the input string to be split into lines; must not be null
     * @return a new instance of {@link StringLineProvider} containing lines derived
     *         from the provided text
     * @throws NullPointerException if the provided text is null
     */
    public static StringLineProvider of(String text) {
        Objects.requireNonNull(text);
        if (text.isEmpty()) {
            return new StringLineProvider(null);
        }
        return new StringLineProvider(text.split("\n"));
    }

    /**
     * Retrieves the next parsed line from the source as an {@link Optional} of {@link Line}.
     * The method advances through the available lines sequentially, parsing and returning
     * each line. If the source does not contain any more lines,
     * an empty {@link Optional} is returned.
     *
     * @return an {@link Optional} containing the next parsed {@link Line}, or an empty
     *         {@link Optional} if no more lines are available
     */
    @Override
    public Optional<Line> nextLine() {
        if (lines == null || currentLine >= lines.length) {
            return Optional.empty();
        }
        var line = parser.parse(lines[currentLine++]);
        return Optional.of(line);
    }

}
