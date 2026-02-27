package io.github.sekelenao.smallyaml.test.api.document;

import io.github.sekelenao.smallyaml.api.document.BoundedDocument;
import io.github.sekelenao.smallyaml.api.document.property.MultipleMandatoryIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.MultipleOptionalIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleMandatoryIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleOptionalIdentifier;
import io.github.sekelenao.smallyaml.api.exception.document.NotRegisteredIdentifierException;
import io.github.sekelenao.smallyaml.test.util.ExceptionsTester;
import io.github.sekelenao.smallyaml.test.util.Reflections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class BoundedDocumentTest {

    @Nested
    @DisplayName("Lifecycle and Registration")
    final class LifecycleAndRegistration {

        @Test
        @DisplayName("Empty document")
        void empty() {
            var doc = BoundedDocument.empty();
            assertAll(
                () -> assertTrue(doc.registeredIdentifiers().isEmpty()),
                () -> assertEquals("{}", doc.toString()));
        }

        @Test
        @DisplayName("Registration checks")
        void registration() throws IOException {
            var id = SingleMandatoryIdentifier.define("key");
            var doc = BoundedDocument.factoryBuilder()
                .register(id)
                .buildFactory()
                .createDocument("key: value ");
            assertAll(
                () -> assertTrue(doc.hasRegistered(id)),
                () -> assertFalse(doc.hasRegistered(SingleMandatoryIdentifier.define("unknown"))),
                () -> assertEquals(Collections.singleton(id), doc.registeredIdentifiers()),
                () -> assertThrows(NullPointerException.class, () -> doc.hasRegistered(null)));
        }

        @Test
        @DisplayName("Unregistered identifier errors")
        void unregisteredErrors() {
            var doc = BoundedDocument.empty();
            var sm = SingleMandatoryIdentifier.define("unknown");
            var so = SingleOptionalIdentifier.define("unknown");
            var mm = MultipleMandatoryIdentifier.define("unknown");
            var mo = MultipleOptionalIdentifier.define("unknown");
            assertAll(
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(sm),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(so),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(mm),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(mo),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(sm, Integer::parseInt),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(so, Integer::parseInt),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(mm, Integer::parseInt),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(mo, Integer::parseInt),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.getBoolean(sm),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.getBooleanOrDefault(so, false),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.getInt(sm),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.getInt(so),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.getInts(mm),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.getInts(mo),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.getLong(sm),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.getLong(so),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.getLongs(mm),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.getLongs(mo),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.getDouble(sm),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.getDouble(so),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.getDoubles(mm),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.getDoubles(mo),
                    "Not registered identifier"
                )
            );
        }

    }

    @Nested
    @DisplayName("Generic Accessors (with mapping)")
    final class GenericAccessors {

        @Test
        @DisplayName("Single values")
        void singleSub() throws IOException {
            var id = SingleMandatoryIdentifier.define("int");
            var optId = SingleOptionalIdentifier.define("opt");
            var optEmptyId = SingleOptionalIdentifier.define("optEmpty");
            var doc = BoundedDocument.factoryBuilder()
                .register(id, optId, optEmptyId)
                .buildFactory()
                .createDocument("""
                    int: 123
                    opt: 456
                    """);
            assertAll(
                () -> assertEquals(123, doc.get(id, Integer::parseInt).intValue()),
                () -> assertEquals(Optional.of(456), doc.get(optId, Integer::parseInt)),
                () -> assertEquals(Optional.empty(), doc.get(optEmptyId, Integer::parseInt)),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> doc.get((SingleMandatoryIdentifier) null, Integer::parseInt)
                ),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> doc.get((SingleOptionalIdentifier) null, Integer::parseInt)
                ),
                () -> assertThrows(NullPointerException.class, () -> doc.get(id, null)),
                () -> assertThrows(NullPointerException.class, () -> doc.get(optId, null)));
        }

        @Test
        @DisplayName("Multiple values")
        void multipleSub() throws IOException {
            var id = MultipleMandatoryIdentifier.define("list");
            var optId = MultipleOptionalIdentifier.define("optList");
            var optEmptyId = MultipleOptionalIdentifier.define("optEmptyList");
            var doc = BoundedDocument.factoryBuilder()
                .register(id, optId, optEmptyId)
                .buildFactory()
                .createDocument("""
                    list:
                        - 1
                        - 2
                    optList:
                        - 3
                        - 4
                    """);
            assertAll(
                () -> assertEquals(List.of(1, 2), doc.get(id, Integer::parseInt)),
                () -> assertEquals(List.of(3, 4), doc.get(optId, Integer::parseInt).orElseThrow()),
                () -> assertEquals(Optional.empty(), doc.get(optEmptyId, Integer::parseInt)),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> doc.get((MultipleMandatoryIdentifier) null, Integer::parseInt)
                ),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> doc.get((MultipleOptionalIdentifier) null, Integer::parseInt)
                ),
                () -> assertThrows(NullPointerException.class, () -> doc.get(id, null)),
                () -> assertThrows(NullPointerException.class, () -> doc.get(optId, null)));
        }

    }

    @Nested
    @DisplayName("String Accessors")
    final class StringAccessors {

        @Test
        @DisplayName("All types")
        void strings() throws IOException {
            var s = SingleMandatoryIdentifier.define("s");
            var so = SingleOptionalIdentifier.define("so");
            var soe = SingleOptionalIdentifier.define("soe");
            var m = MultipleMandatoryIdentifier.define("m");
            var mo = MultipleOptionalIdentifier.define("mo");
            var moe = MultipleOptionalIdentifier.define("moe");

            var doc = BoundedDocument.factoryBuilder()
                .register(s, so, soe)
                .register(m, mo, moe)
                .buildFactory()
                .createDocument("""
                    s: val
                    so: valOpt
                    m:
                        - a
                        - b
                    mo:
                        - c
                        - d
                    """);
            assertAll(
                () -> assertEquals("val", doc.get(s)),
                () -> assertEquals("valOpt", doc.get(so).orElseThrow()),
                () -> assertEquals(Optional.empty(), doc.get(soe)),
                () -> assertEquals(List.of("a", "b"), doc.get(m)),
                () -> assertEquals(List.of("c", "d"), doc.get(mo).orElseThrow()),
                () -> assertEquals(Optional.empty(), doc.get(moe)));
        }

    }

    @Nested
    @DisplayName("Boolean Accessors")
    final class BooleanAccessors {

        @Test
        @DisplayName("Mandatory boolean")
        void booleans() throws IOException {
            var b1 = SingleMandatoryIdentifier.define("b1");
            var b2 = SingleMandatoryIdentifier.define("b2");
            var doc = BoundedDocument.factoryBuilder()
                .register(b1, b2)
                .buildFactory()
                .createDocument("""
                    b1: true
                    b2: false
                    """);
            assertAll(
                () -> assertTrue(doc.getBoolean(b1)),
                () -> assertFalse(doc.getBoolean(b2)),
                () -> assertThrows(NullPointerException.class, () -> doc.getBoolean(null))
            );
        }

        @Test
        @DisplayName("Boolean or default (empty value)")
        void booleanOrDefault() throws IOException {
            var id = SingleOptionalIdentifier.define("key");
            var factory = BoundedDocument.factoryBuilder().register(id).buildFactory();
            var doc = factory.createDocument("");
            var doc2 = factory.createDocument("key: faLsE");
            assertAll(
                () -> assertTrue(doc.getBooleanOrDefault(id, true)),
                () -> assertFalse(doc.getBooleanOrDefault(id, false)),
                () -> assertFalse(doc2.getBooleanOrDefault(id, true)),
                () -> assertThrows(NullPointerException.class, () -> doc.getBooleanOrDefault(null, true))
            );
        }

    }

    @Nested
    @DisplayName("Primitive Accessors (int, long, double)")
    final class PrimitiveAccessors {

        @Test
        @DisplayName("Ints")
        void ints() throws IOException {
            var sm = SingleMandatoryIdentifier.define("sm");
            var so = SingleOptionalIdentifier.define("so");
            var mm = MultipleMandatoryIdentifier.define("mm");
            var mo = MultipleOptionalIdentifier.define("mo");

            var doc = BoundedDocument.factoryBuilder()
                .register(sm, so, mm, mo)
                .buildFactory()
                .createDocument("""
                    sm: 1
                    so: 2
                    mm:
                        - 3
                        - 4
                    mo:
                        - 5
                        - 6
                    """);
            assertAll(
                () -> assertEquals(1, doc.getInt(sm)),
                () -> assertEquals(2, doc.getInt(so).orElseThrow()),
                () -> assertArrayEquals(new int[]{3, 4}, doc.getInts(mm)),
                () -> assertArrayEquals(new int[]{5, 6}, doc.getInts(mo).orElseThrow()),
                () -> assertThrows(NullPointerException.class, () -> doc.getInt((SingleMandatoryIdentifier) null)),
                () -> assertThrows(NullPointerException.class, () -> doc.getInt((SingleOptionalIdentifier) null)),
                () -> assertThrows(NullPointerException.class, () -> doc.getInts((MultipleMandatoryIdentifier) null)),
                () -> assertThrows(NullPointerException.class, () -> doc.getInts((MultipleOptionalIdentifier) null))
            );
        }

        @Test
        @DisplayName("Longs")
        void longs() throws IOException {
            var sm = SingleMandatoryIdentifier.define("sm");
            var so = SingleOptionalIdentifier.define("so");
            var mm = MultipleMandatoryIdentifier.define("mm");
            var mo = MultipleOptionalIdentifier.define("mo");

            var doc = BoundedDocument.factoryBuilder()
                .register(sm, so, mm, mo)
                .buildFactory()
                .createDocument("""
                    sm: 1
                    so: 2
                    mm:
                        - 3
                        - 4
                    mo:
                        - 5
                        - 6
                    """);
            assertAll(
                () -> assertEquals(1L, doc.getLong(sm)),
                () -> assertEquals(2L, doc.getLong(so).orElseThrow()),
                () -> assertArrayEquals(new long[]{3L, 4L}, doc.getLongs(mm)),
                () -> assertArrayEquals(new long[]{5L, 6L}, doc.getLongs(mo).orElseThrow()),
                () -> assertThrows(NullPointerException.class, () -> doc.getLong((SingleMandatoryIdentifier) null)),
                () -> assertThrows(NullPointerException.class, () -> doc.getLong((SingleOptionalIdentifier) null)),
                () -> assertThrows(NullPointerException.class, () -> doc.getLongs((MultipleMandatoryIdentifier) null)),
                () -> assertThrows(NullPointerException.class, () -> doc.getLongs((MultipleOptionalIdentifier) null)));
        }

        @Test
        @DisplayName("Doubles")
        void doubles() throws IOException {
            var sm = SingleMandatoryIdentifier.define("sm");
            var so = SingleOptionalIdentifier.define("so");
            var mm = MultipleMandatoryIdentifier.define("mm");
            var mo = MultipleOptionalIdentifier.define("mo");

            var doc = BoundedDocument.factoryBuilder()
                .register(sm, so, mm, mo)
                .buildFactory()
                .createDocument("""
                    sm: 1.1
                    so: 2.2
                    mm:
                        - 3.3
                        - 4.4
                    mo:
                        - 5.5
                        - 6.6
                    """);
            assertAll(
                () -> assertEquals(1.1, doc.getDouble(sm)),
                () -> assertEquals(2.2, doc.getDouble(so).orElseThrow()),
                () -> assertArrayEquals(new double[]{3.3, 4.4}, doc.getDoubles(mm)),
                () -> assertArrayEquals(new double[]{5.5, 6.6}, doc.getDoubles(mo).orElseThrow()),
                () -> assertThrows(NullPointerException.class, () -> doc.getDouble((SingleMandatoryIdentifier) null)),
                () -> assertThrows(NullPointerException.class, () -> doc.getDouble((SingleOptionalIdentifier) null)),
                () -> assertThrows(NullPointerException.class, () -> doc.getDoubles((MultipleMandatoryIdentifier) null)),
                () -> assertThrows(NullPointerException.class, () -> doc.getDoubles((MultipleOptionalIdentifier) null))
            );
        }

        @Test
        @DisplayName("Optional primitives (empty)")
        void primitivesEmpty() throws IOException {
            var doc = BoundedDocument.factoryBuilder()
                .register(SingleOptionalIdentifier.define("so"))
                .register(MultipleOptionalIdentifier.define("mo"))
                .buildFactory()
                .createDocument("");
            assertAll(
                () -> assertTrue(doc.getInt(SingleOptionalIdentifier.define("so")).isEmpty()),
                () -> assertTrue(doc.getInts(MultipleOptionalIdentifier.define("mo")).isEmpty()),
                () -> assertTrue(doc.getLong(SingleOptionalIdentifier.define("so")).isEmpty()),
                () -> assertTrue(doc.getLongs(MultipleOptionalIdentifier.define("mo")).isEmpty()),
                () -> assertTrue(doc.getDouble(SingleOptionalIdentifier.define("so")).isEmpty()),
                () -> assertTrue(doc.getDoubles(MultipleOptionalIdentifier.define("mo")).isEmpty()));
        }

    }

    @Nested
    @DisplayName("Iterable")
    final class Iterable {

        @Test
        @DisplayName("Iterator logic")
        void iterator() throws IOException {

            var id1 = SingleMandatoryIdentifier.define("key1");
            var id2 = MultipleMandatoryIdentifier.define("key2");
            var id3 = SingleOptionalIdentifier.define("key3");

            var doc = BoundedDocument.factoryBuilder()
                .register(id1, id2, id3)
                .buildFactory()
                .createDocument("""
                    key1: val
                    key2:
                        - a
                    """);
            var iterator = doc.iterator();
            assertAll(
                () -> assertTrue(iterator.hasNext()),
                () -> assertTrue(iterator.hasNext()),
                () -> {
                    var one = iterator.next();
                    assertEquals("key1", one.key());
                    assertEquals("val", one.value());
                },
                () -> assertTrue(iterator.hasNext()),
                () -> assertTrue(iterator.hasNext()),
                () -> {
                    var two = iterator.next();
                    assertEquals("key2", two.key());
                    assertEquals(Collections.singletonList("a"), two.value());
                },
                () -> assertFalse(iterator::hasNext),
                () -> assertThrows(NoSuchElementException.class, iterator::next),
                () -> assertFalse(iterator::hasNext)
            );
        }

        @Test
        @DisplayName("Defensive programming in Iterator")
        void iteratorIllegalState() throws ReflectiveOperationException {
            var map = Map.of(SingleMandatoryIdentifier.define("key"), new Object());
            var malformedMap = new Reflections.ConstructorArgument<>(Map.class, map);
            var doc = Reflections.instantiateByPrivateConstructor(BoundedDocument.class, malformedMap);
            var it = doc.iterator();
            assertThrows(IllegalStateException.class, it::next);
        }

        @ParameterizedTest(name = "{0}")
        @DisplayName("Random size iterator")
        @MethodSource("io.github.sekelenao.smallyaml.test.util.Randoms#intStreamWithSize5")
        void hugeIterator(int size) throws IOException {
            var factoryBuilder = BoundedDocument.factoryBuilder();
            var yaml = new StringBuilder();
            for(int i = 0; i < size; i++) {
                factoryBuilder.register(SingleOptionalIdentifier.define("key" + i));
                yaml.append("key").append(i)
                    .append(": ")
                    .append(i)
                    .append("\n");
            }
            var doc = factoryBuilder.buildFactory()
                .createDocument(yaml.toString());
            var foundKeys = new HashSet<String>(size);
            var iterator = doc.iterator();
            iterator.forEachRemaining(entry -> foundKeys.add(entry.key()));
            assertAll(
                () -> assertEquals(size, foundKeys.size()),
                () -> assertFalse(iterator::hasNext),
                () -> assertThrows(NoSuchElementException.class, iterator::next)
            );
        }

        @Test
        @DisplayName("Simple stream")
        void simpleStream() throws IOException {
            var doc = BoundedDocument.factoryBuilder()
                .register(SingleMandatoryIdentifier.define("key"))
                .buildFactory()
                .createDocument("key: val");
            var stream = doc.stream();
            assertAll(
                () -> assertFalse(stream.isParallel()),
                () -> {
                    var one = stream.findFirst().orElseThrow();
                    assertEquals("key", one.key());
                },
                () -> assertEquals(1, doc.stream().count())
            );
        }

        @ParameterizedTest(name = "{0}")
        @DisplayName("Random size Stream")
        @MethodSource("io.github.sekelenao.smallyaml.test.util.Randoms#intStreamWithSize5")
        void stream(int size) throws IOException {
            var factoryBuilder = BoundedDocument.factoryBuilder();
            var yaml = new StringBuilder();
            for(int i = 0; i < size; i++) {
                factoryBuilder.register(SingleOptionalIdentifier.define("key" + i));
                yaml.append("key").append(i)
                    .append(": ")
                    .append(i)
                    .append("\n");
            }
            var doc = factoryBuilder.buildFactory()
                .createDocument(yaml.toString());
            assertEquals(size, doc.stream().parallel().count());
        }

        @Test
        @DisplayName("Simple Spliterator")
        void spliterator() throws IOException {
            var doc = BoundedDocument.factoryBuilder()
                .register(SingleMandatoryIdentifier.define("key"))
                .buildFactory()
                .createDocument("key: val");
            var spliterator = doc.spliterator();
            assertAll(
                () -> assertTrue(spliterator.hasCharacteristics(Spliterator.NONNULL)),
                () -> assertTrue(spliterator.hasCharacteristics(Spliterator.IMMUTABLE)),
                () -> assertTrue(spliterator.hasCharacteristics(Spliterator.DISTINCT)),
                () -> assertFalse(spliterator.hasCharacteristics(Spliterator.ORDERED)),
                () -> assertTrue(spliterator.tryAdvance(p -> assertEquals("key", p.key())))
            );
        }

    }

    @Nested
    @DisplayName("Equals, HashCode and toString")
    final class EqualsHashCodeAndToString {

        @Test
        @SuppressWarnings("all")
        @DisplayName("Equals")
        void equals() throws IOException {
            var id = SingleMandatoryIdentifier.define("key");
            var factoryBuilder = BoundedDocument.factoryBuilder()
                .register(id);
            var factory = factoryBuilder.buildFactory();
            var doc1 = factory.createDocument("key: val");
            var doc2 = factory.createDocument("key: val");
            var doc3 = factory.createDocument("key: other");
            var factory2 = factoryBuilder.register(SingleMandatoryIdentifier.define("one"))
                .buildFactory();
            var doc4 = factory2.createDocument("key: val\none: val");
            assertAll(
                () -> assertEquals(doc1, doc2),
                () -> assertEquals(doc2, doc1),
                () -> assertNotEquals(doc1, doc3),
                () -> assertNotEquals(doc3, doc1),
                () -> assertNotEquals(doc1, doc4),
                () -> assertNotEquals(doc1, null),
                () -> assertNotEquals(doc1, new Object()),
                () -> assertEquals("{key: val}", doc1.toString())
            );
        }

        @Test
        @DisplayName("HashCode")
        void hashCodeTest() throws IOException {
            var id = SingleMandatoryIdentifier.define("key");
            var factory = BoundedDocument.factoryBuilder()
                .register(id)
                .buildFactory();
            var doc1 = factory.createDocument("key: val");
            var doc2 = factory.createDocument("key: val");
            var doc3 = factory.createDocument("key: other");
            assertAll(
                () -> assertEquals(doc1.hashCode(), doc2.hashCode()),
                () -> assertNotEquals(doc1.hashCode(), doc3.hashCode())
            );
        }

        @Test
        @DisplayName("To string")
        void toStringTest() throws IOException {
            var id = SingleOptionalIdentifier.define("key");
            var factoryBuilder = BoundedDocument.factoryBuilder()
                .register(id);
            var factory = factoryBuilder.buildFactory();
            var doc1 = factory.createDocument("key: val");
            var doc2 = factory.createDocument("unknown: val");
            var doc3 = factory.createDocument("key: other");
            var factory2 = factoryBuilder.register(SingleMandatoryIdentifier.define("one"))
                .buildFactory();
            var doc5 = factory2.createDocument("one: val");
            assertAll(
                () -> assertEquals("{key: val}", doc1.toString()),
                () -> assertEquals("{}", doc2.toString()),
                () -> assertEquals("{key: other}", doc3.toString()),
                () -> assertEquals("{one: val}", doc5.toString())
            );
        }

    }

}
