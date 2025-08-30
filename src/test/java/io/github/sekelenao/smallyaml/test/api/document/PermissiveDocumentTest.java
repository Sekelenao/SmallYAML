package io.github.sekelenao.smallyaml.test.api.document;

import io.github.sekelenao.smallyaml.api.document.PermissiveDocument;
import io.github.sekelenao.smallyaml.api.exception.config.MissingPropertyException;
import io.github.sekelenao.smallyaml.api.line.provider.BufferedReaderLineProvider;
import io.github.sekelenao.smallyaml.test.util.TestUtilities;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import io.github.sekelenao.smallyaml.test.util.document.CorrectTestDocument;
import io.github.sekelenao.smallyaml.test.util.document.DocumentTester;
import io.github.sekelenao.smallyaml.test.util.resource.TestResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
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
                () -> assertEquals("three", document.getAsStringOrThrows("one.two.three")),
                () -> assertEquals(List.of("five", "six", "seven"), document.getAsListOrThrows("one.four")),
                () -> assertEquals("end", document.getAsStringOrThrows("eight.nine.ten")),
                () -> assertEquals(Optional.empty(), document.getAsString("unknown")),
                () -> assertEquals(Optional.of("three"), document.getAsString("one.two.three")),
                () -> assertThrows(MissingPropertyException.class, () -> document.getAsStringOrThrows("unknown")),
                () -> assertEquals("default", document.getAsStringOrDefault("unknown", "default")),
                () -> TestUtilities.checkAmountOfEachPropertyType(document, 2, 1)
            );
        }
    }

    @Test
    @Tag(TestingTag.PARSING)
    @DisplayName("Contains all records for all documents")
    void containsAllRecordsForAllDocuments() throws URISyntaxException, IOException {
        for (var correctTestDocument : CorrectTestDocument.values()){
            var file = TestResource.find(correctTestDocument);
            try(var bufferedReaderLineProvider = new BufferedReaderLineProvider(Files.newBufferedReader(file))) {
                var document = PermissiveDocument.from(bufferedReaderLineProvider);
                var expectedRecordsCsvPath = correctTestDocument.expectedRecordsCsvResourcePath();
                DocumentTester.ensureAllRecordsArePresent(
                    expectedRecordsCsvPath,
                    document::getAsStringOrThrows,
                    document::getAsListOrThrows
                );
                DocumentTester.ensureAllRecordsArePresent(
                    expectedRecordsCsvPath,
                    key -> document.getAsStringOrDefault(key, "@"),
                    key -> document.getAsListOrDefault(key, List.of("@"))
                );
            }
        }
    }

}