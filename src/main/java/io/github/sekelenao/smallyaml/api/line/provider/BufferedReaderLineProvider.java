package io.github.sekelenao.smallyaml.api.line.provider;

import io.github.sekelenao.smallyaml.internal.parsing.line.Line;
import io.github.sekelenao.smallyaml.internal.parsing.parser.LineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public final class BufferedReaderLineProvider implements LineProvider, AutoCloseable {

    private final LineParser parser = new LineParser();

    private final BufferedReader bufferedReader;

    private BufferedReaderLineProvider(BufferedReader bufferedReader){
        this.bufferedReader = bufferedReader;
    }

    public static BufferedReaderLineProvider with(BufferedReader bufferedReader){
        Objects.requireNonNull(bufferedReader);
        return new BufferedReaderLineProvider(bufferedReader);
    }

    @Override
    public Optional<Line> nextLine() throws IOException {
        return Optional.ofNullable(bufferedReader.readLine()).map(parser::parse);
    }

    @Override
    public void close() throws IOException {
        bufferedReader.close();
    }

}
