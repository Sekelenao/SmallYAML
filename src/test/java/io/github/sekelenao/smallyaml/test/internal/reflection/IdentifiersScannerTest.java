package io.github.sekelenao.smallyaml.test.internal.reflection;

import io.github.sekelenao.smallyaml.api.document.property.MultipleMandatoryIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.MultipleOptionalIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleMandatoryIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.SingleOptionalIdentifier;
import io.github.sekelenao.smallyaml.internal.reflection.IdentifiersScanner;
import io.github.sekelenao.smallyaml.test.util.ExceptionsTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static io.github.sekelenao.smallyaml.test.internal.reflection.IdentifiersScannerTest.TestConfiguration.APP_NAME;
import static io.github.sekelenao.smallyaml.test.internal.reflection.IdentifiersScannerTest.TestConfiguration.HOST;
import static io.github.sekelenao.smallyaml.test.internal.reflection.IdentifiersScannerTest.TestConfiguration.PORT;
import static io.github.sekelenao.smallyaml.test.internal.reflection.IdentifiersScannerTest.TestConfiguration.USERS;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class IdentifiersScannerTest {

    public static final class TestConfiguration {

        public static final SingleMandatoryIdentifier HOST = SingleMandatoryIdentifier.define("host");

        public static final SingleMandatoryIdentifier PORT = SingleMandatoryIdentifier.define("port");

        public static final SingleOptionalIdentifier APP_NAME = SingleOptionalIdentifier.define("appName");

        public static final MultipleMandatoryIdentifier USERS = MultipleMandatoryIdentifier.define("users");

        @SuppressWarnings("unused")
        public final SingleOptionalIdentifier no = SingleOptionalIdentifier.define("no");

        @SuppressWarnings("unused")
        private static final SingleOptionalIdentifier NO2 = SingleOptionalIdentifier.define("no2");

        @SuppressWarnings({"FieldMayBeFinal", "unused"})
        public static SingleOptionalIdentifier no3 = SingleOptionalIdentifier.define("no3");

        @SuppressWarnings("unused")
        private static final SingleOptionalIdentifier NO4 = SingleOptionalIdentifier.define("no4");

    }

    public static final class DuplicatedIdentifier {

        @SuppressWarnings("unused")
        public static final SingleMandatoryIdentifier FIRST = SingleMandatoryIdentifier.define("same");

        @SuppressWarnings("unused")
        public static final MultipleOptionalIdentifier SECOND = MultipleOptionalIdentifier.define("same");

    }

    public static final class NullIdentifier {

        @SuppressWarnings("unused")
        public static final SingleOptionalIdentifier NULL = null;

    }

    public static final class EmptyClass {}

    static final class IllegalAccess {

        @SuppressWarnings("unused")
        public static final SingleMandatoryIdentifier ACCESS = SingleMandatoryIdentifier.define("access");

    }

    @Test
    @DisplayName("Assertions")
    void assertions(){
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> IdentifiersScanner.scan(null)),
            () -> assertThrows(NullPointerException.class, () -> IdentifiersScanner.scan(NullIdentifier.class))
        );
    }

    @Test
    @DisplayName("Scanner is working")
    void scannerIsWorking() {
        var set = IdentifiersScanner.scan(TestConfiguration.class);
        assertAll(
            () -> assertEquals(4, set.size()),
            () -> assertEquals(Set.of(HOST, PORT, APP_NAME, USERS), set),
            () -> assertEquals(Collections.emptySet(), IdentifiersScanner.scan(EmptyClass.class))
        );
    }

    @Test
    @DisplayName("Duplicated identifiers are detected")
    void duplicatedIdentifiersAreDetected(){
        ExceptionsTester.assertIsThrownAndContains(
            IllegalArgumentException.class,
            () -> IdentifiersScanner.scan(DuplicatedIdentifier.class),
            "Duplicated identifier definition"
        );
    }

    @Test
    @DisplayName("Illegal access is detected")
    void illegalAccessIsDetected(){
        ExceptionsTester.assertIsThrownAndContains(
            IllegalStateException.class,
            () -> IdentifiersScanner.scan(IllegalAccess.class),
            "Failed to access to the following field"
        );
    }

}
