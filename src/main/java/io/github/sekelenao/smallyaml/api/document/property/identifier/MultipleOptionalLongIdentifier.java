package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;
import java.util.function.ToLongFunction;

public record MultipleOptionalLongIdentifier(String key, ToLongFunction<? super String> mapper) implements PropertyIdentifier {

    public MultipleOptionalLongIdentifier {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
    }

    public static MultipleOptionalLongIdentifier define(String key, ToLongFunction<? super String> mapper){
        return new MultipleOptionalLongIdentifier(key, mapper);
    }

    public static MultipleOptionalLongIdentifier define(String key){
        return new MultipleOptionalLongIdentifier(key, Long::parseLong);
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
