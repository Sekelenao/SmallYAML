package io.github.sekelenao.smallyaml.api.document;

import io.github.sekelenao.smallyaml.api.document.property.MultipleValuesProperty;
import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleMandatoryIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleValueProperty;
import io.github.sekelenao.smallyaml.api.document.property.UnknownPropertyConsumer;
import io.github.sekelenao.smallyaml.api.exception.document.NotRegisteredIdentifierException;
import io.github.sekelenao.smallyaml.internal.collection.BoundedMapParsingCollector;
import io.github.sekelenao.smallyaml.internal.collection.ValueList;
import io.github.sekelenao.smallyaml.internal.parsing.SmallYAMLParser;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class BoundedDocument implements Document {

    private final Map<PropertyIdentifier, Object> properties;

    public static final class BoundedDocumentBuilder {

        private final Set<Class<?>> classesToScan;

        private BoundedDocumentBuilder(Class<?> type){
            Objects.requireNonNull(type);
            this.classesToScan = new HashSet<>();
            classesToScan.add(type);
        }

        public BoundedDocumentBuilder and(Class<?> type){
            Objects.requireNonNull(type);
            classesToScan.add(type);
            return this;
        }

        public BoundedDocument thenFillFrom(LineProvider lineProvider, UnknownPropertyConsumer consumer) throws IOException {
            Objects.requireNonNull(lineProvider);
            Objects.requireNonNull(consumer);
            var collector = new BoundedMapParsingCollector(classesToScan, consumer);
            var parser = new SmallYAMLParser();
            parser.parse(lineProvider, collector);
            return new BoundedDocument(collector.underlyingMapAsView());
        }

        public BoundedDocument thenFillFrom(LineProvider lineProvider) throws IOException {
            Objects.requireNonNull(lineProvider);
            return thenFillFrom(lineProvider, UnknownPropertyConsumer.NOOP);
        }

    }

    private BoundedDocument(Map<PropertyIdentifier, Object> properties) {
        this.properties = properties;
    }

    public static <E extends Enum<E> & PropertyIdentifier> BoundedDocumentBuilder scan(Class<E> type){
        Objects.requireNonNull(type);
        return new BoundedDocumentBuilder(type);
    }

    public static BoundedDocument empty(){
        return new BoundedDocument(Collections.emptyMap());
    }

    public boolean hasProperty(PropertyIdentifier identifier){
        Objects.requireNonNull(identifier);
        return properties.get(identifier) != null;
    }

    public Property.Type typeOf(PropertyIdentifier identifier){
        Objects.requireNonNull(identifier);
        if(properties.get(identifier) == null){
            throw new NoSuchElementException();
        }
        return identifier.type();
    }

    public <T> T getSingle(SingleMandatoryIdentifier identifier, Function<? super String, T> mapper){
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(mapper);
        if(!properties.containsKey(identifier)){
            throw NotRegisteredIdentifierException.forFollowing(identifier);
        }
        return mapper.apply((String) properties.get(identifier));
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
