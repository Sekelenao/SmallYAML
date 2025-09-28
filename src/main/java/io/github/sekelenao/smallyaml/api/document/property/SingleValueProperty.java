package io.github.sekelenao.smallyaml.api.document.property;

import java.util.Objects;

public record SingleValueProperty(String key, String value) implements Property {

    public SingleValueProperty {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
    }

}