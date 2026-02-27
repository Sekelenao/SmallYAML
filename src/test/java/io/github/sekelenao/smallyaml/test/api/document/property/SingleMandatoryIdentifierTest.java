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

@DisplayName("SingleMandatoryIdentifier")
final class SingleMandatoryIdentifierTest {

    @Test
    @DisplayName("Define and basic properties")
    void define() {
        var id = SingleMandatoryIdentifier.define("Key");
        assertAll(
            () -> assertEquals("key", id.key(), "Key should be lowercase"),
            () -> assertEquals(Property.Type.SINGLE, id.type()),
            () -> assertEquals(Property.Presence.MANDATORY, id.presence())
        );
    }

    @Test
    @DisplayName("Define with null key")
    void defineNull() {
        assertThrows(NullPointerException.class, () -> SingleMandatoryIdentifier.define(null));
    }

    @Test
    @SuppressWarnings("all")
    @DisplayName("equals and hashCode")
    void equalsHashCode() {
        var id1 = SingleMandatoryIdentifier.define("key");
        var id2 = SingleMandatoryIdentifier.define("KEY");
        var id3 = SingleMandatoryIdentifier.define("other");
        var id4 = MultipleMandatoryIdentifier.define("key");

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
