package io.github.sekelenao.smallyaml.test.util.document;

import io.github.sekelenao.smallyaml.api.document.Document;

@FunctionalInterface
public interface DocumentSingleStringGetter<D extends Document> {

    String get(D document, String string);

}
