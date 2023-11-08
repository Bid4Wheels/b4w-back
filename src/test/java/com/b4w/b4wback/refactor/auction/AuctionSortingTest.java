package com.b4w.b4wback.refactor.auction;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.service.interfaces.AuctionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private CreateAuctionDTO auctionDTO;
    private List<Auction> auctions;
    private HttpHeaders headers;
    private HttpHeaders headers2;
    private HttpHeaders headers3;

    @BeforeEach
    public void setUp() {
        auctionDTO = new CreateAuctionDTO(1L, "titulo", "vendo auto",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0), "Toyota",
                "Corolla", 1000, 100, GasType.GASOLINE, 2019, "Blanco",
                4, GearShiftType.MANUAL, new ArrayList<>(Arrays.asList("azul", "nuevo")));
        CreateUserDTO userDTO = new CreateUserDTO("Nico", "Borja", "test@mail.com",
                "+5491154964341", "1Afjfslkjfl");
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO), Void.class);
        CreateUserDTO userDTO2 = new CreateUserDTO("Mico", "Vorja", "bejero@dusyum.com",
                "+5491112345678", "1Afjfslkjfl");
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO2), CreateUserDTO.class);
        CreateUserDTO userDTO3 = new CreateUserDTO("Rico", "Vorja", "bejero23@dusyum.com",
                "+5491112345678", "1Afjfslkjfl");
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO3), CreateUserDTO.class);

        String jwtToken = authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        String jwtToken2 = authenticateAndGetToken(new SignInRequest(userDTO2.getEmail(), userDTO2.getPassword()), restTemplate);
        headers2 = new HttpHeaders();
        headers2.set("Authorization", "Bearer " + jwtToken2);

        String jwtToken3 = authenticateAndGetToken(new SignInRequest(userDTO3.getEmail(), userDTO3.getPassword()), restTemplate);
        headers3 = new HttpHeaders();
        headers3.set("Authorization", "Bearer " + jwtToken3);

    }

    private HttpEntity<CreateUserDTO> createHttpEntity(CreateUserDTO createUserDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(createUserDTO, headers);
    }

    @Test
    void Test121_WhenGetAuctionsByUserRespondWithListOfAuctions() {
        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);

        ResponseEntity<String> getAuctionsResponse = restTemplate.exchange("/auction/user/1", HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatus.OK, getAuctionsResponse.getStatusCode());
        assertTrue(getAuctionsResponse.getBody() != null && getAuctionsResponse.getBody().contains("OPEN"));
    }

    @Test
    void Test122_WhenGetAuctionsByOtherUserRespondWithEmptyList() {
        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);

        ResponseEntity<String> getAuctionsResponse = restTemplate.exchange("/auction/user/1", HttpMethod.GET,
                new HttpEntity<>(headers2), String.class);
        assertEquals(HttpStatus.OK, getAuctionsResponse.getStatusCode());
    }

    @Test
    void Test33_WhenGetAuctionsByUserRespondWithListOfAuctionsWithAllStatusFilteredByCreationDate() throws JsonProcessingException {

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
        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);

        restTemplate.exchange("/bid", HttpMethod.POST,new HttpEntity<>(new CreateBidDTO(100000000, 2L,1L), headers2), String.class);

        ResponseEntity<String> getAuctionsResponse = restTemplate.exchange("/auction/bidder/2", HttpMethod.GET,
                new HttpEntity<>(headers2), String.class);
        System.out.println(getAuctionsResponse.getBody());
        assertEquals(HttpStatus.OK, getAuctionsResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(getAuctionsResponse.getBody()).contains("OPEN"));
    }

    @Test
    void Test122_WhenGetAuctionsBiddedByOtherUserRespondWithListOfAuctions() {
        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, headers), String.class);

        restTemplate.exchange("/bid", HttpMethod.POST,new HttpEntity<>(new CreateBidDTO(100000000, 2L,1L), headers2), String.class);

        ResponseEntity<String> getAuctionsResponse = restTemplate.exchange("/auction/bidder/2", HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
        System.out.println(getAuctionsResponse.getBody());
        assertEquals(HttpStatus.OK, getAuctionsResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(getAuctionsResponse.getBody()).contains("OPEN"));
    }

    @Test
    void Test33_WhenGetAuctionsBiddedByUserRespondWithListOfAuctionsWithAllStatusFilteredByCreationDate() throws JsonProcessingException {
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
        auctionDTO = new CreateAuctionDTO(1L,"Subasta de automovil 1","text",
                LocalDateTime.of(2024, 3, 3, 18, 0, 0),"Aston Martin",
                "Amr23",200,4000, GasType.DIESEL,2,"Green",
                2, GearShiftType.MANUAL, new ArrayList<>(List.of("awesome")));

        auctionService.createAuction(auctionDTO);

        CreateAuctionDTO auctionDTO2 = new CreateAuctionDTO(1L, "Subasta de automovil 2", "text",
                LocalDateTime.of(2023, 11, 17, 3, 0, 0), "Volkswagen",
                "golf 1,4 TSI", 16000, 107000, GasType.GASOLINE, 7, "Silver",
                5, GearShiftType.MANUAL, new ArrayList<>(List.of("awesome", "nice")));

        auctionService.createAuction(auctionDTO2);

        CreateAuctionDTO auctionDTO3 = new CreateAuctionDTO(1L, "Subasta de automovil 3", "text",
                LocalDateTime.of(2024, 1, 10, 3, 0, 0), "Ford",
                "Fiesta", 10000, 23354, GasType.GASOLINE, 10, "Silver",
                4, GearShiftType.AUTOMATIC, new ArrayList<>(List.of("silver")));

        auctionService.createAuction(auctionDTO3);
    }
    @Test
    void Test25_WhenFilterAuctionByBrandAndAuctionExistsRespondWithListOfAuctionsFiltered(){
        generateFilterAuctions();

        auctions = auctionRepository.findAll();
        String value = "Ford";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setBrand(value);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 3"));
    }

    @Test
    void Test26_WhenFilterAuctionByBrandAndAuctionNotExistsRespondWithListOfAuctionsEmpty(){
        generateFilterAuctions();

        auctions = auctionRepository.findAll();
        String value = "Toyota";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setBrand(value);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("[]"));
    }

    @Test
    void Test27_WhenFilterByColorAndExistingAuctionReturnListOfAuctions(){
        generateFilterAuctions();

        auctions = auctionRepository.findAll();
        String value = "Silver";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setColor(value);

        auctions = auctions.stream().filter(a-> a.getColor().equals(value)).toList();

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 2"));
        assertTrue(res.getBody().contains("Subasta de automovil 3"));
    }

    @Test
    void Test28_WhenFilterByColorAndNotExistingAuctionReturnListOfAuctionsEmpty() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        String value = "Red";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setColor(value);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("[]"));
    }

    @Test
    void Test29_WhenFilterByGasTypeAndExistingAuctionReturnListOfAuctions(){
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        GasType value = GasType.DIESEL;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setGasType(value);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 1"));
    }

    @Test
    void Test210_WhenFilterByGasTypeAndNotExistingAuctionReturnListOfAuctionsEmpty() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        GasType value = GasType.ELECTRICITY;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setGasType(value);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("[]"));
    }

    @Test
    void Test211_WhenFilterByBasePriceMinAndExistingAuctionReturnListOfAuctions() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int min = 300;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setPriceMin(min);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 2"));
        assertTrue(res.getBody().contains("Subasta de automovil 3"));
    }

    @Test
    void Test212_WhenFilterByBasePriceMinAndNotExistingAuctionReturnListOfAuctionsEmpty() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        Integer min = 9999999;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setPriceMin(min);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("[]"));
    }
    @Test
    void Test213_WhenFilterByBasePriceMaxAndExistingAuctionReturnListOfAuctions() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int max = 300;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setPriceMax(max);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 1"));
    }

    @Test
    void Test214_WhenFilterByBasePriceMaxAndNotExistingAuctionReturnListOfAuctionsEmpty() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        Integer max = 100;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setPriceMax(max);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("[]"));
    }

    @Test
    void Test215_WhenFilterByBasePriceMinAndMaxAndExistingAuctionReturnListOfAuctions() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int min = 100;
        int max = 11000;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setPriceMin(min);
        filter.setPriceMax(max);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 1"));
        assertTrue(res.getBody().contains("Subasta de automovil 3"));
    }

    @Test
    void Test216_WhenFilterByBasePriceMinAndMaxAndNotExistingAuctionReturnListOfAuctionsEmpty() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int min = 100;
        int max = 150;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setPriceMin(min);
        filter.setPriceMax(max);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("[]"));
    }

    @Test
    void Test217_WhenFilterByYearMinAndExistingAuctionReturnListOfAuctions() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int min = 2;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setModelYearMin(min);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 1"));
        assertTrue(res.getBody().contains("Subasta de automovil 2"));
    }

