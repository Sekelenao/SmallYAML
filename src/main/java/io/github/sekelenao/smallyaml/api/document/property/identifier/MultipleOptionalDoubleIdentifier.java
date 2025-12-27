package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;
import java.util.function.ToDoubleFunction;

public record MultipleOptionalDoubleIdentifier(String key, ToDoubleFunction<? super String> mapper) implements PropertyIdentifier {

    public MultipleOptionalDoubleIdentifier {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
    }

    public static MultipleOptionalDoubleIdentifier define(String key, ToDoubleFunction<? super String> mapper){
        return new MultipleOptionalDoubleIdentifier(key, mapper);
    }

    public static MultipleOptionalDoubleIdentifier define(String key){
        return new MultipleOptionalDoubleIdentifier(key, Long::parseLong);
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
