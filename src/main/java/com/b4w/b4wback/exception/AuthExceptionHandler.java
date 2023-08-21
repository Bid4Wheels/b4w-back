package com.b4w.b4wback.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<String> handleUserNotAuthenticated(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials.");
    }
}
