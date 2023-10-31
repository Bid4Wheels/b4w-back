package com.b4w.b4wback.refactor.user;

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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserAuthenticationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    private final String baseUrl="/auth";
    CreateUserDTO userDTO;
    @Autowired
    UserService userService;
    @BeforeEach
    public void setUp(){
        userDTO = new CreateUserDTO("User", "Test", "user@test.com",
                "+5491112345678", "Test1234$");
    }

    @Test
    void Test11_WhenReceivesExistingUserWithNoInvalidCredentialsShouldReturnStatusOK(){
        SignInRequest signInRequest=new SignInRequest(userDTO.getEmail(), userDTO.getPassword());
        userService.createUser(userDTO);
        ResponseEntity<JwtResponse> response = restTemplate.exchange(baseUrl + "/login", HttpMethod.POST,
                new HttpEntity<>(signInRequest), JwtResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());// Body must not be null
        assertThat(response.getBody().getToken(), Matchers.notNullValue()); //Token must not be null

    }


    @Test
    void Test12_WhenReceivesExistingUserWithInvalidPasswordShouldReturnStatusUNAUTHORIZED(){
        SignInRequest signInRequest=new SignInRequest(userDTO.getEmail(), "fail");
        userService.createUser(userDTO);
        ResponseEntity<String> response=restTemplate.postForEntity(baseUrl+"/login",signInRequest, String.class);
        assertEquals(response.getStatusCode(),HttpStatus.UNAUTHORIZED);
    }
}
