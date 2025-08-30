package io.github.sekelenao.smallyaml.test.util.document;

import io.github.sekelenao.skcsv.SkCsv;
import io.github.sekelenao.smallyaml.test.util.resource.TestResource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class DocumentTester {

    private DocumentTester() {
        throw new AssertionError("You cannot instantiate this class");
    }

    public static void ensureAllRecordsArePresent(
        String expectedRecordsCsvPath, Function<String, String> stringGetter, Function<String, List<String>> listGetter
    ) throws URISyntaxException, IOException {

        var path = TestResource.find(expectedRecordsCsvPath);
        var expectedRecordsCsv = SkCsv.from(path);
        for (var line : expectedRecordsCsv) {
            var key = line.getFirst();
            int size = line.size();
            if (size < 2) {
                throw new IllegalArgumentException("Expected records CSV is invalid: " + path.toAbsolutePath());
            } else if (size == 2) {
                var expectedValue = line.get(1);
                assertEquals(expectedValue, stringGetter.apply(key));
            } else {
                var expectedValue = line.stream().skip(1).toList();
                assertEquals(expectedValue, listGetter.apply(key));
            }
        }

    }

}
