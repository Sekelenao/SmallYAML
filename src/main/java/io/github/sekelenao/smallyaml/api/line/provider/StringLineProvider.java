package io.github.sekelenao.smallyaml.api.line.provider;

import io.github.sekelenao.smallyaml.internal.parsing.line.Line;
import io.github.sekelenao.smallyaml.internal.parsing.parser.LineParser;

import java.util.Objects;
import java.util.Optional;

public final class StringLineProvider implements LineProvider {

    private final LineParser parser = new LineParser();

    private final String[] lines; // WARNING: Nullable to avoid array allocation

    private int currentLine = 0;

    private StringLineProvider(String[] lines) {
        this.lines = lines;
    }

    public static StringLineProvider of(String text) {
        Objects.requireNonNull(text);
        if (text.isEmpty()) {
            return new StringLineProvider(null);
        }
        return new StringLineProvider(text.split("\n"));
    }

    @Override
    public Optional<Line> nextLine() {
        if (lines == null || currentLine >= lines.length) {
            return Optional.empty();
        }
        var line = parser.parse(lines[currentLine++]);
        return Optional.of(line);
    }

}
