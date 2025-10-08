package io.github.sekelenao.smallyaml.internal.parsing.line;

import io.github.sekelenao.smallyaml.internal.util.Assertions;

import java.util.Objects;

public record KeyLine(int depth, String key) implements Line {

    public KeyLine {
        Assertions.isPositiveOrZero(depth);
        Objects.requireNonNull(key);
    }

}