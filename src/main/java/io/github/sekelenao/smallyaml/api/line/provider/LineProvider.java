package io.github.sekelenao.smallyaml.api.line.provider;

import io.github.sekelenao.smallyaml.internal.parsing.line.Line;

import java.io.IOException;
import java.util.Optional;

public interface LineProvider extends AutoCloseable {

    Optional<Line> nextLine() throws IOException;

}