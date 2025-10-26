package io.github.sekelenao.smallyaml.test.api.line.provider;

import io.github.sekelenao.smallyaml.api.line.provider.BufferedReaderLineProvider;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import io.github.sekelenao.smallyaml.test.util.line.provider.LineProviderTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

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
            try(var ignored = BufferedReaderLineProvider.with(null)) {
                fail("Should not reach here");
            }
        });
    }

    @Test
    @DisplayName("Complete config parsing")
    void completeConfigParsing() throws IOException {
        var content = """
        # Single value
        single-value: value
        
        #Multiple values
        multiple:
            values:
                - 1
                - 2
                - 3
        """;
        var bufferedReader = new BufferedReader(new StringReader(content));
        try (var provider = BufferedReaderLineProvider.with(bufferedReader)) {
            var lineProviderTester = LineProviderTester.forFollowing(provider);
            assertAll(
                () -> lineProviderTester.ensureEmptyLinesAmount(3),
                () -> lineProviderTester.ensureKeyLinesAmount(2),
                () -> lineProviderTester.ensureListValueLinesAmount(3),
                () -> lineProviderTester.ensureKeyValueLinesAmount(1)
            );
        }
    }

}