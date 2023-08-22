package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.auth.JwtResponse;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.service.interfaces.AuthenticationService;
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
        JwtResponse token=authenticationService.signIn(request);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

}
