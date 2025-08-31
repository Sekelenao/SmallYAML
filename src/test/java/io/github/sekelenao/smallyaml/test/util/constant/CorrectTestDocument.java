package io.github.sekelenao.smallyaml.test.util.constant;

import io.github.sekelenao.smallyaml.test.util.resource.TestResource;

import java.util.Locale;

public enum CorrectTestDocument implements TestResource {

    DOTS_DOCUMENT,
    REGULAR_TEST_DOCUMENT,
    SIMPLE,
    COMPLETE;

    public String resourcePath() {
        return String.format("document/correct/%s/document.yaml", this.name().toLowerCase(Locale.ROOT));
    }

    public String csvResourcePath() {
        return String.format("document/correct/%s/expected-records.csv", this.name().toLowerCase(Locale.ROOT));
    }

}
