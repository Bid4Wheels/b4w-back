package com.b4w.b4wback.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class DefaultExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(
                        f -> {
                            String msg = f.getDefaultMessage();
                            return f.getField() + ": " + msg;
                        })
                .reduce("", (a, s) -> a + s + '\n');
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected String handleDataIntegrityViolation(DataIntegrityViolationException ex){
        return ex.getMessage();
    }


    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }


    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<String> handleUserNotAuthenticated(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials.");
    }


    @ExceptionHandler(BadRequestParametersException.class)
    protected ResponseEntity<String> handleCredentialsException(BadRequestParametersException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(UrlAlreadySentException.class)
    protected ResponseEntity<String> handleUrlAlreadySentException(UrlAlreadySentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
