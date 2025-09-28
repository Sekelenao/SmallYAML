package io.github.sekelenao.smallyaml.test.util.document;

import io.github.sekelenao.smallyaml.api.document.Document;
import io.github.sekelenao.smallyaml.api.line.provider.LineProvider;

import java.io.IOException;

@FunctionalInterface
public interface DocumentProvider<D extends Document> {

    D from(LineProvider lineProvider) throws IOException;

}
