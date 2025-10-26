package io.github.sekelenao.smallyaml.test.api.document;

import io.github.sekelenao.smallyaml.api.document.PermissiveDocument;
import io.github.sekelenao.smallyaml.api.exception.document.WrongPropertyTypeException;
import io.github.sekelenao.smallyaml.api.line.provider.BufferedReaderLineProvider;
import io.github.sekelenao.smallyaml.api.line.provider.StringLineProvider;
import io.github.sekelenao.smallyaml.test.util.Exceptions;
import io.github.sekelenao.smallyaml.test.util.constant.RegularTestDocument;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import io.github.sekelenao.smallyaml.test.util.document.DocumentsTester;
import io.github.sekelenao.smallyaml.test.util.resource.TestResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(TestingTag.API)
@Tag(TestingTag.COLLECTION)
final class PermissiveDocumentTest {

    private static final String EXPECTED_SINGLE_MESSAGE = "Expected single value but was multiple values";

    private static final String EXPECTED_MULTIPLE_MESSAGE = "Expected multiple values but was single value";

    private static final String UNKNOWN_KEY = "UNKNOWN";

    private final PermissiveDocument regularTestDocument;

    public PermissiveDocumentTest() throws URISyntaxException, IOException {
        var file = TestResource.find(RegularTestDocument.TEST_DOCUMENT);
        try (var bufferedReaderLineProvider = BufferedReaderLineProvider.with(Files.newBufferedReader(file))) {
            this.regularTestDocument = PermissiveDocument.from(bufferedReaderLineProvider);
        }
    }

    @Nested
    @DisplayName("Static factories methods")
    final class Factories {

