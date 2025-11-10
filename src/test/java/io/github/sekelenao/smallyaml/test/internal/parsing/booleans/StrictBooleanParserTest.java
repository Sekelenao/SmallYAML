package io.github.sekelenao.smallyaml.test.internal.parsing.booleans;

import io.github.sekelenao.smallyaml.api.exception.parsing.BooleanFormatException;
import io.github.sekelenao.smallyaml.internal.parsing.booleans.StrictBooleanParser;
import io.github.sekelenao.smallyaml.test.util.Reflections;
import io.github.sekelenao.smallyaml.test.util.constant.TestingTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Tag(TestingTag.PARSING)
@Tag(TestingTag.INTERNAL)
final class StrictBooleanParserTest {

    @Test
    @DisplayName("Assertions")
    void assertions(){
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> StrictBooleanParser.parse(null)),
                () -> Reflections.ensureIsUtilityClass(StrictBooleanParser.class)
        );
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"wrong_value", "", "tru", "on", "off", "yes", "no", " "})
    @DisplayName("Strict parsing for wrong values")
    void strictParsingForValidValues(String rawValue) {
        assertThrows(BooleanFormatException.class, () -> StrictBooleanParser.parse(rawValue));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"TRUE", "true", "TrUe"})
    @DisplayName("Strict parsing for true values")
    void strictParsingForTrueValues(String rawValue){
        assertTrue(StrictBooleanParser.parse(rawValue));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"FALSE", "false", "FaLsE"})
    @DisplayName("Strict parsing for false values")
    void strictParsingForFalseValues(String rawValue){
        assertFalse(StrictBooleanParser.parse(rawValue));
    }

}
