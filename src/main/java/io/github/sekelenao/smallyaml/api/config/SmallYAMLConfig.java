package io.github.sekelenao.smallyaml.api.config;

import io.github.sekelenao.smallyaml.api.exception.config.MissingPropertyException;
import io.github.sekelenao.smallyaml.api.exception.config.WrongTypeException;
import io.github.sekelenao.smallyaml.internal.collection.ValueList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public final class SmallYAMLConfig implements Iterable<SmallYAMLProperty> {

    private final Map<String, Object> container = new HashMap<>();

    public void add(String key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        container.put(key, value);
    }

    public void addValueInList(String key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        container.merge(key, new ValueList(value), (existing, newValue) -> {
            if (existing instanceof ValueList existingList) {
                existingList.add(value);
                return existingList;
            }
            throw WrongTypeException.withExpectedInsteadOf(existing.getClass(), List.class);
        });
    }

    public Optional<String> getAsString(String key){
        Objects.requireNonNull(key);
        var value = container.get(key);
        if(value == null){
            return Optional.empty();
        }
        if(value instanceof String valueAsString){
            return Optional.of(valueAsString);
        }
        throw WrongTypeException.withExpectedInsteadOf(value.getClass(), String.class);
    }

    public String getAsStringOrDefault(String key, String defaultValue){
        Objects.requireNonNull(key);
        Objects.requireNonNull(defaultValue);
        var value = container.get(key);
        if(value == null){
            return defaultValue;
        }
        if(value instanceof String valueAsString){
            return valueAsString;
        }
        throw WrongTypeException.withExpectedInsteadOf(value.getClass(), String.class);
    }

    public String getAsStringOrThrows(String key){
        Objects.requireNonNull(key);
        var value = container.get(key);
        if(value == null){
            throw MissingPropertyException.forFollowing(key);
        }
        if(value instanceof String valueAsString){
            return valueAsString;
        }
        throw WrongTypeException.withExpectedInsteadOf(value.getClass(), String.class);
    }

    public Optional<List<String>> getAsListValue(String key){
        Objects.requireNonNull(key);
        var value = container.get(key);
        if(value == null){
            return Optional.empty();
        }
        if(value instanceof ValueList valueList){
            return Optional.of(valueList.asListView());
        }
        throw WrongTypeException.withExpectedInsteadOf(value.getClass(), List.class);
    }

    public List<String> getAsListOrDefault(String key, List<String> defaultValue){
        Objects.requireNonNull(key);
        Objects.requireNonNull(defaultValue);
        var value = container.get(key);
        if(value == null){
            return defaultValue;
        }
        if(value instanceof ValueList valueList){
            return valueList.asListView();
        }
        throw WrongTypeException.withExpectedInsteadOf(value.getClass(), List.class);
    }

    public List<String> getAsListOrThrows(String key){
        Objects.requireNonNull(key);
        var value = container.get(key);
        if(value == null){
            throw MissingPropertyException.forFollowing(key);
        }
        if(value instanceof ValueList valueList){
            return valueList.asListView();
        }
        throw WrongTypeException.withExpectedInsteadOf(value.getClass(), List.class);
    }

    @Override
    public Iterator<SmallYAMLProperty> iterator() {
        return new Iterator<>() {

            private final Iterator<Map.Entry<String, Object>> iterator = container.entrySet().iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public SmallYAMLProperty next() {
                if (!hasNext()){
                    throw new NoSuchElementException();
                }
                var current = iterator.next();
                return switch (current.getValue()){
                    case String value -> new SmallYAMLValueProperty(current.getKey(), value);
                    case ValueList valueList -> new SmallYAMLListProperty(current.getKey(), valueList);
                    default -> throw new IllegalStateException("Unexpected value: " + current.getValue());
                };
            }

        };
    }
}
