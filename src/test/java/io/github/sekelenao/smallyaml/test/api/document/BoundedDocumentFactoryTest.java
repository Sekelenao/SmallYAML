package io.github.sekelenao.smallyaml.test.api.document;

import io.github.sekelenao.smallyaml.api.document.BoundedDocument;
import io.github.sekelenao.smallyaml.api.document.LineProvider;
import io.github.sekelenao.smallyaml.api.document.property.MultipleOptionalIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleMandatoryIdentifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class BoundedDocumentFactoryTest {

    @Nested
    final class Assertions {

        @Test
        @DisplayName("Assertions")
        void nullChecks() {
            var factory = BoundedDocument.factoryBuilder()
                .register(SingleMandatoryIdentifier.define("key"))
                .buildFactory();
            var inputStream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> factory.createDocument((LineProvider) null)),
                () -> assertThrows(NullPointerException.class, () -> factory.createDocument((BufferedReader) null)),
                () -> assertThrows(NullPointerException.class, () -> factory.createDocument((InputStream) null)),
                () -> assertThrows(NullPointerException.class, () -> factory.createDocument(null, StandardCharsets.UTF_8)),
                () -> assertThrows(NullPointerException.class, () -> factory.createDocument(inputStream , null)),
                () -> assertThrows(NullPointerException.class, () -> factory.createDocument((String) null)));
        }

    }

    @Nested
    final class Creation {

        @Test
        @DisplayName("Creation from various sources")
        void createFromVariousSources() {
            var id = SingleMandatoryIdentifier.define("key");
            var listId = MultipleOptionalIdentifier.define("list");
            var factory = BoundedDocument.factoryBuilder()
                .register(id, listId)
                .buildFactory();
            var yaml = """
                key: value
                list:
                  - one
                  - two
                """;
            assertAll(
                () -> {
                    try (var provider = LineProvider.with(yaml)) {
                        var doc = assertDoesNotThrow(() -> factory.createDocument(provider));
                        assertEquals("value", doc.get(id));
                        assertEquals(List.of("one", "two"), doc.get(listId).orElseThrow());
                    }
                },
                () -> {
                    try (var reader = new BufferedReader(new StringReader(yaml))) {
                        var doc = assertDoesNotThrow(() -> factory.createDocument(reader));
                        assertEquals("value", doc.get(id));
                        assertEquals(List.of("one", "two"), doc.get(listId).orElseThrow());
                    }
                },
                () -> {
                    try (var is = new ByteArrayInputStream(yaml.getBytes())) {
                        var doc = assertDoesNotThrow(() -> factory.createDocument(is));
                        assertEquals("value", doc.get(id));
                        assertEquals(List.of("one", "two"), doc.get(listId).orElseThrow());
                    }
                },
                () -> {
                    try (var is = new ByteArrayInputStream(yaml.getBytes())) {
                        var doc = assertDoesNotThrow(() -> factory.createDocument(is, StandardCharsets.UTF_8));
                        assertEquals("value", doc.get(id));
                        assertEquals(List.of("one", "two"), doc.get(listId).orElseThrow());
                    }
                },
                () -> {
                    var doc = assertDoesNotThrow(() -> factory.createDocument(yaml));
                    assertEquals("value", doc.get(id));
                    assertEquals(List.of("one", "two"), doc.get(listId).orElseThrow());
                });
        }

    }

}
