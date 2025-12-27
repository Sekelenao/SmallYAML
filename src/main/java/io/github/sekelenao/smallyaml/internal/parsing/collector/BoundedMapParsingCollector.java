package io.github.sekelenao.smallyaml.internal.parsing.collector;

import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.document.property.UnknownPropertyConsumer;
import io.github.sekelenao.smallyaml.api.document.property.identifier.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.exception.document.DuplicatedPropertyException;
import io.github.sekelenao.smallyaml.api.exception.document.MissingPropertyException;
import io.github.sekelenao.smallyaml.api.exception.document.WrongPropertyTypeException;
import io.github.sekelenao.smallyaml.internal.collection.ValueList;
import io.github.sekelenao.smallyaml.internal.reflection.PropertyIdentifiersReflector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class BoundedMapParsingCollector implements ParsingCollector {

    private final Map<String, PropertyIdentifier> identifiers = new HashMap<>();

    private final UnknownPropertyConsumer unknownPropertyConsumer;

    private final Map<PropertyIdentifier, Object> map = new HashMap<>();

    public BoundedMapParsingCollector(Set<Class<?>> types, UnknownPropertyConsumer consumer) {
        Objects.requireNonNull(types);
        this.unknownPropertyConsumer = Objects.requireNonNull(consumer);
        for (var targetIdentifier : types) {
            for (var identifier : PropertyIdentifiersReflector.get(targetIdentifier)){
                identifiers.put(identifier.key(), identifier);
            }
        }
    }

    @Override
    public void collectSingleValue(String key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        var identifier = identifiers.get(key);
        if(identifier != null){
            if(identifier.type() != Property.Type.SINGLE){
                throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
            }
            if(map.containsKey(identifier)){
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
        var identifier = identifiers.get(key);
        if(identifier == null){
            if(isNewList){
                unknownPropertyConsumer.accept(key, value);
            }
            return;
        }
        if(!isNewList && !map.containsKey(identifier)){
            throw new IllegalStateException("Expected existing list for: " + key);
        }
        if(identifier.type() != Property.Type.MULTIPLE){
            throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
        }
        if(map.containsKey(identifier) && isNewList){
            throw DuplicatedPropertyException.forFollowing(key);
        }
        map.merge(identifier, new ValueList(value), (existing, newValue) -> {
            if (existing instanceof ValueList existingList) {
                existingList.add(value);
                return existingList;
            }
            throw new IllegalStateException("Unexpected type: " + existing.getClass());
        });
    }

    public Map<PropertyIdentifier, Object> underlyingMapAsView(){
        for (var identifier : identifiers.values()){
            if(identifier.presence() == Property.Presence.MANDATORY && !map.containsKey(identifier)){
                throw MissingPropertyException.forFollowing(identifier.key());
            }
        }
        return Collections.unmodifiableMap(map);
    }

}
