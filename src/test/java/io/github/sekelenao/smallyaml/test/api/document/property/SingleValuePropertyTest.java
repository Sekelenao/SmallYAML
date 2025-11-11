package io.github.sekelenao.smallyaml.test.api.document.property;

import io.github.sekelenao.smallyaml.api.document.property.SingleValueProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class SingleValuePropertyTest {

    @Test
    @DisplayName("Assertions")
    void assertions() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> new SingleValueProperty(null, "value")),
            () -> assertThrows(NullPointerException.class, () -> new SingleValueProperty("key", null))
        );
    }

    @Test
    @DisplayName("Getters are working")
    void gettersAreWorking() {
        var singleValueProperty = new SingleValueProperty("key", "value");
        assertAll(
            () -> assertEquals("key", singleValueProperty.key()),
            () -> assertEquals("value", singleValueProperty.value())
        );
    }

}