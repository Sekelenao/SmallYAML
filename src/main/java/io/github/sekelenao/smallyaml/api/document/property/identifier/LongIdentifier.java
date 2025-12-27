package io.github.sekelenao.smallyaml.api.document.property.identifier;

import java.util.function.ToLongFunction;

public interface LongIdentifier {

    ToLongFunction<? super String> mapper();

}
