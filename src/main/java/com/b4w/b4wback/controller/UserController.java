package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.UserDTO;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.service.JSONBuilder;
import com.b4w.b4wback.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {this.userService = userService;}

    @PostMapping
    public ResponseEntity<?> postNewUser(@Valid @RequestBody CreateUserDTO userDTO){
        val user = new UserDTO(userService.createUser(userDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add( "\""+fieldName + "\":\"" + errorMessage + "\"");
        });
        return JSONBuilder.ArrayToJSON(errors);
    }
}
