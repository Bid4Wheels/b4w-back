package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.GetPasswordCodeDTO;
import com.b4w.b4wback.dto.PasswordChangerDTO;
import com.b4w.b4wback.dto.UserDTO;
import com.b4w.b4wback.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable long id){
        val user = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> getPasswordChangerForId(@PathVariable long id, @Valid @RequestBody PasswordChangerDTO userDTO){
        val user = userService.getPasswordChangerForId(id, userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/password")
    public ResponseEntity<?> getPasswordCode(@RequestBody GetPasswordCodeDTO email){
        val user = userService.getPasswordCode(email);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
