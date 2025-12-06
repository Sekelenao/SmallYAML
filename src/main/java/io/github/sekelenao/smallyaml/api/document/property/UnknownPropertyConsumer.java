package io.github.sekelenao.smallyaml.api.document.property;

@FunctionalInterface
public interface UnknownPropertyConsumer {

    UnknownPropertyConsumer NOOP = (key, value) -> {};

    void accept(String key, Object value);

}
