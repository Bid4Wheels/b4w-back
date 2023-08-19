package com.b4w.b4wback.authtest;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.auth.JwtResponse;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuthUserRepository;
import com.b4w.b4wback.util.UserGenerator;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private List<CreateUserDTO> users;
    PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
    @Autowired
    private AuthUserRepository authUserRepository;
    private final String baseUrl="/auth";
    @BeforeEach
    public void setUp(){
        users = UserGenerator.generateUsers();
        users.forEach(user -> authUserRepository.save(User.builder().email(user.getEmail()).name(user.getName()).lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber()).password(passwordEncoder.encode(user.getPassword())).build()));
    }

    @Test
    void Test001_AuthControllerWhenReceivesExistingUserWithNoInvalidCredentialsShouldReturnStatusOK(){
        for (CreateUserDTO user: users) {
            SignInRequest signInRequest=new SignInRequest(user.getEmail(), user.getPassword());
            ResponseEntity<JwtResponse> response = restTemplate.exchange(baseUrl + "/login", HttpMethod.POST,
                    new HttpEntity<>(signInRequest), JwtResponse.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());// Body must not be null
            assertThat(response.getBody().getToken(), Matchers.notNullValue()); //Token must not be null
        }
    }


    @Test
    void Test002_AuthControllerWhenReceivesExistingUserWithInvalidPasswordShouldReturnStatusNOTFOUND(){
        for (CreateUserDTO user: users) {
            SignInRequest signInRequest=new SignInRequest(user.getEmail(), "BAD_PASSWORD");
            ResponseEntity<String> response=restTemplate.postForEntity(baseUrl+"/login",signInRequest, String.class);
            assertEquals(response.getStatusCode(),HttpStatus.NOT_FOUND);
        }
    }
    @Test
    void Test003_AuthControllerWhenReceivesInvalidEmailWithCorrectPasswordShouldReturnStatusNOTFOUND(){
        for (CreateUserDTO user: users) {
            SignInRequest signInRequest=new SignInRequest("bad_email@gmail.com", user.getPassword());
            ResponseEntity<String> response=restTemplate.postForEntity(baseUrl+"/login",signInRequest, String.class);
            assertEquals(response.getStatusCode(),HttpStatus.NOT_FOUND);
        }
    }

    @Test
    void Test004_AuthControllerWhenReceivesNullFieldsShouldReturnStatusNOTFOUND(){
        SignInRequest signInRequest=new SignInRequest(null,null);
        ResponseEntity<String> response=restTemplate.postForEntity(baseUrl+"/login",signInRequest, String.class);
        assertEquals(response.getStatusCode(),HttpStatus.NOT_FOUND);
    }
}
