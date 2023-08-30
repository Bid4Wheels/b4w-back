package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateOfferDTO;
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
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private CreateAuctionDTO auctionDTO;
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
        auctionDTO = auctionService.createAuction(new CreateAuctionDTO(user.get().getId(),"Subasta de automovil",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",
                4, GearShiftType.AUTOMATIC));


        List<Auction> auctions = auctionRepository.findAll();
        auctionId = auctions.get(0).getId();
    }



    private HttpEntity<CreateOfferDTO> createHttpEntity(CreateOfferDTO offerDTO, CreateUserDTO userDTO){
        HttpHeaders headers = createHeaderWithToken(userDTO);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(offerDTO, headers);
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

        ResponseEntity<?> postBidResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
               createHttpEntity(new CreateOfferDTO(10, user.get().getId(), auctionId), userDTOS.get(userNumber)),
                String.class);

        assertEquals(HttpStatus.CREATED, postBidResponse.getStatusCode());
    }

    @Test
    void Test002_BidControllerWhenPostNewBidWithBidderSameAsAuctionerShouldGetBadRequestErrorWithMessage(){
        int userNumber = 0;
        Optional<User> user = userRepository.findByEmail(userDTOS.get(userNumber).getEmail());

        ResponseEntity<String> postBidResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(new CreateOfferDTO(10, user.get().getId(), auctionId), userDTOS.get(userNumber)),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postBidResponse.getStatusCode());
        assertTrue(postBidResponse.getBody().contains("User can't bid in own auctions"));
    }

    @Test
    void Test003_BidControllerWhenPostNewBidWithLowerBidShouldGetBadRequestErrorWithMessage(){
        int userNumber = 1;
        Optional<User> user = userRepository.findByEmail(userDTOS.get(userNumber).getEmail());

        restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(new CreateOfferDTO(10, user.get().getId(), auctionId), userDTOS.get(userNumber)),
                String.class);

        userNumber = 2;
        user = userRepository.findByEmail(userDTOS.get(userNumber).getEmail());
        ResponseEntity<String> postBidResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(new CreateOfferDTO(5, user.get().getId(), auctionId), userDTOS.get(userNumber)),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, postBidResponse.getStatusCode());
        assertTrue(postBidResponse.getBody().contains("There is an offer with a higher amount"));
    }

    @Test
    void Test004_BidControllerWhenPostNewBidWithNonExistentAuthorShouldGetNotFoundEntityWithMessage(){
        int userNumber = 1;
        Optional<User> user = userRepository.findByEmail(userDTOS.get(userNumber).getEmail());

        ResponseEntity<String> postBidResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                createHttpEntity(new CreateOfferDTO(10, user.get().getId(), 10L), userDTOS.get(userNumber)),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, postBidResponse.getStatusCode());
        assertTrue(postBidResponse.getBody().contains("The auction could not be found"));
    }
}
