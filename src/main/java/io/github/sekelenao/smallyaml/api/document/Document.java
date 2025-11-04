package io.github.sekelenao.smallyaml.api.document;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Iterator;

/**
 * Represents a generic document containing key-value property mappings.
 * Provides access to the properties through iteration.
 * <p>
 * The {@code Document} interface is intended to serve as a base abstraction
 * for implementations handling structured data with properties that may
 * hold single or multiple values. It allows clients to iterate over
 * the contained properties, each of which is represented by the {@code Property}
 * interface.
 * <p>
 * The properties within a document are key-value pairs where keys are
 * unique identifiers, and values can encapsulate complex structures
 * using single or multiple values.
 *
 * @since 1.0.0
 */
public interface Document {

    /**
     * Returns an iterator over the properties contained in this document.
     * Each property represents a key-value mapping, where the key is a unique identifier
     * and the value may either be a single value or a list of values.
     *
     * @return an {@code Iterator} over the {@code Property} objects in this document
     *
     * @since 1.0.0
     */
    Iterator<Property> iterator();

}
