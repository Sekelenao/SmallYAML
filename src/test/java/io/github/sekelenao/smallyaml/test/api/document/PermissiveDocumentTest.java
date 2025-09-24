package io.github.sekelenao.smallyaml.test.api.document;

import io.github.sekelenao.smallyaml.api.document.PermissiveDocument;
import io.github.sekelenao.smallyaml.api.document.property.MultipleValuesProperty;
import io.github.sekelenao.smallyaml.api.document.property.SingleValueProperty;
import io.github.sekelenao.smallyaml.api.exception.document.MissingPropertyException;
import io.github.sekelenao.smallyaml.api.exception.document.WrongPropertyTypeException;
import io.github.sekelenao.smallyaml.api.line.provider.BufferedReaderLineProvider;
import io.github.sekelenao.smallyaml.api.line.provider.LineProvider;
import io.github.sekelenao.smallyaml.internal.parsing.line.Line;
import io.github.sekelenao.smallyaml.test.util.Exceptions;
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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(TestingTag.API)
@Tag(TestingTag.COLLECTION)
final class PermissiveDocumentTest {

    private static final String UNKNOWN_KEY = "UNKNOWN";

    private final PermissiveDocument regularTestDocument;

    public PermissiveDocumentTest() throws URISyntaxException, IOException {
        var file = TestResource.find(RegularTestDocument.TEST_DOCUMENT);
        try (var bufferedReaderLineProvider = new BufferedReaderLineProvider(Files.newBufferedReader(file))) {
            this.regularTestDocument = PermissiveDocument.from(bufferedReaderLineProvider);
        }
    }

    @Test
    @DisplayName("PermissiveDocument assertions")
    void assertions() {
        assertThrows(NullPointerException.class, () -> PermissiveDocument.from(null));
    }

