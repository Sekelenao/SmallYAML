package io.github.sekelenao.smallyaml.api.document.property.identifier;

import java.util.function.ToIntFunction;

public interface IntIdentifier {

    ToIntFunction<? super String> mapper();

}
