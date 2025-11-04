package io.github.sekelenao.smallyaml.api.mapping;

import java.util.function.Function;

/**
 * Represents a functional interface for defining a mapping between a source type and a target type.
 * This interface extends the {@code Function} interface, allowing for the transformation
 * of a value of type {@code S} (source) to type {@code T} (target).
 * <p>
 * Primarily designed to be used for property value mappings
 *
 * @since 1.0.0
 */
@FunctionalInterface
public interface PropertyValueMapper<S, T> extends Function<S, T> {

}