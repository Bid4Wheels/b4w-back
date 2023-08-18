package com.b4w.b4wback.service.impl;

import com.b4w.b4wback.dto.auth.JwtResponse;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.repository.AuthUserRepository;
import com.b4w.b4wback.service.AuthenticationService;
import com.b4w.b4wback.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManger;
    private final AuthUserRepository authUserRepository;
    @Override
    public JwtResponse signin(SignInRequest request) {
        Authentication authentication=authenticationManger.authenticate(authenticationManger.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())));
        var jwt = jwtService.generateToken(authentication.getName());
        return JwtResponse.builder().token(jwt).build();
    }

    @Override
    public UserDetailsService userDetailsService() {
        return username -> authUserRepository.findUserByEmail(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
    }
}
