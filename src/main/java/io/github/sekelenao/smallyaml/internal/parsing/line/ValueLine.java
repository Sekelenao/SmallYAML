package io.github.sekelenao.smallyaml.internal.parsing.line;

import io.github.sekelenao.smallyaml.internal.util.Assertions;

import java.util.Objects;

public record ValueLine(int depth, String value) implements Line {

      public ValueLine {
         Assertions.isPositiveOrZero(depth);
         Objects.requireNonNull(value);
      }

   }