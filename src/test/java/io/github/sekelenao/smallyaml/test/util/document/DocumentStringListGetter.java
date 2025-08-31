package io.github.sekelenao.smallyaml.test.util.document;

import io.github.sekelenao.smallyaml.api.document.Document;

import java.util.List;

@FunctionalInterface
public interface DocumentStringListGetter<D extends Document> {

    List<String> get(D document, String string);

}
