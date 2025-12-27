package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;

public record SingleMandatoryBooleanIdentifier(String key) implements PropertyIdentifier, BooleanIdentifier {

    public SingleMandatoryBooleanIdentifier {
        Objects.requireNonNull(key);
    }

    public static SingleMandatoryBooleanIdentifier define(String key){
        return new SingleMandatoryBooleanIdentifier(key);
    }

    @Override
    public Property.Type type() {
        return Property.Type.SINGLE;
    }

    @Override
    public Property.Presence presence() {
        return Property.Presence.MANDATORY;
    }

}