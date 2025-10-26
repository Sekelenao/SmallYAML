package io.github.sekelenao.smallyaml.api.exception.document;

import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.exception.SmallYAMLException;

/**
 * Exception thrown to indicate that a property has a type mismatch based on the expected type.
 * <p>
 * This exception is used to signal a discrepancy between the actual property type
 * and the expected type during operations that require a specific property cardinality.
 * For instance, it may be thrown when an operation expects a single-value property
 * but encounters a multi-value property instead, or vice versa.
 * <p>
 * The exception message provides additional context on whether the mismatch was with
 * a single or multiple value property.
 */
public class WrongPropertyTypeException extends SmallYAMLException {

    private WrongPropertyTypeException(String message) {
        super(message);
    }

    /**
     * Constructs a {@code WrongPropertyTypeException} with a message indicating the expected property type.
     * This method is used to signal a type mismatch between the expected and actual property types.
     *
     * @param expectedType the expected type of the property, which can either be {@code Property.Type.SINGLE}
     *                     or {@code Property.Type.MULTIPLE}
     * @return a new instance of {@code WrongPropertyTypeException} containing a message that specifies
     *         the expected property type and the mismatch
     */
    public static WrongPropertyTypeException withExpected(Property.Type expectedType){
        return new WrongPropertyTypeException(
            switch (expectedType){
                case Property.Type.SINGLE -> "Expected single value but was multiple values";
                case Property.Type.MULTIPLE -> "Expected multiple values but was single value";
            }
        );
    }

}
