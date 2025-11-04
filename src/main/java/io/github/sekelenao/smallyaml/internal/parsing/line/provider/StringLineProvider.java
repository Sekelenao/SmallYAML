package io.github.sekelenao.smallyaml.internal.parsing.line.provider;

import io.github.sekelenao.smallyaml.api.line.provider.LineProvider;

import java.util.NoSuchElementException;
import java.util.Objects;

public final class StringLineProvider implements LineProvider {

    private final String text;

    private int index;

    public StringLineProvider(String text) {
        Objects.requireNonNull(text);
        this.text = text;
    }

    @Override
    public boolean hasNext() {
        return index < text.length();
    }

    @Override
    public String next() {
        if(!hasNext()){
            throw new NoSuchElementException();
        }
        var line = new StringBuilder();
        while(index < text.length()){
            var currentChar = text.charAt(index++);
            if(currentChar == '\n'){
                return line.toString();
            }
            line.append(currentChar);
        }
        return line.toString();
    }

}
