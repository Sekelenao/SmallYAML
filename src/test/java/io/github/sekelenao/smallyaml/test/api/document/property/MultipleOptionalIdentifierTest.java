package io.github.sekelenao.smallyaml.test.api.document.property;

import io.github.sekelenao.smallyaml.api.document.property.MultipleOptionalIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.document.property.SingleOptionalIdentifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("MultipleOptionalIdentifier")
final class MultipleOptionalIdentifierTest {

    @Test
    @DisplayName("Define and basic properties")
    void define() {
        var id = MultipleOptionalIdentifier.define("Key");
        assertAll(
            () -> assertEquals("key", id.key(), "Key should be lowercase"),
            () -> assertEquals(Property.Type.MULTIPLE, id.type()),
            () -> assertEquals(Property.Presence.OPTIONAL, id.presence())
        );
    }

    @Test
    @DisplayName("Define with null key")
    void defineNull() {
        assertThrows(NullPointerException.class, () -> MultipleOptionalIdentifier.define(null));
    }

    @Test
    @SuppressWarnings("all")
    @DisplayName("equals and hashCode")
    void equalsHashCode() {
        var id1 = MultipleOptionalIdentifier.define("key");
        var id2 = MultipleOptionalIdentifier.define("KEY");
        var id3 = MultipleOptionalIdentifier.define("other");
        var id4 = SingleOptionalIdentifier.define("key");

        assertAll(
            () -> assertEquals(id1, id2),
            () -> assertEquals(id1.hashCode(), id2.hashCode()),
            () -> assertNotEquals(id1, id3),
            () -> assertNotEquals(id1.hashCode(), id3.hashCode()),
            () -> assertEquals(id1, id4),
            () -> assertNotEquals(id1, new Object()),
            () -> assertNotEquals(id1, null)
        );
    }
}
