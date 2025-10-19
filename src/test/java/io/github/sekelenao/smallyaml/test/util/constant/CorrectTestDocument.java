package io.github.sekelenao.smallyaml.test.util.constant;

import io.github.sekelenao.smallyaml.test.util.resource.TestResource;

import java.util.Locale;

public enum CorrectTestDocument implements TestResource {

    SINGLE_PROPERTY,
    GENERIC_DOCUMENT_FOR_TEST,
    DOTS,
    EMPTY,
    EMPTY_WITH_ONLY_KEYS,
    KEY_WITH_BLANK_VALUE,
    APPLICATION_CONFIGURATION,
    STRANGE_INDENTATION;

    public String resourcePath() {
        return String.format("document/correct/%s/document.yaml", this.name().toLowerCase(Locale.ROOT));
    }

    public String csvResourcePath() {
        return String.format("document/correct/%s/expected-records.csv", this.name().toLowerCase(Locale.ROOT));
    }

}
