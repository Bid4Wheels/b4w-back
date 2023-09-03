package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.service.interfaces.S3Service;
import com.b4w.b4wback.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    private final S3Service s3Service;
    public UserController(UserService userService,S3Service s3Service) {this.userService = userService; this.s3Service=s3Service;}

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

    @PatchMapping()
    public ResponseEntity<?> createPasswordCodeForId(@Valid @RequestBody PasswordChangerDTO passwordChangerDTO){
        userService.createPasswordCodeForId(passwordChangerDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/password")
    public ResponseEntity<?> checkPasswordCode(@RequestBody GetPasswordCodeDTO passwordCodeDTO){
        userService.checkPasswordCode(passwordCodeDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO){
        userService.changePassword(changePasswordDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> modifyUser(@PathVariable long id, @Valid @RequestBody ModifyUserDTO modifyUserDTO){
        userService.modifyUser(id, modifyUserDTO);
        return  ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/image-url")
    public ResponseEntity<?>uploadImageUser(@RequestHeader("Authorization") String token){
        String imageUrl=s3Service.getUploadURL(token);
        return ResponseEntity.status(HttpStatus.OK).body(imageUrl);
    }
}
