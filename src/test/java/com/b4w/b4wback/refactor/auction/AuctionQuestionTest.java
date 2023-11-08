package com.b4w.b4wback.refactor.auction;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.Question.CreateQuestionDTO;
import com.b4w.b4wback.dto.Question.GetQuestionDTO;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.service.interfaces.UserService;
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
import java.util.List;


import static com.b4w.b4wback.util.HttpEntityCreator.createHeaderWithToken;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuctionQuestionTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserService userService;
    private CreateQuestionDTO questionDTO;
    private List<CreateUserDTO> userDTOS;

    @BeforeEach
    public void setup() {
        userDTOS = new ArrayList<>();
        userDTOS.add(new CreateUserDTO("Pablo", "Pil", "fsajkdf@gmail.com",
                "+5491112345678", "Aa1bcdefgh"));

        userDTOS.add(new CreateUserDTO("Nico", "Pil", "pilo@gmail.com",
                "+5491112345678", "Aa1bcdefgh"));

        for (CreateUserDTO userDTO : userDTOS) {
            String psw = userDTO.getPassword();
            userService.createUser(userDTO);
            userDTO.setPassword(psw);
        }

        CreateAuctionDTO auctionDTO = new CreateAuctionDTO(1L, "titulo", "vendo auto",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0), "Toyota",
                "Corolla", 1000, 100, GasType.GASOLINE, 2019, "Blanco",
                4, GearShiftType.MANUAL, new ArrayList<>(Arrays.asList("azul", "nuevo")));

        String jwtToken = authenticateAndGetToken(new SignInRequest("fsajkdf@gmail.com", "Aa1bcdefgh"), restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);

        questionDTO = new CreateQuestionDTO("Make a question", 1L);
    }
    private HttpEntity<CreateQuestionDTO> createHttpEntity(CreateQuestionDTO questionDTO, CreateUserDTO userDTO){
        HttpHeaders headers = createHeaderWithToken(userDTO, restTemplate);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(questionDTO, headers);
    }
    @Test
    void Test11_WhenCreateQuestionOnAnExistingAuctionReturnCreated(){

        ResponseEntity<GetQuestionDTO> postBidResponse = restTemplate.exchange("/QandA/question", HttpMethod.POST,
                createHttpEntity(questionDTO,userDTOS.get(1)), GetQuestionDTO.class);

        assertEquals(HttpStatus.CREATED, postBidResponse.getStatusCode());
        assertTrue(postBidResponse.hasBody());

        GetQuestionDTO questionDTO = postBidResponse.getBody();
        assertNotNull(questionDTO);
        assertEquals("Make a question", questionDTO.getQuestion());
        assertEquals("pilo@gmail.com", questionDTO.getUser().getEmail());
    }

    @Test
    void Test22_WhenCreateQuestionOfOwnAuctionReturnBadRequest(){
        ResponseEntity<String> postBidResponse = restTemplate.exchange("/QandA/question", HttpMethod.POST,
                createHttpEntity(questionDTO,userDTOS.get(0)), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postBidResponse.getStatusCode());
        System.out.println(postBidResponse.getBody());
        assertEquals("The author of the auction can't be the same of the question", postBidResponse.getBody());
    }

    @Test
    void Test64_WhenDeleteOwnQuestionReturnOk() {
        restTemplate.exchange("/QandA/question", HttpMethod.POST,createHttpEntity(questionDTO, userDTOS.get(1)), GetQuestionDTO.class);

        ResponseEntity<String> deleteQuestionResponse = restTemplate.exchange("/QandA/question/1", HttpMethod.DELETE,
                createHttpEntity(questionDTO, userDTOS.get(1)),
                String.class);

        assertEquals(HttpStatus.OK, deleteQuestionResponse.getStatusCode());
    }


}
