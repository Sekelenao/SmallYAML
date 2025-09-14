package io.github.sekelenao.smallyaml.test.api.line.provider;

import io.github.sekelenao.smallyaml.api.line.provider.InputStreamLineProvider;
import io.github.sekelenao.smallyaml.internal.parsing.line.EmptyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.KeyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.KeyValueLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.ListValueLine;
import io.github.sekelenao.smallyaml.test.util.constant.RegularTestDocument;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import io.github.sekelenao.smallyaml.test.util.resource.TestResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@Tag(TestingTag.API)
@Tag(TestingTag.PARSING)
final class InputStreamLineProviderTest {

    @Test
    @DisplayName("Assertions")
    void assertions() {
        assertThrows(NullPointerException.class, () -> {
            try(var ignored = new InputStreamLineProvider(null)) {
                fail("Should not reach here");
            }
        });
    }

    @Test
    @DisplayName("Complete config parsing")
    void completeConfigParsing() throws IOException {
        int emptyLineCount = 0;
        int keyLineCount = 0;
        int listValueLineCount = 0;
        int keyValueLineCount = 0;
        var inputStream = TestResource.asInputStream(RegularTestDocument.TEST_DOCUMENT);
        try (var provider = new InputStreamLineProvider(inputStream)){
            var line = provider.nextLine();
            while (line.isPresent()) {
                switch (line.get()){
                    case EmptyLine ignored -> emptyLineCount++;
                    case KeyLine ignored -> keyLineCount++;
                    case ListValueLine ignored -> listValueLineCount++;
                    case KeyValueLine ignored -> keyValueLineCount++;
                }
                line = provider.nextLine();
            }
        }
        assertEquals(RegularTestDocument.EMPTY_LINE_COUNT, emptyLineCount);
        assertEquals(RegularTestDocument.KEY_LINE_COUNT, keyLineCount);
        assertEquals(RegularTestDocument.LIST_VALUE_LINE_COUNT, listValueLineCount);
        assertEquals(RegularTestDocument.KEY_VALUE_LINE_COUNT, keyValueLineCount);
    }

}