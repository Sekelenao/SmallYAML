package io.github.sekelenao.smallyaml.internal.parsing;

import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.api.line.provider.LineProvider;
import io.github.sekelenao.smallyaml.internal.parsing.line.EmptyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.KeyLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.KeyValueLine;
import io.github.sekelenao.smallyaml.internal.parsing.line.Line;
import io.github.sekelenao.smallyaml.internal.parsing.line.ListValueLine;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SmallYAMLParser {

    private final LinkedList<KeyLine> previousKeys = new LinkedList<>();

    private Class<? extends Line> previousLineType;

    private String generateKey(String... lastPart){
        var keysStream = previousKeys.stream().map(KeyLine::key);
        if (lastPart.length > 0) {
            keysStream = Stream.concat(keysStream, Stream.of(lastPart));
        }
        return keysStream.collect(Collectors.joining("."));
    }

    private void checkDepthAndUpdateContext(int depth, String key){
        while (!previousKeys.isEmpty() && depth <= previousKeys.getLast().depth()){
            previousKeys.removeLast();
        }
        if(previousKeys.isEmpty() && depth > 0){
            throw ParsingException.wrongIndentation("first key should start at indentation 0", key);
        }
    }

    private void onKeyLine(KeyLine keyLine){
        checkDepthAndUpdateContext(keyLine.depth(), keyLine.key());
        previousKeys.addLast(keyLine);
    }

    private void onListValueLine(ListValueLine listValueLine, ParsingCollector collector){
        if(previousLineType != KeyLine.class && previousLineType != ListValueLine.class){
            throw ParsingException.wrongValue("no key to attach", "- " + listValueLine.value());
        }
        collector.collectListValue(generateKey(), listValueLine.value(), previousLineType != ListValueLine.class);
    }

    private void onKeyValueLine(KeyValueLine keyValueLine, ParsingCollector collector){
        checkDepthAndUpdateContext(keyValueLine.depth(), keyValueLine.key());
        collector.collectSingleValue(generateKey(keyValueLine.key()), keyValueLine.value());
    }

    public void parse(LineProvider lineProvider, ParsingCollector collector) throws IOException {
        Objects.requireNonNull(lineProvider);
        Objects.requireNonNull(collector);
        var nextLine = lineProvider.nextLine();
        while (nextLine.isPresent()){
            var line = nextLine.get();
            switch (line){
                case EmptyLine ignored -> {/* do nothing */}
                case KeyLine keyLine -> {
                    onKeyLine(keyLine);
                    previousLineType = KeyLine.class;
                }
                case ListValueLine listValueLine -> {
                    onListValueLine(listValueLine, collector);
                    previousLineType = ListValueLine.class;
                }
                case KeyValueLine keyValueLine -> {
                    onKeyValueLine(keyValueLine, collector);
                    previousLineType =  KeyValueLine.class;
                }
            }
            nextLine = lineProvider.nextLine();
        }
    }


}
