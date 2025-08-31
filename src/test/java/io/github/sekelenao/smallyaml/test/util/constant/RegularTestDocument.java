package io.github.sekelenao.smallyaml.test.util.constant;

import java.util.List;

public class RegularTestDocument {

    private RegularTestDocument() {
        throw new IllegalStateException("You cannot instantiate this class");
    }

    public static final CorrectTestDocument TEST_DOCUMENT = CorrectTestDocument.GENERIC_DOCUMENT_FOR_TEST;

    public static final int EMPTY_LINE_COUNT = 3;

    public static final int KEY_LINE_COUNT = 1;

    public static final int LIST_VALUE_LINE_COUNT = 3;

    public static final int KEY_VALUE_LINE_COUNT = 1;

    public static final String SINGLE_VALUE_KEY = "single-value";

    public static final String MULTIPLE_VALUES_KEY = "multiple-values";

    public static final String SINGLE_VALUE = "value";

    public static final List<String> MULTIPLE_VALUES = List.of("one", "two", "three");

}
