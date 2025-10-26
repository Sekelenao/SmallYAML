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

/**
 * Represents a permissive document abstraction designed for parsing and accessing
 * hierarchical property data structures. This class supports flexible value access
 * based on property keys, including single and multiple values of different types.
 * Implements {@link Document} and {@link Iterable}, facilitating various operations
 * and iterability over properties.
 * <p>
 * The class is immutable and final, ensuring thread-safety and preventing inheritance.
 */
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

    /**
     * Creates a new instance of {@code PermissiveDocument} by parsing the contents
     * provided by the specified {@code LineProvider}. The method uses a YAML parser
     * to process the lines and collect the properties into a permissive document structure.
     *
     * @param lineProvider the source of lines to be parsed; must not be null
     * @return a new {@code PermissiveDocument} containing the parsed properties
     * @throws IOException if an I/O error occurs while reading lines from the provider
     * @throws NullPointerException if {@code lineProvider} is null
     */
    public static PermissiveDocument from(LineProvider lineProvider) throws IOException {
        Objects.requireNonNull(lineProvider);
        var parser = new SmallYAMLParser();
        var collector = new PermissiveDocumentCollector();
        parser.parse(lineProvider, collector);
        return new PermissiveDocument(collector.collectedProperties);
    }

    /**
     * Creates and returns an empty instance of {@code PermissiveDocument}.
     * The returned document does not contain any properties.
     *
     * @return a new, empty {@code PermissiveDocument} instance
     */
    public static PermissiveDocument empty(){
        return new PermissiveDocument(new HashMap<>());
    }

    /**
     * Checks if the document contains a property with the specified key.
     * <p>
     * Note: A property without an associated value does not exist.
     * This method only returns {@code true} for keys that have a value.
     *
     * @param key the key of the property to check for; must not be null
     * @return {@code true} if the property exists in the document, {@code false} otherwise
     * @throws NullPointerException if the specified key is null
     */
    public boolean hasProperty(String key){
        Objects.requireNonNull(key);
        return properties.containsKey(key);
    }

    /**
     * Retrieves the value associated with the specified key as a single string, if available.
     * If the key does not exist or the associated value is null, the method returns an empty {@code Optional}.
     * <p>
     * If the value is of an incompatible type, an exception is thrown.
     *
     * @param key the key of the property to retrieve; must not be null
     * @return an {@code Optional} containing the string value if present and valid; otherwise an empty {@code Optional}
     * @throws NullPointerException if the key is null
     * @throws WrongPropertyTypeException if the value associated with the key is not a single string
     */
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

    /**
     * Retrieves the value associated with the specified key as a list of strings, if available.
     * If the key does not exist or the associated value is null, the method returns an empty {@code Optional}.
     * <p>
     * If the value is of an incompatible type, an exception is thrown.
     *
     * @param key the key of the property to retrieve; must not be null
     * @return an {@code Optional} containing the list of strings if present and valid; otherwise an empty {@code Optional}
     * @throws NullPointerException if the key is null
     * @throws WrongPropertyTypeException if the value associated with the key is not a list of strings
     */
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

    /**
     * Retrieves the value associated with the specified key as a boolean. If the key does not
     * exist or the associated value is null, the method returns the provided default value.
     * The value is parsed using strict boolean parsing logic.
     * <p>
     * A boolean is {@code true} or {@code false} case-insensitive.
     * <p>
     * If the value is of an incompatible type, an exception is thrown.
     *
     * @param key the key of the property to retrieve; must not be null
     * @param defaultValue the default boolean value to return if the key does not exist or the value is null
     * @return the boolean value associated with the key, or the default value if the key or value does not exist
     * @throws NullPointerException if the key is null
     * @throws WrongPropertyTypeException if the value associated with the key is not a single string
     */
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

    /**
     * Retrieves the value associated with the specified key as a boolean. If the key does not
     * exist or the associated value is null, an exception is thrown.
     * The value is parsed using strict boolean parsing logic.
     * <p>
     * A boolean is {@code true} or {@code false} case-insensitive.
     * <p>
     * If the value is of an incompatible type, an exception is thrown.
     *
     * @param key the key of the property to retrieve; must not be null
     * @return the boolean value associated with the key
     * @throws NullPointerException if the key is null
     * @throws NoSuchElementException if the key does not exist or the value associated with the key is null
     * @throws WrongPropertyTypeException if the value associated with the key is not a single string
     */
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

    /**
     * Retrieves the value associated with the specified key as a single integer, if available.
     * If the key does not exist or the associated value is null, the method returns an empty {@code OptionalInt}.
     * <p>
     * If the value is of an incompatible type, an exception is thrown.
     *
     * @param key the key of the property to retrieve; must not be null
     * @return an {@code OptionalInt} containing the integer value if present and valid; otherwise an empty {@code OptionalInt}
     * @throws NullPointerException if the key is null
     * @throws NumberFormatException if the value cannot be parsed as an integer
     * @throws WrongPropertyTypeException if the value associated with the key is not a single string
     */
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

    /**
     * Retrieves the value associated with the specified key as an array of integers, if available.
     * If the key does not exist or the associated value is null, the method returns an empty {@code Optional}.
     * <p>
     * If the value is of an incompatible type, an exception is thrown.
     *
     * @param key the key of the property to retrieve; must not be null
     * @return an {@code Optional} containing the array of integers if present and valid; otherwise an empty {@code Optional}
     * @throws NullPointerException if the key is null
     * @throws NumberFormatException if any value in the list cannot be parsed as an integer
     * @throws WrongPropertyTypeException if the value associated with the key is not a list of strings
     */
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

    /**
     * Retrieves the value associated with the specified key as a single long, if available.
     * If the key does not exist or the associated value is null, the method returns an empty {@code OptionalLong}.
     * <p>
     * If the value is of an incompatible type, an exception is thrown.
     *
     * @param key the key of the property to retrieve; must not be null
     * @return an {@code OptionalLong} containing the long value if present and valid; otherwise an empty {@code OptionalLong}
     * @throws NullPointerException if the key is null
     * @throws NumberFormatException if the value cannot be parsed as a long
     * @throws WrongPropertyTypeException if the value associated with the key is not a single string
     */
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

    /**
     * Retrieves the value associated with the specified key as an array of longs, if available.
     * If the key does not exist or the associated value is null, the method returns an empty {@code Optional}.
     * <p>
     * If the value is of an incompatible type, an exception is thrown.
     *
     * @param key the key of the property to retrieve; must not be null
     * @return an {@code Optional} containing the array of longs if present and valid; otherwise an empty {@code Optional}
     * @throws NullPointerException if the key is null
     * @throws NumberFormatException if any value in the list cannot be parsed as a long
     * @throws WrongPropertyTypeException if the value associated with the key is not a list of strings
     */
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

    /**
     * Retrieves the value associated with the specified key as a single double, if available.
     * If the key does not exist or the associated value is null, the method returns an empty {@code OptionalDouble}.
     * <p>
     * If the value is of an incompatible type, an exception is thrown.
     *
     * @param key the key of the property to retrieve; must not be null
     * @return an {@code OptionalDouble} containing the double value if present and valid; otherwise an empty {@code OptionalDouble}
     * @throws NullPointerException if the key is null
     * @throws NumberFormatException if the value cannot be parsed as a double
     * @throws WrongPropertyTypeException if the value associated with the key is not a single string
     */
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

    /**
     * Retrieves the value associated with the specified key as an array of doubles, if available.
     * If the key does not exist or the associated value is null, the method returns an empty {@code Optional}.
     * <p>
     * If the value is of an incompatible type, an exception is thrown.
     *
     * @param key the key of the property to retrieve; must not be null
     * @return an {@code Optional} containing the array of doubles if present and valid; otherwise an empty {@code Optional}
     * @throws NullPointerException if the key is null
     * @throws NumberFormatException if any value in the list cannot be parsed as a double
     * @throws WrongPropertyTypeException if the value associated with the key is not a list of strings
     */
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

    /**
     * Retrieves the value associated with the specified key as a single value, if available,
     * and applies the provided mapper to convert it.
     * If the key does not exist or the associated value is null, the method returns an empty {@code Optional}.
     * <p>
     * If the value is of an incompatible type, an exception is thrown.
     *
     * @param <T> the type of the mapped value
     * @param key the key of the property to retrieve; must not be null
     * @param mapper the mapper to convert the string value; must not be null
     * @return an {@code Optional} containing the mapped value if present and valid; otherwise an empty {@code Optional}
     * @throws NullPointerException if the key or mapper is null
     * @throws WrongPropertyTypeException if the value associated with the key is not a single string
     */
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

    /**
     * Retrieves the value associated with the specified key as a list of values, if available,
     * and applies the provided mapper to convert each element.
     * If the key does not exist or the associated value is null, the method returns an empty {@code Optional}.
     * <p>
     * If the value is of an incompatible type, an exception is thrown.
     *
     * @param <T> the type of the mapped values
     * @param key the key of the property to retrieve; must not be null
     * @param mapper the mapper to convert each string value; must not be null
     * @return an {@code Optional} containing the list of mapped values if present and valid; otherwise an empty {@code Optional}
     * @throws NullPointerException if the key or mapper is null
     * @throws WrongPropertyTypeException if the value associated with the key is not a list of strings
     */
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

    /**
     * Extracts and returns a set of immediate child keys (subkeys) for the provided key
     * from the available keys in the properties.
     * <p>
     * Note: If a subkey does not have any child property with a value, it is not considered
     * to exist and will not be included in the returned set.
     *
     * @param key the parent key for which immediate subkeys are to be retrieved; must not be null
     * @return an unmodifiable set of subkeys that are immediate children of the specified key
     *         from the properties or an empty set if no subkeys are found
     */
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

    /**
     * Returns an iterator that allows traversal over the properties.
     * Each property is represented as a {@link Property} object.
     * The iterator converts entries into
     * {@link SingleValueProperty} or {@link MultipleValuesProperty}
     * based on the type of the value.
     *
     * @return an {@link Iterator} for iterating over {@link Property} objects
     */
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
