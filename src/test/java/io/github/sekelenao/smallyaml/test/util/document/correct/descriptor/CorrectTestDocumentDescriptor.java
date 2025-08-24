package io.github.sekelenao.smallyaml.test.util.document.correct.descriptor;

import java.util.List;
import java.util.Objects;

public interface CorrectTestDocumentDescriptor {

    record KeyValue(String key, Object value) {

        public KeyValue {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
        }

        public static KeyValue of(String key, Object value){
            return new KeyValue(key, value);
        }

    }

    List<KeyValue> expectedKeyValueList();

}
