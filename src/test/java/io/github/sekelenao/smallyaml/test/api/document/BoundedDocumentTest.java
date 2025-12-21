package io.github.sekelenao.smallyaml.test.api.document;

import io.github.sekelenao.smallyaml.api.document.BoundedDocument;
import io.github.sekelenao.smallyaml.api.exception.document.MissingPropertyException;
import io.github.sekelenao.smallyaml.api.line.provider.LineProvider;
import io.github.sekelenao.smallyaml.test.util.ExceptionsTester;
import io.github.sekelenao.smallyaml.test.util.document.property.identifier.ApplicationIdentifiers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class BoundedDocumentTest {

    @Nested
    @DisplayName("Builder")
    final class Builder {

        @Test
        @DisplayName("Assertions")
        void assertions() {
            var initializedBuilder = BoundedDocument.with(ApplicationIdentifiers.class);
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> BoundedDocument.with(null)),
                () -> assertThrows(NullPointerException.class, () -> initializedBuilder.and(null)),
                () -> assertThrows(NullPointerException.class, () -> initializedBuilder.thenFillFrom(null))
            );
        }

        @Test
        @DisplayName("Builder is working")
        void builderIsWorking() throws IOException {
            var boundedDocument = BoundedDocument.with(ApplicationIdentifiers.class)
                .thenFillFrom(LineProvider.with("""
                app:
                  version: correct
                """));
            assertAll(
                () -> assertEquals("correct", boundedDocument.getSingleString(ApplicationIdentifiers.VERSION))
            );
        }

        @Test
        @DisplayName("Builder with absent mandatory property")
        void mandatory() throws IOException {
            var lineProvider = LineProvider.with("""
                app:
                  port: 8080
                  base-path: /api
                """);
            var builder = BoundedDocument.with(ApplicationIdentifiers.class);
            ExceptionsTester.assertIsThrownAndContains(
                MissingPropertyException.class,
                () -> builder.thenFillFrom(lineProvider),
                "Missing property 'app.version'"
            );
            lineProvider.close();
        }

    }

}
