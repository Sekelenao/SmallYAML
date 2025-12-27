package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;
import java.util.function.ToDoubleFunction;

public record SingleMandatoryDoubleIdentifier(String key, ToDoubleFunction<? super String> mapper) implements PropertyIdentifier {

    public SingleMandatoryDoubleIdentifier {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
    }

    public static SingleMandatoryDoubleIdentifier define(String key, ToDoubleFunction<? super String> mapper){
        return new SingleMandatoryDoubleIdentifier(key, mapper);
    }

    public static SingleMandatoryDoubleIdentifier define(String key){
        return new SingleMandatoryDoubleIdentifier(key, Long::parseLong);
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
