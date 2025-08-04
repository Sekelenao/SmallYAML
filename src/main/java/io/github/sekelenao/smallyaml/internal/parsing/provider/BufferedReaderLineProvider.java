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
        return Optional.empty(); // TODO
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }
}
