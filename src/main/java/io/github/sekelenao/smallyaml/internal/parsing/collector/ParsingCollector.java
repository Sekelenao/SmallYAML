package io.github.sekelenao.smallyaml.internal.parsing.collector;

public interface ParsingCollector {

    void collectSingleValue(String key, String value);

    void collectListValue(String key, String value, boolean isNewList);

}