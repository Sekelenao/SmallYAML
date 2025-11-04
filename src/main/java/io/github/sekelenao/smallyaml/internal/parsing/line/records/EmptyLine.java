package io.github.sekelenao.smallyaml.internal.parsing.line.records;

public final class EmptyLine implements Line {

    public static final EmptyLine SINGLE_INSTANCE = new EmptyLine();

    private EmptyLine(){
        // Private constructor to allow only one allocation
    }

}