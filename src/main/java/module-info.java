module SmallYAML {

    // Exception
    exports io.github.sekelenao.smallyaml.api.exception;
    exports io.github.sekelenao.smallyaml.api.exception.parsing;

    // Config
    exports io.github.sekelenao.smallyaml.api.config;

    // Parsing
    exports io.github.sekelenao.smallyaml.api.parsing;
    exports io.github.sekelenao.smallyaml.api.parsing.provider;

    // For tests
    exports io.github.sekelenao.smallyaml.internal.collection to SmallYAML.test;
    exports io.github.sekelenao.smallyaml.internal.parsing.line to SmallYAML.test;
    exports io.github.sekelenao.smallyaml.internal.parsing.parser to SmallYAML.test;
}