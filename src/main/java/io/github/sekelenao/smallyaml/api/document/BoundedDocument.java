package io.github.sekelenao.smallyaml.api.document;

import io.github.sekelenao.smallyaml.api.document.property.MultipleMandatoryIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.MultipleOptionalIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.MultipleValuesProperty;
import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleMandatoryIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleOptionalIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleValueProperty;
import io.github.sekelenao.smallyaml.api.exception.document.NotRegisteredIdentifierException;
import io.github.sekelenao.smallyaml.internal.collection.EmptyValue;
import io.github.sekelenao.smallyaml.internal.collection.ValueList;
import io.github.sekelenao.smallyaml.internal.parsing.StrictBooleanParser;

import java.util.Collections;
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
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents a document with a fixed set of properties, identified by {@link PropertyIdentifier}s.
 * A {@code BoundedDocument} provides type-safe access to its properties, ensuring that only
 * registered properties can be retrieved.
 *
 * @since 0.2.0
 */
public class BoundedDocument implements Document {

    private final Map<PropertyIdentifier, Object> properties;

    /**
     * Package-private constructor, use {@link BoundedDocumentFactory#createDocument(LineProvider)}
     * to create an instance.
     *
     * @param properties the map of properties
     */
    BoundedDocument(Map<PropertyIdentifier, Object> properties) {
        this.properties = Objects.requireNonNull(properties);
    }

    /**
     * Creates an empty {@code BoundedDocument}.
     *
     * @return an empty BoundedDocument
     *
     * @since 0.2.0
     */
    public static BoundedDocument empty(){
        return new BoundedDocument(Collections.emptyMap());
    }

    /**
     * Returns a new {@link BoundedDocumentFactoryBuilder} to configure and build
     * a {@link BoundedDocumentFactory}.
     *
     * @return a new BoundedDocumentFactoryBuilder
     *
     * @since 0.2.0
     */
    public static BoundedDocumentFactoryBuilder factoryBuilder(){
        return new BoundedDocumentFactoryBuilder();
    }

    /**
     * Checks if a property with the given identifier is registered in this document.
     *
     * @param identifier the property identifier to check
     * @return {@code true} if the identifier is registered, {@code false} otherwise
     * @throws NullPointerException if identifier is null
     *
     * @since 0.2.0
     */
    public boolean hasRegistered(PropertyIdentifier identifier){
        Objects.requireNonNull(identifier);
        return properties.containsKey(identifier);
    }

    /**
     * Returns the set of all property identifiers registered in this document.
     *
     * @return an unmodifiable set of property identifiers
     *
     * @since 0.2.0
     */
    public Set<PropertyIdentifier> registeredIdentifiers(){
        return Collections.unmodifiableSet(properties.keySet());
    }

    /**
     * Retrieves the value of a single mandatory property and maps it using the provided mapper.
     *
     * @param <T>        the type of the mapped value
     * @param identifier the identifier of the property
     * @param mapper     the function to map the property's string value
     * @return the mapped value
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier or mapper is null
     *
     * @since 0.2.0
     */
    public <T> T get(SingleMandatoryIdentifier identifier, Function<? super String, T> mapper){
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(mapper);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return mapper.apply((String) properties.get(identifier));
    }

    /**
     * Retrieves the value of a single optional property and maps it using the provided mapper.
     *
     * @param <T>        the type of the mapped value
     * @param identifier the identifier of the property
     * @param mapper     the function to map the property's string value
     * @return an {@link Optional} containing the mapped value, or empty if the property is missing
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier or mapper is null
     *
     * @since 0.2.0
     */
    public <T> Optional<T> get(SingleOptionalIdentifier identifier, Function<? super String, T> mapper){
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(mapper);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        var value = properties.get(identifier);
        if(value == EmptyValue.INSTANCE){
            return Optional.empty();
        }
        var mappedValue = mapper.apply((String) value);
        return Optional.of(mappedValue);
    }

