package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.auth.JwtResponse;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.service.AuthenticationService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    @PostMapping("/login")
    public ResponseEntity<JwtResponse>signIn(@RequestBody SignInRequest request){
        JwtResponse token=authenticationService.signin(request);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

}
