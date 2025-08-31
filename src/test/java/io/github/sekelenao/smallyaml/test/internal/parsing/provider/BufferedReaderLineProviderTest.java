package io.github.sekelenao.smallyaml.test.internal.parsing.provider;

import io.github.sekelenao.smallyaml.api.line.provider.BufferedReaderLineProvider;
import io.github.sekelenao.smallyaml.internal.parsing.line.EmptyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.KeyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.KeyValueLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.ListValueLine;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import io.github.sekelenao.smallyaml.test.util.constant.CorrectTestDocument;
import io.github.sekelenao.smallyaml.test.util.resource.TestResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@Tag(TestingTag.PARSING)
final class BufferedReaderLineProviderTest {

    @Test
    @DisplayName("Assertions")
    void assertions() {
        assertThrows(NullPointerException.class, () -> {
            try(var ignored = new BufferedReaderLineProvider(null)) {
                fail("Should not reach here");
            }
        });
    }

    @Test
    @DisplayName("Complete config parsing")
    void completeConfigParsing() throws IOException, URISyntaxException {
        int emptyLineCount = 0;
        int keyLineCount = 0;
        int listValueLineCount = 0;
        int keyValueLineCount = 0;
        var bufferedReader = Files.newBufferedReader(TestResource.find(CorrectTestDocument.SIMPLE));
        try (var provider = new BufferedReaderLineProvider(bufferedReader)){
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
        assertEquals(3, emptyLineCount);
        assertEquals(5, keyLineCount);
        assertEquals(3, listValueLineCount);
        assertEquals(2, keyValueLineCount);
    }

}