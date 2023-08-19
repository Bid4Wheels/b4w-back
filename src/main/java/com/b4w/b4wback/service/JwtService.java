package com.b4w.b4wback.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUsername(String token);

    String generateToken(String email);
    boolean isTokenValid(String token, UserDetails userDetails);
}
