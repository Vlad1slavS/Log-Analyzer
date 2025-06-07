package io.github.logprocessor.Exception;

public class WithdrawalException extends RuntimeException {
  public WithdrawalException(String message) {
    super(message);
  }

  public WithdrawalException(String message, Throwable cause) {
    super(message, cause);
  }
}
