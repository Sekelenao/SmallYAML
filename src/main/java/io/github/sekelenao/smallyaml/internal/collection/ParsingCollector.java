package io.github.sekelenao.smallyaml.internal.collection;

public interface ParsingCollector {

    void collectSingleValue(String key, String value);

    void collectListValue(String key, String value, boolean isNewList);

}