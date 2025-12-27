package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;
import java.util.function.Function;

public record MultipleMandatoryIdentifier<T>(String key, Function<? super String, T> mapper)
    implements PropertyIdentifier, GenericIdentifier<T> {

    public MultipleMandatoryIdentifier {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
    }

    public static <T> MultipleOptionalIdentifier<T> define(String key, Function<? super String, T> mapper){
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
        return new MultipleOptionalIdentifier<>(key, mapper);
    }

    public static MultipleMandatoryIdentifier<String> define(String key){
        Objects.requireNonNull(key);
        return new MultipleMandatoryIdentifier<>(key, Function.identity());
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
