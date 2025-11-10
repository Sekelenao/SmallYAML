package io.github.sekelenao.smallyaml.api.document;

import io.github.sekelenao.smallyaml.api.document.property.MultipleValuesProperty;
import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.document.property.SingleValueProperty;
import io.github.sekelenao.smallyaml.api.exception.document.WrongPropertyTypeException;
import io.github.sekelenao.smallyaml.api.line.provider.LineProvider;
import io.github.sekelenao.smallyaml.api.mapping.PropertyValueMapper;
import io.github.sekelenao.smallyaml.internal.collection.ValueList;
import io.github.sekelenao.smallyaml.internal.parsing.SmallYAMLParser;
import io.github.sekelenao.smallyaml.internal.parsing.booleans.StrictBooleanParser;
import io.github.sekelenao.smallyaml.internal.parsing.collector.MapParsingCollector;

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
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents a permissive document abstraction designed for parsing and accessing
 * hierarchical property data structures. This class supports flexible value access
 * based on property keys, including single and multiple values of different types.
 * Implements {@link Document} and {@link Iterable}, facilitating various operations
 * and iterability over properties.
 * <p>
 * The class is immutable and final, ensuring thread-safety and preventing inheritance.
 *
 * @since 1.0.0
 */
public final class PermissiveDocument implements Iterable<Property<?>>, Document {

    private final Map<String, Object> properties;

    private PermissiveDocument(Map<String, Object> properties){
        this.properties = properties;
    }

    /**
     * Creates a new instance of PermissiveDocument by parsing the content provided
     * by the given {@link LineProvider}. The provider must return a non-null stream.
     *
     * @param lineProvider the LineProvider, which supplies lines to be parsed,
     *                     must not be null
     * @return a PermissiveDocument instance containing the parsed properties
     * @throws IOException if an I/O error occurs during parsing
     *
     * @since 1.0.0
     */
    public static PermissiveDocument from(LineProvider lineProvider) throws IOException {
        Objects.requireNonNull(lineProvider);
        var collector = new MapParsingCollector();
        var parser = new SmallYAMLParser();
        parser.parse(lineProvider, collector);
        return new PermissiveDocument(collector.underlyingMapAsView());
    }

    /**
     * Creates and returns an empty instance of {@code PermissiveDocument}.
     * The returned document does not contain any properties.
     *
     * @return a new, empty {@code PermissiveDocument} instance
     *
     * @since 1.0.0
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
     *
     * @since 1.0.0
     */
    public boolean hasProperty(String key){
        Objects.requireNonNull(key);
        return properties.containsKey(key);
    }

    /**
     * Determines the type of the property associated with the given key.
     *
     * @param key the key of the property whose type is to be determined.
     * @return the type of the property, either {@link Property.Type#SINGLE} for single values or
     * {@link Property.Type#MULTIPLE} for lists.
     *
     * @throws NullPointerException if the provided key is null.
     * @throws NoSuchElementException if no property exists for the given key.
     * @throws IllegalStateException if the value type of the property is unexpected.
     */
    public Property.Type typeOf(String key){
        Objects.requireNonNull(key);
        if(!properties.containsKey(key)){
            throw new NoSuchElementException();
        }
        var value = properties.get(key);
        return switch (value){
            case String ignored -> Property.Type.SINGLE;
            case ValueList ignored -> Property.Type.MULTIPLE;
            default -> throw new IllegalStateException("Unexpected type: " + value.getClass());
        };
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
     *
     * @since 1.0.0
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
     *
     * @since 1.0.0
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
     *
     * @since 1.0.0
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
     *
     * @since 1.0.0
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
     *
     * @since 1.0.0
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
     *
     * @since 1.0.0
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
     *
     * @since 1.0.0
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
     *
     * @since 1.0.0
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
     *
     * @since 1.0.0
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
     *
     * @since 1.0.0
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
     *
     * @since 1.0.0
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
     *
     * @since 1.0.0
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
     *
     * @since 1.0.0
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
     * @since 1.0.0
     */
    @Override
    public Iterator<Property<?>> iterator() {
        return new Iterator<>() {

            private final Iterator<Map.Entry<String, Object>> iterator = properties.entrySet().iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Property<?> next() {
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

    private static final class PropertySpliterator implements Spliterator<Property<?>> {

        private final Spliterator<Map.Entry<String, Object>> wrappedSpliterator;

        PropertySpliterator(Spliterator<Map.Entry<String, Object>> wrappedSpliterator) {
            this.wrappedSpliterator = Objects.requireNonNull(wrappedSpliterator);
        }

        @Override
        public boolean tryAdvance(Consumer<? super Property<?>> action) {
            Objects.requireNonNull(action);
            return wrappedSpliterator.tryAdvance(entry -> {
                Property<?> property = switch (entry.getValue()) {
                    case String value -> new SingleValueProperty(entry.getKey(), value);
                    case ValueList valueList -> new MultipleValuesProperty(entry.getKey(), valueList.asListView());
                    default -> throw new IllegalStateException("Unexpected value: " + entry.getValue());
                };
                action.accept(property);
            });
        }

        @Override
        public Spliterator<Property<?>> trySplit() {
            var splitResult = wrappedSpliterator.trySplit();
            if (splitResult != null) {
                return new PropertySpliterator(splitResult);
            }
            return null;
        }

        @Override
        public long estimateSize() {
            return wrappedSpliterator.estimateSize();
        }

        @Override
        public int characteristics() {
            return wrappedSpliterator.characteristics() | Spliterator.NONNULL | Spliterator.IMMUTABLE;
        }

    }

    /**
     * Creates and returns a {@code Spliterator} for the properties.
     *
     * @return a {@code Spliterator} over the {@code Property} elements
     *
     * @since 1.0.0
     */
    @Override
    public Spliterator<Property<?>> spliterator() {
        return new PropertySpliterator(properties.entrySet().spliterator());
    }

    /**
     * Creates a sequential Stream of Property elements from the current object.
     * The method uses the object's spliterator to construct the Stream.
     *
     * @return a sequential Stream of Property elements.
     *
     * @since 1.0.0
     */
    @SuppressWarnings("java:S1452")
    public Stream<Property<?>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Compares this {@code PermissiveDocument} to the specified object.
     * The result is {@code true} if and only if the argument is not {@code null}
     * and is a {@code PermissiveDocument} object that contains the same number
     * of properties and all its properties are equal to the corresponding properties
     * of this document.
     *
     * <p>This contract ensures that the equality comparison is reflexive, symmetric,
     * transitive, consistent, and correctly handles comparisons with {@code null}.
     *
     * @param other the object to compare with this {@code PermissiveDocument}
     * @return {@code true} if the specified object is equal to this {@code PermissiveDocument}, {@code false} otherwise.
     *
     * @since 1.0.0
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof PermissiveDocument otherDocument
            && properties.size() == otherDocument.properties.size()
            && properties.equals(otherDocument.properties);
    }

    /**
     * Computes the hash code for this object based on its properties.
     *
     * @return the hash code value for this object
     *
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return properties.hashCode();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string consisting of the string representation of the properties contained in this document
     *
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return properties.toString();
    }

}
