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

public interface LineProvider extends AutoCloseable {

    static LineProvider with(String text){
        Objects.requireNonNull(text);
        return new StringLineProvider(text);
    }

    static LineProvider with(BufferedReader bufferedReader){
        Objects.requireNonNull(bufferedReader);
        return new BufferedReaderLineProvider(bufferedReader);
    }

    static LineProvider with(InputStream inputStream, Charset charset){
        Objects.requireNonNull(inputStream);
        Objects.requireNonNull(charset);
        var inputStreamReader = new InputStreamReader(inputStream, charset);
        return new BufferedReaderLineProvider(new BufferedReader(inputStreamReader));
    }

    static LineProvider with(InputStream inputStream){
        Objects.requireNonNull(inputStream);
        return with(inputStream, StandardCharsets.UTF_8);
    }

    boolean hasNext() throws IOException;

    String next() throws IOException;

    @Override
    default void close() throws IOException {
        /* By default, this method does nothing */
    }

}