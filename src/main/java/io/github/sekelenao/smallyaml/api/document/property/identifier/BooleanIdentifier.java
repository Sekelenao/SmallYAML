package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;

public record BooleanIdentifier(String key) implements PropertyIdentifier {

    public BooleanIdentifier {
        Objects.requireNonNull(key);
    }

    public static BooleanIdentifier define(String key){
        return new BooleanIdentifier(key);
    }

    @Override
    public Property.Type type() {
        return Property.Type.SINGLE;
    }

    @Override
    public Property.Presence presence() {
        return Property.Presence.OPTIONAL;
    }

}