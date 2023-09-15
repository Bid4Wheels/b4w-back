package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.auth.JwtResponse;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.AuthenticationService;
import com.b4w.b4wback.service.interfaces.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManger;
    private final UserRepository userRepository;
    @Override
    public JwtResponse signIn(SignInRequest request) {
        Authentication authentication=authenticationManger.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        Optional<User> userWithEmail=userRepository.findByEmail(request.getEmail());
        var jwt = jwtService.generateToken(authentication.getName(),userWithEmail.get().getId());
        //Is already checked with the authentication Manager
        return JwtResponse.builder().token(jwt).id(userWithEmail.get().getId()).build();
    }

}
