package io.github.sekelenao.smallyaml.api.document;

import io.github.sekelenao.smallyaml.api.document.property.MultipleValuesProperty;
import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleMandatoryIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleOptionalIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleValueProperty;
import io.github.sekelenao.smallyaml.api.exception.document.NotRegisteredIdentifierException;
import io.github.sekelenao.smallyaml.internal.collection.EmptyValue;
import io.github.sekelenao.smallyaml.internal.collection.ValueList;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class BoundedDocument implements Document {

    private final Map<PropertyIdentifier, Object> properties;

    BoundedDocument(Map<PropertyIdentifier, Object> properties) {
        this.properties = Objects.requireNonNull(properties);
    }

    public static BoundedDocument empty(){
        return new BoundedDocument(Collections.emptyMap());
    }

    public boolean hasRegistered(PropertyIdentifier identifier){
        Objects.requireNonNull(identifier);
        return properties.containsKey(identifier);
    }

    public <T> T getSingle(SingleMandatoryIdentifier identifier, Function<? super String, T> mapper){
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(mapper);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return mapper.apply((String) properties.get(identifier));
    }

    public <T> Optional<T> getSingle(SingleOptionalIdentifier identifier, Function<? super String, T> mapper){
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


}
