package io.github.sekelenao.smallyaml.api.exception.document;

import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.exception.SmallYAMLException;

public class WrongTypeException extends SmallYAMLException {

    public enum Type { SINGLE, MULTIPLE }

    private WrongTypeException(String message) {
        super(message);
    }

    public static WrongTypeException withExpected(Property.Type expectedType){
        return new WrongTypeException(
            switch (expectedType){
                case Property.Type.SINGLE -> "Expected single value but was multiple values";
                case Property.Type.MULTIPLE -> "Expected multiple values but was single value";
            }
        );
    }

}
