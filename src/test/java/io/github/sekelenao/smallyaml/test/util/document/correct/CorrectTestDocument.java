package io.github.sekelenao.smallyaml.test.util.document.correct;

import io.github.sekelenao.smallyaml.test.util.document.correct.descriptor.CompleteDescriptor;
import io.github.sekelenao.smallyaml.test.util.document.correct.descriptor.CorrectTestDocumentDescriptor;
import io.github.sekelenao.smallyaml.test.util.document.correct.descriptor.SimpleDescriptor;
import io.github.sekelenao.smallyaml.test.util.resource.TestResource;

import java.util.Objects;

public enum CorrectTestDocument implements TestResource {

    SIMPLE(new SimpleDescriptor()),
    COMPLETE(new CompleteDescriptor());
    
    private final CorrectTestDocumentDescriptor descriptor;
    
    CorrectTestDocument(CorrectTestDocumentDescriptor descriptor){
        this.descriptor = Objects.requireNonNull(descriptor);
    }

    public String path() {
        return String.format("test-config/correct/%s.yaml", this.name().toLowerCase());
    }

}
