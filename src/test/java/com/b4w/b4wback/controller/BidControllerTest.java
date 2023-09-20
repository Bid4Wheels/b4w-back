package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.BidDTO;
import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateBidDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.auth.JwtResponse;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.AuctionService;
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
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class BidControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private AuctionRepository auctionRepository;

    private final String baseUrl = "/bid";
    private List<CreateUserDTO> userDTOS;
    private long auctionId;

    @BeforeEach
    public void setup() {
        userDTOS = new ArrayList<>();

        CreateUserDTO userDTO = new CreateUserDTO("Nico", "Borja", "bejero7623@dusyum.com",
                "+5491112345678", "1Afjfslkjfl");
        userDTOS.add(userDTO);
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO), CreateUserDTO.class);

        userDTO = new CreateUserDTO("Mati", "Breg", "breg@dusyum.com",
                "+5491187654321", "1Afjfslkjfl");
        userDTOS.add(userDTO);
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO), CreateUserDTO.class);

        userDTO = new CreateUserDTO("Pedro", "Bull", "bull@dusyum.com",
                "+5491187654322", "1Aklhjkl");
        userDTOS.add(userDTO);
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO), CreateUserDTO.class);


        Optional<User> user = userRepository.findByEmail(userDTOS.get(0).getEmail());
        auctionService.createAuction(new CreateAuctionDTO(user.get().getId(),"Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",1,30000, GasType.GASOLINE,2022,"Silver",
                4, GearShiftType.AUTOMATIC, null));


        List<Auction> auctions = auctionRepository.findAll();
        auctionId = auctions.get(0).getId();
    }



    private HttpEntity<CreateBidDTO> createHttpEntity(CreateBidDTO bidDTO, CreateUserDTO userDTO){
        HttpHeaders headers = createHeaderWithToken(userDTO);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(bidDTO, headers);
    }

    private HttpEntity<CreateUserDTO> createHttpEntity(CreateUserDTO createUserDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(createUserDTO, headers);
    }

    private HttpHeaders createHeaderWithToken(CreateUserDTO userDTO){
        String jwtToken = authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);
        return headers;
    }

    private String authenticateAndGetToken(SignInRequest signInRequest) {
        String loginURL = "/auth/login";
        ResponseEntity<JwtResponse> response = restTemplate.exchange(loginURL, HttpMethod.POST,
                new HttpEntity<>(signInRequest), JwtResponse.class);
        return Objects.requireNonNull(response.getBody()).getToken();
    }

    @Test
    void Test001_BidControllerWhenPostNewBidWithValidDTOShouldCreateBidCorrectly() {
        int userNumber = 1;
        Optional<User> user = userRepository.findByEmail(userDTOS.get(userNumber).getEmail());

        ResponseEntity<BidDTO> postBidResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
               createHttpEntity(new CreateBidDTO(10, user.get().getId(), auctionId), userDTOS.get(userNumber)),
                BidDTO.class);

        assertEquals(HttpStatus.CREATED, postBidResponse.getStatusCode());
        assertTrue(postBidResponse.hasBody());

        BidDTO bid = postBidResponse.getBody();
        assertEquals(10, bid.getAmount());
        assertEquals(user.get().getId(), bid.getUserId());
        assertNotNull(bid.getTime());
    }

    @Test
    void Test002_BidControllerWhenPostNewBidWithBidderSameAsAuctionerShouldGetBadRequestErrorWithMessage(){
        int userNumber = 0;
        Optional<User> user = userRepository.findByEmail(userDTOS.get(userNumber).getEmail());

        ResponseEntity<String> postBidResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(new CreateBidDTO(10, user.get().getId(), auctionId), userDTOS.get(userNumber)),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postBidResponse.getStatusCode());
        assertTrue(postBidResponse.getBody().contains("User can't bid in own auctions"));
    }

    @Test
    void Test003_BidControllerWhenPostNewBidWithLowerBidShouldGetBadRequestErrorWithMessage(){
        int userNumber = 1;
        Optional<User> user = userRepository.findByEmail(userDTOS.get(userNumber).getEmail());

        restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(new CreateBidDTO(10, user.get().getId(), auctionId), userDTOS.get(userNumber)),
                String.class);

        userNumber = 2;
        user = userRepository.findByEmail(userDTOS.get(userNumber).getEmail());
        ResponseEntity<String> postBidResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(new CreateBidDTO(5, user.get().getId(), auctionId), userDTOS.get(userNumber)),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postBidResponse.getStatusCode());
        assertTrue(postBidResponse.getBody().contains("There is an bid with a higher amount"));
    }

    @Test
    void Test004_BidControllerWhenPostNewBidWithNonExistentAuthorShouldGetNotFoundEntityWithMessage(){
        int userNumber = 1;
        Optional<User> user = userRepository.findByEmail(userDTOS.get(userNumber).getEmail());

        ResponseEntity<String> postBidResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(new CreateBidDTO(10, user.get().getId(), 10L), userDTOS.get(userNumber)),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, postBidResponse.getStatusCode());
        assertTrue(postBidResponse.getBody().contains("The auction could not be found"));
    }
}
