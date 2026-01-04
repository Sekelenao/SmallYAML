package io.github.sekelenao.smallyaml.internal.collection;

import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.UnknownPropertyConsumer;
import io.github.sekelenao.smallyaml.api.exception.document.DuplicatedPropertyException;
import io.github.sekelenao.smallyaml.api.exception.document.MissingPropertyException;
import io.github.sekelenao.smallyaml.api.exception.document.WrongPropertyTypeException;
import io.github.sekelenao.smallyaml.internal.reflection.IdentifiersScanner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class BoundedMapParsingCollector implements ParsingCollector {

    private final Map<PropertyIdentifier, Object> map = new HashMap<>();

    private final Map<String, PropertyIdentifier> reversedRegistry = new HashMap<>();

    private final UnknownPropertyConsumer unknownPropertyConsumer;

    public BoundedMapParsingCollector(Set<Class<?>> typesToScan, UnknownPropertyConsumer consumer) {
        Objects.requireNonNull(typesToScan);
        this.unknownPropertyConsumer = Objects.requireNonNull(consumer);
        for (var type : typesToScan) {
            for (var identifier : IdentifiersScanner.get(type)){
                if(reversedRegistry.containsKey(identifier.key())){
                    throw new IllegalArgumentException("Duplicated identifier definition: " + identifier.key());
                }
                reversedRegistry.put(identifier.key(), identifier);
                map.put(identifier, EmptyValue.INSTANCE);
            }
        }
    }

    @Override
    public void collectSingleValue(String key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        var identifier = reversedRegistry.get(key);
        if(identifier != null){
            if(identifier.type() != Property.Type.SINGLE){
                throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
            }
            if(map.get(identifier) != EmptyValue.INSTANCE){
                throw DuplicatedPropertyException.forFollowing(key);
            }
            map.put(identifier, value);
        } else {
            unknownPropertyConsumer.accept(key, value);
        }
    }

    @Override
    public void collectListValue(String key, String value, boolean isNewList) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        var identifier = reversedRegistry.get(key);
        if(identifier == null){
            unknownPropertyConsumer.accept(key, value);
            return;
        }
        if(!isNewList && !map.containsKey(identifier)){
            throw new IllegalStateException("Expected existing list for: " + key);
        }
        if(identifier.type() != Property.Type.MULTIPLE){
            throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
        }
        var actualValue = map.get(identifier);
        if(actualValue != EmptyValue.INSTANCE && isNewList){
            throw DuplicatedPropertyException.forFollowing(key);
        }
        switch (actualValue){
            case EmptyValue ignored -> new ValueList(value);
            case ValueList valueList -> valueList.add(value);
            default -> throw new IllegalStateException("Unexpected type: " + actualValue.getClass());
        }
    }

    public Map<PropertyIdentifier, Object> underlyingMapAsView(){
        for (var identifier : reversedRegistry.values()){
            if(identifier.presence() == Property.Presence.MANDATORY && map.get(identifier) == EmptyValue.INSTANCE){
                throw MissingPropertyException.forFollowing(identifier.key());
            }
        }
        return Collections.unmodifiableMap(map);
    }

}
