package io.github.sekelenao.smallyaml.test.api.document;

import io.github.sekelenao.smallyaml.api.document.PermissiveDocument;
import io.github.sekelenao.smallyaml.api.parsing.line.provider.BufferedReaderLineProvider;
import io.github.sekelenao.smallyaml.test.util.TestUtilities;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag(TestingTag.COLLECTION)
final class PermissiveDocumentTest {

    @Test
    @DisplayName("PermissiveDocument assertions")
    void assertions(){
        assertThrows(NullPointerException.class, () -> PermissiveDocument.from(null));
    }

    @Test
    @DisplayName("Parsing fake simple config is working")
    void parsingFakeSimpleConfig() throws URISyntaxException, IOException {
        var file = TestUtilities.findResource("fake-simple-config.yaml");
        try(var bufferedReaderLineProvider = new BufferedReaderLineProvider(Files.newBufferedReader(file))) {
            var document = PermissiveDocument.from(bufferedReaderLineProvider);
            assertAll(
                () -> assertEquals("three", document.getAsStringOrThrows("one.two.three")),
                () -> assertEquals(List.of("five", "six", "seven"), document.getAsListOrThrows("one.four")),
                () -> TestUtilities.checkAmountOfEachPropertyType(document, 2, 1)
            );
        }
    }

}