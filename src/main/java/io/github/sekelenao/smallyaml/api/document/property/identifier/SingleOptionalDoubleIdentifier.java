package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;
import java.util.function.ToDoubleFunction;

public record SingleOptionalDoubleIdentifier(String key, ToDoubleFunction<? super String> mapper)
    implements PropertyIdentifier, DoubleIdentifier {

    public SingleOptionalDoubleIdentifier {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
    }

    public static SingleOptionalDoubleIdentifier define(String key, ToDoubleFunction<? super String> mapper){
        return new SingleOptionalDoubleIdentifier(key, mapper);
    }

    public static SingleOptionalDoubleIdentifier define(String key){
        return new SingleOptionalDoubleIdentifier(key, Long::parseLong);
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
