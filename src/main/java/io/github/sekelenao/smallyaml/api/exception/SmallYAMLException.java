package io.github.sekelenao.smallyaml.api.exception;

public class SmallYAMLException extends RuntimeException {

    public SmallYAMLException(String message, Throwable cause) {
        super(message, cause);
    }

    public SmallYAMLException(String message) {
        super(message);
    }

}
