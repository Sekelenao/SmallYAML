package io.github.sekelenao.smallyaml.test.api.line.provider;

import io.github.sekelenao.smallyaml.api.line.provider.BufferedReaderLineProvider;
import io.github.sekelenao.smallyaml.test.util.constant.RegularTestDocument;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import io.github.sekelenao.smallyaml.test.util.line.provider.LineProviderTester;
import io.github.sekelenao.smallyaml.test.util.resource.TestResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@Tag(TestingTag.API)
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
        var bufferedReader = Files.newBufferedReader(TestResource.find(RegularTestDocument.TEST_DOCUMENT));
        try (var provider = new BufferedReaderLineProvider(bufferedReader)){
            var lineProviderTester = LineProviderTester.forFollowing(provider);
            assertAll(
                () -> lineProviderTester.ensureEmptyLinesAmount(RegularTestDocument.EMPTY_LINE_COUNT),
                () -> lineProviderTester.ensureKeyLinesAmount(RegularTestDocument.KEY_LINE_COUNT),
                () -> lineProviderTester.ensureListValueLinesAmount(RegularTestDocument.LIST_VALUE_LINE_COUNT),
                () -> lineProviderTester.ensureKeyValueLinesAmount(RegularTestDocument.KEY_VALUE_LINE_COUNT)
            );
        }
    }

}