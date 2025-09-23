package io.github.sekelenao.smallyaml.api.document;

import io.github.sekelenao.smallyaml.api.document.property.MultipleValuesProperty;
import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.document.property.SingleValueProperty;
import io.github.sekelenao.smallyaml.api.exception.document.DuplicatedPropertyException;
import io.github.sekelenao.smallyaml.api.exception.document.MissingPropertyException;
import io.github.sekelenao.smallyaml.api.exception.document.WrongTypeException;
import io.github.sekelenao.smallyaml.api.line.provider.LineProvider;
import io.github.sekelenao.smallyaml.internal.collection.ValueList;
import io.github.sekelenao.smallyaml.internal.parsing.ParsingCollector;
import io.github.sekelenao.smallyaml.internal.parsing.SmallYAMLParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class PermissiveDocument implements Iterable<Property>, Document {

    private final Map<String, Object> properties;

    private static final class PermissiveDocumentCollector implements ParsingCollector {

        private final Map<String, Object> collectedProperties = new HashMap<>();

        @Override
        public void collectSingleValue(String key, String value) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
            if(collectedProperties.containsKey(key)){
                throw DuplicatedPropertyException.forFollowing(key);
            }
            collectedProperties.put(key, value);
        }

        @Override
        public void collectListValue(String key, String value, boolean isNewList) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
            collectedProperties.merge(key, new ValueList(value), (existing, newValue) -> {
                if(isNewList){
                    throw DuplicatedPropertyException.forFollowing(key);
                }
                if (existing instanceof ValueList existingList) {
                    existingList.add(value);
                    return existingList;
                }
                throw new IllegalStateException("Unexpected type: " + existing.getClass());
            });
        }

    }

    private PermissiveDocument(Map<String, Object> properties){
        this.properties = Objects.requireNonNull(properties);
    }

    public static PermissiveDocument from(LineProvider lineProvider) throws IOException {
        Objects.requireNonNull(lineProvider);
        var parser = new SmallYAMLParser();
        var collector = new PermissiveDocumentCollector();
        parser.parse(lineProvider, collector);
        return new PermissiveDocument(collector.collectedProperties);
    }

    public Optional<String> getAsString(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            return Optional.empty();
        }
        if(value instanceof String valueAsString){
            return Optional.of(valueAsString);
        }
        throw WrongTypeException.withExpected(Property.Type.SINGLE);
    }

    public String getAsStringOrDefault(String key, String defaultValue){
        Objects.requireNonNull(key);
        Objects.requireNonNull(defaultValue);
        var value = properties.get(key);
        if(value == null){
            return defaultValue;
        }
        if(value instanceof String valueAsString){
            return valueAsString;
        }
        throw WrongTypeException.withExpected(Property.Type.SINGLE);
    }

    public String getAsStringOrThrows(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            throw MissingPropertyException.forFollowing(key);
        }
        if(value instanceof String valueAsString){
            return valueAsString;
        }
        throw WrongTypeException.withExpected(Property.Type.SINGLE);
    }

    public Optional<List<String>> getAsStringList(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            return Optional.empty();
        }
        if(value instanceof ValueList valueList){
            return Optional.of(valueList.asListView());
        }
        throw WrongTypeException.withExpected(Property.Type.MULTIPLE);
    }

    public List<String> getAsStringListOrDefault(String key, List<String> defaultValue){
        Objects.requireNonNull(key);
        Objects.requireNonNull(defaultValue);
        var value = properties.get(key);
        if(value == null){
            return defaultValue;
        }
        if(value instanceof ValueList valueList){
            return valueList.asListView();
        }
        throw WrongTypeException.withExpected(Property.Type.MULTIPLE);
    }

    public List<String> getAsStringListOrThrows(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            throw MissingPropertyException.forFollowing(key);
        }
        if(value instanceof ValueList valueList){
            return valueList.asListView();
        }
        throw WrongTypeException.withExpected(Property.Type.MULTIPLE);
    }

    public <T> Optional<T> getSingle(String key, Function<? super String, T> converter){
        Objects.requireNonNull(key);
        Objects.requireNonNull(converter);
        var value = properties.get(key);
        if(value == null){
            return Optional.empty();
        }
        if(value instanceof String valueAsString){
            var convertedValue = converter.apply(valueAsString);
            return Optional.of(convertedValue);
        }
        throw WrongTypeException.withExpected(Property.Type.SINGLE);
    }

    @Override
    public Iterator<Property> iterator() {
        return new Iterator<>() {

            private final Iterator<Map.Entry<String, Object>> iterator = properties.entrySet().iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Property next() {
                if (!hasNext()){
                    throw new NoSuchElementException();
                }
                var current = iterator.next();
                return switch (current.getValue()){
                    case String value -> new SingleValueProperty(current.getKey(), value);
                    case ValueList valueList -> new MultipleValuesProperty(current.getKey(), valueList.asListView());
                    default -> throw new IllegalStateException("Unexpected value: " + current.getValue());
                };
            }

        };
    }
}
