package io.github.sekelenao.smallyaml.api.document.property;

import java.util.List;
import java.util.Objects;

public record MultipleValuesProperty(String key, List<String> valueList) implements Property {

    public MultipleValuesProperty {
        Objects.requireNonNull(key);
        Objects.requireNonNull(valueList);
    }

}