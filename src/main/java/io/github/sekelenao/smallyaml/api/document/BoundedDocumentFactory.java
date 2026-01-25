package io.github.sekelenao.smallyaml.api.document;

import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.UnknownPropertyConsumer;
import io.github.sekelenao.smallyaml.internal.collection.BoundedMapParsingCollector;
import io.github.sekelenao.smallyaml.internal.parsing.SmallYAMLParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

public final class BoundedDocumentFactory {

    private final Map<String, PropertyIdentifier> reversedRegistry;

    private final UnknownPropertyConsumer unknownPropertyConsumer;

    BoundedDocumentFactory(Map<String, PropertyIdentifier> registry, UnknownPropertyConsumer consumer) {
        this.reversedRegistry = Objects.requireNonNull(registry);
        this.unknownPropertyConsumer = Objects.requireNonNull(consumer);
    }

    public BoundedDocument createFrom(LineProvider lineProvider) throws IOException {
        Objects.requireNonNull(lineProvider);
        var collector = new BoundedMapParsingCollector(reversedRegistry, unknownPropertyConsumer);
        var parser = new SmallYAMLParser();
        parser.parse(lineProvider, collector);
        return new BoundedDocument(collector.underlyingMapAsView());
    }

    public BoundedDocument createFrom(BufferedReader reader) throws IOException {
        Objects.requireNonNull(reader);
        try (var provider = LineProvider.with(reader)){
            return createFrom(provider);
        }
    }

    public BoundedDocument createFrom(InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream);
        try (var provider = LineProvider.with(inputStream)){
            return createFrom(provider);
        }
    }

    public BoundedDocument createFrom(String yaml) throws IOException {
        Objects.requireNonNull(yaml);
        try (var provider = LineProvider.with(yaml)){
            return createFrom(provider);
        }
    }

}
