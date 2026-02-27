package io.github.sekelenao.smallyaml.api.document;

import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.UnknownPropertyConsumer;
import io.github.sekelenao.smallyaml.internal.collection.BoundedMapParsingCollector;
import io.github.sekelenao.smallyaml.internal.parsing.SmallYAMLParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

/**
 * Factory for creating instances of {@link BoundedDocument}.
 * A {@code BoundedDocumentFactory} is configured with a registry of {@link PropertyIdentifier}s
 * and can parse YAML content into a {@code BoundedDocument} that only contains the registered properties.
 *
 * @see BoundedDocument#factoryBuilder()
 * @since 0.2.0
 */
public final class BoundedDocumentFactory {

    private final Map<String, PropertyIdentifier> reversedRegistry;

    private final UnknownPropertyConsumer unknownPropertyConsumer;

    /**
     * Package-private constructor, use {@link BoundedDocumentFactoryBuilder#buildFactory()} to get an instance.
     *
     * @param registry the registry of property identifiers
     * @param consumer the consumer for unknown properties
     */
    BoundedDocumentFactory(Map<String, PropertyIdentifier> registry, UnknownPropertyConsumer consumer) {
        this.reversedRegistry = Objects.requireNonNull(registry);
        this.unknownPropertyConsumer = Objects.requireNonNull(consumer);
    }

    /**
     * Creates a {@link BoundedDocument} by parsing lines from the given {@link LineProvider}.
     *
     * @param lineProvider the source of lines to parse; must not be null
     * @return a new BoundedDocument containing the parsed properties
     * @throws IOException if an I/O error occurs during parsing
     * @throws NullPointerException if lineProvider is null
     *
     * @since 0.2.0
     */
    public BoundedDocument createDocument(LineProvider lineProvider) throws IOException {
        Objects.requireNonNull(lineProvider);
        var collector = new BoundedMapParsingCollector(reversedRegistry, unknownPropertyConsumer);
        var parser = new SmallYAMLParser();
        parser.parse(lineProvider, collector);
        return new BoundedDocument(collector.underlyingMapAsView());
    }

    /**
     * Creates a {@link BoundedDocument} by reading from the given {@link BufferedReader}.
     *
     * @param reader the reader to read from; must not be null
     * @return a new BoundedDocument containing the parsed properties
     * @throws IOException if an I/O error occurs during reading or parsing
     * @throws NullPointerException if the reader is null
     *
     * @since 0.2.0
     */
    public BoundedDocument createDocument(BufferedReader reader) throws IOException {
        Objects.requireNonNull(reader);
        try (var provider = LineProvider.with(reader)){
            return createDocument(provider);
        }
    }

    /**
     * Creates a {@link BoundedDocument} by reading from the given {@link InputStream}.
     * The input stream is decoded using UTF-8.
     *
     * @param inputStream the input stream to read from; must not be null
     * @return a new BoundedDocument containing the parsed properties
     * @throws IOException if an I/O error occurs during reading or parsing
     * @throws NullPointerException if inputStream is null
     *
     * @since 0.2.0
     */
    public BoundedDocument createDocument(InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream);
        try (var provider = LineProvider.with(inputStream)){
            return createDocument(provider);
        }
    }

    /**
     * Creates a {@link BoundedDocument} by reading from the given {@link InputStream} and decoding it using the
     * provided {@link Charset}.
     *
     * @param inputStream the input stream to read from; must not be null
     * @param charset the character set to use for decoding; must not be null
     * @return a new {@link BoundedDocument} containing the parsed properties
     * @throws IOException if an I/O error occurs during reading or parsing
     * @throws NullPointerException if either inputStream or charset is null
     */
    public BoundedDocument createDocument(InputStream inputStream, Charset charset) throws IOException {
        Objects.requireNonNull(inputStream);
        Objects.requireNonNull(charset);
        try (var provider = LineProvider.with(inputStream, charset)){
            return createDocument(provider);
        }
    }

    /**
     * Creates a {@link BoundedDocument} by parsing the given YAML string.
     *
     * @param yaml the YAML string to parse; must not be null
     * @return a new BoundedDocument containing the parsed properties
     * @throws IOException if an I/O error occurs during parsing
     * @throws NullPointerException if the String is null
     *
     * @since 0.2.0
     */
    public BoundedDocument createDocument(String yaml) throws IOException {
        Objects.requireNonNull(yaml);
        try (var provider = LineProvider.with(yaml)){
            return createDocument(provider);
        }
    }

}
