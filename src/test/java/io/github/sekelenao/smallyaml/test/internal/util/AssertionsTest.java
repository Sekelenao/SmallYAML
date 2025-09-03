package io.github.sekelenao.smallyaml.test.internal.util;

import io.github.sekelenao.smallyaml.internal.util.Assertions;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag(TestingTag.INTERNAL)
final class AssertionsTest {
    
    @ParameterizedTest
    @ValueSource(ints = {-100, -1})
    @DisplayName("Positive or zero wrong input")
    void positiveOrZero(int value) {
        assertThrows(IllegalArgumentException.class, () -> Assertions.isPositiveOrZero(value));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100})
    @DisplayName("Positive or zero good input")
    void positiveOrZeroGood(int value) {
        assertDoesNotThrow(() -> Assertions.isPositiveOrZero(value));
    }

}
