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

public class BoundedDocument implements Document {

    private final Map<PropertyIdentifier, Object> properties;

    BoundedDocument(Map<PropertyIdentifier, Object> properties) {
        this.properties = Objects.requireNonNull(properties);
    }

    public static BoundedDocument empty(){
        return new BoundedDocument(Collections.emptyMap());
    }

    public static BoundedDocumentFactoryBuilder factoryBuilder(){
        return new BoundedDocumentFactoryBuilder();
    }

    public boolean hasRegistered(PropertyIdentifier identifier){
        Objects.requireNonNull(identifier);
        return properties.containsKey(identifier);
    }

    public Set<PropertyIdentifier> registeredIdentifiers(){
        return Collections.unmodifiableSet(properties.keySet());
    }

    public <T> T get(SingleMandatoryIdentifier identifier, Function<? super String, T> mapper){
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(mapper);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return mapper.apply((String) properties.get(identifier));
    }

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

    public <T> List<T> get(MultipleMandatoryIdentifier identifier, Function<? super String, T> mapper){
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(mapper);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        var value = (ValueList) properties.get(identifier);
        return value.asListView(mapper);
    }

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

    public String get(SingleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return (String) properties.get(identifier);
    }

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

    public List<String> get(MultipleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        var values = (ValueList) properties.get(identifier);
        return values.asListView();
    }

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

    public boolean getBoolean(SingleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return StrictBooleanParser.parse((String) properties.get(identifier));
    }

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

    public int getInt(SingleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return Integer.parseInt((String) properties.get(identifier));
    }

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

    public int[] getInts(MultipleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return ((ValueList) properties.get(identifier)).asArrayOfInts();
    }

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

    public long getLong(SingleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return Long.parseLong((String) properties.get(identifier));
    }

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

    public long[] getLongs(MultipleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return ((ValueList) properties.get(identifier)).asArrayOfLongs();
    }

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

    public double getDouble(SingleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return Double.parseDouble((String) properties.get(identifier));
    }

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

    public double[] getDoubles(MultipleMandatoryIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return ((ValueList) properties.get(identifier)).asArrayOfDoubles();
    }

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

    @Override
    public Spliterator<Property<?>> spliterator() {
        int characteristics = Spliterator.NONNULL | Spliterator.IMMUTABLE | Spliterator.DISTINCT;
        return Spliterators.spliteratorUnknownSize(iterator(), characteristics);
    }

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
