package io.github.sekelenao.smallyaml.internal.parsing.line.records;

public sealed interface Line permits EmptyLine, KeyLine, KeyValueLine, ListValueLine {

}