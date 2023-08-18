package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.service.UserService;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/user")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserDTO user){
        val newUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable long id){
        try {
            val user = userService.getUserById(id);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