    /**
     * Retrieves the values of a multiple mandatory property and maps them using the provided mapper.
     *
     * @param <T>        the type of the mapped values
     * @param identifier the identifier of the property
     * @param mapper     the function to map each of the property's string values
     * @return a list of mapped values
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier or mapper is null
     *
     * @since 0.2.0
     */
    public <T> List<T> get(MultipleMandatoryIdentifier identifier, Function<? super String, T> mapper){
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(mapper);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        var value = (ValueList) properties.get(identifier);
        return value.asListView(mapper);
    }

    /**
     * Retrieves the values of a multiple optional property and maps them using the provided mapper.
     *
     * @param <T>        the type of the mapped values
     * @param identifier the identifier of the property
     * @param mapper     the function to map each of the property's string values
     * @return an {@link Optional} containing a list of mapped values, or empty if the property is missing
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier or mapper is null
     *
     * @since 0.2.0
     */
    public <T> Optional<List<T>> get(MultipleOptionalIdentifier identifier, Function<? super String, T> mapper){
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(mapper);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        var value = properties.get(identifier);
        if(value == EmptyValue.INSTANCE){
            return Optional.empty();
        }
        return Optional.of(((ValueList) value).asListView(mapper));
    }

    /**
     * Retrieves the string value of a single mandatory property.
     *
     * @param identifier the identifier of the property
     * @return the string value of the property
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     *
     * @since 0.2.0
     */
    public String get(SingleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return (String) properties.get(identifier);
    }

    /**
     * Retrieves the string value of a single optional property.
     *
     * @param identifier the identifier of the property
     * @return an {@link Optional} containing the string value, or empty if the property is missing
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     *
     * @since 0.2.0
     */
    public Optional<String> get(SingleOptionalIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        var value = properties.get(identifier);
        if(value == EmptyValue.INSTANCE){
            return Optional.empty();
        }
        return Optional.of((String) value);
    }

    /**
     * Retrieves the string values of a multiple mandatory property.
     *
     * @param identifier the identifier of the property
     * @return a list of string values
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     *
     * @since 0.2.0
     */
    public List<String> get(MultipleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        var values = (ValueList) properties.get(identifier);
        return values.asListView();
    }

    /**
     * Retrieves the string values of a multiple optional property.
     *
     * @param identifier the identifier of the property
     * @return an {@link Optional} containing a list of string values, or empty if the property is missing
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     *
     * @since 0.2.0
     */
    public Optional<List<String>> get(MultipleOptionalIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        var value = properties.get(identifier);
        if(value == EmptyValue.INSTANCE){
            return Optional.empty();
        }
        return Optional.of(((ValueList) value).asListView());
    }

    /**
     * Retrieves the boolean value of a single mandatory property.
     *
     * @param identifier the identifier of the property
     * @return the boolean value
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     * @throws io.github.sekelenao.smallyaml.api.exception.parsing.BooleanFormatException if the value is not a valid boolean
     *
     * @since 0.2.0
     */
    public boolean getBoolean(SingleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return StrictBooleanParser.parse((String) properties.get(identifier));
    }

    /**
     * Retrieves the boolean value of a single optional property, or a default value if missing.
     *
     * @param identifier   the identifier of the property
     * @param defaultValue the default value to return if the property is missing
     * @return the boolean value or the default value
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     * @throws io.github.sekelenao.smallyaml.api.exception.parsing.BooleanFormatException if the value is not a valid boolean
     *
     * @since 0.2.0
     */
    public boolean getBooleanOrDefault(SingleOptionalIdentifier identifier, boolean defaultValue){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        var value = properties.get(identifier);
        if(value == EmptyValue.INSTANCE){
            return defaultValue;
        }
        return StrictBooleanParser.parse((String) value);
    }

    /**
     * Retrieves the integer value of a single mandatory property.
     *
     * @param identifier the identifier of the property
     * @return the integer value
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     * @throws NumberFormatException if the value is not a valid integer
     *
     * @since 0.2.0
     */
    public int getInt(SingleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return Integer.parseInt((String) properties.get(identifier));
    }

