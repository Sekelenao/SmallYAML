package io.github.sekelenao.smallyaml.test.util.line.provider;

import io.github.sekelenao.smallyaml.api.line.provider.LineProvider;
import io.github.sekelenao.smallyaml.internal.parsing.line.EmptyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.KeyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.KeyValueLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.ListValueLine;
import io.github.sekelenao.smallyaml.internal.util.Assertions;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class LineProviderTester {

    private final int emptyLineCount;

    private final int keyLineCount;

    private final int listValueLineCount;

    private final int keyValueLineCount;

    private LineProviderTester(int emptyLineCount, int keyLineCount, int listValueLineCount, int keyValueLineCount) {
        Assertions.isPositiveOrZero(emptyLineCount);
        Assertions.isPositiveOrZero(keyLineCount);
        Assertions.isPositiveOrZero(listValueLineCount);
        Assertions.isPositiveOrZero(keyValueLineCount);
        this.emptyLineCount = emptyLineCount;
        this.keyLineCount = keyLineCount;
        this.listValueLineCount = listValueLineCount;
        this.keyValueLineCount = keyValueLineCount;
    }

    public static LineProviderTester forFollowing(LineProvider lineProvider) throws IOException {
        Objects.requireNonNull(lineProvider);
        int emptyLineCount = 0;
        int keyLineCount = 0;
        int listValueLineCount = 0;
        int keyValueLineCount = 0;
        var line = lineProvider.nextLine();
        while (line.isPresent()) {
            switch (line.get()) {
                case EmptyLine ignored -> emptyLineCount++;
                case KeyLine ignored -> keyLineCount++;
                case ListValueLine ignored -> listValueLineCount++;
                case KeyValueLine ignored -> keyValueLineCount++;
            }
            line = lineProvider.nextLine();
        }
        return new LineProviderTester(emptyLineCount, keyLineCount, listValueLineCount, keyValueLineCount);
    }

    public void ensureEmptyLinesAmount(int expectedAmount) {
        Assertions.isPositiveOrZero(expectedAmount);
        assertEquals(expectedAmount, emptyLineCount);
    }

    public void ensureKeyLinesAmount(int expectedAmount) {
        Assertions.isPositiveOrZero(expectedAmount);
        assertEquals(expectedAmount, keyLineCount);
    }

    public void ensureListValueLinesAmount(int expectedAmount) {
        Assertions.isPositiveOrZero(expectedAmount);
        assertEquals(expectedAmount, listValueLineCount);
    }

    public void ensureKeyValueLinesAmount(int expectedAmount) {
        Assertions.isPositiveOrZero(expectedAmount);
        assertEquals(expectedAmount, keyValueLineCount);
    }

}
