package com.b4w.b4wback.refactor.user;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.ModifyUserDTO;
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

import static com.b4w.b4wback.util.HttpEntityCreator.createHeaderWithToken;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserModificationTest {
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

    private HttpEntity<ModifyUserDTO> createHttpEntity(ModifyUserDTO modifyUserDTO){
        HttpHeaders headers = createHeaderWithToken(userDTO, restTemplate);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(modifyUserDTO, headers);
    }
    @Test
    void Test11_WhenModifyingUserWithCorrectDataReturnsStatus200(){
        restTemplate.exchange(baseUrl, HttpMethod.POST, createHttpEntity(userDTO), CreateUserDTO.class);
        ResponseEntity<String> modUserResponse = restTemplate.exchange(baseUrl+"/1",
                HttpMethod.PATCH,
                createHttpEntity(new ModifyUserDTO("Test", "Test", "+5491164340422")),
                String.class);
        assertEquals(HttpStatus.OK, modUserResponse.getStatusCode());
        Optional<User> user = userRepository.findByEmail(userDTO.getEmail());
        assertEquals("Test", user.get().getName());
        assertEquals("Test", user.get().getLastName());
        assertEquals("+5491164340422", user.get().getPhoneNumber());
    }

    @Test
    void Test12_WhenModifyingUserWithIncorrectDataReturnsStatus400(){
        restTemplate.exchange(baseUrl, HttpMethod.POST, createHttpEntity(userDTO), CreateUserDTO.class);
        ResponseEntity<String> modUserResponse = restTemplate.exchange(baseUrl+"/1",
                HttpMethod.PATCH,
                createHttpEntity(new ModifyUserDTO("Test", "Test", "123")),
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, modUserResponse.getStatusCode());
        Optional<User> user = userRepository.findByEmail(userDTO.getEmail());
        assertEquals("User", user.get().getName());
        assertEquals("Test", user.get().getLastName());
        assertEquals("+5491112345678", user.get().getPhoneNumber());
    }

//    @Test
//    void Test14_WhenModifyingUserWithNewImageReturnsStatus200(){
//
//    }
}