    /**
     * Retrieves the integer value of a single optional property.
     *
     * @param identifier the identifier of the property
     * @return an {@link OptionalInt} containing the integer value, or empty if the property is missing
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     * @throws NumberFormatException if the value is not a valid integer
     *
     * @since 0.2.0
     */
    public OptionalInt getInt(SingleOptionalIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        var value = properties.get(identifier);
        if(value == EmptyValue.INSTANCE){
            return OptionalInt.empty();
        }
        return OptionalInt.of(Integer.parseInt((String) value));
    }

    /**
     * Retrieves the integer values of a multiple mandatory property.
     *
     * @param identifier the identifier of the property
     * @return an array of integers
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     * @throws NumberFormatException if any value is not a valid integer
     *
     * @since 0.2.0
     */
    public int[] getInts(MultipleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return ((ValueList) properties.get(identifier)).asArrayOfInts();
    }

    /**
     * Retrieves the integer values of a multiple optional property.
     *
     * @param identifier the identifier of the property
     * @return an {@link Optional} containing an array of integers, or empty if the property is missing
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     * @throws NumberFormatException if any value is not a valid integer
     *
     * @since 0.2.0
     */
    public Optional<int[]> getInts(MultipleOptionalIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        var value = properties.get(identifier);
        if(value == EmptyValue.INSTANCE){
            return Optional.empty();
        }
        return Optional.of(((ValueList) value).asArrayOfInts());
    }

    /**
     * Retrieves the long value of a single mandatory property.
     *
     * @param identifier the identifier of the property
     * @return the long value
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     * @throws NumberFormatException if the value is not a valid long
     *
     * @since 0.2.0
     */
    public long getLong(SingleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return Long.parseLong((String) properties.get(identifier));
    }

    /**
     * Retrieves the long value of a single optional property.
     *
     * @param identifier the identifier of the property
     * @return an {@link OptionalLong} containing the long value, or empty if the property is missing
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     * @throws NumberFormatException if the value is not a valid long
     *
     * @since 0.2.0
     */
    public OptionalLong getLong(SingleOptionalIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        var value = properties.get(identifier);
        if(value == EmptyValue.INSTANCE){
            return OptionalLong.empty();
        }
        return OptionalLong.of(Long.parseLong((String) value));
    }

    /**
     * Retrieves the long values of a multiple mandatory property.
     *
     * @param identifier the identifier of the property
     * @return an array of longs
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     * @throws NumberFormatException if any value is not a valid long
     *
     * @since 0.2.0
     */
    public long[] getLongs(MultipleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return ((ValueList) properties.get(identifier)).asArrayOfLongs();
    }

    /**
     * Retrieves the long values of a multiple optional property.
     *
     * @param identifier the identifier of the property
     * @return an {@link Optional} containing an array of longs, or empty if the property is missing
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     * @throws NumberFormatException if any value is not a valid long
     *
     * @since 0.2.0
     */
    public Optional<long[]> getLongs(MultipleOptionalIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        var value = properties.get(identifier);
        if(value == EmptyValue.INSTANCE){
            return Optional.empty();
        }
        return Optional.of(((ValueList) value).asArrayOfLongs());
    }

    /**
     * Retrieves the double value of a single mandatory property.
     *
     * @param identifier the identifier of the property
     * @return the double value
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     * @throws NumberFormatException if the value is not a valid double
     *
     * @since 0.2.0
     */
    public double getDouble(SingleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return Double.parseDouble((String) properties.get(identifier));
    }

