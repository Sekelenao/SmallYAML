package io.github.sekelenao.smallyaml.test.internal.util;

import io.github.sekelenao.smallyaml.internal.util.Assertions;
import io.github.sekelenao.smallyaml.test.util.Reflections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class AssertionsTest {

    @Test
    @DisplayName("Private constructor throws AssertionError")
    void privateConstructorTest() {
        assertAll(() -> Reflections.ensureIsUtilityClass(Assertions.class));
    }

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
