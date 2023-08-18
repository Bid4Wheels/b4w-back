package com.b4w.b4wback.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUsername(String token);

    String generateToken(String email);
    //7 dias token.
    boolean isTokenValid(String token, UserDetails userDetails);
}
