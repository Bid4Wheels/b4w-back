package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.auth.JwtResponse;
import com.b4w.b4wback.dto.auth.SignInRequest;

public interface AuthenticationService {
    JwtResponse signin(SignInRequest request);
}
