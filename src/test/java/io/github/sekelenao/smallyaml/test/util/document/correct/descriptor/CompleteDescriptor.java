package io.github.sekelenao.smallyaml.test.util.document.correct.descriptor;

import java.util.Collections;
import java.util.List;

public class CompleteDescriptor implements CorrectTestDocumentDescriptor {

    @Override
    public List<KeyValue> expectedKeyValueList() {
        return Collections.emptyList();
    }

}
