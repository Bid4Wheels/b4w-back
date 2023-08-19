package com.b4w.b4wback.service.impl;

import com.b4w.b4wback.repository.AuthUserRepository;
import com.b4w.b4wback.service.AuthUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserDetailServiceImpl implements AuthUserDetailService {
    private final AuthUserRepository authUserRepository;
    @Override
    public UserDetailsService userDetailsService() {
        return username -> authUserRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
    }
}
