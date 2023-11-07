package com.b4w.b4wback.refactor.auction;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Tag;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.service.interfaces.AuctionService;
import com.b4w.b4wback.service.interfaces.TagService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.b4w.b4wback.util.HttpEntityCreator.authenticateAndGetToken;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuctionSortingTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private TagService tagService;
    private CreateAuctionDTO auctionDTO;
    private CreateAuctionDTO auctionDTO2;
    private CreateAuctionDTO auctionDTO3;
    private CreateUserDTO userDTO;
    private CreateUserDTO userDTO2;
    private CreateUserDTO userDTO3;
    private List<Auction> auctions;
    private List<Tag> tags;



    @BeforeEach
    public void setUp() {
        auctionDTO = new CreateAuctionDTO(1L, "titulo", "vendo auto",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0), "Toyota",
                "Corolla", 1000, 100, GasType.GASOLINE, 2019, "Blanco",
                4, GearShiftType.MANUAL, new ArrayList<>(Arrays.asList("azul", "nuevo")));
        userDTO = new CreateUserDTO("Nico", "Borja", "test@mail.com",
                "+5491154964341", "1Afjfslkjfl");
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO), Void.class);
        userDTO2 = new CreateUserDTO("Mico", "Vorja", "bejero@dusyum.com",
                "+5491112345678", "1Afjfslkjfl");
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO2), CreateUserDTO.class);
        userDTO3 = new CreateUserDTO("Rico", "Vorja", "bejero23@dusyum.com",
                "+5491112345678", "1Afjfslkjfl");
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO3), CreateUserDTO.class);
    }

    private HttpEntity<CreateUserDTO> createHttpEntity(CreateUserDTO createUserDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(createUserDTO, headers);
    }

    @Test
    void Test121_WhenGetAuctionsByUserRespondWithListOfAuctions() {
        String jwtToken = authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);


        ResponseEntity<String> getAuctionsResponse = restTemplate.exchange("/auction/user/1", HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatus.OK, getAuctionsResponse.getStatusCode());
        assertTrue(getAuctionsResponse.getBody() != null && getAuctionsResponse.getBody().contains("OPEN"));
    }

    @Test
    void Test122_WhenGetAuctionsByOtherUserRespondWithEmptyList() {
        String jwtToken = authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);

        String jwtToken2 = authenticateAndGetToken(new SignInRequest(userDTO2.getEmail(), userDTO2.getPassword()), restTemplate);
        HttpHeaders headers2 = new HttpHeaders();
        headers2.set("Authorization", "Bearer " + jwtToken2);
        ResponseEntity<String> getAuctionsResponse = restTemplate.exchange("/auction/user/1", HttpMethod.GET,
                new HttpEntity<>(headers2), String.class);
        assertEquals(HttpStatus.OK, getAuctionsResponse.getStatusCode());
    }

    @Test
    void Test33_WhenGetAuctionsByUserRespondWithListOfAuctionsWithAllStatusFilteredByCreationDate() throws JsonProcessingException {
        String jwtToken = authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);
        Auction auction = auctionRepository.findById(1L).get();
        auction.setStatus(AuctionStatus.FINISHED);
        auctionRepository.save(auction);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);
        auction = auctionRepository.findById(2L).get();
        auction.setStatus(AuctionStatus.AWATINGDELIVERY);
        auctionRepository.save(auction);


        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);


        ResponseEntity<String> res = restTemplate.exchange("/auction/new", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatus.OK, res.getStatusCode());

        System.out.println(res.getBody());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("AWATINGDELIVERY"));
        assertTrue(res.getBody().contains("FINISHED"));
        assertTrue(res.getBody().contains("OPEN"));

        String jsonData = res.getBody();


        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonData);

        JsonNode contentArray = jsonNode.get("content");
        int elementCount = contentArray.size();

        assertEquals(3, elementCount);
    }

    @Test
    void Test121_WhenGetAuctionsBiddedByUserRespondWithListOfAuctions() {
        String jwtToken = authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        ResponseEntity<String> res1 = restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);

        String jwtToken2 = authenticateAndGetToken(new SignInRequest(userDTO2.getEmail(), userDTO2.getPassword()), restTemplate);
        HttpHeaders headers2 = new HttpHeaders();
        headers2.set("Authorization", "Bearer " + jwtToken2);
        restTemplate.exchange("/bid", HttpMethod.POST,new HttpEntity<>(new CreateBidDTO(100000000, 2L,1L), headers2), String.class);

        ResponseEntity<String> getAuctionsResponse = restTemplate.exchange("/auction/bidder/2", HttpMethod.GET,
                new HttpEntity<>(headers2), String.class);
        System.out.println(getAuctionsResponse.getBody());
        assertEquals(HttpStatus.OK, getAuctionsResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(getAuctionsResponse.getBody()).contains("OPEN"));
    }

    @Test
    void Test122_WhenGetAuctionsBiddedByOtherUserRespondWithListOfAuctions() {
        String jwtToken = authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);

        String jwtToken2 = authenticateAndGetToken(new SignInRequest(userDTO2.getEmail(), userDTO2.getPassword()), restTemplate);
        HttpHeaders headers2 = new HttpHeaders();
        headers2.set("Authorization", "Bearer " + jwtToken2);

        restTemplate.exchange("/bid", HttpMethod.POST,new HttpEntity<>(new CreateBidDTO(100000000, 2L,1L), headers2), String.class);

        ResponseEntity<String> getAuctionsResponse = restTemplate.exchange("/auction/bidder/2", HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
        System.out.println(getAuctionsResponse.getBody());
        assertEquals(HttpStatus.OK, getAuctionsResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(getAuctionsResponse.getBody()).contains("OPEN"));
    }

    @Test
    void Test33_WhenGetAuctionsBiddedByUserRespondWithListOfAuctionsWithAllStatusFilteredByCreationDate() throws JsonProcessingException {
        String jwtToken = authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        String jwtToken2 = authenticateAndGetToken(new SignInRequest(userDTO2.getEmail(), userDTO2.getPassword()), restTemplate);
        HttpHeaders headers2 = new HttpHeaders();
        headers2.set("Authorization", "Bearer " + jwtToken2);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);
        restTemplate.exchange("/bid", HttpMethod.POST,new HttpEntity<>(new CreateBidDTO(100000000, 2L,1L), headers2), String.class);
        Auction auction = auctionRepository.findById(1L).get();
        auction.setStatus(AuctionStatus.FINISHED);
        auctionRepository.save(auction);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);
        restTemplate.exchange("/bid", HttpMethod.POST,new HttpEntity<>(new CreateBidDTO(100000000, 2L,2L), headers2), String.class);

        auction = auctionRepository.findById(2L).get();
        auction.setStatus(AuctionStatus.AWATINGDELIVERY);
        auctionRepository.save(auction);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);
        restTemplate.exchange("/bid", HttpMethod.POST,new HttpEntity<>(new CreateBidDTO(100000000, 2L,3L), headers2), String.class);


        ResponseEntity<String> res = restTemplate.exchange("/auction/bidder/2", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());

        System.out.println(res.getBody());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("AWATINGDELIVERY"));
        assertTrue(res.getBody().contains("FINISHED"));
        assertTrue(res.getBody().contains("OPEN"));

        String jsonData = res.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonData);

        JsonNode contentArray = jsonNode.get("content");
        int elementCount = contentArray.size();

        assertEquals(3, elementCount);

    }

    @Test
    void Test34__WhenGetAuctionsBiddedByUserAndNotHighestBidRespondWithListOfAuctionsWithAllStatusFilteredByCreationDate() throws JsonProcessingException {
        String jwtToken = authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        String jwtToken2 = authenticateAndGetToken(new SignInRequest(userDTO2.getEmail(), userDTO2.getPassword()), restTemplate);
        HttpHeaders headers2 = new HttpHeaders();
        headers2.set("Authorization", "Bearer " + jwtToken2);

        String jwtToken3 = authenticateAndGetToken(new SignInRequest(userDTO3.getEmail(), userDTO3.getPassword()), restTemplate);
        HttpHeaders headers3 = new HttpHeaders();
        headers3.set("Authorization", "Bearer " + jwtToken3);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);
        restTemplate.exchange("/bid", HttpMethod.POST,new HttpEntity<>(new CreateBidDTO(100000000, 2L,1L), headers2), String.class);
        restTemplate.exchange("/bid", HttpMethod.POST,new HttpEntity<>(new CreateBidDTO(100000001, 3L,1L), headers3), String.class);

        Auction auction = auctionRepository.findById(1L).get();
        auction.setStatus(AuctionStatus.FINISHED);
        auctionRepository.save(auction);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);
        restTemplate.exchange("/bid", HttpMethod.POST,new HttpEntity<>(new CreateBidDTO(100000000, 2L,2L), headers2), String.class);
        restTemplate.exchange("/bid", HttpMethod.POST,new HttpEntity<>(new CreateBidDTO(100000001, 3L,2L), headers3), String.class);


        auction = auctionRepository.findById(2L).get();
        auction.setStatus(AuctionStatus.AWATINGDELIVERY);
        auctionRepository.save(auction);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);
        restTemplate.exchange("/bid", HttpMethod.POST,new HttpEntity<>(new CreateBidDTO(100000000, 2L,3L), headers2), String.class);
        restTemplate.exchange("/bid", HttpMethod.POST,new HttpEntity<>(new CreateBidDTO(100000001, 3L,3L), headers3), String.class);

        ResponseEntity<String> res = restTemplate.exchange("/auction/bidder/2", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());

        System.out.println(res.getBody());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("AWATINGDELIVERY"));
        assertTrue(res.getBody().contains("FINISHED"));
        assertTrue(res.getBody().contains("OPEN"));

        String jsonData = res.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonData);

        JsonNode contentArray = jsonNode.get("content");
        int elementCount = contentArray.size();

        assertEquals(3, elementCount);
    }
    private void generateFilterAuctions(){
        auctionDTO = new CreateAuctionDTO(1L,"Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Aston Martin",
                "Amr23",200,4000, GasType.DIESEL,2,"Green",
                2, GearShiftType.MANUAL, new ArrayList<>(List.of("awesome")));

        auctionService.createAuction(auctionDTO);


        auctionDTO2 = new CreateAuctionDTO(1L,"Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Volkswagen",
                "golf 1,4 TSI",16000,107000, GasType.GASOLINE,7,"Silver",
                5, GearShiftType.MANUAL, new ArrayList<>(List.of("awesome", "nice")));

        auctionService.createAuction(auctionDTO2);

        auctionDTO3 = new CreateAuctionDTO(1L,"Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Ford",
                "Fiesta",150000,23354, GasType.GASOLINE,10,"Silver",
                4, GearShiftType.AUTOMATIC, new ArrayList<>(List.of("silver")));

        auctionService.createAuction(auctionDTO3);
    }
    @Test
    void Test25_WhenFilterAuctionByBrandAndAuctionExistsRespondWithListOfAuctionsFiltered(){
        generateFilterAuctions();

        auctions = auctionRepository.findAll();
        String value = "Toyota";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setBrand(value);

        auctions = auctions.stream().filter(a-> a.getBrand().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test25_WhenFilterAuctionByBrandAndAuctionNotExistsRespondWithListOfAuctionsEmpty(){
        generateFilterAuctions();

        auctions = auctionRepository.findAll();
        String value = "Chevrolet";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setBrand(value);

        auctions = auctions.stream().filter(a-> a.getBrand().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(0, page.getTotalElements());
    }

    @Test
    void Test27_WhenFilterByColorAndExistingAuctionReturnListOfAuctions(){
        generateFilterAuctions();

        auctions = auctionRepository.findAll();
        String value = "Silver";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setBrand(value);

        auctions = auctions.stream().filter(a-> a.getBrand().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(0, page.getTotalElements());
    }

    @Test
    void Test28_WhenFilterByColorAndNotExistingAuctionReturnListOfAuctionsEmpty() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        String value = "Red";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setBrand(value);

        auctions = auctions.stream().filter(a -> a.getBrand().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(0, page.getTotalElements());
    }

    @Test
    void Test29_WhenFilterByGasTypeAndExistingAuctionReturnListOfAuctions(){
        generateFilterAuctions();
        GasType value = GasType.DIESEL;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setGasType(value);

        auctions = auctions.stream().filter(a-> a.getGasType().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test29_WhenFilterByGasTypeAndNotExistingAuctionReturnListOfAuctionsEmpty() {
        generateFilterAuctions();
        GasType value = GasType.ELECTRICITY;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setGasType(value);

        auctions = auctions.stream().filter(a -> a.getGasType().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

}