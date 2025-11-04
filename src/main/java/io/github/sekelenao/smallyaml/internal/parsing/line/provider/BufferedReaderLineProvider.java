package io.github.sekelenao.smallyaml.internal.parsing.line.provider;

import io.github.sekelenao.smallyaml.api.line.provider.LineProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class BufferedReaderLineProvider implements LineProvider, AutoCloseable {

    private final BufferedReader bufferedReader;

    private String nextLine;

    public BufferedReaderLineProvider(BufferedReader bufferedReader){
        Objects.requireNonNull(bufferedReader);
        this.bufferedReader = bufferedReader;
    }

    @Override
    public boolean hasNext() throws IOException{
        if(nextLine == null){
            nextLine = bufferedReader.readLine();
        }
        return nextLine != null;
    }

    @Override
    public String next() throws IOException {
        if(!hasNext()){
            throw new NoSuchElementException();
        }
        var current = nextLine;
        nextLine = null;
        return current;
    }

    @Override
    public void close() throws IOException {
        bufferedReader.close();
    }

}