//    @Test
//    void Test218_WhenFilterByYearMinAndNotExistingAuctionReturnListOfAuctionsEmpty() {
//        generateFilterAuctions();
//        auctions = auctionRepository.findAll();
//        int min = 10;
//
//        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
//        filter.setModelYearMin(min);
//
//        auctions = auctions.stream().filter(a->(a.getModelYear() >= min)).toList();
//
//        Page<AuctionDTO> page = auctionService
//                .getAuctionsFiltered(filter, Pageable.ofSize(10));
//        assertEquals(auctions.size(), page.getTotalElements());
//        assertTrue(page.getContent().isEmpty());
//    }

    @Test
    void Test219_WhenFilterByYearMaxAndExistingAuctionReturnListOfAuctions() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int max = 5;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setModelYearMax(max);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 1"));
    }

    @Test
    void Test220_WhenFilterByYearMaxAndNotExistingAuctionReturnListOfAuctionsEmpty() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int max = 1;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setModelYearMax(max);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("[]"));
    }

    @Test
    void Test221_WhenFilterByYearMinAndMaxAndExistingAuctionReturnListOfAuctions() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int min = 1;
        int max = 8;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setModelYearMin(min);
        filter.setModelYearMax(max);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 1"));
        assertTrue(res.getBody().contains("Subasta de automovil 2"));
    }

    @Test
    void Test222_WhenFilterByYearMinAndMaxAndNotExistingAuctionReturnListOfAuctionsEmpty() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int min = 3;
        int max = 5;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setModelYearMin(min);
        filter.setModelYearMax(max);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("[]"));
    }

    @Test
    void Test223_WhenFilterByMillageMinAndExistingAuctionReturnListOfAuctions() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int min = 100000;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setMilageMin(min);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 2"));
    }

    @Test
    void Test224_WhenFilterByMilageMinAndNotExistingAuctionReturnListOfAuctionsEmpty() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int min = 2000000;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setMilageMin(min);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("[]"));
    }

    @Test
    void Test225_WhenFilterByMilageMaxAndExistingAuctionReturnListOfAuctions() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int max = 5000;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setMilageMax(max);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 1"));
    }

    @Test
    void Test226_WhenFilterByMillageMaxAndNotExistingAuctionReturnListOfAuctionsEmpty() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int max = 100;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setMilageMax(max);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("[]"));
    }

    @Test
    void Test227_WhenFilterByMillageMinAndMaxAndExistingAuctionReturnListOfAuctions() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int min = 3000;
        int max = 25000;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setMilageMin(min);
        filter.setMilageMax(max);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 3"));
    }

    @Test
    void Test228_WhenFilterByMillageMinAndMaxAndNotExistingAuctionReturnListOfAuctionsEmpty() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int min = 1000;
        int max = 1500;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setMilageMin(min);
        filter.setMilageMax(max);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("[]"));
    }

    @Test
    void Test229_WhenFilterByModelAndExistsAuctionReturnListOfAuctions() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        String value = "Fiesta";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setModel(value);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 3"));

    }

    @Test
    void Test230_WhenFilterByModelAndNotExistsAuctionReturnListOfAuctionsEmpty() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        String value = "Etios";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setModel(value);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("[]"));
    }

    @Test
    void Test235_WhenFilterByDoorAndExistsAuctionReturnListOfAuctions() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int value = 4;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setDoorsAmount(value);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 3"));

    }

    @Test
    void Test236_WhenFilterByDoorAndNotExistsAuctionReturnListOfAuctionsEmpty() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        int value = 1;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setDoorsAmount(value);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("[]"));
    }

    @Test
    void Test237_WhenFilterByGearShiftTypeAndExistsAuctionReturnListOfAuctions() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();
        GearShiftType value = GearShiftType.MANUAL;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setGearShiftType(value);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 1"));
        assertTrue(res.getBody().contains("Subasta de automovil 2"));
    }

    @Test
    void Test338_WhenFilterByNoParameterReturnListOfAuctions() {
        generateFilterAuctions();
        auctions = auctionRepository.findAll();

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> res = restTemplate.exchange("/auction/filter", HttpMethod.POST, new HttpEntity<>(filter, headers), String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("Subasta de automovil 1"));
        assertTrue(res.getBody().contains("Subasta de automovil 2"));
        assertTrue(res.getBody().contains("Subasta de automovil 3"));
    }
//    @Test
//    void Test1340_WhenFilterByEndingSoonReturnListOfAuctions() {
//        generateFilterAuctions();
//
//        String jwtToken = authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + jwtToken);
//
//        ResponseEntity<String> res = restTemplate.exchange("/auction/ending",HttpMethod.GET, new HttpEntity<>(headers), String.class);
//        assertEquals(HttpStatus.OK, res.getStatusCode());
//        System.out.println(res.getBody());
//        assertTrue(res.getBody().contains("2024-03-03T18:00:00"));
//        assertTrue(res.getBody().contains("2024-01-10T03:00:00"));
//        assertTrue(res.getBody().contains("2023-11-17T03:00:00"));
//    }

    @Test
    void Test2341_WhenFilterByNewlyListedReturnListOfAuctions() throws JsonProcessingException {
        generateFilterAuctions();

        ResponseEntity<String> res = restTemplate.exchange("/auction/new",HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatus.OK, res.getStatusCode());

        assertTrue(Objects.requireNonNull(res.getBody()).contains("2024-03-03T18:00:00"));
        assertTrue(res.getBody().contains("2024-01-10T03:00:00"));
        assertTrue(res.getBody().contains("2023-11-17T03:00:00"));
    }
}