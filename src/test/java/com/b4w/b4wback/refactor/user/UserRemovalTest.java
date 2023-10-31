package com.b4w.b4wback.refactor.user;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.BidRepository;
import com.b4w.b4wback.repository.UserRepository;
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
import java.util.Optional;

import static com.b4w.b4wback.util.HttpEntityCreator.authenticateAndGetToken;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRemovalTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private BidRepository bidRepository;
    private final String baseUrl = "/user";
    private CreateUserDTO userDTO;
    private CreateAuctionDTO auctionDTO;
    @BeforeEach
    public void setup() {
        userDTO = new CreateUserDTO("Nico", "Borja", "test@mail.com",
                "+5491154964341", "1Afjfslkjfl");
        auctionDTO= new CreateAuctionDTO(1L,"Subasta de automovil", "text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",
                4, GearShiftType.AUTOMATIC, null);
    }

    private HttpEntity<CreateUserDTO> createHttpEntity(CreateUserDTO createUserDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(createUserDTO, headers);
    }

    @Test
    void Test11_WhenDeleteUserAuthenticatedShouldReturnOk(){
        restTemplate.exchange(baseUrl, HttpMethod.POST,createHttpEntity(userDTO), UserDTO.class);

        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);

        ResponseEntity<String> deleteUserResponse = restTemplate.exchange(baseUrl,
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.OK, deleteUserResponse.getStatusCode());
        Optional<User> optionalUser = userRepository.findByEmail(userDTO.getEmail());
        User user = optionalUser.get();
        assertTrue(user.isDeleted());
        assertEquals("Deleted", user.getName());
        assertEquals("Deleted", user.getLastName());
        assertEquals("Deleted", user.getPhoneNumber());
    }

    @Test
    void Test22AND23_WhenDeleteUserAlsoDeleteAuctionsAndBidsFromUser(){
        restTemplate.exchange(baseUrl, HttpMethod.POST,createHttpEntity(userDTO), UserDTO.class);

        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO,headers), UserDTO.class);
        restTemplate.exchange("/auction/finish/1", HttpMethod.PATCH, new HttpEntity<>(headers), String.class);
        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO,headers), UserDTO.class);

        CreateUserDTO newUserDTO = new CreateUserDTO("Nico", "Borja", "nico@borja.com",
                "+5491154964341", "1Afjfslkjfl");
        restTemplate.exchange(baseUrl, HttpMethod.POST,createHttpEntity(newUserDTO), UserDTO.class);
        String jwtToken2= authenticateAndGetToken(new SignInRequest(newUserDTO.getEmail(), newUserDTO.getPassword()), restTemplate);
        HttpHeaders headers2= new HttpHeaders();
        headers2.set("Authorization","Bearer " +jwtToken2);

        auctionDTO.setUserId(2L);
        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO,headers2), UserDTO.class);

        CreateBidDTO bidDTO = new CreateBidDTO(150000, 1L, 3L);
        restTemplate.exchange("/bid", HttpMethod.POST, new HttpEntity<>(bidDTO,headers), UserDTO.class);

        Optional<User> optionalUser = userRepository.findByEmail(userDTO.getEmail());
        User user = optionalUser.get();

        List<Auction> auctions= auctionRepository.findByUser(user);
        List<AuctionDTO> auctionDTOS = new ArrayList<>();
        for (Auction auction : auctions){
            auctionDTOS.add((new AuctionDTO(auction)));
        }
        assertFalse(auctionDTOS.isEmpty());

        Optional<Auction> auctionBidded = auctionRepository.findById(3L);
        List<Bid> bids = bidRepository.getBidByAuction(auctionBidded.get());
        assertFalse(bids.isEmpty());

        restTemplate.exchange(baseUrl,
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);

        optionalUser = userRepository.findByEmail(userDTO.getEmail());
        user = optionalUser.get();

        assertTrue(user.isDeleted());

        auctions= auctionRepository.findByUser(user);
        auctionDTOS = new ArrayList<>();
        for (Auction auction : auctions){
            auctionDTOS.add((new AuctionDTO(auction)));
        }
        assertTrue(auctionDTOS.isEmpty());

        auctionBidded = auctionRepository.findById(3L);
        bids = bidRepository.getBidByAuction(auctionBidded.get());
        assertTrue(bids.isEmpty());
    }
}
