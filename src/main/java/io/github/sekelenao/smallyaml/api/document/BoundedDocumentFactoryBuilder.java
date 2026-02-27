package io.github.sekelenao.smallyaml.api.document;

import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.UnknownPropertyConsumer;
import io.github.sekelenao.smallyaml.api.exception.document.DuplicatedIdentifierException;
import io.github.sekelenao.smallyaml.internal.reflection.ClassIdentifiersScanner;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Builder class for creating instances of {@link BoundedDocumentFactory}.
 * It allows registering {@link PropertyIdentifier}s and setting an
 * {@link UnknownPropertyConsumer} to handle unexpected properties during parsing.
 *
 * @since 0.2.0
 */
public final class BoundedDocumentFactoryBuilder {

    private final Map<String, PropertyIdentifier> reversedRegistry;

    private UnknownPropertyConsumer consumer = UnknownPropertyConsumer.NOOP;

    /**
     * Package-private constructor, use {@link BoundedDocument#factoryBuilder()} to get an instance.
     */
    BoundedDocumentFactoryBuilder(){
        this.reversedRegistry = new HashMap<>();
    }

    private void scanAndRegister(Class<?> type){
        var scannedIdentifiers = ClassIdentifiersScanner.scan(type);
        for (var scannedIdentifier : scannedIdentifiers) {
            var key = scannedIdentifier.key();
            if(reversedRegistry.containsKey(key)){
                throw DuplicatedIdentifierException.forFollowing(scannedIdentifier);
            }
            reversedRegistry.put(key, scannedIdentifier);
        }
    }

    /**
     * Scans the provided class(es) for static final {@link PropertyIdentifier} fields and registers them.
     *
     * @param type the primary class to scan
     * @param moreTypes additional classes to scan
     * @return this builder instance for method chaining
     * @throws NullPointerException if any of the provided classes are null
     *
     * @since 0.2.0
     */
    public BoundedDocumentFactoryBuilder scan(Class<?> type, Class<?>... moreTypes) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(moreTypes);
        scanAndRegister(type);
        for (var otherType : moreTypes) {
            scanAndRegister(Objects.requireNonNull(otherType));
        }
        return this;
    }

    private void register(PropertyIdentifier identifier){
        var key = identifier.key();
        if(reversedRegistry.containsKey(key)){
            throw DuplicatedIdentifierException.forFollowing(identifier);
        }
        reversedRegistry.put(key, identifier);
    }

    /**
     * Explicitly registers one or more {@link PropertyIdentifier}s.
     *
     * @param identifier the primary identifier to register
     * @param moreIdentifiers additional identifiers to register
     * @return this builder instance for method chaining
     * @throws NullPointerException if any of the provided identifiers are null
     * @throws DuplicatedIdentifierException if an identifier with the same key is already registered
     *
     * @since 0.2.0
     */
    public BoundedDocumentFactoryBuilder register(PropertyIdentifier identifier, PropertyIdentifier... moreIdentifiers) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(moreIdentifiers);
        register(identifier);
        for (var otherIdentifier : moreIdentifiers) {
            register(Objects.requireNonNull(otherIdentifier));
        }
        return this;
    }

    /**
     * Registers a collection of {@link PropertyIdentifier}s.
     *
     * @param identifiers the collection of identifiers to register
     * @return this builder instance for method chaining
     * @throws NullPointerException if identifiers or any element in the collection is null
     * @throws DuplicatedIdentifierException if an identifier with the same key is already registered
     *
     * @since 0.2.0
     */
    public BoundedDocumentFactoryBuilder register(Iterable<PropertyIdentifier> identifiers) {
        Objects.requireNonNull(identifiers);
        for (var identifier : identifiers) {
            register(Objects.requireNonNull(identifier));
        }
        return this;
    }

    /**
     * Sets the consumer to be notified when an unknown property is encountered during parsing.
     *
     * @param consumer the consumer to handle unknown properties; must not be null
     * @return this builder instance for method chaining
     * @throws NullPointerException if consumer is null
     *
     * @since 0.2.0
     */
    public BoundedDocumentFactoryBuilder unknownPropertyConsumer(UnknownPropertyConsumer consumer){
        Objects.requireNonNull(consumer);
        this.consumer = consumer;
        return this;
    }

    /**
     * Builds and returns a new {@link BoundedDocumentFactory} with the current configuration.
     *
     * @return a new BoundedDocumentFactory instance
     *
     * @since 0.2.0
     */
    public BoundedDocumentFactory buildFactory(){
        return new BoundedDocumentFactory(reversedRegistry, consumer);
    }

}
