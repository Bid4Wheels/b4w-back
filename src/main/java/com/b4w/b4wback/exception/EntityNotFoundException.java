package com.b4w.b4wback.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
