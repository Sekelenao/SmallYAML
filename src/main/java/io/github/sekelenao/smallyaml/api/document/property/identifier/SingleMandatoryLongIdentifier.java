package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;
import java.util.function.ToLongFunction;

public record SingleMandatoryLongIdentifier(String key, ToLongFunction<? super String> mapper) implements PropertyIdentifier {

    public SingleMandatoryLongIdentifier {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
    }

    public static SingleMandatoryLongIdentifier define(String key, ToLongFunction<? super String> mapper){
        return new SingleMandatoryLongIdentifier(key, mapper);
    }

    public static SingleMandatoryLongIdentifier define(String key){
        return new SingleMandatoryLongIdentifier(key, Long::parseLong);
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
