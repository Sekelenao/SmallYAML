package io.github.sekelenao.smallyaml.test.api.line.provider;

import io.github.sekelenao.smallyaml.api.line.provider.InputStreamLineProvider;
import io.github.sekelenao.smallyaml.test.util.constant.RegularTestDocument;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import io.github.sekelenao.smallyaml.test.util.line.provider.LineProviderTester;
import io.github.sekelenao.smallyaml.test.util.resource.TestResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;

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
                try (var ignored = new InputStreamLineProvider(null)) {
                    fail("Should not reach here");
                }
            }),
            () -> assertThrows(NullPointerException.class, () -> {
                try (var ignored = new InputStreamLineProvider(emptyInputStream, null)) {
                    fail("Should not reach here");
                }
            })
        );
    }

    @Test
    @DisplayName("Complete config parsing")
    void completeConfigParsing() throws IOException {
        var inputStream = TestResource.asInputStream(RegularTestDocument.TEST_DOCUMENT);
        try (var provider = new InputStreamLineProvider(inputStream)) {
            var lineProviderTester = LineProviderTester.forFollowing(provider);
            assertAll(
                () -> lineProviderTester.ensureEmptyLinesAmount(RegularTestDocument.EMPTY_LINE_COUNT),
                () -> lineProviderTester.ensureKeyLinesAmount(RegularTestDocument.KEY_LINE_COUNT),
                () -> lineProviderTester.ensureListValueLinesAmount(RegularTestDocument.LIST_VALUE_LINE_COUNT),
                () -> lineProviderTester.ensureKeyValueLinesAmount(RegularTestDocument.KEY_VALUE_LINE_COUNT)
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
    void completeConfigParsingWithDifferentCharsets(String charset) throws IOException, URISyntaxException {
        var test = TestResource.find(RegularTestDocument.TEST_DOCUMENT);
        var targetCharset = Charset.forName(charset);
        var bytes = Files.readString(test).getBytes(targetCharset);
        var inputStream = new ByteArrayInputStream(bytes);
        try (var provider = new InputStreamLineProvider(inputStream)) {
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