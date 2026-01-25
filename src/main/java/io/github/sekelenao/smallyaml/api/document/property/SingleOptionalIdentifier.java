package io.github.sekelenao.smallyaml.api.document.property;

import java.util.Locale;
import java.util.Objects;

public final class SingleOptionalIdentifier implements PropertyIdentifier {

    private final String key;

    private SingleOptionalIdentifier(String key) {
        this.key = key.toLowerCase(Locale.ROOT);
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

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PropertyIdentifier otherIdentifier && key.equals(otherIdentifier.key());
    }
}