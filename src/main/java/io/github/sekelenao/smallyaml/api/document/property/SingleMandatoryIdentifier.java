package io.github.sekelenao.smallyaml.api.document.property;

import java.util.Objects;

public final class SingleMandatoryIdentifier implements PropertyIdentifier {

    private final String key;

    private SingleMandatoryIdentifier(String key) {
        this.key = Objects.requireNonNull(key);
    }

    public static SingleMandatoryIdentifier define(String key) {
        return new SingleMandatoryIdentifier(key);
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
        return Property.Presence.MANDATORY;
    }
}