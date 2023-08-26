package com.b4w.b4wback.exception;

public class PasswordCodeDoesNotMatchException extends RuntimeException {
    public PasswordCodeDoesNotMatchException(String message) {
        super(message);
    }
}
