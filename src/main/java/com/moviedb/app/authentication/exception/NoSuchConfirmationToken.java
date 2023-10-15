package com.moviedb.app.authentication.exception;

public class NoSuchConfirmationToken extends RuntimeException {
    public NoSuchConfirmationToken(String message) {
        super(message);
    }

    public NoSuchConfirmationToken(String message, Throwable cause) {
        super(message, cause);
    }
}
