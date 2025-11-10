module SmallYAML {
    // Exception
    exports io.github.sekelenao.smallyaml.api.exception;
    exports io.github.sekelenao.smallyaml.api.exception.parsing;
    exports io.github.sekelenao.smallyaml.api.exception.document;

    // Document
    exports io.github.sekelenao.smallyaml.api.document;
    exports io.github.sekelenao.smallyaml.api.document.property;

    // Line provider
    exports io.github.sekelenao.smallyaml.api.line.provider;

    // Mapping
    exports io.github.sekelenao.smallyaml.api.mapping;

    // Exports for tests
    exports io.github.sekelenao.smallyaml.internal.collection to SmallYAML.test;
    exports io.github.sekelenao.smallyaml.internal.parsing.booleans to SmallYAML.test;
    exports io.github.sekelenao.smallyaml.internal.parsing to SmallYAML.test;
    exports io.github.sekelenao.smallyaml.internal.util to SmallYAML.test;
    exports io.github.sekelenao.smallyaml.internal.parsing.line.provider to SmallYAML.test;
    exports io.github.sekelenao.smallyaml.internal.parsing.line.records to SmallYAML.test;
    exports io.github.sekelenao.smallyaml.internal.parsing.line.records.parser to SmallYAML.test;
    exports io.github.sekelenao.smallyaml.internal.parsing.line.records.parser.string to SmallYAML.test;
    exports io.github.sekelenao.smallyaml.internal.parsing.collector to SmallYAML.test;

    // Opens for tests
    opens io.github.sekelenao.smallyaml.internal.util to SmallYAML.test;
    opens io.github.sekelenao.smallyaml.api.document to SmallYAML.test;
    opens io.github.sekelenao.smallyaml.internal.parsing.collector to SmallYAML.test;
}