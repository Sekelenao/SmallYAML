package io.github.sekelenao.smallyaml.internal.parsing.line.records;

import io.github.sekelenao.smallyaml.internal.util.Assertions;

import java.util.Objects;

public record KeyValueLine(int depth, String key, String value) implements Line {

    public KeyValueLine {
        Assertions.isPositiveOrZero(depth);
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
    }

}