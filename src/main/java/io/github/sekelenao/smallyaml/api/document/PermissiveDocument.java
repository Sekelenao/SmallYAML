package io.github.sekelenao.smallyaml.api.document;

import io.github.sekelenao.smallyaml.api.document.property.MultipleValuesProperty;
import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.document.property.SingleValueProperty;
import io.github.sekelenao.smallyaml.api.exception.document.DuplicatedPropertyException;
import io.github.sekelenao.smallyaml.api.exception.document.WrongPropertyTypeException;
import io.github.sekelenao.smallyaml.api.line.provider.LineProvider;
import io.github.sekelenao.smallyaml.api.mapping.PropertyValueMapper;
import io.github.sekelenao.smallyaml.internal.collection.ValueList;
import io.github.sekelenao.smallyaml.internal.parsing.ParsingCollector;
import io.github.sekelenao.smallyaml.internal.parsing.SmallYAMLParser;
import io.github.sekelenao.smallyaml.internal.parsing.parser.StrictBooleanParser;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;

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

    public static PermissiveDocument empty(){
        return new PermissiveDocument(new HashMap<>());
    }

    public boolean hasProperty(String key){
        Objects.requireNonNull(key);
        return properties.containsKey(key);
    }

    public Optional<String> getSingleString(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            return Optional.empty();
        }
        if(value instanceof String valueAsString){
            return Optional.of(valueAsString);
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
    }

    public Optional<List<String>> getMultipleStrings(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            return Optional.empty();
        }
        if(value instanceof ValueList valueList){
            return Optional.of(valueList.asListView());
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
    }

    public boolean getSingleBooleanOrDefault(String key, boolean defaultValue){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            return defaultValue;
        }
        if(value instanceof String valueAsString){
            return StrictBooleanParser.parse(valueAsString);
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
    }

    public boolean getSingleBooleanOrThrow(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            throw new NoSuchElementException();
        }
        if(value instanceof String valueAsString){
            return StrictBooleanParser.parse(valueAsString);
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
    }

    public OptionalInt getSingleInt(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            return OptionalInt.empty();
        }
        if (value instanceof String valueAsString) {
            return OptionalInt.of(Integer.parseInt(valueAsString));
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
    }

    public Optional<int[]> getMultipleInts(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            return Optional.empty();
        }
        if(value instanceof ValueList valueList){
            return Optional.of(valueList.asArrayOfInts());
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
    }

    public OptionalLong getSingleLong(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            return OptionalLong.empty();
        }
        if(value instanceof String valueAsString){
            return OptionalLong.of(Long.parseLong(valueAsString));
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
    }

    public Optional<long[]> getMultipleLongs(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            return Optional.empty();
        }
        if(value instanceof ValueList valueList){
            return Optional.of(valueList.asArrayOfLongs());
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
    }

    public OptionalDouble getSingleDouble(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            return OptionalDouble.empty();
        }
        if(value instanceof String valueAsString){
            return OptionalDouble.of(Double.parseDouble(valueAsString));
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
    }

    public Optional<double[]> getMultipleDoubles(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            return Optional.empty();
        }
        if(value instanceof ValueList valueList){
            return Optional.of(valueList.asArrayOfDoubles());
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
    }

    public <T> Optional<T> getSingle(String key, PropertyValueMapper<? super String, T> mapper){
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
        var value = properties.get(key);
        if(value == null){
            return Optional.empty();
        }
        if(value instanceof String valueAsString){
            var mappedValue = mapper.apply(valueAsString);
            return Optional.of(mappedValue);
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
    }

    public <T> Optional<List<T>> getMultiple(String key, PropertyValueMapper<? super String, T> mapper){
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
        var value = properties.get(key);
        if(value == null){
            return Optional.empty();
        }
        if(value instanceof ValueList valueList){
            return Optional.of(valueList.asListView(mapper));
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
    }

    public Set<String> subKeysOf(String key){
        Objects.requireNonNull(key);
        var expectedStart = key + ".";
        var expectedSize = expectedStart.length();
        var setOfSubkeys = new HashSet<String>();
        for(var currentKey : properties.keySet()){
            if(currentKey.length() >= expectedSize && currentKey.startsWith(expectedStart)){
                var nextDotIndex = currentKey.indexOf(".", expectedSize);
                if(nextDotIndex != -1){
                    setOfSubkeys.add(currentKey.substring(0, nextDotIndex));
                }
            }
        }
        return Collections.unmodifiableSet(setOfSubkeys);
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

    @Override
    public boolean equals(Object other) {
        return other instanceof PermissiveDocument otherDocument
            && properties.size() == otherDocument.properties.size()
            && properties.equals(otherDocument.properties);
    }

    @Override
    public int hashCode() {
        return properties.hashCode();
    }

    @Override
    public String toString() {
        return properties.toString();
    }

}
