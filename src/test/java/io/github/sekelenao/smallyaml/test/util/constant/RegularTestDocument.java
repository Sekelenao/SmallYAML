package io.github.sekelenao.smallyaml.test.util.constant;

import java.util.List;

public class RegularTestDocument {

    private RegularTestDocument() {
        throw new IllegalStateException("You cannot instantiate this class");
    }

    public static final CorrectTestDocument TEST_DOCUMENT = CorrectTestDocument.GENERIC_DOCUMENT_FOR_TEST;

    public static final int EMPTY_LINE_COUNT = 15;

    public static final int KEY_LINE_COUNT = 4;

    public static final int LIST_VALUE_LINE_COUNT = 12;

    public static final int KEY_VALUE_LINE_COUNT = 4;

    public static final String SINGLE_STRING_KEY = "single-value";

    public static final String MULTIPLE_STRING_KEY = "multiple-values";

    public static final String SINGLE_STRING_VALUE = "value";

    public static final List<String> MULTIPLE_STRINGS_VALUE = List.of("one", "two", "three");

    public static final String SINGLE_BOOLEAN_KEY = "single-boolean";

    public static final String MULTIPLE_BOOLEAN_KEY = "multiple-booleans";

    public static final String SINGLE_LONG_KEY = "single-long";

    public static final String MULTIPLE_LONGS_KEY = "multiple-longs";

    public static final long SINGLE_LONG_VALUE = 20;

    public static final long[] MULTIPLE_LONGS_VALUE = new long[]{ 1, 2, 3 };

    public static final String SINGLE_DOUBLE_KEY = "single-double";

    public static final String MULTIPLE_DOUBLES_KEY = "multiple-doubles";

    public static final double SINGLE_DOUBLE_VALUE = 1.2;

    public static final double[] MULTIPLE_DOUBLES_VALUE = new double[]{ 1.1, 2, 3.3 };

}
