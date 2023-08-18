package com.b4w.b4wback.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;

@ControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<String> handleUserNotAuthenticated(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Credentials.");
    }
}
