package io.github.sekelenao.smallyaml.test.api.document;

import io.github.sekelenao.smallyaml.api.document.BoundedDocument;
import io.github.sekelenao.smallyaml.api.document.property.SingleMandatoryIdentifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class BoundedDocumentTest {

    @Test
    @DisplayName("Bounded document factory can create multiple documents")
    void boundedDocumentCreation() {
        var yaml = "test: value";
        var identifier = SingleMandatoryIdentifier.define("test");
        var factory = BoundedDocument.factoryBuilder()
            .register(identifier)
            .build();
        assertAll(
            () -> assertEquals("value", factory.createDocument(yaml).get(identifier)),
            () -> assertEquals("value", factory.createDocument(yaml).get(identifier))
        );
    }

}
