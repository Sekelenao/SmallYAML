package io.github.sekelenao.smallyaml.api.document;

import io.github.sekelenao.smallyaml.api.document.property.MultipleValuesProperty;
import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.document.property.SingleValueProperty;
import io.github.sekelenao.smallyaml.api.exception.document.DuplicatedPropertyException;
import io.github.sekelenao.smallyaml.api.exception.document.MissingPropertyException;
import io.github.sekelenao.smallyaml.api.exception.document.WrongPropertyTypeException;
import io.github.sekelenao.smallyaml.api.line.provider.LineProvider;
import io.github.sekelenao.smallyaml.api.mapping.PropertyValueMapper;
import io.github.sekelenao.smallyaml.internal.collection.ValueList;
import io.github.sekelenao.smallyaml.internal.parsing.ParsingCollector;
import io.github.sekelenao.smallyaml.internal.parsing.SmallYAMLParser;
import io.github.sekelenao.smallyaml.internal.parsing.parser.StrictBooleanParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Supplier;

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

    public String getSingleStringOrDefault(String key, Supplier<String> defaultValueSupplier){
        Objects.requireNonNull(key);
        Objects.requireNonNull(defaultValueSupplier);
        var value = properties.get(key);
        if(value == null){
            return defaultValueSupplier.get();
        }
        if(value instanceof String valueAsString){
            return valueAsString;
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
    }

    public String getSingleStringOrThrow(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            throw MissingPropertyException.forFollowing(key);
        }
        if(value instanceof String valueAsString){
            return valueAsString;
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

    public List<String> getMultipleStringsOrDefault(String key, Supplier<List<String>> defaultValueSupplier){
        Objects.requireNonNull(key);
        Objects.requireNonNull(defaultValueSupplier);
        var value = properties.get(key);
        if(value == null){
            return defaultValueSupplier.get();
        }
        if(value instanceof ValueList valueList){
            return valueList.asListView();
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
    }

    public List<String> getMultipleStringsOrThrow(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            throw MissingPropertyException.forFollowing(key);
        }
        if(value instanceof ValueList valueList){
            return valueList.asListView();
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
            throw MissingPropertyException.forFollowing(key);
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

    public int getSingleIntOrDefault(String key, int defaultValue){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            return defaultValue;
        }
        if(value instanceof String valueAsString){
            return Integer.parseInt(valueAsString);
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
    }

    public int getSingleIntOrThrow(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            throw MissingPropertyException.forFollowing(key);
        }
        if(value instanceof String valueAsString){
            return Integer.parseInt(valueAsString);
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

    public int[] getMultipleIntsOrDefault(String key, Supplier<int[]> defaultValueSupplier){
        Objects.requireNonNull(key);
        Objects.requireNonNull(defaultValueSupplier);
        var value = properties.get(key);
        if(value == null){
            return defaultValueSupplier.get();
        }
        if(value instanceof ValueList valueList){
            return valueList.asArrayOfInts();
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
    }

    public int[] getMultipleIntsOrThrow(String key, Supplier<int[]> defaultValueSupplier){
        Objects.requireNonNull(key);
        Objects.requireNonNull(defaultValueSupplier);
        var value = properties.get(key);
        if(value == null){
            throw MissingPropertyException.forFollowing(key);
        }
        if(value instanceof ValueList valueList){
            return valueList.asArrayOfInts();
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

    public long getSingleLongOrDefault(String key, long defaultValue){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            return defaultValue;
        }
        if(value instanceof String valueAsString){
            return Long.parseLong(valueAsString);
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
    }

    public long getSingleLongOrThrow(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            throw MissingPropertyException.forFollowing(key);
        }
        if(value instanceof String valueAsString){
            return Long.parseLong(valueAsString);
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

    public long[] getMultipleLongsOrDefault(String key, Supplier<long[]> defaultValueSupplier){
        Objects.requireNonNull(key);
        Objects.requireNonNull(defaultValueSupplier);
        var value = properties.get(key);
        if(value == null){
            return defaultValueSupplier.get();
        }
        if(value instanceof ValueList valueList){
            return valueList.asArrayOfLongs();
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
    }

    public long[] getMultipleLongsOrThrow(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            throw MissingPropertyException.forFollowing(key);
        }
        if(value instanceof ValueList valueList){
            return valueList.asArrayOfLongs();
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

    public double getSingleDoubleOrDefault(String key, double defaultValue){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            return defaultValue;
        }
        if(value instanceof String valueAsString){
            return Double.parseDouble(valueAsString);
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
    }

    public double getSingleDoubleOrThrow(String key){
        Objects.requireNonNull(key);
        var value = properties.get(key);
        if(value == null){
            throw MissingPropertyException.forFollowing(key);
        }
        if(value instanceof String valueAsString){
            return Double.parseDouble(valueAsString);
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

    public double[] getMultipleDoublesOrDefault(String key, Supplier<double[]> defaultValueSupplier){
        Objects.requireNonNull(key);
        Objects.requireNonNull(defaultValueSupplier);
        var value = properties.get(key);
        if(value == null){
            return defaultValueSupplier.get();
        }
        if(value instanceof ValueList valueList){
            return valueList.asArrayOfDoubles();
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
    }

    public double[] getMultipleDoublesOrThrow(String key){
       Objects.requireNonNull(key);
       var value = properties.get(key);
       if(value == null){
           throw MissingPropertyException.forFollowing(key);
       }
       if(value instanceof ValueList valueList){
           return valueList.asArrayOfDoubles();
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

    public <T> T getSingleOrDefault(String key, PropertyValueMapper<? super String, T> mapper, Supplier<T> defaultValueSupplier){
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(defaultValueSupplier);
        var value = properties.get(key);
        if(value == null){
            return defaultValueSupplier.get();
        }
        if(value instanceof String valueAsString){
            return mapper.apply(valueAsString);
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
    }

    public <T> T getSingleOrThrow(String key, PropertyValueMapper<? super String, T> mapper){
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
        var value = properties.get(key);
        if(value == null){
            throw MissingPropertyException.forFollowing(key);
        }
        if(value instanceof String valueAsString){
            return mapper.apply(valueAsString);
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

    public <T> List<T> getMultipleOrDefault(String key, PropertyValueMapper<? super String, T> mapper, Supplier<List<T>> defaultValueSupplier){
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(defaultValueSupplier);
        var value = properties.get(key);
        if(value == null){
            return defaultValueSupplier.get();
        }
        if(value instanceof ValueList valueList){
            return valueList.asListView(mapper);
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
    }

    public <T> List<T> getMultipleOrThrow(String key, PropertyValueMapper<? super String, T> mapper){
        Objects.requireNonNull(key);
        Objects.requireNonNull(mapper);
        var value = properties.get(key);
        if(value == null){
            throw MissingPropertyException.forFollowing(key);
        }
        if(value instanceof ValueList valueList){
            return valueList.asListView(mapper);
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
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
