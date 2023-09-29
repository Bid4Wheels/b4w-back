package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.BidDTO;
import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.Question.CreateQuestionDTO;
import com.b4w.b4wback.dto.Question.GetQuestionDTO;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.b4w.b4wback.util.HttpEntityCreator.createHeaderWithToken;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class QuestionAndAnswerControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    private final String baseUrl = "/QandA";

    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private UserService userService;

    private List<CreateUserDTO> userDTOS;
    private List<User> users;
    private Auction auction;
    private CreateQuestionDTO createQuestionDTO;


    private HttpEntity<CreateQuestionDTO> createHttpEntity(CreateQuestionDTO questionDTO, CreateUserDTO userDTO){
        HttpHeaders headers = createHeaderWithToken(userDTO, restTemplate);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(questionDTO, headers);
    }


    @BeforeEach
    public void setup(){
        userDTOS = new ArrayList<>();
        userDTOS.add(new CreateUserDTO("Pablo", "Pil", "fsajkdf@gmail,com",
                "+5491112345678", "Aa1bcdefgh"));
        userDTOS.add(new CreateUserDTO("Nico", "Pil", "pilo@gmail,com",
                        "+5491112345678", "Aa1bcdefgh"));
        userDTOS.add(new CreateUserDTO("Esteban", "Chiquito", "bejero@dusyum,com","+5491112345678",
                "Aa1bcdefgh"));


        users = new ArrayList<>();
        for (CreateUserDTO userDTO : userDTOS) {
            String psw = userDTO.getPassword();
            users.add(userService.createUser(userDTO));
            userDTO.setPassword(psw);
        }


        auction = new Auction(new CreateAuctionDTO(users.get(0).getId(), "A","text",
                LocalDateTime.of(2030, 10, 10, 10, 10), "Toyota",
                "A1", 1, 10000, GasType.DIESEL, 1990, "Blue", 4,
                GearShiftType.AUTOMATIC, null), null);
        auction.setUser(users.get(0));
        auction = auctionRepository.save(auction);
        createQuestionDTO = new CreateQuestionDTO("Hello world", auction.getId());
    }

    @Test
    void Test001_QuestionAndAnswerControllerWhenPostNewQuestionInAuctionThenGetQuestionDTO() {
        int userNumber = 1;

        ResponseEntity<GetQuestionDTO> postBidResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(createQuestionDTO, userDTOS.get(userNumber)),
                GetQuestionDTO.class);

        assertEquals(HttpStatus.CREATED, postBidResponse.getStatusCode());
        assertTrue(postBidResponse.hasBody());

        GetQuestionDTO questionDTO = postBidResponse.getBody();
        assertNotNull(questionDTO);
        assertEquals(createQuestionDTO.getQuestion(), questionDTO.getQuestion());
        assertEquals(userDTOS.get(userNumber).getEmail(), questionDTO.getUser().getEmail());
    }

    @Test
    void Test002_QuestionAndAnswerControllerWhenPostNewQuestionInAuctionWithSameUserThenGetBadRequest() {
        int userNumber = 0;

        ResponseEntity<String> postBidResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(createQuestionDTO, userDTOS.get(userNumber)),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postBidResponse.getStatusCode());
        assertNotNull(postBidResponse.getBody());
        assertTrue(postBidResponse.getBody().contains("The author of the auction can't be the same of the question"));
    }

    @Test
    void Test003_QuestionAndAnswerControllerWhenPostNewQuestionInAuctionWithClosedAuctionThenGetBadRequest() {
        int userNumber = 1;

        auction = new Auction(new CreateAuctionDTO(users.get(0).getId(), "A","text",
                LocalDateTime.of(2020, 10, 10, 10, 10), "Toyota",
                "A1", 1, 10000, GasType.DIESEL, 1990, "Blue", 4,
                GearShiftType.AUTOMATIC, null), null);
        auction.setUser(users.get(0));
        auction = auctionRepository.save(auction);

        createQuestionDTO.setAuctionId(auction.getId());
        ResponseEntity<String> postBidResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(createQuestionDTO, userDTOS.get(userNumber)),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postBidResponse.getStatusCode());
        assertNotNull(postBidResponse.getBody());
        assertTrue(postBidResponse.getBody().contains("The auction is already closed"));
    }



}
