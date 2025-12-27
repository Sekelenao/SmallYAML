package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;
import java.util.function.Function;

public record SingleMandatoryIdentifier<T>(String key, Function<? super String, T> mapper) implements PropertyIdentifier {

    public SingleMandatoryIdentifier {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
    }

    public static <T> SingleMandatoryIdentifier<T> define(String key, Function<? super String, T> mapper){
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
        return new SingleMandatoryIdentifier<>(key, mapper);
    }

    public static SingleMandatoryIdentifier<String> define(String key){
        Objects.requireNonNull(key);
        return new SingleMandatoryIdentifier<>(key, Function.identity());
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
