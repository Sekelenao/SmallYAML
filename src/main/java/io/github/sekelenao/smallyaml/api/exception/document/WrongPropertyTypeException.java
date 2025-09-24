package io.github.sekelenao.smallyaml.api.exception.document;

import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.exception.SmallYAMLException;

public class WrongPropertyTypeException extends SmallYAMLException {

    private WrongPropertyTypeException(String message) {
        super(message);
    }

    public static WrongPropertyTypeException withExpected(Property.Type expectedType){
        return new WrongPropertyTypeException(
            switch (expectedType){
                case Property.Type.SINGLE -> "Expected single value but was multiple values";
                case Property.Type.MULTIPLE -> "Expected multiple values but was single value";
            }
        );
    }

}
