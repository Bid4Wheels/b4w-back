package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.auth.JwtResponse;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.model.Tag;
import com.b4w.b4wback.repository.TagRepository;
import com.b4w.b4wback.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class TagControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseUrl = "/tag";
    @Autowired
    private UserService userService;

    private String token;

    @Autowired
    private TagRepository tagRepository;
    @BeforeEach
    public void setup() {
        CreateUserDTO userDTO = new CreateUserDTO("Nico", "Borja", "bejero7623@dusyum.com",
                "+5491112345678", "1Afjfslkjfl");
        SignInRequest signInRequest=new SignInRequest(userDTO.getEmail(), userDTO.getPassword());
        userService.createUser(userDTO);
        token=authenticateAndGetToken(signInRequest);}

    private String authenticateAndGetToken(SignInRequest signInRequest) {
        String loginURL = "/auth/login";
        ResponseEntity<JwtResponse> response = restTemplate.exchange(loginURL, HttpMethod.POST,
                new HttpEntity<>(signInRequest), JwtResponse.class);
        return Objects.requireNonNull(response.getBody()).getToken();
    }
    @Test
    void Test001_TagControllerGetAllTagsShouldReturnStatusOK(){
        List<String> tags = List.of("Tag1", "Tag2", "Tag3", "Tag4", "Tag5");
        tagRepository.saveAll(tags.stream().map(Tag::new).toList());
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        ResponseEntity<List<Tag>> createAuctionDTOResponseEntity= restTemplate.exchange(baseUrl+"/all", HttpMethod.GET,
                new HttpEntity<>(headers), new ParameterizedTypeReference<List<Tag>>() {
                });
        assertEquals(HttpStatus.OK,createAuctionDTOResponseEntity.getStatusCode());
    }
}