    /**
     * Retrieves the double value of a single optional property.
     *
     * @param identifier the identifier of the property
     * @return an {@link OptionalDouble} containing the double value, or empty if the property is missing
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     * @throws NumberFormatException if the value is not a valid double
     *
     * @since 0.2.0
     */
    public OptionalDouble getDouble(SingleOptionalIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        var value = properties.get(identifier);
        if(value == EmptyValue.INSTANCE){
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(Double.parseDouble((String) value));
    }

    /**
     * Retrieves the double values of a multiple mandatory property.
     *
     * @param identifier the identifier of the property
     * @return an array of doubles
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     * @throws NumberFormatException if any value is not a valid double
     *
     * @since 0.2.0
     */
    public double[] getDoubles(MultipleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return ((ValueList) properties.get(identifier)).asArrayOfDoubles();
    }

    /**
     * Retrieves the double values of a multiple optional property.
     *
     * @param identifier the identifier of the property
     * @return an {@link Optional} containing an array of doubles, or empty if the property is missing
     * @throws NotRegisteredIdentifierException if the identifier is not registered in this document
     * @throws NullPointerException if identifier is null
     * @throws NumberFormatException if any value is not a valid double
     *
     * @since 0.2.0
     */
    public Optional<double[]> getDoubles(MultipleOptionalIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        var value = properties.get(identifier);
        if(value == EmptyValue.INSTANCE){
            return Optional.empty();
        }
        return Optional.of(((ValueList) value).asArrayOfDoubles());
    }

    /**
     * Returns an iterator that allows traversal over the properties of this document.
     * <p>
     * Only properties that are present in the document (i.e., not empty) are included.
     *
     * @return an {@link Iterator} over {@link Property} objects
     *
     * @since 0.2.0
     */
    @Override
    public Iterator<Property<?>> iterator() {
        return new Iterator<>() {

            private final Iterator<Map.Entry<PropertyIdentifier, Object>> iterator = properties.entrySet().iterator();

            private Map.Entry<PropertyIdentifier, Object> nextToReturn = null;

            @Override
            public boolean hasNext() {
                if(nextToReturn != null){
                    return true;
                }
                while(iterator.hasNext()){
                    var entry = iterator.next();
                    if(entry.getValue() != EmptyValue.INSTANCE){
                        nextToReturn = entry;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Property<?> next() {
                if (!hasNext()){
                    throw new NoSuchElementException();
                }
                var entry = nextToReturn;
                nextToReturn = null;
                return switch (entry.getValue()){
                    case String value -> new SingleValueProperty(entry.getKey().key(), value);
                    case ValueList valueList -> new MultipleValuesProperty(entry.getKey().key(), valueList.asListView());
                    default -> throw new IllegalStateException("Unexpected value: " + entry.getValue());
                };
            }

        };
    }

    /**
     * Creates and returns a {@link Spliterator} for the properties of this document.
     * <p>
     * Only properties that are present in the document (i.e., not empty) are included.
     *
     * @return a {@link Spliterator} over the {@link Property} elements
     *
     * @since 0.2.0
     */
    @Override
    public Spliterator<Property<?>> spliterator() {
        int characteristics = Spliterator.NONNULL | Spliterator.IMMUTABLE | Spliterator.DISTINCT;
        return Spliterators.spliteratorUnknownSize(iterator(), characteristics);
    }

    /**
     * Returns a sequential {@link Stream} of the properties of this document.
     * <p>
     * Only properties that are present in the document (i.e., not empty) are included.
     *
     * @return a sequential {@link Stream} of {@link Property} objects
     *
     * @since 0.2.0
     */
    @SuppressWarnings("java:S1452")
    public Stream<Property<?>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof BoundedDocument otherDocument
            && properties.size() == otherDocument.properties.size()
            && properties.equals(otherDocument.properties);
    }

    @Override
    public int hashCode() {
        return properties.hashCode();
    }

    @Override
    public String toString() {
        return properties.entrySet().stream()
            .filter(entry -> entry.getValue() != EmptyValue.INSTANCE)
            .map(entry -> entry.getKey().key() + ": " + entry.getValue())
            .collect(Collectors.joining(", ", "{", "}"));
    }

}
