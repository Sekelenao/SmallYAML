package io.github.sekelenao.smallyaml.api.document;

import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.UnknownPropertyConsumer;
import io.github.sekelenao.smallyaml.api.exception.document.DuplicatedIdentifierException;
import io.github.sekelenao.smallyaml.internal.reflection.ClassIdentifiersScanner;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class BoundedDocumentFactoryBuilder {

    private final Map<String, PropertyIdentifier> reversedRegistry;

    private UnknownPropertyConsumer consumer = UnknownPropertyConsumer.NOOP;

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

    public BoundedDocumentFactoryBuilder register(PropertyIdentifier identifier, PropertyIdentifier... moreIdentifiers) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(moreIdentifiers);
        register(identifier);
        for (var otherIdentifier : moreIdentifiers) {
            register(Objects.requireNonNull(otherIdentifier));
        }
        return this;
    }

    public BoundedDocumentFactoryBuilder register(Iterable<PropertyIdentifier> identifiers) {
        Objects.requireNonNull(identifiers);
        for (var identifier : identifiers) {
            register(Objects.requireNonNull(identifier));
        }
        return this;
    }

    public BoundedDocumentFactoryBuilder unknownPropertyConsumer(UnknownPropertyConsumer consumer){
        Objects.requireNonNull(consumer);
        this.consumer = consumer;
        return this;
    }

    public BoundedDocumentFactory build(){
        return new BoundedDocumentFactory(reversedRegistry, consumer);
    }

}
