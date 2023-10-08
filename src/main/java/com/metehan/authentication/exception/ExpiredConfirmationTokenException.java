package com.metehan.authentication.exception;

public class ExpiredConfirmationTokenException extends RuntimeException {
    public ExpiredConfirmationTokenException(String message) {
        super(message);
    }

    public ExpiredConfirmationTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
