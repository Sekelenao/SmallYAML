package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;

public record SingleOptionalBooleanIdentifier(String key) implements PropertyIdentifier, BooleanIdentifier {

    public SingleOptionalBooleanIdentifier {
        Objects.requireNonNull(key);
    }

    public static SingleOptionalBooleanIdentifier define(String key){
        return new SingleOptionalBooleanIdentifier(key);
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