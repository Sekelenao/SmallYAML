package io.github.sekelenao.smallyaml.api.parsing;

import io.github.sekelenao.smallyaml.api.config.PermissiveConfig;
import io.github.sekelenao.smallyaml.api.exception.parsing.ParsingException;
import io.github.sekelenao.smallyaml.api.parsing.provider.LineProvider;
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

    private PermissiveConfig config;

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

    private void treatKeyLine(KeyLine keyLine){
        checkDepthAndUpdateContext(keyLine.depth(), keyLine.key());
        previousKeys.addLast(keyLine);
        previousLineType = KeyLine.class;
    }

    private void treatListValueLine(ListValueLine listValueLine){
        if(previousLineType != KeyLine.class){
            throw ParsingException.wrongValue("key already have a non list value attached", generateKey() + "...");
        }
        config.addValueInList(generateKey(), listValueLine.value());
        previousLineType = ListValueLine.class;
    }

    private void treatKeyValueLine(KeyValueLine keyValueLine){
        checkDepthAndUpdateContext(keyValueLine.depth(), keyValueLine.key());
        config.add(keyValueLine.key(), generateKey(keyValueLine.value()));
        previousLineType =  KeyValueLine.class;
    }

    public PermissiveConfig parse(LineProvider lineProvider) throws IOException {
        Objects.requireNonNull(lineProvider);
        config = new PermissiveConfig();
        var nextLine = lineProvider.nextLine();
        while (nextLine.isPresent()){
            var line = nextLine.get();
            switch (line){
                case EmptyLine ignored -> {/* do nothing */}
                case KeyLine keyLine -> treatKeyLine(keyLine);
                case ListValueLine listValueLine -> treatListValueLine(listValueLine);
                case KeyValueLine keyValueLine -> treatKeyValueLine(keyValueLine);
            }
        }
        return config;
    }


}
