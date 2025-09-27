package io.github.sekelenao.smallyaml.test.internal.collection;

import io.github.sekelenao.smallyaml.internal.collection.ValueList;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(TestingTag.INTERNAL)
@Tag(TestingTag.COLLECTION)
final class ValueListTest {

    @Test
    @DisplayName("Assertions")
    void assertions() {
        assertThrows(NullPointerException.class, () -> new ValueList(null));
    }

    @Test
    @DisplayName("Initial size is correct")
    void initialSizeIsCorrect() {
        var valueList = new ValueList("first");
        assertAll(
                () -> assertEquals(1, valueList.size()),
                () -> assertEquals("first", valueList.get(0))
        );
    }

    @Test
    @DisplayName("Add assertions")
    void addAssertions() {
        var valueList = new ValueList("first");
        assertThrows(NullPointerException.class, () -> valueList.add(null));
    }

    @ParameterizedTest(name = "{displayName} ({0})")
    @MethodSource("io.github.sekelenao.smallyaml.test.util.Randoms#intStreamWithSize50")
    @DisplayName("Adding a lot of value")
    void addingALotOfValue(int amount) {
        var valueList = new ValueList("first");
        for (int i = 1; i < amount; i++) {
            valueList.add("value-" + i);
            assertEquals(i + 1, valueList.size());
            assertEquals("value-" + i, valueList.get(i));
        }
        assertEquals("first", valueList.get(0));
        assertEquals(amount == 0 ? 1 : amount, valueList.size());
        assertEquals("value-" + (amount - 1), valueList.get(valueList.size() - 1));
    }

