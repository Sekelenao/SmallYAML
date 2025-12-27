package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;
import java.util.function.ToLongFunction;

public record MultipleMandatoryLongIdentifier(String key, ToLongFunction<? super String> mapper)
    implements PropertyIdentifier, LongIdentifier {

    public MultipleMandatoryLongIdentifier {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
    }

    public static MultipleMandatoryLongIdentifier define(String key, ToLongFunction<? super String> mapper){
        return new MultipleMandatoryLongIdentifier(key, mapper);
    }

    public static MultipleMandatoryLongIdentifier define(String key){
        return new MultipleMandatoryLongIdentifier(key, Long::parseLong);
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
