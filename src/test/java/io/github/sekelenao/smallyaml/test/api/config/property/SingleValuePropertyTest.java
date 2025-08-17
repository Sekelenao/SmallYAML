package io.github.sekelenao.smallyaml.test.api.config.property;

import io.github.sekelenao.smallyaml.api.config.property.SingleValueProperty;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag(TestingTag.COLLECTION)
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