package com.b4w.b4wback.refactor.auction;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateBidDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import static com.b4w.b4wback.util.HttpEntityCreator.createHeaderWithToken;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static com.b4w.b4wback.util.HttpEntityCreator.authenticateAndGetToken;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class BidCreationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    private CreateAuctionDTO auctionDTO;
    private CreateUserDTO userDTO2;
    private CreateUserDTO userDTO3;
    private HttpHeaders headers;

    @BeforeEach
    public void setUp() {
        auctionDTO = new CreateAuctionDTO(1L, "titulo", "vendo auto",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0), "Toyota",
                "Corolla", 1000, 100, GasType.GASOLINE, 2019, "Blanco",
                4, GearShiftType.MANUAL, new ArrayList<>(Arrays.asList("azul", "nuevo")));

        CreateUserDTO userDTO = new CreateUserDTO("Nico", "Borja", "test@mail.com",
                "+5491154964341", "1Afjfslkjfl");
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO), Void.class);

        userDTO2 = new CreateUserDTO("Mico", "Vorja", "bejero@dusyum.com",
                "+5491112345678", "1Afjfslkjfl");
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO2), CreateUserDTO.class);

        userDTO3 = new CreateUserDTO("Rico", "Vorja", "bejero23@dusyum.com",
                "+5491112345678", "1Afjfslkjfl");
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO3), CreateUserDTO.class);

        String jwtToken = authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

    }
    private HttpEntity<CreateUserDTO> createHttpEntity(CreateUserDTO createUserDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(createUserDTO, headers);
    }

    private HttpEntity<CreateBidDTO> createHttpEntity(CreateBidDTO bidDTO, CreateUserDTO userDTO){
        HttpHeaders headers = createHeaderWithToken(userDTO, restTemplate);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(bidDTO, headers);
    }

    @Test
    void Test24_WhenBiddingFirstForAnAuctionReturnCreated() {
        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);

        ResponseEntity<String> res = restTemplate.exchange("/bid", HttpMethod.POST,
        createHttpEntity(new CreateBidDTO(100000, 2L, 1L), userDTO2), String.class);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    void Test25_WhenBiddingLowerThanPreviousBidReturnBadRequest() {
        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);

        restTemplate.exchange("/bid", HttpMethod.POST,
                createHttpEntity(new CreateBidDTO(100000, 2L, 1L), userDTO2), String.class);

        ResponseEntity<String> res = restTemplate.exchange("/bid", HttpMethod.POST,
                createHttpEntity(new CreateBidDTO(1000, 3L, 1L), userDTO3), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    void Test26_WhenBiddingHigherThanPreviousBidReturnCreated() {
        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);

        restTemplate.exchange("/bid", HttpMethod.POST,
                createHttpEntity(new CreateBidDTO(100000, 2L, 1L), userDTO2), String.class);

        ResponseEntity<String> res = restTemplate.exchange("/bid", HttpMethod.POST,
                createHttpEntity(new CreateBidDTO(100001, 3L, 1L), userDTO3), String.class);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }
}
