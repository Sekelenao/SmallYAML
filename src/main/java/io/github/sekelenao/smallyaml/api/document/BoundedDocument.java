package io.github.sekelenao.smallyaml.api.document;

import io.github.sekelenao.smallyaml.api.document.property.MultipleValuesProperty;
import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.document.property.SingleValueProperty;
import io.github.sekelenao.smallyaml.api.document.property.UnknownPropertyConsumer;
import io.github.sekelenao.smallyaml.api.document.property.identifier.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.exception.document.WrongPropertyTypeException;
import io.github.sekelenao.smallyaml.api.line.provider.LineProvider;
import io.github.sekelenao.smallyaml.internal.collection.ValueList;
import io.github.sekelenao.smallyaml.internal.parsing.SmallYAMLParser;
import io.github.sekelenao.smallyaml.internal.parsing.collector.BoundedMapParsingCollector;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class BoundedDocument implements Document {

    private final Map<PropertyIdentifier, Object> properties;

    private final Set<Class<?>> types;

    public static final class BoundedDocumentBuilder {

        private final Set<Class<?>> types;

        private BoundedDocumentBuilder(Class<?> type){
            Objects.requireNonNull(type);
            this.types = new HashSet<>();
            types.add(type);
        }

        public BoundedDocumentBuilder and(Class<?> type){
            Objects.requireNonNull(type);
            types.add(type);
            return this;
        }

        public BoundedDocument thenFillFrom(LineProvider lineProvider, UnknownPropertyConsumer consumer) throws IOException {
            Objects.requireNonNull(lineProvider);
            Objects.requireNonNull(consumer);
            var collector = new BoundedMapParsingCollector(types, consumer);
            var parser = new SmallYAMLParser();
            parser.parse(lineProvider, collector);
            return new BoundedDocument(collector.underlyingMapAsView(), types);
        }

        public BoundedDocument thenFillFrom(LineProvider lineProvider) throws IOException {
            Objects.requireNonNull(lineProvider);
            return thenFillFrom(lineProvider, UnknownPropertyConsumer.NOOP);
        }

    }

    private BoundedDocument(Map<PropertyIdentifier, Object> properties, Set<Class<?>> types) {
        this.properties = properties;
        this.types = types;
    }

    public static <E extends Enum<E> & PropertyIdentifier> BoundedDocumentBuilder with(Class<E> type){
        Objects.requireNonNull(type);
        return new BoundedDocumentBuilder(type);
    }

    public static BoundedDocument empty(){
        return new BoundedDocument(Collections.emptyMap(), Collections.emptySet());
    }

    public boolean hasProperty(PropertyIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!types.contains(identifier.getClass())){
            throw new IllegalArgumentException("Expected one of: " + types);
        }
        return properties.containsKey(identifier);
    }

    public Property.Type typeOf(PropertyIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(!properties.containsKey(identifier)){
            throw new NoSuchElementException();
        }
        return identifier.type();
    }

    public String getSingleString(PropertyIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(identifier.presence() != Property.Presence.MANDATORY){
            throw new IllegalArgumentException("Expected mandatory property: " + identifier);
        }
        var value = properties.get(identifier);
        if(value instanceof String valueAsString){
            return valueAsString;
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
    }

    public Optional<String> getSingleOptionalString(PropertyIdentifier identifier){
        Objects.requireNonNull(identifier);
        var value = properties.get(identifier);
        if(value == null){
            return Optional.empty();
        }
        if(value instanceof String valueAsString){
            return Optional.of(valueAsString);
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.SINGLE);
    }

    public List<String> getMultipleStrings(PropertyIdentifier identifier){
        Objects.requireNonNull(identifier);
        var value = properties.get(identifier);
        if(identifier.presence() == Property.Presence.MANDATORY){
            throw new IllegalArgumentException("Expected mandatory property: " + identifier);
        }
        if(value instanceof ValueList valueList){
            return valueList.asListView();
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
    }

    public Optional<List<String>> getMultipleOptionalStrings(PropertyIdentifier identifier){
        Objects.requireNonNull(identifier);
        var value = properties.get(identifier);
        if(value == null){
            return Optional.empty();
        }
        if(value instanceof ValueList valueList){
            return Optional.of(valueList.asListView());
        }
        throw WrongPropertyTypeException.withExpected(Property.Type.MULTIPLE);
    }

    @Override
    public Iterator<Property<?>> iterator() {
        return new Iterator<>() {

            private final Iterator<Map.Entry<PropertyIdentifier, Object>> iterator = properties.entrySet().iterator();

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
                    case String value -> new SingleValueProperty(current.getKey().key(), value);
                    case ValueList valueList -> new MultipleValuesProperty(current.getKey().key(), valueList.asListView());
                    default -> throw new IllegalStateException("Unexpected value: " + current.getValue());
                };
            }

        };
    }


}
