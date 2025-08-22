module SmallYAML {
    // Exception
    exports io.github.sekelenao.smallyaml.api.exception;
    exports io.github.sekelenao.smallyaml.api.exception.parsing;
    exports io.github.sekelenao.smallyaml.api.exception.config;

    // Document
    exports io.github.sekelenao.smallyaml.api.document;
    exports io.github.sekelenao.smallyaml.api.document.property;

    // Parsing
    exports io.github.sekelenao.smallyaml.api.parsing.line.provider;

    // For tests
    exports io.github.sekelenao.smallyaml.internal.collection to SmallYAML.test;
    exports io.github.sekelenao.smallyaml.internal.parsing.line to SmallYAML.test;
    exports io.github.sekelenao.smallyaml.internal.parsing.parser to SmallYAML.test;
    exports io.github.sekelenao.smallyaml.internal.parsing to SmallYAML.test;

}