package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;
import java.util.function.ToIntFunction;

public record MultipleMandatoryIntIdentifier(String key, ToIntFunction<? super String> mapper)
    implements PropertyIdentifier, IntIdentifier {

    public MultipleMandatoryIntIdentifier {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
    }

    public static MultipleMandatoryIntIdentifier define(String key, ToIntFunction<? super String> mapper){
        return new MultipleMandatoryIntIdentifier(key, mapper);
    }

    public static MultipleMandatoryIntIdentifier define(String key){
        return new MultipleMandatoryIntIdentifier(key, Integer::parseInt);
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
