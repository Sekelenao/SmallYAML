package io.github.sekelenao.smallyaml.test.api.document;

import io.github.sekelenao.smallyaml.api.document.PermissiveDocument;
import io.github.sekelenao.smallyaml.api.exception.config.MissingPropertyException;
import io.github.sekelenao.smallyaml.api.line.provider.BufferedReaderLineProvider;
import io.github.sekelenao.smallyaml.test.util.constant.CorrectTestDocument;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import io.github.sekelenao.smallyaml.test.util.document.DocumentTester;
import io.github.sekelenao.smallyaml.test.util.resource.TestResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag(TestingTag.PARSING)
@Tag(TestingTag.COLLECTION)
final class PermissiveDocumentTest {

    @Test
    @DisplayName("PermissiveDocument assertions")
    void assertions(){
        assertThrows(NullPointerException.class, () -> PermissiveDocument.from(null));
    }

    @Test
    @DisplayName("Parsing fake simple config is working")
    void parsingFakeSimpleConfig() throws URISyntaxException, IOException {
        var file = TestResource.find(CorrectTestDocument.SIMPLE);
        try(var bufferedReaderLineProvider = new BufferedReaderLineProvider(Files.newBufferedReader(file))) {
            var document = PermissiveDocument.from(bufferedReaderLineProvider);
            assertAll(
                () -> assertEquals(Optional.empty(), document.getAsString("unknown")),
                () -> assertEquals(Optional.of("three"), document.getAsString("one.two.three")),
                () -> assertThrows(MissingPropertyException.class, () -> document.getAsStringOrThrows("unknown")),
                () -> assertEquals("default", document.getAsStringOrDefault("unknown", "default"))
            );
        }
    }

    @Test
    @DisplayName("Contains all records for all documents")
    void containsAllRecordsForAllDocuments() throws URISyntaxException, IOException {
        var documentTester = new DocumentTester<>(PermissiveDocument::from);
        documentTester.testWithAllCorrectDocuments(
            PermissiveDocument::getAsStringOrThrows,
            PermissiveDocument::getAsListOrThrows
        );
    }

}