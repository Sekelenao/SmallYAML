package io.github.sekelenao.smallyaml.api.document.property;

import java.util.Objects;

/**
 * A record that represents a property with a single value associated with a key.
 * Implements the {@code Property} interface. This class is used to represent
 * properties containing a single string value.
 */
public record SingleValueProperty(String key, String value) implements Property {

    /**
     * Constructs a {@code SingleValueProperty} instance, ensuring that both the key and value are non-null.
     *
     * @param key   a non-null string representing the key of the property
     * @param value a non-null string representing the value associated with the key
     * @throws NullPointerException if {@code key} or {@code value} is null
     */
    public SingleValueProperty {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
    }

}