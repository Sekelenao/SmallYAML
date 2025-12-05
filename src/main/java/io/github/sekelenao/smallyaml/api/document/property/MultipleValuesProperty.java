package io.github.sekelenao.smallyaml.api.document.property;

import java.util.List;
import java.util.Objects;

/**
 * A record that represents a property with a key and a list of values. Implements the {@code Property} interface.
 * This class is used to handle properties that can have multiple associated values.
 *
 * @since 0.1.0
 */
public record MultipleValuesProperty(String key, List<String> value) implements Property<List<String>> {

    /**
     * Constructs a {@code MultipleValuesProperty} instance, ensuring that both the key and value list are non-null.
     *
     * @param key       a non-null string representing the key of the property
     * @param value a non-null list of strings representing the values associated with the key
     * @throws NullPointerException if {@code key} or {@code value} is null
     *
     * @since 0.1.0
     */
    public MultipleValuesProperty {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
    }

}