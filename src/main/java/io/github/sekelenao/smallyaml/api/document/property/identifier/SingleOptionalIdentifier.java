package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;
import java.util.function.Function;

public record SingleOptionalIdentifier<T>(String key, Function<? super String, T> mapper) implements PropertyIdentifier {

    public SingleOptionalIdentifier {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
    }

    public static <T> SingleOptionalIdentifier<T> define(String key, Function<? super String, T> mapper){
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
        return new SingleOptionalIdentifier<>(key, mapper);
    }

    public static SingleOptionalIdentifier<String> define(String key){
        Objects.requireNonNull(key);
        return new SingleOptionalIdentifier<>(key, Function.identity());
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
