package io.github.sekelenao.smallyaml.test.api.document.property;

import io.github.sekelenao.smallyaml.api.document.property.MultipleMandatoryIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.document.property.SingleMandatoryIdentifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("MultipleMandatoryIdentifier")
final class MultipleMandatoryIdentifierTest {

    @Test
    @DisplayName("Define and basic properties")
    void define() {
        var id = MultipleMandatoryIdentifier.define("Key");
        assertAll(
            () -> assertEquals("key", id.key(), "Key should be lowercase"),
            () -> assertEquals(Property.Type.MULTIPLE, id.type()),
            () -> assertEquals(Property.Presence.MANDATORY, id.presence())
        );
    }

    @Test
    @DisplayName("Define with null key")
    void defineNull() {
        assertThrows(NullPointerException.class, () -> MultipleMandatoryIdentifier.define(null));
    }

    @Test
    @SuppressWarnings("all")
    @DisplayName("equals and hashCode")
    void equalsHashCode() {
        var id1 = MultipleMandatoryIdentifier.define("key");
        var id2 = MultipleMandatoryIdentifier.define("KEY");
        var id3 = MultipleMandatoryIdentifier.define("other");
        var id4 = SingleMandatoryIdentifier.define("key");

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
