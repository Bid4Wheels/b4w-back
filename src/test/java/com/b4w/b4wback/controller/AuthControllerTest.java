package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.auth.JwtResponse;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.service.interfaces.UserService;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private final String baseUrl="/auth";
    CreateUserDTO userDTO;
    @Autowired
    UserService userService;
    @BeforeEach
    public void setUp(){
        userDTO=new CreateUserDTO("Nico", "Borja", "borja@gmail.com",
                "+5491154964341", "1Afjfslkjfl");
    }

    @Test
    void Test001_AuthControllerWhenReceivesExistingUserWithNoInvalidCredentialsShouldReturnStatusOK(){
            SignInRequest signInRequest=new SignInRequest(userDTO.getEmail(), userDTO.getPassword());
            userService.createUser(userDTO);
            ResponseEntity<JwtResponse> response = restTemplate.exchange(baseUrl + "/login", HttpMethod.POST,
                    new HttpEntity<>(signInRequest), JwtResponse.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());// Body must not be null
            assertThat(response.getBody().getToken(), Matchers.notNullValue()); //Token must not be null

    }


    @Test
    void Test002_AuthControllerWhenReceivesExistingUserWithInvalidPasswordShouldReturnStatusUNAUTHORIZED(){
            SignInRequest signInRequest=new SignInRequest(userDTO.getEmail(), "BAD_PASSWORD");
            userService.createUser(userDTO);
            ResponseEntity<String> response=restTemplate.postForEntity(baseUrl+"/login",signInRequest, String.class);
            assertEquals(response.getStatusCode(),HttpStatus.UNAUTHORIZED);

    }
    @Test
    void Test003_AuthControllerWhenReceivesInvalidEmailWithCorrectPasswordShouldReturnStatusUNAUTHORIZED(){
            SignInRequest signInRequest=new SignInRequest("bad_email@gmail.com", userDTO.getPassword());
            userService.createUser(userDTO);
            ResponseEntity<String> response=restTemplate.postForEntity(baseUrl+"/login",signInRequest, String.class);
            assertEquals(response.getStatusCode(),HttpStatus.UNAUTHORIZED);

    }

    @Test
    void Test004_AuthControllerWhenReceivesPasswordNullFieldReturnStatusUNAUTHORIZED(){
        SignInRequest signInRequest=new SignInRequest(null, userDTO.getPassword());
        userService.createUser(userDTO);
        ResponseEntity<String> response=restTemplate.postForEntity(baseUrl+"/login",signInRequest, String.class);
        assertEquals(response.getStatusCode(),HttpStatus.UNAUTHORIZED);
    }

    @Test
    void Test005_AuthControllerWhenReceivesEmailNullFieldReturnStatusUNAUTHORIZED(){
        SignInRequest signInRequest=new SignInRequest(userDTO.getEmail(), null);
        userService.createUser(userDTO);
        ResponseEntity<String> response=restTemplate.postForEntity(baseUrl+"/login",signInRequest, String.class);
        assertEquals(response.getStatusCode(),HttpStatus.UNAUTHORIZED);
    }

    @Test
    void Test006_AuthControllerWhenReceivesNullFieldsReturnStatusUNAUTHORIZED(){
        SignInRequest signInRequest=new SignInRequest(null, null);
        userService.createUser(userDTO);
        ResponseEntity<String> response=restTemplate.postForEntity(baseUrl+"/login",signInRequest, String.class);
        assertEquals(response.getStatusCode(),HttpStatus.UNAUTHORIZED);
    }
}
