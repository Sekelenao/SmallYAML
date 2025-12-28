package io.github.sekelenao.smallyaml.api.document.property;

import java.util.Objects;

public final class MultipleMandatoryIdentifier implements PropertyIdentifier {

    private final String key;

    private MultipleMandatoryIdentifier(String key) {
        this.key = Objects.requireNonNull(key);
    }

    public static MultipleMandatoryIdentifier define(String key) {
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