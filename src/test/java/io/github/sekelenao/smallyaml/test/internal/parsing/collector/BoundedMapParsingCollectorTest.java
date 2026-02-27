package io.github.sekelenao.smallyaml.test.internal.parsing.collector;

import io.github.sekelenao.smallyaml.api.document.property.MultipleOptionalIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleMandatoryIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.UnknownPropertyConsumer;
import io.github.sekelenao.smallyaml.api.exception.document.DuplicatedPropertyException;
import io.github.sekelenao.smallyaml.api.exception.document.MissingPropertyException;
import io.github.sekelenao.smallyaml.api.exception.document.WrongPropertyTypeException;
import io.github.sekelenao.smallyaml.internal.collection.BoundedMapParsingCollector;
import io.github.sekelenao.smallyaml.internal.collection.ValueList;
import io.github.sekelenao.smallyaml.test.util.ExceptionsTester;
import io.github.sekelenao.smallyaml.test.util.Reflections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

final class BoundedMapParsingCollectorTest {

    @Nested
    @DisplayName("Lifecycle and assertions")
    final class Lifecycle {

        @Test
        @DisplayName("Assertions")
        void assertions() {
            var registry = Map.<String, PropertyIdentifier>of("key", SingleMandatoryIdentifier.define("key"));
            var collector = new BoundedMapParsingCollector(registry, UnknownPropertyConsumer.NOOP);
            assertAll(
                () -> assertThrows(
                    NullPointerException.class,
                    () -> new BoundedMapParsingCollector(null, UnknownPropertyConsumer.NOOP)
                ),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> new BoundedMapParsingCollector(registry, null)
                ),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> collector.collectSingleValue(null, "value")
                ),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> collector.collectSingleValue("key", null)
                ),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> collector.collectListValue(null, "value", true)
                ),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> collector.collectListValue("key", null, true)
                )
            );
        }

    }

    @Nested
    @DisplayName("Single value collection")
    final class SingleValue {

        @Test
        @DisplayName("Success and errors")
        void collectSingleSuccessAndErrors() {
            var id = SingleMandatoryIdentifier.define("single");
            var listId = MultipleOptionalIdentifier.define("list");
            var registry = Map.of("single", id, "list", listId);
            var collector = new BoundedMapParsingCollector(registry, UnknownPropertyConsumer.NOOP);
            assertAll(
                () -> assertDoesNotThrow(() -> collector.collectSingleValue("single", "value")),
                () -> assertEquals("value", collector.underlyingMapAsView().get(id)),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> collector.collectSingleValue("list", "value"),
                    "Expected single value"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    DuplicatedPropertyException.class,
                    () -> collector.collectSingleValue("single", "another"),
                    "Duplicated property 'single'"
                )
            );
        }

        @Test
        @DisplayName("Unknown property consumer is called")
        void unknownPropertyConsumer() {
            var unknownKeys = new ArrayList<String>();
            var unknownValues = new ArrayList<>();
            UnknownPropertyConsumer consumer = (key, value) -> {
                unknownKeys.add(key);
                unknownValues.add(value);
            };
            var collector = new BoundedMapParsingCollector(Collections.emptyMap(), consumer);
            assertAll(
                () -> assertDoesNotThrow(() -> collector.collectSingleValue("unknown", "value")),
                () -> assertTrue(unknownKeys.contains("unknown")),
                () -> assertTrue(unknownValues.contains("value"))
            );
        }

    }

    @Nested
    @DisplayName("List value collection")
    final class ListValue {

        @Test
        @DisplayName("Success and errors")
        void collectListSuccessAndErrors() {
            var singleId = SingleMandatoryIdentifier.define("single");
            var listId = MultipleOptionalIdentifier.define("list");
            var registry = Map.of("single", singleId, "list", listId);
            var collector = new BoundedMapParsingCollector(registry, UnknownPropertyConsumer.NOOP);
            assertAll(
                () -> assertDoesNotThrow(() -> collector.collectSingleValue("single", "value")),
                () -> assertDoesNotThrow(() -> collector.collectListValue("list", "one", true)),
                () -> assertDoesNotThrow(() -> collector.collectListValue("list", "two", false)),
                () -> {
                    var list = (ValueList) collector.underlyingMapAsView().get(listId);
                    assertAll(
                        () -> assertEquals(2, list.size()),
                        () -> assertEquals("one", list.get(0)),
                        () -> assertEquals("two", list.get(1)));
                },
                () -> ExceptionsTester.assertIsThrownAndContains(
                    WrongPropertyTypeException.class,
                    () -> collector.collectListValue("single", "value", true),
                    "Expected multiple values"
                ),
                () -> ExceptionsTester.assertIsThrownAndContains(
                    DuplicatedPropertyException.class,
                    () -> collector.collectListValue("list", "new", true),
                    "Duplicated property 'list'"
                )
            );
        }

        @Test
        @DisplayName("Unknown property consumer is called")
        void unknownPropertyConsumer() {
            var unknownKeys = new ArrayList<String>();
            var unknownValues = new ArrayList<>();
            UnknownPropertyConsumer consumer = (key, value) -> {
                unknownKeys.add(key);
                unknownValues.add(value);
            };
            var collector = new BoundedMapParsingCollector(Map.of(), consumer);
            assertAll(
                () -> assertDoesNotThrow(() -> collector.collectListValue("unknown", "value", true)),
                () -> assertTrue(unknownKeys.contains("unknown")),
                () -> assertTrue(unknownValues.contains("value"))
            );
        }

        @Test
        @DisplayName("Defensive programming assertions")
        void defensiveProgramming() throws ReflectiveOperationException {
            var listId = MultipleOptionalIdentifier.define("list");
            var registry = Map.<String, PropertyIdentifier>of("list", listId);
            var collector = new BoundedMapParsingCollector(registry, UnknownPropertyConsumer.NOOP);

            @SuppressWarnings("unchecked")
            var properties = (Map<PropertyIdentifier, Object>) Reflections.retrievePrivateFieldValue(collector, "properties");
            properties.put(listId, "not-a-list");
            assertAll(
                () -> assertThrows(
                    IllegalStateException.class,
                    () -> collector.collectListValue("list", "value", false)
                ),
                () -> {
                    properties.remove(listId);
                    assertThrows(
                        IllegalStateException.class,
                        () -> collector.collectListValue("list", "value", false)
                    );
                }
            );
        }

    }

    @Nested
    @DisplayName("Result view and mandatory checks")
    final class ResultView {

        @Test
        @DisplayName("Success and missing mandatory")
        void resultView() {
            var mandatoryId = SingleMandatoryIdentifier.define("mandatory");
            var optionalId = MultipleOptionalIdentifier.define("optional");
            var registry = Map.of("mandatory", mandatoryId, "optional", optionalId);
            var collector = new BoundedMapParsingCollector(registry, UnknownPropertyConsumer.NOOP);
            assertAll(
                () -> assertThrows(MissingPropertyException.class, collector::underlyingMapAsView),
                () -> {
                    collector.collectSingleValue("mandatory", "val");
                    var view = collector.underlyingMapAsView();
                    assertEquals("val", view.get(mandatoryId));
                });
        }

    }

}
