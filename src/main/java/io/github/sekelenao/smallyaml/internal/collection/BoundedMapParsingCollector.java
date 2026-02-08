package io.github.sekelenao.smallyaml.internal.collection;

import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.UnknownPropertyConsumer;
import io.github.sekelenao.smallyaml.api.exception.document.DuplicatedPropertyException;
import io.github.sekelenao.smallyaml.api.exception.document.MissingPropertyException;
import io.github.sekelenao.smallyaml.api.exception.document.WrongPropertyTypeException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class BoundedMapParsingCollector implements ParsingCollector {

    private final Map<PropertyIdentifier, Object> properties;

    private final Map<String, PropertyIdentifier> reversedRegistry;

    private final UnknownPropertyConsumer unknownPropertyConsumer;

    public BoundedMapParsingCollector(Map<String, PropertyIdentifier> reversedRegistry, UnknownPropertyConsumer consumer) {
        this.reversedRegistry = Objects.requireNonNull(reversedRegistry);
        this.unknownPropertyConsumer = Objects.requireNonNull(consumer);
        this.properties = reversedRegistry.values().stream()
            .collect(HashMap::new, (map, identifier) -> map.put(identifier, EmptyValue.INSTANCE), HashMap::putAll);
    }

    @Override
    public void collectSingleValue(String key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        var identifier = reversedRegistry.get(key);
        if (identifier != null) {
            if (identifier.type() != Property.Type.SINGLE) {
                throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
            }
            if (properties.get(identifier) != EmptyValue.INSTANCE) {
                throw DuplicatedPropertyException.forFollowing(key);
            }
            properties.put(identifier, value);
        } else {
            unknownPropertyConsumer.accept(key, value);
        }
    }

    @Override
    public void collectListValue(String key, String value, boolean isNewList) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        var identifier = reversedRegistry.get(key);
        if (identifier == null) {
            unknownPropertyConsumer.accept(key, value);
            return;
        }
        if (!isNewList && !properties.containsKey(identifier)) {
            throw new IllegalStateException("Expected existing list for: " + key);
        }
        if (identifier.type() != Property.Type.MULTIPLE) {
            throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
        }
        var actualValue = properties.get(identifier);
        if (actualValue != EmptyValue.INSTANCE && isNewList) {
            throw DuplicatedPropertyException.forFollowing(key);
        }
        switch (actualValue) {
            case EmptyValue.INSTANCE -> properties.put(identifier, new ValueList(value));
            case ValueList valueList -> valueList.add(value);
            default -> throw new IllegalStateException("Unexpected type: " + actualValue.getClass());
        }
    }

    public Map<PropertyIdentifier, Object> underlyingMapAsView() {
        for (var identifier : reversedRegistry.values()) {
            if (identifier.presence() == Property.Presence.MANDATORY && properties.get(identifier) == EmptyValue.INSTANCE) {
                throw MissingPropertyException.forFollowing(identifier.key());
            }
        }
        return Collections.unmodifiableMap(properties);
    }

}
