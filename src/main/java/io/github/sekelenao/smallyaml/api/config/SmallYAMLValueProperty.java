package io.github.sekelenao.smallyaml.api.config;

import java.util.Objects;

public record SmallYAMLValueProperty(String key, String value) implements SmallYAMLProperty {

    public SmallYAMLValueProperty {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
    }

}