package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateReviewDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
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

import static com.b4w.b4wback.util.HttpEntityCreator.authenticateAndGetToken;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class
ReviewControllerTest {
    private final String baseUrl = "/review";
    @Autowired
    private UserService userService;
    private String token;
    private Auction auction;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private AuctionRepository auctionRepository;

    @BeforeEach
    public void setup() {
        List<User> users = new ArrayList<>();
        CreateUserDTO user1 = (new CreateUserDTO("Pablo", "Pil", "fsajkdf@gmail,com",
                "+5491112345678", "Aa1bcdefgh"));
        CreateUserDTO user2 = (new CreateUserDTO("Nico", "Pil", "pilo@gmail,com",
                "+5491112345678", "Aa1bcdefgh"));

        SignInRequest signInRequest = new SignInRequest(user1.getEmail(), user1.getPassword());

        users.add(userService.createUser(user1));
        users.add(userService.createUser(user2));

        token = authenticateAndGetToken(signInRequest, restTemplate);

        auction = new Auction(new CreateAuctionDTO(users.get(0).getId(), "A","text",
                LocalDateTime.of(2030, 10, 10, 10, 10), "Toyota",
                "A1", 1, 10000, GasType.DIESEL, 1990, "Blue", 4,
                GearShiftType.AUTOMATIC, null), null);
        auction.setUser(users.get(0));
        Bid bid = new Bid(10000, users.get(1), auction);
        auction.setBids(List.of(bid));
        auction = auctionRepository.save(auction);
    }

    @Test
    void Test001_ReviewControllerCreateReviewForWinnerShouldReturnStatusCreated(){
        auction.setStatus(AuctionStatus.FINISHED);
        auctionRepository.save(auction);
        CreateReviewDTO createReviewDTO = new CreateReviewDTO("Muy buen producto",5);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        ResponseEntity<String> res = restTemplate.exchange(baseUrl+"/winner/"+auction.getId(), HttpMethod.POST, new HttpEntity<>(createReviewDTO, headers), String.class);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    void Test002_ReviewControllerCreateReviewForWinnerShouldReturnStatusBadRequest(){
        CreateReviewDTO createReviewDTO = new CreateReviewDTO("Muy buen producto",5);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +token);
        ResponseEntity<String> res = restTemplate.exchange(baseUrl+"/winner/"+auction.getId(), HttpMethod.POST, new HttpEntity<>(createReviewDTO, headers), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }
}