        @Test
        @DisplayName("From is working")
        void constructionsAreWorking(){
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> PermissiveDocument.from(null)),
                () -> assertDoesNotThrow(() -> PermissiveDocument.from(StringLineProvider.of("")))
            );
        }

        @Test
        @DisplayName("Empty permissive document")
        void emptyPermissiveDocument(){
            assertEquals("{}", PermissiveDocument.empty().toString());
        }

    }

    @Nested
    @DisplayName("Has property")
    final class HasProperty {

        @Test
        @DisplayName("Has property")
        void hasProperty() {
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> regularTestDocument.hasProperty(null)),
                () -> assertFalse(regularTestDocument.hasProperty(UNKNOWN_KEY)),
                () -> assertTrue(regularTestDocument.hasProperty(RegularTestDocument.SINGLE_STRING_KEY)),
                () -> assertTrue(regularTestDocument.hasProperty(RegularTestDocument.MULTIPLE_STRING_KEY)),
                () -> assertTrue(regularTestDocument.hasProperty(RegularTestDocument.SINGLE_BOOLEAN_KEY)),
                () -> assertTrue(regularTestDocument.hasProperty(RegularTestDocument.MULTIPLE_BOOLEAN_KEY))
            );
        }

    }

    @Nested
    @DisplayName("String getters")
    final class StringGetters {

        @Test
        @DisplayName("Single string getter")
        void singleStringGetterIsWorking() {
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getSingleString(null)),
                () -> assertEquals(Optional.empty(), regularTestDocument.getSingleString(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> regularTestDocument.getSingleString(RegularTestDocument.MULTIPLE_STRING_KEY),
                    EXPECTED_SINGLE_MESSAGE
                ),
                () -> assertEquals(
                    Optional.of(RegularTestDocument.SINGLE_STRING_VALUE),
                    regularTestDocument.getSingleString(RegularTestDocument.SINGLE_STRING_KEY)
                )
            );
        }

        @Test
        @DisplayName("Multiple String getter")
        void optionalListGetterIsWorking() {
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getMultipleStrings(null)),
                () -> assertEquals(Optional.empty(), regularTestDocument.getMultipleStrings(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> regularTestDocument.getMultipleStrings(RegularTestDocument.SINGLE_STRING_KEY),
                    EXPECTED_MULTIPLE_MESSAGE
                ),
                () -> assertEquals(
                    Optional.of(RegularTestDocument.MULTIPLE_STRINGS_VALUE),
                    regularTestDocument.getMultipleStrings(RegularTestDocument.MULTIPLE_STRING_KEY)
                )
            );
        }

    }

    @Nested
    @DisplayName("Mapping getters")
    final class MappingGetters {

        @Test
        @DisplayName("Mapping single getter")
        void mappingGetterIsWorking() {
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getSingle(null, String::trim)),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> regularTestDocument.getSingle(RegularTestDocument.SINGLE_STRING_VALUE, null)
                ),
                () -> assertEquals(Optional.empty(), regularTestDocument.getSingle(UNKNOWN_KEY, String::trim)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> regularTestDocument.getSingle(RegularTestDocument.MULTIPLE_STRING_KEY, String::trim),
                    EXPECTED_SINGLE_MESSAGE
                ),
                () -> assertEquals(
                    RegularTestDocument.SINGLE_STRING_VALUE.toUpperCase(),
                    regularTestDocument.getSingle(RegularTestDocument.SINGLE_STRING_KEY, String::toUpperCase).orElseThrow()
                ),
                () -> assertEquals(
                    RegularTestDocument.SINGLE_STRING_VALUE.length(),
                    regularTestDocument.getSingle(RegularTestDocument.SINGLE_STRING_KEY, String::length).orElseThrow()
                ),
                () -> assertDoesNotThrow(
                    () -> regularTestDocument.getSingle(
                        RegularTestDocument.SINGLE_STRING_KEY,
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
                    () -> regularTestDocument.getMultiple(RegularTestDocument.SINGLE_STRING_VALUE, null)
                ),
                () -> assertEquals(Optional.empty(), regularTestDocument.getMultiple(UNKNOWN_KEY, String::trim)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> regularTestDocument.getMultiple(RegularTestDocument.SINGLE_STRING_KEY, String::trim),
                    EXPECTED_MULTIPLE_MESSAGE
                ),
                () -> assertEquals(
                    RegularTestDocument.MULTIPLE_STRINGS_VALUE.stream().map(String::toUpperCase).toList(),
                    regularTestDocument.getMultiple(RegularTestDocument.MULTIPLE_STRING_KEY, String::toUpperCase).orElseThrow()
                ),
                () -> assertEquals(
                    RegularTestDocument.MULTIPLE_STRINGS_VALUE.stream().map(String::length).toList(),
                    regularTestDocument.getMultiple(RegularTestDocument.MULTIPLE_STRING_KEY, String::length).orElseThrow()
                ),
                () -> assertDoesNotThrow(
                    () -> regularTestDocument.getMultiple(
                        RegularTestDocument.MULTIPLE_STRING_KEY,
                        (CharSequence charSequence) -> charSequence.charAt(0) // Should compile
                    )
                )
            );
        }

    }

    @Nested
    @DisplayName("Primitive getters")
    final class PrimitiveGetters {

        @Test
        @DisplayName("Default boolean single getter")
        void defaultBooleanSingleGetterIsWorking() {
            assertAll(
                () -> assertThrows(
                    NullPointerException.class,
                    () -> regularTestDocument.getSingleBooleanOrDefault(null, true)
                ),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> regularTestDocument.getSingleBooleanOrDefault(RegularTestDocument.MULTIPLE_BOOLEAN_KEY, false),
                    EXPECTED_SINGLE_MESSAGE
                ),
                () -> assertTrue(regularTestDocument.getSingleBooleanOrDefault(UNKNOWN_KEY, true)),
                () -> assertFalse(regularTestDocument.getSingleBooleanOrDefault(RegularTestDocument.SINGLE_BOOLEAN_KEY, true))
            );
        }

        @Test
        @DisplayName("Boolean single getter or throw")
        void booleanSingleGetterOrThrowIsWorking() {
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getSingleBooleanOrThrow(null)),
                () -> assertThrows(NoSuchElementException.class, () -> regularTestDocument.getSingleBooleanOrThrow(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> regularTestDocument.getSingleBooleanOrThrow(RegularTestDocument.MULTIPLE_BOOLEAN_KEY),
                    EXPECTED_SINGLE_MESSAGE
                ),
                () -> assertFalse(regularTestDocument.getSingleBooleanOrThrow(RegularTestDocument.SINGLE_BOOLEAN_KEY))
            );
        }

        @Test
        @DisplayName("Single int getter")
        void singleIntGetterIsWorking() {
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getSingleInt(null)),
                () -> assertEquals(OptionalInt.empty(), regularTestDocument.getSingleInt(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> regularTestDocument.getSingleInt(RegularTestDocument.MULTIPLE_STRING_KEY),
                    EXPECTED_SINGLE_MESSAGE
                ),
                () -> assertEquals(
                    RegularTestDocument.SINGLE_LONG_VALUE,
                    regularTestDocument.getSingleInt(RegularTestDocument.SINGLE_LONG_KEY).orElseThrow()
                )
            );
        }

        @Test
        @DisplayName("Multiple ints getter")
        void multipleIntsGetterIsWorking() {
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getMultipleInts(null)),
                () -> assertEquals(Optional.empty(), regularTestDocument.getMultipleInts(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> regularTestDocument.getMultipleInts(RegularTestDocument.SINGLE_STRING_KEY),
                    EXPECTED_MULTIPLE_MESSAGE
                ),
                () -> assertArrayEquals(
                    Arrays.stream(RegularTestDocument.MULTIPLE_LONGS_VALUE).mapToInt(l -> (int) l).toArray(),
                    regularTestDocument.getMultipleInts(RegularTestDocument.MULTIPLE_LONGS_KEY).orElseThrow()
                )
            );
        }

        @Test
        @DisplayName("Single long getter")
        void singleLongGetterIsWorking() {
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getSingleLong(null)),
                () -> assertEquals(OptionalLong.empty(), regularTestDocument.getSingleLong(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> regularTestDocument.getSingleLong(RegularTestDocument.MULTIPLE_LONGS_KEY),
                    EXPECTED_SINGLE_MESSAGE
                ),
                () -> assertEquals(20L, regularTestDocument.getSingleLong(RegularTestDocument.SINGLE_LONG_KEY).orElseThrow())
            );
        }

        @Test
        @DisplayName("Multiple longs getter")
        void multipleLongsGetterIsWorking() {
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getMultipleLongs(null)),
                () -> assertEquals(Optional.empty(), regularTestDocument.getMultipleLongs(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> regularTestDocument.getMultipleLongs(RegularTestDocument.SINGLE_LONG_KEY),
                    EXPECTED_MULTIPLE_MESSAGE
                ),
                () -> assertArrayEquals(
                    RegularTestDocument.MULTIPLE_LONGS_VALUE,
                    regularTestDocument.getMultipleLongs(RegularTestDocument.MULTIPLE_LONGS_KEY).orElseThrow()
                )
            );
        }

        @Test
        @DisplayName("Single double getter")
        void singleDoubleGetterIsWorking() {
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getSingleDouble(null)),
                () -> assertEquals(OptionalDouble.empty(), regularTestDocument.getSingleDouble(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> regularTestDocument.getSingleDouble(RegularTestDocument.MULTIPLE_DOUBLES_KEY),
                    EXPECTED_SINGLE_MESSAGE
                ),
                () -> assertEquals(
                    RegularTestDocument.SINGLE_DOUBLE_VALUE,
                    regularTestDocument.getSingleDouble(RegularTestDocument.SINGLE_DOUBLE_KEY).orElseThrow()
                )
            );
        }

        @Test
        @DisplayName("Multiple doubles getter")
        void multipleDoublesGetterIsWorking() {
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> regularTestDocument.getMultipleDoubles(null)),
                () -> assertEquals(Optional.empty(), regularTestDocument.getMultipleDoubles(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> regularTestDocument.getMultipleDoubles(RegularTestDocument.SINGLE_DOUBLE_KEY),
                    EXPECTED_MULTIPLE_MESSAGE
                ),
                () -> assertArrayEquals(
                    RegularTestDocument.MULTIPLE_DOUBLES_VALUE,
                    regularTestDocument.getMultipleDoubles(RegularTestDocument.MULTIPLE_DOUBLES_KEY).orElseThrow()
                )
            );
        }

    }

    @Nested
    @DisplayName("Subkeys iteration")
    final class SubKeys {

        @Test
        @DisplayName("Subkeys assertions")
        void assertions() {
            var permissiveDocument = PermissiveDocument.empty();
            assertThrows(NullPointerException.class, () -> permissiveDocument.subKeysOf(null));
        }

        @Test
        @DisplayName("Simple subkeys are found")
        void simpleSubkeysAreFound() throws IOException {
            var document = PermissiveDocument.from(StringLineProvider.of("""
                endpoints:
                    startup:
                        endpoint: /startup
                    readiness:
                        endpoint: /readiness
                    liveness:
                        endpoint: /liveness
                        not:
                            aSubkey: no
                not:
                    aSubkey: no
                """));
            var subKeys = document.subKeysOf("endpoints");
            assertAll(
                () -> assertEquals(3, subKeys.size()),
                () -> assertEquals(Set.of("endpoints.startup", "endpoints.readiness", "endpoints.liveness"), subKeys)
            );
        }

        @Test
        @DisplayName("Simple subkeys are found")
        void NoSubkeysAreFound() throws IOException {
            var document = PermissiveDocument.from(StringLineProvider.of("""
                endpoints:
                    startup:
                        endpoint: /startup
                    readiness:
                        endpoint: /readiness
                    liveness:
                        endpoint: /liveness
                        not:
                            aSubkey: no
                not:
                    aSubkey: no
                """));
            assertAll(
                () -> assertEquals(0, document.subKeysOf("endpoints.startup").size()),
                () -> assertEquals(0, document.subKeysOf("unknown").size())
            );
        }

        @Test
        @DisplayName("Far subkeys are found")
        void farSubkeysAreFound() throws IOException {
            var document = PermissiveDocument.from(StringLineProvider.of("""
                endpoints:
                    enumeration:
                        startup:
                            endpoint: /startup
                        readiness:
                            endpoint: /readiness
                        liveness:
                            endpoint: /liveness
                            not:
                                aSubkey: no
                not:
                    aSubkey: no
                """));
            var subKeys = document.subKeysOf("endpoints.enumeration");
            assertAll(
                () -> assertEquals(3, subKeys.size()),
                () -> assertEquals(
                    Set.of(
                        "endpoints.enumeration.startup",
                        "endpoints.enumeration.readiness",
                        "endpoints.enumeration.liveness"
                    ),
                    subKeys
                )
            );
        }

        @Test
        @DisplayName("Composed subkeys are found")
        void composedSubKeysAreFound() throws IOException {
            var document = PermissiveDocument.from(StringLineProvider.of("""
                endpoints:
                    startup.endpoint: /startup
                    readiness.endpoint: /readiness
                    readiness:
                        not:
                            aSubkey: no
                    liveness.endpoint: /liveness
                    liveness.not:
                        aSubkey: no
                not:
                    aSubkey: no
                """));
            var subKeys = document.subKeysOf("endpoints");
            assertAll(
                () -> assertEquals(3, subKeys.size()),
                () -> assertEquals(Set.of("endpoints.startup", "endpoints.readiness", "endpoints.liveness"), subKeys)
            );
        }

    }

    @Nested
    @DisplayName("Properties iterator")
    final class PropertiesIterator {

        @Test
        @DisplayName("Property iterator")
        void propertyIterator() {
            var iterator = regularTestDocument.iterator();
            for (int i = 0; i < RegularTestDocument.KEY_VALUE_LINE_COUNT + RegularTestDocument.KEY_LINE_COUNT; i++) {
                assertTrue(iterator.hasNext());
                assertDoesNotThrow(iterator::hasNext);
                assertDoesNotThrow(iterator::next);
            }
            assertThrows(NoSuchElementException.class, iterator::next);
        }

        @Test
        @DisplayName("Empty property iterator")
        void emptyPropertyIterator() {
            var document = PermissiveDocument.empty();
            var iterator = document.iterator();
            assertAll(
                () -> assertFalse(iterator::hasNext),
                () -> assertThrows(NoSuchElementException.class, iterator::next)
            );
        }

    }

    @Nested
    @DisplayName("Equals, hashcode and toString")
    final class EqualsHashcodeToString {

        @Test
        @SuppressWarnings("all")
        @DisplayName("Equals is working")
        void equalsIsWorking() throws URISyntaxException, IOException {
            var file = TestResource.find(RegularTestDocument.TEST_DOCUMENT);
            var otherEmptyDocument1 = PermissiveDocument.empty();
            var otherEmptyDocument2 = PermissiveDocument.empty();
            var firstDocument = PermissiveDocument.from(StringLineProvider.of("Key: First"));
            var secondDocument = PermissiveDocument.from(StringLineProvider.of("Key: Second"));
            try (var bufferedReaderLineProvider = BufferedReaderLineProvider.with(Files.newBufferedReader(file))) {
                var sameDocument = PermissiveDocument.from(bufferedReaderLineProvider);
                assertAll(
                    () -> assertEquals(regularTestDocument, sameDocument),
                    () -> assertNotEquals(otherEmptyDocument1, regularTestDocument),
                    () -> assertEquals(otherEmptyDocument1, otherEmptyDocument2),
                    () -> assertFalse(otherEmptyDocument1.equals(Collections.emptyMap())),
                    () -> assertFalse(regularTestDocument.equals(null)),
                    () -> assertEquals(regularTestDocument, regularTestDocument),
                    () -> assertNotEquals(firstDocument, secondDocument)
                );
            }
        }

        @Test
        @DisplayName("Hashcode is working")
        void hashcodeIsWorking() throws URISyntaxException, IOException {
            var file = TestResource.find(RegularTestDocument.TEST_DOCUMENT);
            try (var bufferedReaderLineProvider = BufferedReaderLineProvider.with(Files.newBufferedReader(file))) {
                var sameDocument = PermissiveDocument.from(bufferedReaderLineProvider);
                var emptyDocument = PermissiveDocument.empty();
                var firstDocument = PermissiveDocument.from(StringLineProvider.of("Key: First"));
                var secondDocument = PermissiveDocument.from(StringLineProvider.of("Key: Second"));
                assertAll(
                    () -> assertEquals(regularTestDocument.hashCode(), sameDocument.hashCode()),
                    () -> assertNotEquals(regularTestDocument.hashCode(), new Object().hashCode()),
                    () -> assertEquals(0, emptyDocument.hashCode()),
                    () -> assertNotEquals(0, regularTestDocument.hashCode()),
                    () -> assertNotEquals(firstDocument.hashCode(), secondDocument.hashCode())
                );
            }
        }

        @Test
        @DisplayName("To string is working")
        void toStringIsWorking() throws IOException {
            var emptyDocument = PermissiveDocument.empty();
            var firstDocument = PermissiveDocument.from(StringLineProvider.of("Key: First"));
            var secondDocument = PermissiveDocument.from(StringLineProvider.of("Key: Second"));
            assertAll(
                () -> assertEquals("{single-value=value, single-long=20, multiple-doubles=[1.1, 2, 3.3], single-boolean=False, single-double=1.2, multiple-booleans=[true, False, TRUE], multiple-longs=[1, 2, 3], multiple-values=[one, two, three]}", regularTestDocument.toString()),
                () -> assertEquals("{}", emptyDocument.toString()),
                () -> assertEquals("{Key=First}", firstDocument.toString()),
                () -> assertEquals("{Key=Second}", secondDocument.toString())
            );
        }

    }


    @Nested
    @DisplayName("Templates testing at runtime")
    final class TemplatesAtRuntime {

        @Test
        @Tag(TestingTag.PARSING)
        @DisplayName("Contains all records for all documents")
        void containsAllRecordsForAllDocuments() throws URISyntaxException, IOException {
            var documentTester = new DocumentsTester<>(PermissiveDocument::from, PermissiveDocument.class);
            documentTester.testWithAllCorrectDocuments(
                (document, key) -> document.getSingleString(key).orElseThrow(),
                (document, key) -> document.getMultipleStrings(key).orElseThrow()
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

}
