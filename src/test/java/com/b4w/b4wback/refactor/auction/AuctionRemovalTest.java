package com.b4w.b4wback.refactor.auction;

import com.b4w.b4wback.dto.BidDTO;
import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateBidDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.Question.CreateQuestionDTO;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.model.Question;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.BidRepository;
import com.b4w.b4wback.repository.QuestionRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.b4w.b4wback.util.HttpEntityCreator.authenticateAndGetToken;
import static com.b4w.b4wback.util.HttpEntityCreator.createHeaderWithToken;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuctionRemovalTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AuctionService auctionService;
    private CreateAuctionDTO auctionDTO;
    private CreateUserDTO userDTO;
    private CreateUserDTO userDTO2;

    @BeforeEach
    public void setUp() {
        userDTO = new CreateUserDTO("Nico", "Borja", "test@mail.com",
                "+5491154964341", "1Afjfslkjfl");
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO), CreateUserDTO.class);
        userDTO2 = new CreateUserDTO("Mico", "Vorja", "bejero@dusyum.com",
                "+5491112345678", "1Afjfslkjfl");
        restTemplate.exchange("/user", HttpMethod.POST, createHttpEntity(userDTO2), CreateUserDTO.class);
        auctionDTO= new CreateAuctionDTO(1L,"titulo", "vendo auto",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",1000,100, GasType.GASOLINE,2019,"Blanco",
                4, GearShiftType.MANUAL,new ArrayList<>(Arrays.asList("azul", "nuevo")));

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
    private HttpEntity<CreateQuestionDTO> createHttpEntity(CreateQuestionDTO questionDTO, CreateUserDTO userDTO){
        HttpHeaders headers = createHeaderWithToken(userDTO, restTemplate);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(questionDTO, headers);
    }

    @Test
    void Test11and44_WhenRemovingAnAuctionRemoveAllBidsAndQuestions(){
        Optional<User> bidder= userRepository.findByEmail(userDTO2.getEmail());

        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO,headers),String.class);

        Optional<Auction> auction= auctionRepository.findById(1L);

        restTemplate.exchange("/bid", HttpMethod.POST, createHttpEntity(new CreateBidDTO(10000, bidder.get().getId(),1L),userDTO2), BidDTO.class);

        restTemplate.exchange("/QandA/question", HttpMethod.POST, createHttpEntity(new CreateQuestionDTO("pregunta, cuantos km tiene el auto?", auction.get().getId()), userDTO2), String.class);

        restTemplate.exchange("/QandA/answer", HttpMethod.POST, new HttpEntity<>(new CreateQuestionDTO("como 15000000000", auction.get().getId()),headers), String.class);

        List<Bid> bids = bidRepository.getBidByAuction(auction.get());
        assertFalse(bids.isEmpty());
        List<Question> questions = questionRepository.getQuestionByAuctionId(auction.get().getId());
        assertFalse(questions.isEmpty());

        ResponseEntity<String> deleteAuctionDTOResponseEntity= restTemplate.exchange("/auction/1", HttpMethod.DELETE,
                new HttpEntity<>(headers),String.class);
        assertEquals(HttpStatus.OK, deleteAuctionDTOResponseEntity.getStatusCode());

        bids = bidRepository.getBidByAuction(auction.get());
        assertTrue(bids.isEmpty());
        questions = questionRepository.getQuestionByAuctionId(auction.get().getId());
        assertTrue(questions.isEmpty());
    }

    @Test
    void Test22_WhenDeleteAFinishedAuctionShouldReturnBadRequest(){
        String jwtToken= authenticateAndGetToken(new SignInRequest(userDTO.getEmail(), userDTO.getPassword()), restTemplate);
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer " +jwtToken);
        auctionDTO.setDeadline(LocalDateTime.now().minusHours(1));
        auctionService.createAuction(auctionDTO);
        ResponseEntity<String> deleteAuctionDTOResponseEntity= restTemplate.exchange("/auction/1", HttpMethod.DELETE,
                new HttpEntity<>(headers),String.class);
        assertEquals(HttpStatus.BAD_REQUEST, deleteAuctionDTOResponseEntity.getStatusCode());
    }

    @Test
    void Test45_WhenDeleteAuctionNotAuthenticatedShouldReturnUnauthorized(){
        ResponseEntity<String> deleteAuctionDTOResponseEntity= restTemplate.exchange("/auction/1", HttpMethod.DELETE,
                new HttpEntity<>(new HttpHeaders()),String.class);
        System.out.println(deleteAuctionDTOResponseEntity.getBody());
        assertEquals(HttpStatus.FORBIDDEN, deleteAuctionDTOResponseEntity.getStatusCode());
    }
}
