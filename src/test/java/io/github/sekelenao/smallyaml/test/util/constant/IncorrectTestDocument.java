package io.github.sekelenao.smallyaml.test.util.constant;

import io.github.sekelenao.smallyaml.test.util.resource.TestResource;

import java.util.Locale;

public enum IncorrectTestDocument implements TestResource {

    DUPLICATED_STRING_PROPERTY,
    DUPLICATED_LIST_PROPERTY,
    DUPLICATED_PROPERTY_NOT_SAME_TYPE,
    DUPLICATED_PROPERTY_NOT_SAME_CASE,
    WRONG_INDENTATION_AT_START,
    LIST_VALUE_WITHOUT_KEY,
    LIST_VALUE_ALONE,
    WRONG_WHITESPACE;

    public String resourcePath() {
        return String.format("document/incorrect/%s/document.yaml", this.name().toLowerCase(Locale.ROOT));
    }

    public String csvResourcePath() {
        return String.format("document/incorrect/%s/expected_exception.csv", this.name().toLowerCase(Locale.ROOT));
    }

}
