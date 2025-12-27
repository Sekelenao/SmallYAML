package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;
import java.util.function.ToDoubleFunction;

public record MultipleMandatoryDoubleIdentifier(String key, ToDoubleFunction<? super String> mapper)
    implements PropertyIdentifier, DoubleIdentifier {

    public MultipleMandatoryDoubleIdentifier {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
    }

    public static MultipleMandatoryDoubleIdentifier define(String key, ToDoubleFunction<? super String> mapper){
        return new MultipleMandatoryDoubleIdentifier(key, mapper);
    }

    public static MultipleMandatoryDoubleIdentifier define(String key){
        return new MultipleMandatoryDoubleIdentifier(key, Long::parseLong);
    }

    @Override
    public Property.Type type() {
        return Property.Type.MULTIPLE;
    }

    @Override
    public Property.Presence presence() {
        return Property.Presence.MANDATORY;
    }

}
