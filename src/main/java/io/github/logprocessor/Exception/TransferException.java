package io.github.logprocessor.Exception;

public class TransferException extends RuntimeException {
    public TransferException(String message) {
        super(message);
    }

    public TransferException(String message, Throwable cause) {
        super(message, cause);
    }
}
