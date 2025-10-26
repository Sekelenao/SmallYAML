package io.github.sekelenao.smallyaml.test.api.line.provider;

import io.github.sekelenao.smallyaml.api.line.provider.InputStreamLineProvider;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import io.github.sekelenao.smallyaml.test.util.line.provider.LineProviderTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@Tag(TestingTag.API)
@Tag(TestingTag.PARSING)
final class InputStreamLineProviderTest {

    @Test
    @DisplayName("Assertions")
    void assertions() {
        var emptyInputStream = new ByteArrayInputStream(new byte[0]);
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> {
                try (var ignored = InputStreamLineProvider.with(null)) {
                    fail("Should not reach here");
                }
            }),
            () -> assertThrows(NullPointerException.class, () -> {
                try (var ignored = InputStreamLineProvider.with(emptyInputStream, null)) {
                    fail("Should not reach here");
                }
            })
        );
    }

    @Test
    @DisplayName("Complete config parsing with UTF_8 constructor")
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
        var inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        try (var provider = InputStreamLineProvider.with(inputStream)) {
            var lineProviderTester = LineProviderTester.forFollowing(provider);
            assertAll(
                () -> lineProviderTester.ensureEmptyLinesAmount(3),
                () -> lineProviderTester.ensureKeyLinesAmount(2),
                () -> lineProviderTester.ensureListValueLinesAmount(3),
                () -> lineProviderTester.ensureKeyValueLinesAmount(1)
            );
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "UTF-8", "ISO-8859-1", "ISO-8859-2",
        "ISO-8859-15", "US-ASCII", "Windows-1250",
        "Windows-1252"
    })
    @DisplayName("Complete config parsing with different charsets")
    void completeConfigParsing(String charset) throws IOException {
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
        var targetCharset = Charset.forName(charset);
        var inputStream = new ByteArrayInputStream(content.getBytes(targetCharset));
        try (var provider = InputStreamLineProvider.with(inputStream, targetCharset)) {
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