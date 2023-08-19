package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private final String baseUrl = "/user";
    private CreateUserDTO userDTO;

    @BeforeEach
    public void setup() {
        userDTO = new CreateUserDTO("Nico", "Borja", "borja@gmail.com",
                8493123, "1Afjfslkjfl");
    }

    private HttpEntity<CreateUserDTO> createHttpEntity(CreateUserDTO createUserDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(createUserDTO, headers);
    }

    @Test
    void Test001_UserControllerWhenPostNewUserWithValidDTOShouldCreateUserCorrectly(){
        ResponseEntity<UserDTO> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), UserDTO.class);

        UserDTO createdUser = postUserResponse.getBody();
        assertEquals(HttpStatus.CREATED, postUserResponse.getStatusCode());
        assertNotNull(createdUser);
        assertEquals(userDTO.getName(), createdUser.getName());
        assertEquals(userDTO.getLastName(), createdUser.getLastName());
        assertEquals(userDTO.getEmail(), createdUser.getEmail());
        assertEquals(userDTO.getPhoneNumber(), createdUser.getPhoneNumber());
    }

    @Test
    void Test002_UserControllerWhenPostNewUserWithDTOMissingNameShouldRespondBadRequest(){
        userDTO.setName(null);

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("name"));
        assertTrue(error.contains("name can't be blank"));
    }

    @Test
    void Test003_UserControllerWhenPostNewUserWithDTONameEmptyShouldRespondBadRequest(){
        userDTO.setName("");

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("name"));
        assertTrue(error.contains("name can't be blank"));
    }

    @Test
    void Test004_UserControllerWhenPostNewUserWithDTOMissingLastNameShouldRespondBadRequest(){
        userDTO.setLastName(null);

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("lastName"));
        assertTrue(error.contains("last name can't be blank"));
    }

    @Test
    void Test005_UserControllerWhenPostNewUserWithDTOEmptyLastNameShouldRespondBadRequest(){
        userDTO.setLastName("");

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("lastName"));
        assertTrue(error.contains("last name can't be blank"));
    }

    @Test
    void Test006_UserControllerWhenPostNewUserWithDTOMissingEmailShouldRespondBadRequest(){
        userDTO.setEmail(null);

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("email"));
        assertTrue(error.contains("email can't be blank"));
    }

    @Test
    void Test007_UserControllerWhenPostNewUserWithDTOEmptyEmailShouldRespondBadRequest(){
        userDTO.setEmail("");

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("email"));
        assertTrue(error.contains("email can't be blank"));
    }

    //TODO: should implement all validation error of mail if already tested validation?

    @Test
    void Test008_UserControllerWhenPostNewUserWithDTOMissingPhoneNumberShouldRespondBadRequest(){
        userDTO.setPhoneNumber(null);

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("phoneNumber"));
        assertTrue(error.contains("phone number can't be blank"));
    }

    @Test
    void Test009_UserControllerWhenPostNewUserWithDTOMissingPasswordShouldRespondBadRequest(){
        userDTO.setPassword(null);

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("password"));
        assertTrue(error.contains("password can't be blank"));
    }

    @Test
    void Test010_UserControllerWhenPostNewUserWithDTOEmptyPassWordShouldRespondBadRequest(){
        userDTO.setPassword("");

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("password"));
        assertTrue(error.contains("password can't be blank"));
    }
    //TODO: should implement all validation error of password if already tested validation?

}
