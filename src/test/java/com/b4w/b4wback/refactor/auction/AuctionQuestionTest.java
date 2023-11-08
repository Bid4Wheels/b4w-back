package com.b4w.b4wback.refactor.auction;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.Question.CreateQuestionDTO;
import com.b4w.b4wback.dto.Question.GetQuestionDTO;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.repository.AuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import static com.b4w.b4wback.util.HttpEntityCreator.authenticateAndGetToken;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;


import static com.b4w.b4wback.util.HttpEntityCreator.createHeaderWithToken;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuctionQuestionTest {
    @Autowired
    private TestRestTemplate restTemplate;
    private CreateUserDTO userDTO;
    private CreateAuctionDTO auctionDTO;

    @BeforeEach
    public void setup() {
        userDTO = new CreateUserDTO("Pablo", "Pil", "fsajkdf@gmail.com",
                "+5491112345678", "Aa1bcdefgh");
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO), Void.class);

        CreateUserDTO userDTO2 = new CreateUserDTO("Nico", "Pil", "pilo@gmail.com",
                "+5491112345678", "Aa1bcdefgh");
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO2), Void.class);

        auctionDTO = new CreateAuctionDTO(1L, "titulo", "vendo auto",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0), "Toyota",
                "Corolla", 1000, 100, GasType.GASOLINE, 2019, "Blanco",
                4, GearShiftType.MANUAL, new ArrayList<>(Arrays.asList("azul", "nuevo")));
    }
    private HttpEntity<CreateQuestionDTO> createHttpEntity(CreateQuestionDTO questionDTO, CreateUserDTO userDTO){
        HttpHeaders headers = createHeaderWithToken(userDTO, restTemplate);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(questionDTO, headers);
    }

    private HttpEntity<CreateUserDTO> createHttpEntity(CreateUserDTO createUserDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(createUserDTO, headers);
    }

    @Test
    void Test11_WhenCreateQuestionOnAnExistingAuctionReturn(){
        String jwtToken = authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);

            ResponseEntity<GetQuestionDTO> postBidResponse = restTemplate.exchange("/QandA/question", HttpMethod.POST,
                    createHttpEntity(new CreateQuestionDTO("Make a question", 1L), userDTO), GetQuestionDTO.class);
        assertEquals(HttpStatus.CREATED, postBidResponse.getStatusCode());
        assertEquals(HttpStatus.CREATED, postBidResponse.getStatusCode());
        assertTrue(postBidResponse.hasBody());

        GetQuestionDTO questionDTO = postBidResponse.getBody();
        assertNotNull(questionDTO);
        assertEquals("Make a question", questionDTO.getQuestion());
        assertEquals(userDTO.getEmail(), questionDTO.getUser().getEmail());
    }

}
