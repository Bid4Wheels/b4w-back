package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.ModifyUserDTO;
import com.b4w.b4wback.dto.UserDTO;
import com.b4w.b4wback.service.interfaces.UserService;
import com.b4w.b4wback.dto.auth.JwtResponse;
import com.b4w.b4wback.dto.auth.SignInRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Objects;

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
        userDTO = new CreateUserDTO("Nico", "Borja", "bejero7623@dusyum.com",
                "+5491154964341", "1Afjfslkjfl");
    }

    private HttpEntity<CreateUserDTO> createHttpEntity(CreateUserDTO createUserDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(createUserDTO, headers);
    }

    private HttpHeaders createHeaderWithToken(){
        String jwtToken = authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);
        return headers;
    }

    private HttpEntity<ModifyUserDTO> createHttpEntity(ModifyUserDTO modifyUserDTO){
        HttpHeaders headers = createHeaderWithToken();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(modifyUserDTO, headers);
    }

    private  String authenticateAndGetToken(SignInRequest signInRequest){
        String loginURL ="/auth/login";
        ResponseEntity<JwtResponse> response = restTemplate.exchange(loginURL, HttpMethod.POST,
                new HttpEntity<>(signInRequest), JwtResponse.class);
        return Objects.requireNonNull(response.getBody()).getToken();
    }
    @Test
    void Test001_UserControllerWhenPostNewUserWithValidDTOShouldCreateUserCorrectly() {
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
    void Test002_UserControllerWhenPostNewUserWithDTOMissingNameShouldRespondBadRequest() {
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
    void Test003_UserControllerWhenPostNewUserWithDTONameEmptyShouldRespondBadRequest() {
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
    void Test004_UserControllerWhenPostNewUserWithDTOMissingLastNameShouldRespondBadRequest() {
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
    void Test005_UserControllerWhenPostNewUserWithDTOEmptyLastNameShouldRespondBadRequest() {
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
    void Test006_UserControllerWhenPostNewUserWithDTOMissingEmailShouldRespondBadRequest() {
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
    void Test007_UserControllerWhenPostNewUserWithDTOEmptyEmailShouldRespondBadRequest() {
        userDTO.setEmail("");

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("email"));
        assertTrue(error.contains("email can't be blank"));
    }


    @Test
    void Test008_UserControllerWhenPostNewUserWithDTOMissingPhoneNumberShouldRespondBadRequest() {
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
    void Test009_UserControllerWhenPostNewUserWithDTOMissingPasswordShouldRespondBadRequest() {
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
    void Test010_UserControllerWhenPostNewUserWithDTOEmptyPassWordShouldRespondBadRequest() {
        userDTO.setPassword("");

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("password"));
        assertTrue(error.contains("password can't be blank"));
    }

    @Test
    void Test011_UserControllerWhenGetUserByIdShouldReturnUserCorrectly() {
        ResponseEntity<CreateUserDTO> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), CreateUserDTO.class);

        CreateUserDTO createdUser = postUserResponse.getBody();
        assertEquals(HttpStatus.CREATED, postUserResponse.getStatusCode());
        assertNotNull(createdUser);
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()));
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);
        ResponseEntity<UserDTO> getUserResponse = restTemplate.exchange(baseUrl + "/1",
                HttpMethod.GET, new HttpEntity<>(headers), UserDTO.class);

        UserDTO getUser = getUserResponse.getBody();
        assertEquals(HttpStatus.OK, getUserResponse.getStatusCode());
        assertNotNull(getUser);
        assertEquals(createdUser.getName(), getUser.getName());
        assertEquals(createdUser.getLastName(), getUser.getLastName());
        assertEquals(createdUser.getEmail(), getUser.getEmail());
        assertEquals(createdUser.getPhoneNumber(), getUser.getPhoneNumber());
    }

    @Test
    void Test012_UserControllerWhenGetUserByIdWithInvalidIdShouldRespondNotFound() {
        ResponseEntity<CreateUserDTO> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), CreateUserDTO.class);

        CreateUserDTO createdUser = postUserResponse.getBody();
        assertEquals(HttpStatus.CREATED, postUserResponse.getStatusCode());
        assertNotNull(createdUser);
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()));
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);
        ResponseEntity<String> getUserResponse = restTemplate.exchange(baseUrl + "/2",
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatus.NOT_FOUND, getUserResponse.getStatusCode());
        assertEquals(getUserResponse.getBody(),"User not found.");
    }

    @Test
    void Test013_UserControllerWhenModifyingUserWithCorrectDataReturnsStatus200(){
        restTemplate.exchange(baseUrl, HttpMethod.POST, createHttpEntity(userDTO), CreateUserDTO.class);
        ResponseEntity<String> modUserResponse = restTemplate.exchange(baseUrl+"/1",
                HttpMethod.PATCH,
                createHttpEntity(new ModifyUserDTO("Pedro", "Goliat", "+5491112345678")),
                String.class);
        assertEquals(HttpStatus.OK, modUserResponse.getStatusCode());
    }

    @Test
    void Test015_UserControllerWhenModifyingUserWithNullNameReturnsStatus400(){
        restTemplate.exchange(baseUrl, HttpMethod.POST, createHttpEntity(userDTO), CreateUserDTO.class);
        ResponseEntity<String> modUserResponse = restTemplate.exchange(baseUrl+"/1",
                HttpMethod.PATCH,
                createHttpEntity(new ModifyUserDTO(null, "Goliat", "+5491112345678")),
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, modUserResponse.getStatusCode());
        String error = modUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("name can't be blank"));
    }

    @Test
    void Test016_UserControllerWhenModifyingUserWithNullLastNameReturnsStatus400(){
        restTemplate.exchange(baseUrl, HttpMethod.POST, createHttpEntity(userDTO), CreateUserDTO.class);
        ResponseEntity<String> modUserResponse = restTemplate.exchange(baseUrl+"/1",
                HttpMethod.PATCH,
                createHttpEntity(new ModifyUserDTO("Pedro", null, "+5491112345678")),
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, modUserResponse.getStatusCode());
        String error = modUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("last name can't be blank"));
    }

    @Test
    void Test017_UserControllerWhenModifyingUserWithNullPhoneReturnsStatus400(){
        restTemplate.exchange(baseUrl, HttpMethod.POST, createHttpEntity(userDTO), CreateUserDTO.class);
        ResponseEntity<String> modUserResponse = restTemplate.exchange(baseUrl+"/1",
                HttpMethod.PATCH,
                createHttpEntity(new ModifyUserDTO("Pedro", "Goliat", null)),
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, modUserResponse.getStatusCode());
        String error = modUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("phone number can't be blank"));
    }


}
