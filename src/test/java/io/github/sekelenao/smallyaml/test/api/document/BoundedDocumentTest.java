package io.github.sekelenao.smallyaml.test.api.document;

import io.github.sekelenao.smallyaml.api.document.BoundedDocument;
import io.github.sekelenao.smallyaml.api.document.property.MultipleMandatoryIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.MultipleOptionalIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleMandatoryIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleOptionalIdentifier;
import io.github.sekelenao.smallyaml.api.exception.document.NotRegisteredIdentifierException;
import io.github.sekelenao.smallyaml.internal.collection.EmptyValue;
import io.github.sekelenao.smallyaml.internal.collection.ValueList;
import io.github.sekelenao.smallyaml.test.util.ExceptionsTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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
            var id = SingleMandatoryIdentifier.define("unknown");
            var optId = SingleOptionalIdentifier.define("unknownOpt");
            var multId = MultipleMandatoryIdentifier.define("unknownMult");
            var multOptId = MultipleOptionalIdentifier.define("unknownMultOpt");
            assertAll(
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(id),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(optId),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(multId),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(multOptId),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(id, Integer::parseInt),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(optId, Integer::parseInt),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(multId, Integer::parseInt),
                    "Not registered identifier"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    NotRegisteredIdentifierException.class,
                    () -> doc.get(multOptId, Integer::parseInt),
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
            var doc = BoundedDocument.factoryBuilder()
                .register(id)
                .buildFactory()
                .createDocument("");
            assertAll(
                () -> assertTrue(doc.getBooleanOrDefault(id, true)),
                () -> assertFalse(doc.getBooleanOrDefault(id, false)),
                () -> assertThrows(NullPointerException.class, () -> doc.getBooleanOrDefault(null, true))
            );
        }

    }

    /*

    @Nested
    @DisplayName("Primitive Accessors (Int, Long, Double)")
    final class PrimitiveAccessors {

        @Test
        @DisplayName("Ints")
        void ints() {
            var sm = SingleMandatoryIdentifier.define("sm");
            var so = SingleOptionalIdentifier.define("so");
            var mm = MultipleMandatoryIdentifier.define("mm");
            var mo = MultipleOptionalIdentifier.define("mo");

            var props = new HashMap<io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier, Object>();
            props.put(sm, "1");
            props.put(so, "2");
            props.put(mm, new ValueList("3").add("4"));
            props.put(mo, new ValueList("5").add("6"));

            var doc = createDocument(props);

            assertAll(
                () -> assertEquals(1, doc.getInt(sm)),
                () -> assertEquals(2, doc.getInt(so).getAsInt()),
                () -> assertArrayEquals(new int[]{3, 4}, doc.getInts(mm)),
                () -> assertArrayEquals(new int[]{5, 6}, doc.getInts(mo).orElseThrow()),
                () -> {
                    assertThrows(NullPointerException.class,
                        () -> doc.getInt((SingleMandatoryIdentifier) null));
                },
                () -> {
                    assertThrows(NullPointerException.class,
                        () -> doc.getInt((SingleOptionalIdentifier) null));
                },
                () -> {
                    assertThrows(NullPointerException.class,
                        () -> doc.getInts((MultipleMandatoryIdentifier) null));
                },
                () -> {
                    assertThrows(NullPointerException.class,
                        () -> doc.getInts((MultipleOptionalIdentifier) null));
                });
        }

        @Test
        @DisplayName("Longs")
        void longs() {
            var sm = SingleMandatoryIdentifier.define("sm");
            var so = SingleOptionalIdentifier.define("so");
            var mm = MultipleMandatoryIdentifier.define("mm");
            var mo = MultipleOptionalIdentifier.define("mo");

            var props = new HashMap<io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier, Object>();
            props.put(sm, "1");
            props.put(so, "2");
            props.put(mm, new ValueList("3").add("4"));
            props.put(mo, new ValueList("5").add("6"));

            var doc = createDocument(props);

            assertAll(
                () -> assertEquals(1L, doc.getLong(sm)),
                () -> assertEquals(2L, doc.getLong(so).getAsLong()),
                () -> assertArrayEquals(new long[]{3L, 4L}, doc.getLongs(mm)),
                () -> assertArrayEquals(new long[]{5L, 6L}, doc.getLongs(mo).orElseThrow()),
                () -> {
                    assertThrows(NullPointerException.class,
                        () -> doc.getLong((SingleMandatoryIdentifier) null));
                },
                () -> {
                    assertThrows(NullPointerException.class,
                        () -> doc.getLong((SingleOptionalIdentifier) null));
                },
                () -> {
                    assertThrows(NullPointerException.class,
                        () -> doc.getLongs((MultipleMandatoryIdentifier) null));
                },
                () -> {
                    assertThrows(NullPointerException.class,
                        () -> doc.getLongs((MultipleOptionalIdentifier) null));
                });
        }

        @Test
        @DisplayName("Doubles")
        void doubles() {
            var sm = SingleMandatoryIdentifier.define("sm");
            var so = SingleOptionalIdentifier.define("so");
            var mm = MultipleMandatoryIdentifier.define("mm");
            var mo = MultipleOptionalIdentifier.define("mo");

            var props = new HashMap<io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier, Object>();
            props.put(sm, "1.1");
            props.put(so, "2.2");
            props.put(mm, new ValueList("3.3").add("4.4"));
            props.put(mo, new ValueList("5.5").add("6.6"));

            var doc = createDocument(props);

            assertAll(
                () -> assertEquals(1.1, doc.getDouble(sm)),
                () -> assertEquals(2.2, doc.getDouble(so).getAsDouble()),
                () -> assertArrayEquals(new double[]{3.3, 4.4}, doc.getDoubles(mm)),
                () -> assertArrayEquals(new double[]{5.5, 6.6},
                    doc.getDoubles(mo).orElseThrow()),
                () -> {
                    assertThrows(NullPointerException.class,
                        () -> doc.getDouble((SingleMandatoryIdentifier) null));
                },
                () -> {
                    assertThrows(NullPointerException.class,
                        () -> doc.getDouble((SingleOptionalIdentifier) null));
                },
                () -> {
                    assertThrows(NullPointerException.class, () -> doc
                        .getDoubles((MultipleMandatoryIdentifier) null));
                },
                () -> {
                    assertThrows(NullPointerException.class, () -> doc
                        .getDoubles((MultipleOptionalIdentifier) null));
                });
        }

        @Test
        @DisplayName("Optional primitives (empty)")
        void primitivesEmpty() {
            var doc = createDocument(Map.of(
                SingleOptionalIdentifier.define("so"), EmptyValue.INSTANCE,
                MultipleOptionalIdentifier.define("mo"), EmptyValue.INSTANCE));

            assertAll(
                () -> assertTrue(doc.getInt(SingleOptionalIdentifier.define("so")).isEmpty()),
                () -> assertTrue(
                    doc.getInts(MultipleOptionalIdentifier.define("mo")).isEmpty()),
                () -> assertTrue(doc.getLong(SingleOptionalIdentifier.define("so")).isEmpty()),
                () -> assertTrue(doc.getLongs(MultipleOptionalIdentifier.define("mo"))
                    .isEmpty()),
                () -> assertTrue(
                    doc.getDouble(SingleOptionalIdentifier.define("so")).isEmpty()),
                () -> assertTrue(doc.getDoubles(MultipleOptionalIdentifier.define("mo"))
                    .isEmpty()));
        }

    }

    @Nested
    @DisplayName("Iterable and Standard Methods")
    final class IterableAndStandard {

        @Test
        @DisplayName("Iterator logic")
        void iterator() {
            var id1 = SingleMandatoryIdentifier.define("key1");
            var id2 = MultipleMandatoryIdentifier.define("key2");
            var id3 = SingleOptionalIdentifier.define("key3"); // EmptyValue

            var props = new HashMap<io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier, Object>();
            props.put(id1, "val");
            props.put(id2, new ValueList("a"));
            props.put(id3, EmptyValue.INSTANCE);

            var doc = createDocument(props);

            var list = doc.stream().toList();
            assertAll(
                () -> assertEquals(2, list.size()),
                () -> assertTrue(list.stream().anyMatch(p -> p.key().equals("key1"))),
                () -> assertTrue(list.stream().anyMatch(p -> p.key().equals("key2"))),
                () -> {
                    var it = doc.iterator();
                    it.next();
                    it.next();
                    assertThrows(NoSuchElementException.class, it::next);
                },
                () -> assertNotNull(doc.spliterator()));
        }

        @Test
        @DisplayName("Standard overrides")
        void standard() {
            var id = SingleMandatoryIdentifier.define("key");
            var doc1 = createDocument(Map.of(id, "val"));
            var doc2 = createDocument(Map.of(id, "val"));
            var doc3 = createDocument(Map.of(id, "other"));

            assertAll(
                () -> assertEquals(doc1, doc2),
                () -> assertNotEquals(doc1, doc3),
                () -> assertNotEquals(doc1, null),
                () -> assertNotEquals(doc1, new Object()),
                () -> assertEquals(doc1.hashCode(), doc2.hashCode()),
                () -> assertTrue(doc1.toString().contains("key: val")));
        }

        @Test
        @DisplayName("Defensive programming (IllegalStateException in Iterator)")
        @SuppressWarnings("unchecked")
        void iteratorIllegalState() {
            var props = new HashMap<io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier, Object>();
            props.put(SingleMandatoryIdentifier.define("bad"), 123); // Not a String or ValueList

            var doc = createDocument(props);
            var it = doc.iterator();
            assertThrows(IllegalStateException.class, it::next);
        }

    }

     */

}
