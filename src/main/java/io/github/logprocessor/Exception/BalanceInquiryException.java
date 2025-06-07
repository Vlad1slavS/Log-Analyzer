package io.github.logprocessor.Exception;

public class BalanceInquiryException extends RuntimeException {
    public BalanceInquiryException(String message) {
        super(message);
    }

    public BalanceInquiryException(String message, Throwable cause) {
        super(message, cause);
    }
}
