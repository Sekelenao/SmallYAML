package io.github.sekelenao.smallyaml.test.api.document;

import io.github.sekelenao.smallyaml.api.document.BoundedDocument;
import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class BoundedDocumentFactoryBuilderTest {

    private static final class Foo {}

    @Test
    @DisplayName("Assertions")
    void assertions() {
        var builder = BoundedDocument.factoryBuilder();
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> builder.scan(null)),
            () -> assertThrows(NullPointerException.class, () -> builder.register((PropertyIdentifier) null)),
            () -> assertThrows(NullPointerException.class, () -> builder.register(null)),
            () -> assertThrows(
                NullPointerException.class,
                () -> builder.scan(Foo.class, (Class<?>[]) null)
            )
        );
    }

}
