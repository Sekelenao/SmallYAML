package io.github.sekelenao.smallyaml.test.api.document;

import io.github.sekelenao.smallyaml.api.document.PermissiveDocument;
import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.exception.document.WrongPropertyTypeException;
import io.github.sekelenao.smallyaml.api.line.provider.LineProvider;
import io.github.sekelenao.smallyaml.test.util.Exceptions;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import io.github.sekelenao.smallyaml.test.util.document.DocumentsTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Tag(TestingTag.API)
@Tag(TestingTag.COLLECTION)
final class PermissiveDocumentTest {

    private static final String EXPECTED_SINGLE_MESSAGE = "Expected single value but was multiple values";

    private static final String EXPECTED_MULTIPLE_MESSAGE = "Expected multiple values but was single value";

    private static final String UNKNOWN_KEY = "UNKNOWN";

    @Nested
    @DisplayName("Static factories methods")
    final class Factories {

        @Test
        @DisplayName("From is working")
        void constructionsAreWorking() {
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> PermissiveDocument.from(null)),
                () -> assertDoesNotThrow(() -> PermissiveDocument.from(LineProvider.with("")))
            );
        }

        @Test
        @DisplayName("Empty permissive document")
        void emptyPermissiveDocument() {
            assertEquals("{}", PermissiveDocument.empty().toString());
        }

    }

    @Nested
    @DisplayName("Has property")
    final class HasProperty {

        @Test
        @DisplayName("Has property")
        void hasProperty() throws IOException {
            var document = PermissiveDocument.from(LineProvider.with("""
                single-value: value
                multiple-values:
                    - one
                    - two
                    - three
                """));
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> document.hasProperty(null)),
                () -> assertFalse(document.hasProperty(UNKNOWN_KEY)),
                () -> assertFalse(document.hasProperty("no-value")),
                () -> assertTrue(document.hasProperty("single-value")),
                () -> assertTrue(document.hasProperty("multiple-values"))
            );
        }

    }

    @Nested
    @DisplayName("String getters")
    final class StringGetters {

        @Test
        @DisplayName("Single string getter")
        void singleStringGetterIsWorking() throws IOException {
            var document = PermissiveDocument.from(LineProvider.with("""
                single-value: value
                multiple-values:
                    - one
                    - two
                    - three
                """));
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> document.getSingleString(null)),
                () -> assertEquals(Optional.empty(), document.getSingleString(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> document.getSingleString("multiple-values"),
                    EXPECTED_SINGLE_MESSAGE
                ),
                () -> assertEquals(Optional.of("value"), document.getSingleString("single-value"))
            );
        }

        @Test
        @DisplayName("Multiple String getter")
        void optionalListGetterIsWorking() throws IOException {
            var document = PermissiveDocument.from(LineProvider.with("""
                single-value: value
                multiple-values:
                    - one
                    - two
                    - three
                """));
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> document.getMultipleStrings(null)),
                () -> assertEquals(Optional.empty(), document.getMultipleStrings(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> document.getMultipleStrings("single-value"),
                    EXPECTED_MULTIPLE_MESSAGE
                ),
                () -> assertEquals(Optional.of(List.of("one", "two", "three")),
                    document.getMultipleStrings("multiple-values")
                )
            );
        }

    }

    @Nested
    @DisplayName("Mapping getters")
    final class MappingGetters {

        @Test
        @DisplayName("Mapping single getter")
        void mappingGetterIsWorking() throws IOException {
            var document = PermissiveDocument.from(LineProvider.with("""
                single-value: value
                multiple-values:
                    - one
                    - two
                    - three
                """));
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> document.getSingle(null, String::trim)),
                () -> assertThrows(NullPointerException.class, () -> document.getSingle(UNKNOWN_KEY, null)),
                () -> assertEquals(Optional.empty(), document.getSingle(UNKNOWN_KEY, String::trim)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> document.getSingle("multiple-values", String::trim),
                    EXPECTED_SINGLE_MESSAGE
                ),
                () -> assertEquals("VALUE", document.getSingle("single-value", String::toUpperCase).orElseThrow()),
                () -> assertEquals(5, document.getSingle("single-value", String::length).orElseThrow()),
                () -> assertDoesNotThrow(
                    () -> document.getSingle(
                        "single-value",
                        (CharSequence charSequence) -> charSequence.charAt(0) // Should compile
                    )
                )
            );
        }

        @Test
        @DisplayName("Mapping multiple getter")
        void mappingListGetterIsWorking() throws IOException {
            var document = PermissiveDocument.from(LineProvider.with("""
                single-value: value
                multiple-values:
                    - one
                    - two
                    - three
                """));
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> document.getMultiple(null, String::trim)),
                () -> assertThrows(NullPointerException.class, () -> document.getMultiple(UNKNOWN_KEY, null)),
                () -> assertEquals(Optional.empty(), document.getMultiple(UNKNOWN_KEY, String::trim)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> document.getMultiple("single-value", String::trim),
                    EXPECTED_MULTIPLE_MESSAGE
                ),
                () -> assertEquals(
                    List.of("ONE", "TWO", "THREE"),
                    document.getMultiple("multiple-values", String::toUpperCase).orElseThrow()
                ),
                () -> assertEquals(List.of(3, 3, 5), document.getMultiple("multiple-values", String::length).orElseThrow()),
                () -> assertDoesNotThrow(
                    () -> document.getMultiple(
                        "multiple-values",
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
        void defaultBooleanSingleGetterIsWorking() throws IOException {
            var document = PermissiveDocument.from(LineProvider.with("""
                single-boolean: false
                multiple-booleans:
                    - true
                    - false
                    - true
                """));
            assertAll(
                () -> assertThrows(
                    NullPointerException.class,
                    () -> document.getSingleBooleanOrDefault(null, true)
                ),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> document.getSingleBooleanOrDefault("multiple-booleans", false),
                    EXPECTED_SINGLE_MESSAGE
                ),
                () -> assertTrue(document.getSingleBooleanOrDefault(UNKNOWN_KEY, true)),
                () -> assertFalse(document.getSingleBooleanOrDefault("single-boolean", true))
            );
        }

        @Test
        @DisplayName("Boolean single getter or throw")
        void booleanSingleGetterOrThrowIsWorking() throws IOException {
            var document = PermissiveDocument.from(LineProvider.with("""
                single-boolean: false
                multiple-booleans:
                    - true
                    - false
                    - true
                """));
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> document.getSingleBooleanOrThrow(null)),
                () -> assertThrows(NoSuchElementException.class, () -> document.getSingleBooleanOrThrow(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> document.getSingleBooleanOrThrow("multiple-booleans"),
                    EXPECTED_SINGLE_MESSAGE
                ),
                () -> assertFalse(document.getSingleBooleanOrThrow("single-boolean"))
            );
        }

        @Test
        @DisplayName("Single int getter")
        void singleIntGetterIsWorking() throws IOException {
            var document = PermissiveDocument.from(LineProvider.with("""
                single-int: "-20"
                multiple-ints:
                    - 1
                    - 2
                    - 3
                """));
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> document.getSingleInt(null)),
                () -> assertEquals(OptionalInt.empty(), document.getSingleInt(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> document.getSingleInt("multiple-ints"),
                    EXPECTED_SINGLE_MESSAGE
                ),
                () -> assertEquals(-20, document.getSingleInt("single-int").orElseThrow())
            );
        }

        @Test
        @DisplayName("Multiple ints getter")
        void multipleIntsGetterIsWorking() throws IOException {
            var document = PermissiveDocument.from(LineProvider.with("""
                single-int: 20
                multiple-ints:
                    - 1
                    - 2
                    - 3
                """));
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> document.getMultipleInts(null)),
                () -> assertEquals(Optional.empty(), document.getMultipleInts(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> document.getMultipleInts("single-int"),
                    EXPECTED_MULTIPLE_MESSAGE
                ),
                () -> assertArrayEquals(new int[]{1, 2, 3}, document.getMultipleInts("multiple-ints").orElseThrow())
            );
        }

        @Test
        @DisplayName("Single long getter")
        void singleLongGetterIsWorking() throws IOException {
            var document = PermissiveDocument.from(LineProvider.with("""
                single-long: 9223372036854775807
                multiple-longs:
                    - 1
                    - 2
                    - 3
                """));
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> document.getSingleLong(null)),
                () -> assertEquals(OptionalLong.empty(), document.getSingleLong(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> document.getSingleLong("multiple-longs"),
                    EXPECTED_SINGLE_MESSAGE
                ),
                () -> assertEquals(9223372036854775807L, document.getSingleLong("single-long").orElseThrow())
            );
        }

        @Test
        @DisplayName("Multiple longs getter")
        void multipleLongsGetterIsWorking() throws IOException {
            var document = PermissiveDocument.from(LineProvider.with("""
                single-long: 9223372036854775807
                multiple-longs:
                    - 1
                    - 2
                    - 3
                """));
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> document.getMultipleLongs(null)),
                () -> assertEquals(Optional.empty(), document.getMultipleLongs(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> document.getMultipleLongs("single-long"),
                    EXPECTED_MULTIPLE_MESSAGE
                ),
                () -> assertArrayEquals(new long[]{1, 2, 3}, document.getMultipleLongs("multiple-longs").orElseThrow())
            );
        }

        @Test
        @DisplayName("Single double getter")
        void singleDoubleGetterIsWorking() throws IOException {
            var document = PermissiveDocument.from(LineProvider.with("""
                single-double: "20.20"
                multiple-doubles:
                    - "1.1"
                    - "2.2"
                    - "3.3"
                """));
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> document.getSingleDouble(null)),
                () -> assertEquals(OptionalDouble.empty(), document.getSingleDouble(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> document.getSingleDouble("multiple-doubles"),
                    EXPECTED_SINGLE_MESSAGE
                ),
                () -> assertEquals(20.20, document.getSingleDouble("single-double").orElseThrow())
            );
        }

        @Test
        @DisplayName("Multiple doubles getter")
        void multipleDoublesGetterIsWorking() throws IOException {
            var document = PermissiveDocument.from(LineProvider.with("""
                single-double: "20.20"
                multiple-doubles:
                    - "1.1"
                    - "2.2"
                    - "3.3"
                """));
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> document.getMultipleDoubles(null)),
                () -> assertEquals(Optional.empty(), document.getMultipleDoubles(UNKNOWN_KEY)),
                () -> Exceptions.isThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> document.getMultipleDoubles("single-double"),
                    EXPECTED_MULTIPLE_MESSAGE
                ),
                () -> assertArrayEquals(
                    new double[]{1.1, 2.2, 3.3},
                    document.getMultipleDoubles("multiple-doubles").orElseThrow()
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
            var document = PermissiveDocument.from(LineProvider.with("""
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
            var document = PermissiveDocument.from(LineProvider.with("""
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
            var document = PermissiveDocument.from(LineProvider.with("""
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
            var document = PermissiveDocument.from(LineProvider.with("""
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
        void propertyIterator() throws IOException {
            var document = PermissiveDocument.from(LineProvider.with("""
                single-value: value
                multiple-values:
                    - one
                    - two
                    - three
                last-value: last
                """));
            var iterator = document.iterator();
            for (int i = 0; i < 3; i++) {
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
    @DisplayName("Spliterator and Stream")
    final class SpliteratorAndStream {

        private static LineProvider generateLineProvider(int size) {
            var builder = new StringBuilder();
            for (int i = 0; i < size - 1; i++) {
                builder.append(i).append(": value\n");
            }
            builder.append(size - 1).append(":").append("\n")
                .append("- item1").append("\n")
                .append("- item2").append("\n")
                .append("- item3");
            return LineProvider.with(builder.toString());
        }

        @ParameterizedTest(name = "{displayName} ({0})")
        @MethodSource("io.github.sekelenao.smallyaml.test.util.Randoms#intStreamWithSize5")
        @DisplayName("PermissiveDocument stream is working sequential and parallel")
        void streamIsWorking(int size) throws IOException {
            var document = PermissiveDocument.from(generateLineProvider(size));
            var expectedSet = IntStream.range(0, size)
                .boxed()
                .collect(Collectors.toSet());
            var result = document.stream()
                .map(Property::key)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
            var parallelResult = document.stream()
                .parallel()
                .map(Property::key)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
            assertAll(
                () -> assertEquals(expectedSet, result),
                () -> assertEquals(expectedSet, parallelResult),
                () -> assertEquals(size, result.size()),
                () -> assertEquals(size, parallelResult.size())
            );
        }

        @Test
        @DisplayName("PermissiveDocument parallel stream is really parallel")
        void streamParallel() throws IOException {
            var document = PermissiveDocument.from(generateLineProvider(100));
            var thread = Thread.currentThread();
            var otherThreadCount = document.stream()
                .parallel()
                .mapToInt(ignored -> thread != Thread.currentThread() ? 1 : 0)
                .sum();
            assertNotEquals(0, otherThreadCount);
        }

        @Test
        @DisplayName("Empty document is streamable")
        void emptyDocumentIsStreamable() {
            var document = PermissiveDocument.empty();
            assertAll(
                () -> assertEquals(0, document.stream().count()),
                () -> assertEquals(0, document.stream().parallel().count())
            );
        }

        @Test
        @DisplayName("PropertySpliterator trySplit returns null for an empty source")
        void trySplitReturnsNullForEmptySource() {
            var emptyDocument = PermissiveDocument.empty();
            var emptySpliterator = emptyDocument.spliterator();
            assertAll(
                () -> assertNull(emptySpliterator.trySplit()),
                () -> assertFalse(emptySpliterator.tryAdvance(p -> fail("Should not advance on an empty spliterator"))),
                () -> assertEquals(0, emptySpliterator.estimateSize())
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
            var document = PermissiveDocument.from(LineProvider.with("""
                single-value: value
                multiple-values:
                    - one
                    - two
                    - three
                """));
            var sameDocument = PermissiveDocument.from(LineProvider.with("""
                single-value: value
                multiple-values:
                    - one
                    - two
                    - three
                """));
            var emptyDocument = PermissiveDocument.empty();
            var otherEmptyDocument = PermissiveDocument.empty();
            var firstDocument = PermissiveDocument.from(LineProvider.with("Key: First"));
            var secondDocument = PermissiveDocument.from(LineProvider.with("Key: Second"));
            assertAll(
                () -> assertEquals(document, sameDocument),
                () -> assertNotEquals(emptyDocument, document),
                () -> assertEquals(emptyDocument, otherEmptyDocument),
                () -> assertFalse(emptyDocument.equals(Collections.emptyMap())),
                () -> assertFalse(document.equals(null)),
                () -> assertEquals(document, document),
                () -> assertNotEquals(firstDocument, secondDocument)
            );
        }

        @Test
        @DisplayName("Hashcode is working")
        void hashcodeIsWorking() throws IOException {
            var document = PermissiveDocument.from(LineProvider.with("""
                single-value: value
                multiple-values:
                    - one
                    - two
                    - three
                """));
            var sameDocument = PermissiveDocument.from(LineProvider.with("""
                single-value: value
                multiple-values:
                    - one
                    - two
                    - three
                """));
            var emptyDocument = PermissiveDocument.empty();
            var firstDocument = PermissiveDocument.from(LineProvider.with("Key: First"));
            var secondDocument = PermissiveDocument.from(LineProvider.with("Key: Second"));
            assertAll(
                () -> assertEquals(document.hashCode(), sameDocument.hashCode()),
                () -> assertNotEquals(new Object().hashCode(), document.hashCode()),
                () -> assertEquals(0, emptyDocument.hashCode()),
                () -> assertNotEquals(0, document.hashCode()),
                () -> assertNotEquals(firstDocument.hashCode(), secondDocument.hashCode())
            );
        }

        @Test
        @DisplayName("To string is working")
        void toStringIsWorking() throws IOException {
            var emptyDocument = PermissiveDocument.empty();
            var document = PermissiveDocument.from(LineProvider.with("""
                single-value: value
                multiple-values:
                    - one
                    - two
                    - three
                """));
            var firstDocument = PermissiveDocument.from(LineProvider.with("Key: First"));
            var secondDocument = PermissiveDocument.from(LineProvider.with("Key: Second"));
            assertAll(
                () -> assertEquals("{single-value=value, multiple-values=[one, two, three]}", document.toString()),
                () -> assertEquals("{}", emptyDocument.toString()),
                () -> assertEquals("{key=First}", firstDocument.toString()),
                () -> assertEquals("{key=Second}", secondDocument.toString())
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
                (document, key) -> document.getSingleString(key)
                    .orElseThrow(() -> new NoSuchElementException("Following key is absent or empty: " + key)),
                (document, key) -> document.getMultipleStrings(key)
                    .orElseThrow(() -> new NoSuchElementException("Following key is absent or empty: " + key))
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
