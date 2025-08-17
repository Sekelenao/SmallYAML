package io.github.sekelenao.smallyaml.api.config;

import io.github.sekelenao.smallyaml.api.exception.config.DuplicatedPropertyException;
import io.github.sekelenao.smallyaml.api.exception.config.WrongTypeException;
import io.github.sekelenao.smallyaml.internal.collection.ValueList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ConfigBuilder {

    private final Map<String, Object> container = new HashMap<>();

    public void add(String key, String value, boolean allowMultipleValues) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        if(!allowMultipleValues){
            if(container.containsKey(key)){
                throw DuplicatedPropertyException.forFollowing(key);
            }
            container.put(key, value);
            return;
        }
        container.merge(key, new ValueList(value), (existing, newValue) -> {
            if (existing instanceof ValueList existingList) {
                existingList.add(value);
                return existingList;
            }
            throw WrongTypeException.withExpectedInsteadOf(existing.getClass(), List.class);
        });
    }

}
