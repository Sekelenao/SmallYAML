package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;
import java.util.function.ToIntFunction;

public record SingleOptionalIntIdentifier(String key, ToIntFunction<? super String> mapper)
    implements PropertyIdentifier, IntIdentifier {

    public SingleOptionalIntIdentifier {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
    }

    public static SingleOptionalIntIdentifier define(String key, ToIntFunction<? super String> mapper){
        return new SingleOptionalIntIdentifier(key, mapper);
    }

    public static SingleOptionalIntIdentifier define(String key){
        return new SingleOptionalIntIdentifier(key, Integer::parseInt);
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
