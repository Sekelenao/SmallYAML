package io.github.sekelenao.smallyaml.api.document.property.identifier;


import io.github.sekelenao.smallyaml.api.document.property.Property;

public interface PropertyIdentifier {

    /**
     * Retrieves the key associated with the property identifier.
     *
     * @return the key of the property
     */
    String key();

    Property.Type type();

    Property.Presence presence();

}
