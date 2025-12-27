package io.github.sekelenao.smallyaml.api.document.property.identifier;

import java.util.function.ToDoubleFunction;

public interface DoubleIdentifier {

    ToDoubleFunction<? super String> mapper();

}
