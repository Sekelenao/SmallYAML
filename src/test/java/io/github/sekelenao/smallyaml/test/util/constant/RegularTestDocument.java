package io.github.sekelenao.smallyaml.test.util.constant;

import java.util.List;

public class RegularTestDocument {

    private RegularTestDocument() {
        throw new IllegalStateException("You cannot instantiate this class");
    }

    public static final CorrectTestDocument TEST_DOCUMENT = CorrectTestDocument.REGULAR_TEST_DOCUMENT;

    public static final String SINGLE_VALUE_KEY = "single-value";

    public static final String MULTIPLE_VALUES_KEY = "multiple-values";

    public static final String SINGLE_VALUE = "value";

    public static final List<String> MULTIPLE_VALUES = List.of("one", "two", "three");

}
