package io.github.sekelenao.smallyaml.api.document.property.identifier;

import java.util.function.Function;

public interface GenericIdentifier<T> {

    Function<? super String, T> mapper();

}
