package io.github.sekelenao.smallyaml.api.exception.parsing;

import io.github.sekelenao.smallyaml.api.exception.SmallYAMLException;

import java.util.Objects;

/**
 * Exception class representing errors encountered during the parsing process
 * within the SmallYAML framework. This exception is designed to provide
 * specific error messages for different types of parsing issues such as
 * invalid values, keys, or indentation.
 */
public class ParsingException extends SmallYAMLException {

    private ParsingException(String message) {
        super(message);
    }

    /**
     * Constructs a {@link ParsingException} indicating that a provided value is invalid
     * based on the given details.
     *
     * @param details a description or explanation about what makes the value invalid
     * @param value   the value that caused the parsing error
     * @return a {@link ParsingException} containing the error details and invalid value
     *         to be thrown in parsing operations
     */
    public static ParsingException wrongValue(String details, String value){
        Objects.requireNonNull(details);
        Objects.requireNonNull(value);
        return new ParsingException("Invalid value: " + details + " for: '" + value + "'");
    }

    /**
     * Constructs a {@link ParsingException} that indicates an issue with the formatting or
     * content of a key in the parsing process.
     *
     * @param details a message providing specific details about what is wrong with the key
     * @param key     the key that caused the parsing error
     * @return a {@link ParsingException} containing details about the invalid key
     *         to be thrown during the parsing operation
     */
    public static ParsingException wrongKey(String details, String key){
        Objects.requireNonNull(details);
        Objects.requireNonNull(key);
        return new ParsingException("Invalid key: " + details + " for: '" + key + "'");
    }

    /**
     * Constructs a {@code ParsingException} indicating an error related to incorrect
     * or unexpected indentation in a parsed line.
     *
     * @param details a description or explanation about what makes the indentation invalid
     * @param line the line of text where the indentation error occurred
     * @return a {@code ParsingException} containing the error details and the problematic line
     *         to be thrown during parsing operations
     */
    public static ParsingException wrongIndentation(String details, String line){
        Objects.requireNonNull(details);
        Objects.requireNonNull(line);
        return new ParsingException("Invalid indentation: " + details + " for: '" + line + "'");
    }

}
