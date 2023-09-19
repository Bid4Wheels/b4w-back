package com.b4w.b4wback.service;

import com.b4w.b4wback.service.interfaces.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application.properties")
class JwtServiceImplTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Value("${token.signing.key}")
    private String jwtSigningKey;
    @Mock
    private UserDetails userDetails;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtService, "jwtSigningKey", jwtSigningKey);
    }

    @Test
    void Test001_JwtServiceImplWhenExtractUsernameShouldAssertEquals() {
        String token = generateToken();
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("user@example.com", extractedUsername);
    }

    @Test
    void Test002_JwtServiceImplWhenGenerateTokenShouldAssertNotNull() {
        String token = jwtService.generateToken("user@example.com",1L);
        assertNotNull(token);
    }

    @Test
    void Test003_JwtServiceImplWhenTokenIsValidShouldAssertTrue() {
        String token = generateToken();
        when(userDetails.getUsername()).thenReturn("user@example.com");
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void Test004_JwtServiceImplWhenTokenIsNotValidShouldAssertFalse() {
        String token = generateToken();
        when(userDetails.getUsername()).thenReturn("another@example.com");
        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void Test005_JwtServiceImplWhenExtractUserIdShouldReturnEquals(){
        String token = generateToken();
        Long extractedId = jwtService.extractId(token);
        assertEquals(1L, extractedId);
    }

    private String generateToken() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", 1L);
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject("user@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}

