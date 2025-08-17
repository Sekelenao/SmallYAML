package io.github.sekelenao.smallyaml.test.api.config;

import io.github.sekelenao.smallyaml.api.config.PermissiveConfig;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag(TestingTag.COLLECTION)
final class PermissiveConfigTest {

    @Test
    @DisplayName("Add assertions")
    void addAssertions() {
        var config = new PermissiveConfig();
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> config.add(null, "Ok")),
            () -> assertThrows(NullPointerException.class, () -> config.add("key", null))
        );
    }

}