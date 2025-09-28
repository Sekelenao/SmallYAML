package io.github.sekelenao.smallyaml.internal.parsing.line;

import io.github.sekelenao.smallyaml.internal.util.Assertions;

import java.util.Objects;

public record ListValueLine(int depth, String value) implements Line {

    public ListValueLine {
        Assertions.isPositiveOrZero(depth);
        Objects.requireNonNull(value);
    }

}