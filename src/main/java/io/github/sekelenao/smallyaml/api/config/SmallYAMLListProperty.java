package io.github.sekelenao.smallyaml.api.config;

import io.github.sekelenao.smallyaml.internal.collection.ValueList;

import java.util.Objects;

public record SmallYAMLListProperty(String key, ValueList valueList) implements SmallYAMLProperty {

    public SmallYAMLListProperty {
        Objects.requireNonNull(key);
        Objects.requireNonNull(valueList);
    }

}