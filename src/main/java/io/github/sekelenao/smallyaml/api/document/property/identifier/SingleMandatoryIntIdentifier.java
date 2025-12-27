package io.github.sekelenao.smallyaml.api.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Objects;
import java.util.function.ToIntFunction;

public record SingleMandatoryIntIdentifier(String key, ToIntFunction<? super String> mapper) implements PropertyIdentifier {

    public SingleMandatoryIntIdentifier {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
    }

    public static SingleMandatoryIntIdentifier define(String key, ToIntFunction<? super String> mapper){
        return new SingleMandatoryIntIdentifier(key, mapper);
    }

    public static SingleMandatoryIntIdentifier define(String key){
        return new SingleMandatoryIntIdentifier(key, Integer::parseInt);
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
