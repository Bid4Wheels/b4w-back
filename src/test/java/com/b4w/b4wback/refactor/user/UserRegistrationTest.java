package com.b4w.b4wback.refactor.user;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static com.b4w.b4wback.util.HttpEntityCreator.authenticateAndGetToken;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRegistrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    private final String baseUrl = "/user";
    private CreateUserDTO userDTO;
    @BeforeEach
    public void setup() {
        userDTO = new CreateUserDTO("User", "Test", "user@test.com",
                "+5491112345678", "Test1234$");
    }

    private HttpEntity<CreateUserDTO> createHttpEntity(CreateUserDTO createUserDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(createUserDTO, headers);
    }


    @Test
    void Test11_WhenPostNewUserWithValidDTOShouldCreateUserCorrectly() {
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
    void Test12_WhenPostNewUserWithDTOMissingNameShouldRespondBadRequest() {
        userDTO.setName(null);

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("name can't be blank"));
    }

    @Test
    void Test13_WhenPostNewUserWithDTOMissingLastNameShouldRespondBadRequest() {
        userDTO.setLastName(null);

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("last name can't be blank"));
    }

    @Test
    void Test14_WhenPostNewUserWithDTOMissingPasswordShouldRespondBadRequest() {
        userDTO.setPassword(null);

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        System.out.println(error);
        assertTrue(error.contains("password can't be blank"));
    }

    @Test
    void Test16_WhenPostNewUserWithDTOInvalidPhoneShouldRespondBadRequest() {
        userDTO.setPhoneNumber("12345");

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("invalid phone number size, require at least 14"));
    }
    @Test
    void Test17_WhenPostNewUserWithDTOInvalidEmailShouldRespondBadRequest() {
        userDTO.setEmail("user@test");

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("email"));
        assertTrue(error.contains("must be a well-formed email address\n"));
    }

    @Test
    void Test18_WhenPostNewUserWithDTOAllNullShouldRespondBadRequest() {
        userDTO.setName(null);
        userDTO.setLastName(null);
        userDTO.setEmail(null);
        userDTO.setPhoneNumber(null);
        userDTO.setPassword(null);

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
        assertTrue(error.contains("name: name can't be blank"));
        assertTrue(error.contains("lastName: last name can't be blank"));
        assertTrue(error.contains("email: email can't be blank"));
        assertTrue(error.contains("password: password can't be blank"));
        assertTrue(error.contains("phoneNumber: phone number can't be blank"));
    }

    @Test
    void test311_WhenCreateUserWithAllValidShouldBeFoundOnDB(){
        restTemplate.exchange(baseUrl, HttpMethod.POST,createHttpEntity(userDTO), UserDTO.class);
        Optional<User> user = userRepository.findByEmail(userDTO.getEmail());
        assertEquals("User", user.get().getName());
        assertEquals("Test", user.get().getLastName());
        assertEquals("user@test.com", user.get().getEmail());
        assertEquals("+5491112345678", user.get().getPhoneNumber());
    }

    @Test
    void test412_WhenCreateUserWithEmailAlreadyExistShouldRespondBadRequest(){
        restTemplate.exchange(baseUrl, HttpMethod.POST,createHttpEntity(userDTO), UserDTO.class);
        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
    }

    @Test
    void test413_WhenCreateUserWithDeletedEmailShouldRespondBadRequest(){
        restTemplate.exchange(baseUrl, HttpMethod.POST,createHttpEntity(userDTO), UserDTO.class);

        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);

        restTemplate.exchange(baseUrl,HttpMethod.DELETE, new HttpEntity<>(headers), String.class);

        ResponseEntity<String> postUserResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(userDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postUserResponse.getStatusCode());
        String error = postUserResponse.getBody();
        assertNotNull(error);
    }


}
