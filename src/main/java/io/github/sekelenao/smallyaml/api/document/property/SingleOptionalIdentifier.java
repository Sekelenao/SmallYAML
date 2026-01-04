package io.github.sekelenao.smallyaml.api.document.property;

import java.util.Objects;

public final class SingleOptionalIdentifier implements PropertyIdentifier {

    private final String key;

    private SingleOptionalIdentifier(String key) {
        this.key = key;
    }

    public static SingleOptionalIdentifier define(String key) {
        Objects.requireNonNull(key);
        return new SingleOptionalIdentifier(key);
    }

    @Override
    public String key() {
        return key;
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