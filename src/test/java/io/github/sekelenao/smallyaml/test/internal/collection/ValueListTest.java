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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @MethodSource("io.github.sekelenao.smallyaml.test.util.TestUtilities#intProvider")
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
        ValueList list = new ValueList("first");
        assertAll(
                () -> assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> list.get(2))
        );
    }

    @Test
    @DisplayName("Iterator is working")
    void iteratorIsWorking() {
        ValueList list = new ValueList("0");
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
        ValueList list = new ValueList("0");
        var iterator = list.iterator();
        list.add("forbidden");
        assertThrows(ConcurrentModificationException.class, iterator::hasNext);
        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    @DisplayName("For each is working")
    void forEachIsWorking() {
        ValueList list = new ValueList("0");
        list.add("1");
        list.add("2");
        list.add("3");
        for (int i = 0; i < list.size(); i++) {
            assertEquals(String.valueOf(i), list.get(i));
        }
    }

}