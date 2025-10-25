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

public final class InputStreamLineProvider implements LineProvider, AutoCloseable {

    private final LineParser parser = new LineParser();

    private final BufferedReader bufferedReader;

    public InputStreamLineProvider(InputStream inputStream) {
        this(inputStream, StandardCharsets.UTF_8);
    }

    public InputStreamLineProvider(InputStream inputStream, Charset charset) {
        Objects.requireNonNull(inputStream);
        Objects.requireNonNull(charset);
        var inputStreamReader = new InputStreamReader(inputStream, charset);
        this.bufferedReader = new BufferedReader(inputStreamReader);
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
