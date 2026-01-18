package io.github.sekelenao.smallyaml.test.api.document;

import io.github.sekelenao.smallyaml.api.document.BoundedDocument;
import io.github.sekelenao.smallyaml.api.document.LineProvider;
import io.github.sekelenao.smallyaml.api.document.property.SingleMandatoryIdentifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class BoundedDocumentTest {

    @Test
    @DisplayName("Bounded document creation")
    void boundedDocumentCreation() throws IOException {
        var yaml = "test: value";
        var identifier = SingleMandatoryIdentifier.define("test");
        var factory = BoundedDocument.factoryBuilder()
            .register(identifier)
            .build();
        try (var provider = LineProvider.with(yaml)){
            var document = factory.create(provider);
            assertEquals("value", document.get(identifier));
        }
        try (var provider2 = LineProvider.with(yaml)){
            var document2 = factory.create(provider2);
            assertEquals("value", document2.get(identifier));
        }
    }

}
