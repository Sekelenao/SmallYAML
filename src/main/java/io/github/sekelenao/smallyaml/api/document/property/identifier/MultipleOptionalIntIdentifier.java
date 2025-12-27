package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;
import java.util.function.ToIntFunction;

public record MultipleOptionalIntIdentifier(String key, ToIntFunction<? super String> mapper) implements PropertyIdentifier {

    public MultipleOptionalIntIdentifier {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
    }

    public static MultipleOptionalIntIdentifier define(String key, ToIntFunction<? super String> mapper){
        return new MultipleOptionalIntIdentifier(key, mapper);
    }

    public static MultipleOptionalIntIdentifier define(String key){
        return new MultipleOptionalIntIdentifier(key, Integer::parseInt);
    }

    @Override
    public Property.Type type() {
        return Property.Type.MULTIPLE;
    }

    @Override
    public Property.Presence presence() {
        return Property.Presence.OPTIONAL;
    }

}
