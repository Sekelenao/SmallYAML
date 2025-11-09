package io.github.sekelenao.smallyaml.test.api.document.property;

import io.github.sekelenao.smallyaml.api.document.property.MultipleValuesProperty;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag(TestingTag.API)
@Tag(TestingTag.COLLECTION)
final class MultipleValuesPropertyTest {

    @Test
    @DisplayName("Assertions")
    void assertions() {
        List<String> emptyList = Collections.emptyList();
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> new MultipleValuesProperty(null, emptyList)),
            () -> assertThrows(NullPointerException.class, () -> new MultipleValuesProperty("key", null))
        );
    }

    @Test
    @DisplayName("Getters are working")
    void gettersAreWorking() {
        var multipleValuesProperty = new MultipleValuesProperty("key", List.of("1", "2", "3"));
        assertAll(
            () -> assertEquals("key", multipleValuesProperty.key()),
            () -> assertEquals("1", multipleValuesProperty.value().getFirst()),
            () -> assertEquals("2", multipleValuesProperty.value().get(1)),
            () -> assertEquals("3", multipleValuesProperty.value().getLast())
        );
    }

}
