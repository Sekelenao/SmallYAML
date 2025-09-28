package io.github.sekelenao.smallyaml.api.mapping;

import java.util.function.Function;

@FunctionalInterface
public interface PropertyValueMapper<S, T> extends Function<S, T> {

}