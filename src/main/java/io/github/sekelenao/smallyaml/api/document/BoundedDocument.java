package io.github.sekelenao.smallyaml.api.document;

import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.UnknownPropertyConsumer;
import io.github.sekelenao.smallyaml.api.line.provider.LineProvider;
import io.github.sekelenao.smallyaml.internal.parsing.SmallYAMLParser;
import io.github.sekelenao.smallyaml.internal.parsing.collector.BoundedMapParsingCollector;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BoundedDocument implements Document {

    private final Map<PropertyIdentifier, Object> properties;

    public static final class BoundedDocumentBuilder {

        private final Set<Class<? extends PropertyIdentifier>> types;

        private <E extends Enum<E> & PropertyIdentifier> BoundedDocumentBuilder(Class<E> type){
            Objects.requireNonNull(type);
            this.types = new HashSet<>();
            types.add(type);
        }

        public <E extends Enum<E> & PropertyIdentifier> BoundedDocumentBuilder and(Class<E> type){
            Objects.requireNonNull(type);
            types.add(type);
            return this;
        }

        public BoundedDocument thenConstructFrom(LineProvider lineProvider, UnknownPropertyConsumer consumer) throws IOException {
            Objects.requireNonNull(lineProvider);
            Objects.requireNonNull(consumer);
            var collector = new BoundedMapParsingCollector(types, consumer);
            var parser = new SmallYAMLParser();
            parser.parse(lineProvider, collector);
            return new BoundedDocument(collector.underlyingMapAsView());
        }

        public BoundedDocument thenConstructFrom(LineProvider lineProvider) throws IOException {
            Objects.requireNonNull(lineProvider);
            return thenConstructFrom(lineProvider, UnknownPropertyConsumer.NOOP);
        }

    }

    private BoundedDocument(Map<PropertyIdentifier, Object> properties) {
        this.properties = properties;
    }

    public static <E extends Enum<E> & PropertyIdentifier> BoundedDocumentBuilder register(Class<E> type){
        Objects.requireNonNull(type);
        return new BoundedDocumentBuilder(type);
    }

    @Override
    public Iterator<Property<?>> iterator() {
        return null;
    }


}
