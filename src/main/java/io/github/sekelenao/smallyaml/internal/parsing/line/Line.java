package io.github.sekelenao.smallyaml.internal.parsing.line;

public sealed interface Line permits EmptyLine, KeyLine, KeyValueLine, ListValueLine {

}