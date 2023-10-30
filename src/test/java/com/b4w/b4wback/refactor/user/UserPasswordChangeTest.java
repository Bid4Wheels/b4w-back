package com.b4w.b4wback.refactor.user;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.repository.UserRepository;
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
public class UserPasswordChangeTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    private final String baseUrl = "/user";
    private CreateUserDTO userDTO;
    private GetPasswordCodeDTO getPasswordCodeDTO;
    private PasswordChangerDTO passwordChangerDTO;
    @BeforeEach
    public void setup() {
        userDTO = new CreateUserDTO("User", "Test", "test@mail.com",
                "+5491112345678", "Test1234$");
        getPasswordCodeDTO = new GetPasswordCodeDTO("test@mail.com", 123456);
        passwordChangerDTO = new PasswordChangerDTO("test@mail.com");
    }

    private HttpEntity<CreateUserDTO> createHttpEntity(CreateUserDTO createUserDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(createUserDTO, headers);
    }

    @Test
    void Test11_WhenGetPasswordCodeForIdWithValidIdShouldReturnOk() {
        restTemplate.exchange(baseUrl, HttpMethod.POST,createHttpEntity(userDTO), CreateUserDTO.class);

        ResponseEntity<PasswordChangerDTO> getUserResponse = restTemplate.exchange(baseUrl,
                HttpMethod.PATCH,new HttpEntity<>(passwordChangerDTO), PasswordChangerDTO.class);

        assertEquals(HttpStatus.OK, getUserResponse.getStatusCode());
        assertNotNull(userRepository.findByEmail(userDTO.getEmail()).get().getPasswordCode());
    }

    @Test
    void Test12_WhenGetPasswordCodeForIdWithInvalidIdShouldRespondNotFound() {
        ResponseEntity<String> checkPasswordResponse = restTemplate.exchange(baseUrl,
                HttpMethod.PATCH, new HttpEntity<>(new PasswordChangerDTO("test2@mail.com")), String.class);

        assertEquals(HttpStatus.NOT_FOUND, checkPasswordResponse.getStatusCode());
    }

//    @Test
//    void Test23_UserControllerWhenChangePasswordAndDeletedUserShouldReturnBadRequest(){
//        restTemplate.exchange(baseUrl, HttpMethod.POST,new HttpEntity<>(userDTO), CreateUserDTO.class);
//
//        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
//        HttpHeaders headers= new HttpHeaders();
//        headers.set("Authorization","Bearer " +jwtToken);
//
//        restTemplate.exchange(baseUrl,HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
//
//        ResponseEntity<ChangePasswordDTO> changePasswordResponse = restTemplate.exchange(baseUrl + "/password", HttpMethod.PATCH,
//                new HttpEntity<>(changePasswordDTO), ChangePasswordDTO.class);
//
//        assertEquals(HttpStatus.BAD_REQUEST, changePasswordResponse.getStatusCode());
//    }
    @Test
    void Test26_WhenCheckingPasswordCodeAndEqualsShouldReturnOK(){
        restTemplate.exchange(baseUrl, HttpMethod.POST,createHttpEntity(userDTO), CreateUserDTO.class);

        restTemplate.exchange(baseUrl,HttpMethod.PATCH, new HttpEntity<>(passwordChangerDTO), String.class);

        int pas = userRepository.findByEmail(userDTO.getEmail()).get().getPasswordCode();
        getPasswordCodeDTO.setPasswordCode(pas);

        ResponseEntity<String> checkPasswordCodeResponse = restTemplate.exchange(baseUrl + "/password", HttpMethod.POST,
                new HttpEntity<>(getPasswordCodeDTO), String.class);

        assertEquals(HttpStatus.OK, checkPasswordCodeResponse.getStatusCode());
    }
    @Test
    void Test37_WhenCheckingPasswordCodeAndNotEqualsShouldReturnBadRequest(){
        restTemplate.exchange(baseUrl, HttpMethod.POST,createHttpEntity(userDTO), CreateUserDTO.class);

        restTemplate.exchange(baseUrl + "/1", HttpMethod.PATCH,new HttpEntity<>(passwordChangerDTO), PasswordChangerDTO.class);

        ResponseEntity<String> checkPasswordCodeResponse = restTemplate.exchange(baseUrl + "/password", HttpMethod.POST,
                new HttpEntity<>(getPasswordCodeDTO), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, checkPasswordCodeResponse.getStatusCode());
        assertEquals(checkPasswordCodeResponse.getBody(),"Password code does not match");
    }
}