    @Test
    @DisplayName("Get assertions")
    void getAssertions() {
        var list = new ValueList("first");
        assertAll(
                () -> assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> list.get(2))
        );
    }

    @Test
    @DisplayName("Iterator is working")
    void iteratorIsWorking() {
        var list = new ValueList("0");
        list.add("1");
        list.add("2");
        list.add("3");
        var iterator = list.iterator();
        for (int i = 0; i < list.size(); i++) {
            assertEquals(String.valueOf(i), iterator.next());
        }
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    @DisplayName("Iterator concurrent modification")
    void iteratorConcurrentModification() {
        var list = new ValueList("0");
        var iterator = list.iterator();
        list.add("forbidden");
        assertThrows(ConcurrentModificationException.class, iterator::hasNext);
        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    @DisplayName("For each is working")
    void forEachIsWorking() {
        var list = new ValueList("0");
        list.add("1");
        list.add("2");
        list.add("3");
        for (int i = 0; i < list.size(); i++) {
            assertEquals(String.valueOf(i), list.get(i));
        }
    }

    @Test
    @DisplayName("List view stream is working")
    void streamIsWorking() {
        var valueList = new ValueList("0");
        for (int i = 1; i < 10_000; i++) {
            valueList.add(String.valueOf(i));
        }
        var expectedList = IntStream.range(0, 10_000).boxed().toList();
        var actualList = valueList.asListView().stream().map(Integer::parseInt).toList();
        assertAll(
            () -> assertEquals(actualList, expectedList),
            () -> assertEquals(10_000, actualList.size())
        );
    }

    @Test
    @DisplayName("List view stream is working with parallel")
    void streamIsWorkingWithParallel() {
        var valueList = new ValueList("0");
        for (int i = 1; i < 10_000; i++) {
            valueList.add(String.valueOf(i));
        }
        var expectedList = IntStream.range(0, 10_000).boxed().toList();
        var actualList = valueList.asListView().parallelStream().map(Integer::parseInt).toList();
        assertAll(
            () -> assertEquals(actualList, expectedList),
            () -> assertEquals(10_000, actualList.size())
        );
    }

    @ParameterizedTest(name = "{displayName} ({0})")
    @MethodSource("io.github.sekelenao.smallyaml.test.util.Randoms#intStreamWithSize5")
    @DisplayName("List view parallel stream with random size")
    void streamParallelRandomSize(int size) {
        var valueList = new ValueList("0");
        for (int i = 1; i < size; i++) {
            valueList.add(String.valueOf(i));
        }
        var expectedList = IntStream.range(0, size).boxed().toList();
        var actualList = valueList.asListView().parallelStream().map(Integer::parseInt).toList();
        assertAll(
            () -> assertEquals(actualList, expectedList),
            () -> assertEquals(size, actualList.size())
        );
    }

    @Test
    @DisplayName("List view parallel stream is really parallel")
    void streamParallel() {
        var valueList = new ValueList("0");
        for (int i = 1; i < 10_000; i++) {
            valueList.add(String.valueOf(i));
        }
        var thread = Thread.currentThread();
        var otherThreadCount = valueList.asListView().stream()
            .parallel()
            .mapToInt(ignored -> thread != Thread.currentThread() ? 1 : 0)
            .sum();
        assertNotEquals(0, otherThreadCount);
    }

    @Test
    @DisplayName("List view short stream")
    void shortStream() {
        var valueList = new ValueList("0");
        for (int i = 1; i < 3; i++) {
            valueList.add(String.valueOf(i));
        }
        var expectedList = IntStream.range(0, 3).boxed().toList();
        var actualList = valueList.asListView().stream().map(Integer::parseInt).toList();
        assertAll(
            () -> assertEquals(actualList, expectedList),
            () -> assertEquals(3, actualList.size())
        );
    }

    @Test
    @DisplayName("List view spliterator trySplit returns null when middle == index")
    void spliteratorTrySplitReturnsNullWhenMiddleEqualsIndex() {
        var valueList = new ValueList("a");
        valueList.add("b");
        valueList.add("c");
        var spliterator = valueList.asListView().spliterator();
        assertAll(
            () -> assertTrue(spliterator.tryAdvance(s -> {})),
            () -> assertTrue(spliterator.tryAdvance(s -> {})),
            () -> {
                var split = spliterator.trySplit();
                // index = 2, end = 3 -> middle = (2+3)/2 = 2 → middle == index
                assertNull(split, "Expected trySplit to return null when middle == index");
            },
            () -> assertTrue(spliterator.tryAdvance(s -> {}), "Last element should still be consumable"),
            () -> assertFalse(spliterator.tryAdvance(s -> {}), "No more elements should be left")
        );
    }

    @Test
    @DisplayName("List view trySplit returns null when middle == index (one element left and total >= 1024)")
    void trySplitReturnsNullWhenMiddleEqualsIndex() {
        final int size = 1024;
        var valueList = new ValueList("0");
        for (int i = 1; i < size; i++) {
            valueList.add(String.valueOf(i));
        }
        var spliterator = valueList.asListView().spliterator();
        // Consume size - 1 elements so that only one element remains (index == end - 1)
        for (int i = 0; i < size - 1; i++) {
            assertTrue(spliterator.tryAdvance(e -> {}), "tryAdvance should succeed while elements remain");
        }
        // Now index = end - 1 -> middle == index -> trySplit() must return null
        var splitWhenOneLeft = spliterator.trySplit();
        assertAll(
            () -> assertNull(splitWhenOneLeft, "trySplit must return null when middle == index (one element left)"),
            // The last element must still be consumable, then nothing remains
            () -> assertTrue(spliterator.tryAdvance(e -> {}), "Last element should still be consumable"),
            () -> assertFalse(spliterator.tryAdvance(e -> {}), "No more elements should remain")
        );
    }

    @Test
    @DisplayName("As array of booleans")
    void asArrayOfStrictBooleans() {
        var valueList = new ValueList("True");
        valueList.add("False");
        valueList.add("true");
        valueList.add("false");
        valueList.add("TRuE");
        var wrongValueList = new ValueList("NotABoolean");
        assertAll(
            () -> assertArrayEquals(new boolean[]{ true, false, true, false, true }, valueList.asArrayOfStrictBooleans()),
            () -> assertThrows(IllegalArgumentException.class, wrongValueList::asArrayOfStrictBooleans)
        );
    }

}