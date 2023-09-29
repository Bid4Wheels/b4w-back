package com.b4w.b4wback.util;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.auth.JwtResponse;
import com.b4w.b4wback.dto.auth.SignInRequest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

public class HttpEntityCreator {
    public static HttpHeaders createHeaderWithToken(CreateUserDTO userDTO, TestRestTemplate restTemplate){
        String jwtToken = authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);
        return headers;
    }

    private static String authenticateAndGetToken(SignInRequest signInRequest, TestRestTemplate restTemplate) {
        String loginURL = "/auth/login";
        ResponseEntity<JwtResponse> response = restTemplate.exchange(loginURL, HttpMethod.POST,
                new HttpEntity<>(signInRequest), JwtResponse.class);
        return Objects.requireNonNull(response.getBody()).getToken();
    }
}
