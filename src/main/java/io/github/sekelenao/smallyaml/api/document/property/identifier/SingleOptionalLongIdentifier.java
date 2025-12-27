package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;
import java.util.function.ToLongFunction;

public record SingleOptionalLongIdentifier(String key, ToLongFunction<? super String> mapper) implements PropertyIdentifier {

    public SingleOptionalLongIdentifier {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
    }

    public static SingleOptionalLongIdentifier define(String key, ToLongFunction<? super String> mapper){
        return new SingleOptionalLongIdentifier(key, mapper);
    }

    public static SingleOptionalLongIdentifier define(String key){
        return new SingleOptionalLongIdentifier(key, Long::parseLong);
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
