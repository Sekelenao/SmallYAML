package io.github.sekelenao.smallyaml.test.api.line.provider;

import io.github.sekelenao.smallyaml.api.line.provider.LineProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

final class LineProviderTest {

    @Nested
    @DisplayName("InputStream line provider")
    final class InputStreamLineProviderTest {

        @Test
        @DisplayName("Assertions")
        void assertions() {
            var emptyInputStream = new ByteArrayInputStream(new byte[0]);
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> {
                    try (var ignored = LineProvider.with((InputStream) null)) {
                        fail("Should not reach here");
                    }
                }),
                () -> assertThrows(NullPointerException.class, () -> {
                    try (var ignored = LineProvider.with(emptyInputStream, null)) {
                        fail("Should not reach here");
                    }
                })
            );
        }

        @Test
        @DisplayName("Has next is working as expected")
        void hasNextIsWorking() throws IOException {
            var content = "Alone";
            var inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            try(var lineProvider = LineProvider.with(inputStream)) {
                assertAll(
                    () -> assertTrue(lineProvider.hasNext() && lineProvider.hasNext()),
                    () -> assertEquals("Alone", lineProvider.next()),
                    () -> assertFalse(lineProvider.hasNext()),
                    () -> assertThrows(NoSuchElementException.class, lineProvider::next)
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
                        1-$
                        2-$
                        3-$
                        4-$
                        5-$
                        Not the final line to be parsed : END AFTER TWO
                        
                        
                        """;
            var inputStream = new ByteArrayInputStream(content.getBytes(Charset.forName(charset)));
            try (var lineProvider = LineProvider.with(inputStream)) {
                for(int i = 1; i < 6; i++) {
                    assertTrue(lineProvider.hasNext());
                    assertEquals(String.join("-", String.valueOf(i), "$"), lineProvider.next());
                }
                assertTrue(lineProvider.hasNext());
                assertEquals("Not the final line to be parsed : END AFTER TWO", lineProvider.next());
                assertTrue(lineProvider.hasNext());
                assertEquals("", lineProvider.next());
                assertTrue(lineProvider.hasNext());
                assertEquals("", lineProvider.next());
                assertFalse(lineProvider.hasNext());
                assertThrows(NoSuchElementException.class, lineProvider::next);
            }
        }

    }

    @Nested
    @DisplayName("BufferedReader line provider")
    final class BufferedReaderLineProviderTest {

        @Test
        @DisplayName("Assertions")
        void assertions() {
            assertThrows(NullPointerException.class, () -> {
                try(var ignored = LineProvider.with((BufferedReader) null)) {
                    fail("Should not reach here");
                }
            });
        }

        @Test
        @DisplayName("Has next is working as expected")
        void hasNextIsWorking() throws IOException {
            try(var lineProvider = LineProvider.with(new BufferedReader(new StringReader("Alone")))) {
                assertAll(
                    () -> assertTrue(lineProvider.hasNext() && lineProvider.hasNext()),
                    () -> assertEquals("Alone", lineProvider.next()),
                    () -> assertFalse(lineProvider.hasNext()),
                    () -> assertThrows(NoSuchElementException.class, lineProvider::next)
                );
            }
        }

    }

    @Nested
    @DisplayName("String line provider")
    final class StringLineProviderTest {

        @Test
        @DisplayName("String line provider assertions")
        void assertions() {
            assertThrows(NullPointerException.class, () -> {
                try (var ignored = LineProvider.with((String) null)) {
                    fail("Should not reach here");
                }
            });
        }

        @Test
        @DisplayName("Empty string line provider is working")
        void stringLineProviderIsWorking() throws IOException {
            try(var stringLineProvider = LineProvider.with("")) {
                assertAll(
                    () -> assertFalse(stringLineProvider.hasNext()),
                    () -> assertThrows(NoSuchElementException.class, stringLineProvider::next)
                );
            }

        }

        @Test
        @DisplayName("String line provider is working")
        void stringLineProviderIsWorking2() throws IOException {
            var lineProvider = LineProvider.with("""
            first:
                second:
            
            fourth: value
            """);
            assertAll(
                () -> assertTrue(lineProvider.hasNext()),
                () -> assertEquals("first:", lineProvider.next()),
                () -> assertTrue(lineProvider.hasNext()),
                () -> assertEquals("    second:", lineProvider.next()),
                () -> assertTrue(lineProvider.hasNext()),
                () -> assertEquals("", lineProvider.next()),
                () -> assertTrue(lineProvider.hasNext()),
                () -> assertEquals("fourth: value", lineProvider.next()),
                () -> assertFalse(lineProvider.hasNext()),
                () -> assertThrows(NoSuchElementException.class, lineProvider::next)
            );
            lineProvider.close();
        }

    }

}
