package io.github.sekelenao.smallyaml.test.api.line.provider;

import io.github.sekelenao.smallyaml.api.line.provider.StringLineProvider;
import io.github.sekelenao.smallyaml.internal.parsing.line.EmptyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.KeyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.KeyValueLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class StringLineProviderTest {

    @Test
    @DisplayName("String line provider assertions")
    void assertions() {
        assertThrows(NullPointerException.class, () -> StringLineProvider.of(null));
    }

    @Test
    @DisplayName("Empty string line provider is working")
    void stringLineProviderIsWorking() {
        var stringLineProvider = StringLineProvider.of("");
        assertAll(
            () -> assertEquals(Optional.empty(), stringLineProvider.nextLine()),
            () -> assertEquals(Optional.empty(), stringLineProvider.nextLine())
        );
    }

    @Test
    @DisplayName("String line provider is working")
    void stringLineProviderIsWorking2() {
        var stringLineProvider = StringLineProvider.of("""
            first:
                second:
            
            fourth: value
            """);
        assertAll(
            () -> assertEquals(new KeyLine(0, "first"), stringLineProvider.nextLine().orElseThrow()),
            () -> assertEquals(new KeyLine(4, "second"), stringLineProvider.nextLine().orElseThrow()),
            () -> assertEquals(EmptyLine.SINGLE_INSTANCE, stringLineProvider.nextLine().orElseThrow()),
            () -> assertEquals(new KeyValueLine(0, "fourth", "value"), stringLineProvider.nextLine().orElseThrow()),
            () -> assertEquals(Optional.empty(), stringLineProvider.nextLine())
        );
    }

}
