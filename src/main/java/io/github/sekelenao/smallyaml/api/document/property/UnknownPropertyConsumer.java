package io.github.sekelenao.smallyaml.api.document.property;

/**
 * A functional interface for consuming properties that are not registered
 * in a {@link io.github.sekelenao.smallyaml.api.document.BoundedDocumentFactory}.
 *
 * @since 0.2.0
 */
@FunctionalInterface
public interface UnknownPropertyConsumer {

    /**
     * A consumer that does nothing.
     */
    UnknownPropertyConsumer NOOP = (key, value) -> {};

    /**
     * Consumes an unknown property.
     *
     * @param key   the key of the unknown property
     * @param value the value of the unknown property
     *
     * @since 0.2.0
     */
    void accept(String key, Object value);

}
