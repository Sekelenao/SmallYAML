package io.github.sekelenao.smallyaml.test.internal.parsing.collector;

import io.github.sekelenao.smallyaml.api.exception.document.DuplicatedPropertyException;
import io.github.sekelenao.smallyaml.internal.collection.ValueList;
import io.github.sekelenao.smallyaml.internal.parsing.collector.MapParsingCollector;
import io.github.sekelenao.smallyaml.test.util.Reflections;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag(TestingTag.PARSING)
@Tag(TestingTag.INTERNAL)
final class MapParsingCollectorTest {

    @Test
    @DisplayName("Collect single value")
    void parsingCollectorAssertions() {
        var collector = new MapParsingCollector();
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> collector.collectSingleValue(null, "value")),
                () -> assertThrows(NullPointerException.class, () -> collector.collectSingleValue("key", null)),
                () -> assertDoesNotThrow(() -> collector.collectSingleValue("one", "1")),
                () -> assertDoesNotThrow(() -> collector.collectSingleValue("two", "1")),
                () -> assertThrows(DuplicatedPropertyException.class, () -> collector.collectSingleValue("two", "1")),
                () -> assertEquals(Map.of("one", "1", "two", "1"), collector.underlyingMapAsView())
        );
    }

    @Test
    @DisplayName("Collect multiple values")
    void collectMultipleValues() {
        var collector = new MapParsingCollector();
        var firstValueList = new ValueList("1");
        firstValueList.add("1");
        var secondValueList = new ValueList("1");
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> collector.collectListValue(null, "value", true)),
                () -> assertThrows(NullPointerException.class, () -> collector.collectListValue("key", null, true)),
                () -> assertDoesNotThrow(() -> collector.collectListValue("one", "1", true)),
                () -> assertDoesNotThrow(() -> collector.collectListValue("two", "1", false)),
                () -> assertDoesNotThrow(() -> collector.collectListValue("one", "1", false)),
                () -> assertThrows(DuplicatedPropertyException.class, () -> collector.collectListValue("one", "1", true)),
                () -> assertEquals(Map.of("one", firstValueList, "two", secondValueList), collector.underlyingMapAsView())
        );
    }

    @Test
    @DisplayName("Collect different types")
    void collectDifferentTypes() {
        var collector = new MapParsingCollector();
        assertAll(
                () -> assertDoesNotThrow(() -> collector.collectSingleValue("key", "1")),
                () -> assertDoesNotThrow(() -> collector.collectListValue("list-key", "1", true))
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Defensive programming assertion")
    void defensiveProgrammingAssertion() throws ReflectiveOperationException {
        var collector = new MapParsingCollector();
        var field = (Map<String, Object>) Reflections.retrievePrivateFieldValue(collector, "map");
        field.put("key", List.of("value"));
        assertThrows(IllegalStateException.class, () -> collector.collectListValue("key", "value", false));
    }

}
