package io.github.sekelenao.smallyaml.test.api.document;

import io.github.sekelenao.smallyaml.api.document.PermissiveDocument;
import io.github.sekelenao.smallyaml.api.document.property.MultipleValuesProperty;
import io.github.sekelenao.smallyaml.api.document.property.SingleValueProperty;
import io.github.sekelenao.smallyaml.api.exception.config.MissingPropertyException;
import io.github.sekelenao.smallyaml.api.exception.config.WrongTypeException;
import io.github.sekelenao.smallyaml.api.line.provider.BufferedReaderLineProvider;
import io.github.sekelenao.smallyaml.test.util.constant.RegularTestDocument;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import io.github.sekelenao.smallyaml.test.util.document.DocumentsTester;
import io.github.sekelenao.smallyaml.test.util.resource.TestResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(TestingTag.COLLECTION)
final class PermissiveDocumentTest {

    private static final String UNKNOWN_KEY = "UNKNOWN";

    private final PermissiveDocument regularTestDocument;

    public PermissiveDocumentTest() throws URISyntaxException, IOException {
        var file = TestResource.find(RegularTestDocument.TEST_DOCUMENT);
        try(var bufferedReaderLineProvider = new BufferedReaderLineProvider(Files.newBufferedReader(file))) {
            this.regularTestDocument = PermissiveDocument.from(bufferedReaderLineProvider);
        }
    }

    @Test
    @DisplayName("PermissiveDocument assertions")
    void assertions(){
        assertThrows(NullPointerException.class, () -> PermissiveDocument.from(null));
    }

    @Test
    @DisplayName("Throwing string getter")
    void throwingGetterIsWorking() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getAsStringOrThrows(null)),
            () -> assertThrows(MissingPropertyException.class, () -> regularTestDocument.getAsStringOrThrows(UNKNOWN_KEY)),
            () -> assertThrows(
                WrongTypeException.class,
                () -> regularTestDocument.getAsStringOrThrows(RegularTestDocument.MULTIPLE_VALUES_KEY)
            ),
            () -> assertEquals(
                RegularTestDocument.SINGLE_VALUE,
                regularTestDocument.getAsStringOrThrows(RegularTestDocument.SINGLE_VALUE_KEY)
            )
        );
    }

    @Test
    @DisplayName("Throwing list getter")
    void throwingListGetterIsWorking() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getAsListOrThrows(null)),
            () -> assertThrows(MissingPropertyException.class, () -> regularTestDocument.getAsListOrThrows(UNKNOWN_KEY)),
            () -> assertThrows(
                WrongTypeException.class,
                () -> regularTestDocument.getAsListOrThrows(RegularTestDocument.SINGLE_VALUE_KEY)
            ),
            () -> assertEquals(
                RegularTestDocument.MULTIPLE_VALUES,
                regularTestDocument.getAsListOrThrows(RegularTestDocument.MULTIPLE_VALUES_KEY)
            )
        );
    }

    @Test
    @DisplayName("Optional string getter")
    void optionalGetterIsWorking() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getAsString(null)),
            () -> assertEquals(Optional.empty(), regularTestDocument.getAsString(UNKNOWN_KEY)),
            () -> assertThrows(
                WrongTypeException.class,
                () -> regularTestDocument.getAsString(RegularTestDocument.MULTIPLE_VALUES_KEY)
            ),
            () -> assertEquals(
                Optional.of(RegularTestDocument.SINGLE_VALUE),
                regularTestDocument.getAsString(RegularTestDocument.SINGLE_VALUE_KEY)
            )
        );
    }

    @Test
    @DisplayName("Optional list getter")
    void optionalListGetterIsWorking() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getAsList(null)),
            () -> assertEquals(Optional.empty(), regularTestDocument.getAsList(UNKNOWN_KEY)),
            () -> assertThrows(
                WrongTypeException.class,
                () -> regularTestDocument.getAsList(RegularTestDocument.SINGLE_VALUE_KEY)
            ),
            () -> assertEquals(
                Optional.of(RegularTestDocument.MULTIPLE_VALUES),
                regularTestDocument.getAsList(RegularTestDocument.MULTIPLE_VALUES_KEY)
            )
        );
    }

    @Test
    @DisplayName("Default string getter")
    void defaultGetterIsWorking() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getAsStringOrDefault(null, "")),
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getAsStringOrDefault(RegularTestDocument.SINGLE_VALUE, null)
            ),
            () -> assertThrows(
                WrongTypeException.class,
                () -> regularTestDocument.getAsStringOrDefault(RegularTestDocument.MULTIPLE_VALUES_KEY, "")
            ),
            () -> assertEquals(
                RegularTestDocument.SINGLE_VALUE,
                regularTestDocument.getAsStringOrDefault(RegularTestDocument.SINGLE_VALUE_KEY, "")
            ),
            () -> assertEquals("", regularTestDocument.getAsStringOrDefault(UNKNOWN_KEY, ""))
        );
    }

    @Test
    @DisplayName("Default list getter")
    void defaultListGetterIsWorking(){
        List<String> emptyList = Collections.emptyList();
        assertAll(
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getAsListOrDefault(null, emptyList)
            ),
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getAsListOrDefault(RegularTestDocument.MULTIPLE_VALUES_KEY, null)
            ),
            () -> assertThrows(
                WrongTypeException.class,
                () -> regularTestDocument.getAsListOrDefault(RegularTestDocument.SINGLE_VALUE_KEY, emptyList)
            ),
            () -> assertEquals(
                RegularTestDocument.MULTIPLE_VALUES,
                regularTestDocument.getAsListOrDefault(RegularTestDocument.MULTIPLE_VALUES_KEY, emptyList)
            ),
            () -> assertEquals(emptyList, regularTestDocument.getAsListOrDefault(UNKNOWN_KEY, emptyList))
        );
    }

    @Test
    @DisplayName("Property iterator")
    void propertyIterator() {
        var iterator = regularTestDocument.iterator();
        assertAll(
            () -> assertTrue(iterator.hasNext()),
            () -> assertEquals(
                new SingleValueProperty(RegularTestDocument.SINGLE_VALUE_KEY, RegularTestDocument.SINGLE_VALUE),
                iterator.next()
            ),
            () -> assertTrue(iterator.hasNext()),
            () -> assertEquals(
                new MultipleValuesProperty(RegularTestDocument.MULTIPLE_VALUES_KEY, RegularTestDocument.MULTIPLE_VALUES),
                iterator.next()
            ),
            () -> assertThrows(NoSuchElementException.class, iterator::next)
        );
    }

    @Test
    @Tag(TestingTag.PARSING)
    @DisplayName("Contains all records for all documents")
    void containsAllRecordsForAllDocuments() throws URISyntaxException, IOException {
        var documentTester = new DocumentsTester<>(PermissiveDocument::from);
        documentTester.testWithAllCorrectDocuments(
            PermissiveDocument::getAsStringOrThrows,
            PermissiveDocument::getAsListOrThrows
        );
    }

}