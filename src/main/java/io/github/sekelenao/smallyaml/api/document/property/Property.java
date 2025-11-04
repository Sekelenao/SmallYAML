package io.github.sekelenao.smallyaml.api.document.property;

/**
 * Represents a generic property abstraction, which can either hold a single value
 * or multiple values associated with a property key. It serves as the parent type
 * for specific property implementations.
 * <p>
 * This interface is sealed and only permits implementations explicitly defined
 * as {@code SingleValueProperty} and {@code MultipleValuesProperty}.
 *
 * @since 1.0.0
 */
public sealed interface Property permits SingleValueProperty, MultipleValuesProperty {

    /**
     * Represents the type of property. A property can either hold a single value
     * or multiple values.
     * <p>
     * This enum is part of the {@code Property} interface and is used to distinguish
     * between properties with different cardinalities.
     * <p>
     * Enum constants:
     * - SINGLE: Indicates a property that holds a single value.
     * - MULTIPLE: Indicates a property that holds multiple values.
     *
     * @since 1.0.0
     */
    enum Type { SINGLE, MULTIPLE }

    /**
     * Retrieves the key associated with the property.
     *
     * @return a string representing the key of the property; never null.
     *
     * @since 1.0.0
     */
    String key();

}
