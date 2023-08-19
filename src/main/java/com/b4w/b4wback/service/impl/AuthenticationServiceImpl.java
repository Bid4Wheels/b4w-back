package com.b4w.b4wback.service.impl;

import com.b4w.b4wback.dto.auth.JwtResponse;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.service.AuthenticationService;
import com.b4w.b4wback.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManger;
    @Override
    public JwtResponse signin(SignInRequest request) {
        Authentication authentication=authenticationManger.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        var jwt = jwtService.generateToken(authentication.getName());
        return JwtResponse.builder().token(jwt).build();
    }

}
