package io.github.sekelenao.smallyaml.api.document.property;

import java.util.Objects;

public final class MultipleMandatoryIdentifier implements PropertyIdentifier {

    private final String key;

    private MultipleMandatoryIdentifier(String key) {
        this.key = key;
    }

    public static MultipleMandatoryIdentifier define(String key) {
        Objects.requireNonNull(key);
        return new MultipleMandatoryIdentifier(key);
    }

    @Override
    public String key() {
        return key;
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