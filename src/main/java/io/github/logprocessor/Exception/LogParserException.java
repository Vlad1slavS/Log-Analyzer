package io.github.logprocessor.Exception;

public class LogParserException extends Exception {
    public LogParserException(String message) {
        super(message);
    }

    public LogParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
