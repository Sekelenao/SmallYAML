module SmallYAML.test {
    requires SmallYAML;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;

    opens io.github.sekelenao.smallyaml.test.util to org.junit.platform.commons;
    opens io.github.sekelenao.smallyaml.test.internal.parsing.parser to org.junit.platform.commons;
    opens io.github.sekelenao.smallyaml.test.internal.parsing.provider to org.junit.platform.commons;
    opens io.github.sekelenao.smallyaml.test.internal.collection to org.junit.platform.commons;
}
