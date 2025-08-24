package io.github.sekelenao.smallyaml.test.util.document.correct.descriptor;

import java.util.List;

public final class SimpleDescriptor implements CorrectTestDocumentDescriptor {

    @Override
    public List<KeyValue> expectedKeyValueList() {
        return List.of(
            KeyValue.of("one.four", List.of("five", "six", "seven"))
        );
    }

}
