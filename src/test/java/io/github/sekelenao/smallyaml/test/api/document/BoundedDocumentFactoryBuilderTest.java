package io.github.sekelenao.smallyaml.test.api.document;

import io.github.sekelenao.smallyaml.api.document.BoundedDocument;
import io.github.sekelenao.smallyaml.api.document.property.MultipleMandatoryIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.MultipleOptionalIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleMandatoryIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleOptionalIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.UnknownPropertyConsumer;
import io.github.sekelenao.smallyaml.api.exception.document.DuplicatedIdentifierException;
import io.github.sekelenao.smallyaml.api.exception.document.PropertyDiscoveryException;
import io.github.sekelenao.smallyaml.test.util.ExceptionsTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class BoundedDocumentFactoryBuilderTest {

    private static final class EmptyClass {}

    @Test
    @DisplayName("Assertions")
    void assertions() {
        var builder = BoundedDocument.factoryBuilder();
        var identifier = SingleMandatoryIdentifier.define("test");
        var secondIdentifier = SingleMandatoryIdentifier.define("test2");
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> builder.scan(null)),
            () -> assertThrows(NullPointerException.class, () -> builder.register((PropertyIdentifier) null)),
            () -> assertThrows(NullPointerException.class, () -> builder.register(null)),
            () -> assertThrows(
                NullPointerException.class,
                () -> builder.scan(EmptyClass.class, (Class<?>[]) null)
            ),
            () -> assertThrows(
                NullPointerException.class,
                () -> builder.register(identifier, (PropertyIdentifier[]) null)
            ),
            () -> assertThrows(
                NullPointerException.class,
                () -> builder.register(identifier, new PropertyIdentifier[]{secondIdentifier, null})
            ),
            () -> assertThrows(
                NullPointerException.class,
                () -> builder.scan(EmptyClass.class, new Class<?>[]{EmptyClass.class, null})
            )
        );
    }

    static final class PackagePrivateClass {

        @SuppressWarnings("unused")
        public static final SingleMandatoryIdentifier FIELD_NAME = SingleMandatoryIdentifier.define("test");

    }

    @Test
    @DisplayName("Property discovery error")
    void propertyDiscoveryErrors() {
        var builder = BoundedDocument.factoryBuilder();
        ExceptionsTester.assertIsThrownAndContains(
            PropertyDiscoveryException.class,
            () -> builder.scan(PackagePrivateClass.class),
            "Could not access following field: 'FIELD_NAME'"
        );
    }

    public static final class Foo {
        public static final SingleOptionalIdentifier FOO = SingleOptionalIdentifier.define("foo");
    }

    @Test
    @DisplayName("Duplicated property definition")
    void duplicatedPropertyDefinition1() {
        var builder = BoundedDocument.factoryBuilder();
        var otherFoo = SingleMandatoryIdentifier.define("foo");
        var otherFoo2 = MultipleMandatoryIdentifier.define("foo");
        var otherFoo3 = MultipleOptionalIdentifier.define("foo");
        assertAll(
            () -> ExceptionsTester.assertIsThrownAndContains(
                DuplicatedIdentifierException.class,
                () -> builder.scan(Foo.class, Foo.class),
                "Duplicated identifier definition: 'foo'"
            ),
            () -> ExceptionsTester.assertIsThrownAndContains(
                DuplicatedIdentifierException.class,
                () -> {
                    builder.scan(Foo.class);
                    builder.scan(Foo.class);
                },
                "Duplicated identifier definition: 'foo'"
            ),
            () -> ExceptionsTester.assertIsThrownAndContains(
                DuplicatedIdentifierException.class,
                () -> {
                    builder.scan(Foo.class);
                    builder.register(Foo.FOO);
                },
                "Duplicated identifier definition: 'foo'"
            ),
            () -> ExceptionsTester.assertIsThrownAndContains(
                DuplicatedIdentifierException.class,
                () -> {
                    builder.scan(Foo.class);
                    builder.register(otherFoo);
                },
                "Duplicated identifier definition: 'foo'"
            ),
            () -> ExceptionsTester.assertIsThrownAndContains(
                DuplicatedIdentifierException.class,
                () -> {
                    builder.scan(Foo.class);
                    builder.register(otherFoo2);
                },
                "Duplicated identifier definition: 'foo'"
            ),
            () -> ExceptionsTester.assertIsThrownAndContains(
                DuplicatedIdentifierException.class,
                () -> {
                    builder.scan(Foo.class);
                    builder.register(otherFoo3);
                },
                "Duplicated identifier definition: 'foo'"
            ),
            () -> ExceptionsTester.assertIsThrownAndContains(
                DuplicatedIdentifierException.class,
                () -> builder.register(Collections.singletonList(Foo.FOO))
                    .register(otherFoo3),
                "Duplicated identifier definition: 'foo'"
            )
        );
    }

    @Test
    @DisplayName("Factory is working")
    void factoryIsWorking() throws IOException {
        var bar = MultipleOptionalIdentifier.define("bar");
        var document = BoundedDocument.factoryBuilder()
            .register(bar)
            .scan(Foo.class)
            .buildFactory()
            .createDocument("");
        var baz = SingleMandatoryIdentifier.define("baz");
        assertAll(
            () -> assertTrue(document.hasRegistered(Foo.FOO)),
            () -> assertTrue(document.hasRegistered(bar)),
            () -> assertFalse(document.hasRegistered(baz))
        );
    }

    @Test
    @DisplayName("Register iterable")
    void registerIterable() throws IOException {
        var builder = BoundedDocument.factoryBuilder();
        var id1 = SingleMandatoryIdentifier.define("id1");
        var id2 = SingleMandatoryIdentifier.define("id2");
        builder.register(List.of(id1, id2));
        var factory = builder.buildFactory();
        var document = factory.createDocument("id1: value1\nid2: value2");
        assertAll(
            () -> assertTrue(document.hasRegistered(id1)),
            () -> assertTrue(document.hasRegistered(id2))
        );
    }

    @Test
    @DisplayName("Register iterable assertions")
    void registerIterableAssertions() {
        var builder = BoundedDocument.factoryBuilder();
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> builder.register(null)),
            () -> assertThrows(NullPointerException.class, () -> builder.register(Collections.singletonList(null)))
        );
    }

    @Test
    @DisplayName("Unknown property consumer")
    void unknownPropertyConsumer() throws IOException {
        var builder = BoundedDocument.factoryBuilder();
        var id1 = SingleMandatoryIdentifier.define("id1");
        var unknownKeys = new ArrayList<String>();
        var unknownValues = new ArrayList<>();
        UnknownPropertyConsumer consumer = (key, value) -> {
            unknownKeys.add(key);
            unknownValues.add(value);
        };
        
        var factory = builder.register(id1)
            .unknownPropertyConsumer(consumer)
            .buildFactory();
        
        factory.createDocument("id1: value1\nunknown: value2");
        
        assertAll(
            () -> assertFalse(unknownKeys.contains("id1")),
            () -> assertTrue(unknownKeys.contains("unknown")),
            () -> assertTrue(unknownValues.contains("value2"))
        );
    }

    @Test
    @DisplayName("Unknown property consumer assertions")
    void unknownPropertyConsumerAssertions() {
        var builder = BoundedDocument.factoryBuilder();
        assertThrows(NullPointerException.class, () -> builder.unknownPropertyConsumer(null).buildFactory());
    }

    @Test
    @DisplayName("Default unknown property consumer is NOOP")
    void defaultUnknownPropertyConsumerIsNoop() {
        var factory = BoundedDocument.factoryBuilder()
                .buildFactory();
        assertDoesNotThrow(() -> factory.createDocument("unknown: value2"));
    }

}


