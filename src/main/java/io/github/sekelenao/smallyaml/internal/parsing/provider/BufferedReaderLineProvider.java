package io.github.sekelenao.smallyaml.internal.parsing.provider;

import io.github.sekelenao.smallyaml.internal.parsing.line.Line;
import io.github.sekelenao.smallyaml.internal.parsing.parser.LineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public final class BufferedReaderLineProvider implements LineProvider {

    private final LineParser parser = new LineParser();

    private final BufferedReader reader;

    public BufferedReaderLineProvider(BufferedReader reader){
        this.reader = Objects.requireNonNull(reader);
    }

    @Override
    public Optional<Line> nextLine() throws IOException {
        var rawLine = reader.readLine();
        if(rawLine == null){
            return Optional.empty();
        }
        return Optional.of(parser.parse(rawLine));
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

}