    @Test
    @DisplayName("Throwing string getter")
    void throwingGetterIsWorking() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getSingleStringOrThrows(null)),
            () -> Exceptions.isThrownAndContains(
                MissingPropertyException.class,
                () -> regularTestDocument.getSingleStringOrThrows(UNKNOWN_KEY),
                UNKNOWN_KEY
            ),
            () -> Exceptions.isThrownAndContains(
                WrongPropertyTypeException.class,
                () -> regularTestDocument.getSingleStringOrThrows(RegularTestDocument.MULTIPLE_VALUES_KEY),
                "Expected single value but was multiple values"
            ),
            () -> assertEquals(
                RegularTestDocument.SINGLE_VALUE,
                regularTestDocument.getSingleStringOrThrows(RegularTestDocument.SINGLE_VALUE_KEY)
            )
        );
    }

    @Test
    @DisplayName("Throwing list getter")
    void throwingListGetterIsWorking() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getMultipleStringsOrThrows(null)),
            () -> assertThrows(
                MissingPropertyException.class,
                () -> regularTestDocument.getMultipleStringsOrThrows(UNKNOWN_KEY),
                UNKNOWN_KEY
            ),
            () -> Exceptions.isThrownAndContains(
                WrongPropertyTypeException.class,
                () -> regularTestDocument.getMultipleStringsOrThrows(RegularTestDocument.SINGLE_VALUE_KEY),
                "Expected multiple values but was single value"
            ),
            () -> assertEquals(
                RegularTestDocument.MULTIPLE_VALUES,
                regularTestDocument.getMultipleStringsOrThrows(RegularTestDocument.MULTIPLE_VALUES_KEY)
            )
        );
    }

    @Test
    @DisplayName("Optional string getter")
    void optionalGetterIsWorking() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getSingleString(null)),
            () -> assertEquals(Optional.empty(), regularTestDocument.getSingleString(UNKNOWN_KEY)),
            () -> Exceptions.isThrownAndContains(
                WrongPropertyTypeException.class,
                () -> regularTestDocument.getSingleString(RegularTestDocument.MULTIPLE_VALUES_KEY),
                "Expected single value but was multiple values"
            ),
            () -> assertEquals(
                Optional.of(RegularTestDocument.SINGLE_VALUE),
                regularTestDocument.getSingleString(RegularTestDocument.SINGLE_VALUE_KEY)
            )
        );
    }

    @Test
    @DisplayName("Optional list getter")
    void optionalListGetterIsWorking() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getMultipleStrings(null)),
            () -> assertEquals(Optional.empty(), regularTestDocument.getMultipleStrings(UNKNOWN_KEY)),
            () -> Exceptions.isThrownAndContains(
                WrongPropertyTypeException.class,
                () -> regularTestDocument.getMultipleStrings(RegularTestDocument.SINGLE_VALUE_KEY),
                "Expected multiple values but was single value"
            ),
            () -> assertEquals(
                Optional.of(RegularTestDocument.MULTIPLE_VALUES),
                regularTestDocument.getMultipleStrings(RegularTestDocument.MULTIPLE_VALUES_KEY)
            )
        );
    }

    @Test
    @DisplayName("Default string getter")
    void defaultGetterIsWorking() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getSingleStringOrDefault(null, "")),
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getSingleStringOrDefault(RegularTestDocument.SINGLE_VALUE, null)
            ),
            () -> Exceptions.isThrownAndContains(
                WrongPropertyTypeException.class,
                () -> regularTestDocument.getSingleStringOrDefault(RegularTestDocument.MULTIPLE_VALUES_KEY, ""),
                "Expected single value but was multiple values"
            ),
            () -> assertEquals(
                RegularTestDocument.SINGLE_VALUE,
                regularTestDocument.getSingleStringOrDefault(RegularTestDocument.SINGLE_VALUE_KEY, "")
            ),
            () -> assertEquals("", regularTestDocument.getSingleStringOrDefault(UNKNOWN_KEY, ""))
        );
    }

    @Test
    @DisplayName("Default string list getter")
    void defaultListGetterIsWorking() {
        List<String> emptyList = Collections.emptyList();
        assertAll(
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getMultipleStringsOrDefault(null, emptyList)
            ),
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getMultipleStringsOrDefault(RegularTestDocument.MULTIPLE_VALUES_KEY, null)
            ),
            () -> Exceptions.isThrownAndContains(
                WrongPropertyTypeException.class,
                () -> regularTestDocument.getMultipleStringsOrDefault(RegularTestDocument.SINGLE_VALUE_KEY, emptyList),
                "Expected multiple values but was single value"
            ),
            () -> assertEquals(
                RegularTestDocument.MULTIPLE_VALUES,
                regularTestDocument.getMultipleStringsOrDefault(RegularTestDocument.MULTIPLE_VALUES_KEY, emptyList)
            ),
            () -> assertEquals(emptyList, regularTestDocument.getMultipleStringsOrDefault(UNKNOWN_KEY, emptyList))
        );
    }

    @Test
    @DisplayName("Mapping single getter")
    void mappingGetterIsWorking() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getSingle(null, String::trim)),
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getSingle(RegularTestDocument.SINGLE_VALUE, null)
            ),
            () -> assertEquals(Optional.empty(), regularTestDocument.getSingle(UNKNOWN_KEY, String::trim)),
            () -> Exceptions.isThrownAndContains(
                WrongPropertyTypeException.class,
                () -> regularTestDocument.getSingle(RegularTestDocument.MULTIPLE_VALUES_KEY, String::trim),
                "Expected single value but was multiple values"
            ),
            () -> assertEquals(
                RegularTestDocument.SINGLE_VALUE.toUpperCase(),
                regularTestDocument.getSingle(RegularTestDocument.SINGLE_VALUE_KEY, String::toUpperCase).orElseThrow()
            ),
            () -> assertEquals(
                RegularTestDocument.SINGLE_VALUE.length(),
                regularTestDocument.getSingle(RegularTestDocument.SINGLE_VALUE_KEY, String::length).orElseThrow()
            ),
            () -> assertDoesNotThrow(
                () -> regularTestDocument.getSingle(
                    RegularTestDocument.SINGLE_VALUE_KEY,
                    (CharSequence charSequence) -> charSequence.charAt(0))
            )
        );
    }

    @Test
    @DisplayName("Mapping multiple getter")
    void mappingListGetterIsWorking() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getMultiple(null, String::trim)),
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getMultiple(RegularTestDocument.SINGLE_VALUE, null)
            ),
            () -> assertEquals(Optional.empty(), regularTestDocument.getMultiple(UNKNOWN_KEY, String::trim)),
            () -> Exceptions.isThrownAndContains(
                WrongPropertyTypeException.class,
                () -> regularTestDocument.getMultiple(RegularTestDocument.SINGLE_VALUE_KEY, String::trim),
                "Expected multiple values but was single value"
            ),
            () -> assertEquals(
                RegularTestDocument.MULTIPLE_VALUES.stream().map(String::toUpperCase).toList(),
                regularTestDocument.getMultiple(RegularTestDocument.MULTIPLE_VALUES_KEY, String::toUpperCase).orElseThrow()
            ),
            () -> assertEquals(
                RegularTestDocument.MULTIPLE_VALUES.stream().map(String::length).toList(),
                regularTestDocument.getMultiple(RegularTestDocument.MULTIPLE_VALUES_KEY, String::length).orElseThrow()
            ),
            () -> assertDoesNotThrow(
                () -> regularTestDocument.getMultiple(
                    RegularTestDocument.MULTIPLE_VALUES_KEY,
                    (CharSequence charSequence) -> charSequence.charAt(0) // Should compile
                )
            )
        );
    }

    @Test
    @DisplayName("Mapping single getter or default")
    void mappingGetterOrDefaultIsWorking() {
        assertAll(
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getSingleOrDefault(null, String::trim, "")
            ),
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getSingleOrDefault(RegularTestDocument.SINGLE_VALUE_KEY, null, "")
            ),
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getSingleOrDefault(RegularTestDocument.SINGLE_VALUE_KEY, String::length, null)
            ),
            () -> assertEquals(
                "default",
                regularTestDocument.getSingleOrDefault(UNKNOWN_KEY, String::toUpperCase, "default")
            ),
            () -> assertEquals(
                10,
                regularTestDocument.getSingleOrDefault(UNKNOWN_KEY, String::length, 10)
            ),
            () -> Exceptions.isThrownAndContains(
                WrongPropertyTypeException.class,
                () -> regularTestDocument.getSingleOrDefault(RegularTestDocument.MULTIPLE_VALUES_KEY, String::trim, ""),
                "Expected single value but was multiple values"
            ),
            () -> assertEquals(
                RegularTestDocument.SINGLE_VALUE.toUpperCase(),
                regularTestDocument.getSingleOrDefault(
                    RegularTestDocument.SINGLE_VALUE_KEY,
                    String::toUpperCase,
                    "Default"
                )
            ),
            () -> assertEquals(
                RegularTestDocument.SINGLE_VALUE.length(),
                regularTestDocument.getSingleOrDefault(RegularTestDocument.SINGLE_VALUE_KEY, String::length, 48)
            ),
            () -> assertDoesNotThrow(
                () -> regularTestDocument.getSingleOrDefault(
                    RegularTestDocument.SINGLE_VALUE_KEY,
                    (CharSequence charSequence) -> charSequence.charAt(0),
                    "Default"
                )
            )
        );
    }

    @Test
    @DisplayName("Mapping multiple getter or default")
    void mappingMultipleGetterOrDefaultIsWorking() {
        var stringList = List.of("default");
        var intList = List.of(10);
        List<CharSequence> charsequenceList = List.of("Default");
        assertAll(
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getMultipleOrDefault(null, String::trim, stringList)
            ),
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getMultipleOrDefault(RegularTestDocument.MULTIPLE_VALUES_KEY, null, stringList)
            ),
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getMultipleOrDefault(RegularTestDocument.MULTIPLE_VALUES_KEY, String::length, null)
            ),
            () -> assertEquals(
                stringList,
                regularTestDocument.getMultipleOrDefault(UNKNOWN_KEY, String::toUpperCase, stringList)
            ),
            () -> assertEquals(intList, regularTestDocument.getMultipleOrDefault(UNKNOWN_KEY, String::length, intList)),
            () -> Exceptions.isThrownAndContains(
                WrongPropertyTypeException.class,
                () -> regularTestDocument.getMultipleOrDefault(RegularTestDocument.SINGLE_VALUE_KEY, String::trim, stringList),
                "Expected multiple values but was single value"
            ),
            () -> assertEquals(
                RegularTestDocument.MULTIPLE_VALUES.stream().map(String::toUpperCase).toList(),
                regularTestDocument.getMultipleOrDefault(
                    RegularTestDocument.MULTIPLE_VALUES_KEY,
                    String::toUpperCase,
                    stringList
                )
            ),
            () -> assertEquals(
                RegularTestDocument.MULTIPLE_VALUES.stream().map(String::length).toList(),
                regularTestDocument.getMultipleOrDefault(RegularTestDocument.MULTIPLE_VALUES_KEY, String::length, intList)
            ),
            () -> assertDoesNotThrow(
                () -> regularTestDocument.getMultipleOrDefault(
                    RegularTestDocument.MULTIPLE_VALUES_KEY,
                    (CharSequence charSequence) -> charSequence,
                    charsequenceList
                )
            )
        );
    }

    @Test
    @DisplayName("Mapping single getter or throws")
    void mappingGetterOrThrowsIsWorking() {
        assertAll(
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getSingleOrThrows(null, String::length)
            ),
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getSingleOrThrows(RegularTestDocument.SINGLE_VALUE, null)
            ),
            () -> assertThrows(
                MissingPropertyException.class,
                () -> regularTestDocument.getSingleOrThrows(UNKNOWN_KEY, String::length)
            ),
            () -> Exceptions.isThrownAndContains(
                WrongPropertyTypeException.class,
                () -> regularTestDocument.getSingleOrThrows(RegularTestDocument.MULTIPLE_VALUES_KEY, String::length),
                "Expected single value but was multiple values"
            ),
            () -> assertEquals(
                RegularTestDocument.SINGLE_VALUE.toUpperCase(),
                regularTestDocument.getSingleOrThrows(RegularTestDocument.SINGLE_VALUE_KEY, String::toUpperCase)
            ),
            () -> assertEquals(
                RegularTestDocument.SINGLE_VALUE.length(),
                regularTestDocument.getSingleOrThrows(RegularTestDocument.SINGLE_VALUE_KEY, String::length)
            ),
            () -> assertDoesNotThrow(
                () -> regularTestDocument.getSingleOrThrows(
                    RegularTestDocument.SINGLE_VALUE_KEY,
                    (CharSequence charsequence) -> charsequence.charAt(0)
                )
            )
        );
    }

    @Test
    @DisplayName("Mapping multiple getter or throws")
    void mappingListGetterOrThrowsIsWorking() {
        assertAll(
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getMultipleOrThrows(null, String::length)
            ),
            () -> assertThrows(
                NullPointerException.class,
                () -> regularTestDocument.getMultipleOrThrows(RegularTestDocument.SINGLE_VALUE, null)
            ),
            () -> assertThrows(
                MissingPropertyException.class,
                () -> regularTestDocument.getMultipleOrThrows(UNKNOWN_KEY, String::length)
            ),
            () -> Exceptions.isThrownAndContains(
                WrongPropertyTypeException.class,
                () -> regularTestDocument.getMultipleOrThrows(RegularTestDocument.SINGLE_VALUE_KEY, String::length),
                "Expected multiple values but was single value"
            ),
            () -> assertEquals(
                RegularTestDocument.MULTIPLE_VALUES.stream().map(String::toUpperCase).toList(),
                regularTestDocument.getMultipleOrThrows(RegularTestDocument.MULTIPLE_VALUES_KEY, String::toUpperCase)
            ),
            () -> assertArrayEquals(
                RegularTestDocument.MULTIPLE_VALUES.stream().mapToInt(String::length).toArray(),
                regularTestDocument.getMultipleOrThrows(RegularTestDocument.MULTIPLE_VALUES_KEY, String::length)
                    .stream().mapToInt(Integer::intValue).toArray()
            ),
            () -> assertDoesNotThrow(
                () -> regularTestDocument.getMultipleOrThrows(
                    RegularTestDocument.MULTIPLE_VALUES_KEY,
                    (CharSequence charsequence) -> charsequence.charAt(0)
                )
            )
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
    @DisplayName("Empty property iterator")
    void emptyPropertyIterator() throws IOException {
        var document = PermissiveDocument.from(new LineProvider() {

            @Override
            public Optional<Line> nextLine() {
                return Optional.empty();
            }

            @Override
            public void close() { /* No resource to close */ }

        });
        var iterator = document.iterator();
        assertAll(
            () -> assertFalse(iterator::hasNext),
            () -> assertThrows(NoSuchElementException.class, iterator::next)
        );
    }

    @Test
    @Tag(TestingTag.PARSING)
    @DisplayName("Contains all records for all documents")
    void containsAllRecordsForAllDocuments() throws URISyntaxException, IOException {
        var documentTester = new DocumentsTester<>(PermissiveDocument::from, PermissiveDocument.class);
        documentTester.testWithAllCorrectDocuments(
            PermissiveDocument::getSingleStringOrThrows,
            PermissiveDocument::getMultipleStringsOrThrows
        );
    }

    @Test
    @Tag(TestingTag.PARSING)
    @DisplayName("Failing on all wrong documents")
    void failingOnAllWrongDocuments() throws Exception {
        var documentTester = new DocumentsTester<>(PermissiveDocument::from, PermissiveDocument.class);
        documentTester.testWithAllIncorrectDocuments();
    }

}