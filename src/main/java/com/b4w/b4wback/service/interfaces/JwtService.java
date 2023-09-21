package com.b4w.b4wback.service.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUsername(String token);
    Long extractId(String token);
    String generateToken(String email,Long userID);
    boolean isTokenValid(String token, UserDetails userDetails);
}
