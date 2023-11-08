package com.b4w.b4wback.refactor.auction;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.Question.AnswerQuestionDTO;
import com.b4w.b4wback.dto.Question.CreateQuestionDTO;
import com.b4w.b4wback.dto.Question.GetQuestionDTO;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.model.Auction;
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
import java.util.*;

import static com.b4w.b4wback.util.HttpEntityCreator.authenticateAndGetToken;
import static com.b4w.b4wback.util.HttpEntityCreator.createHeaderWithToken;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuctionAnswerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private AuctionRepository auctionRepository;
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
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer " + jwtToken);

        restTemplate.exchange("/auction", HttpMethod.POST, new HttpEntity<>(auctionDTO, header), String.class);


        questionDTO = new CreateQuestionDTO("Make a question", 1L);
    }

    private HttpEntity<CreateQuestionDTO> createHttpEntity(CreateQuestionDTO questionDTO, CreateUserDTO userDTO){
        HttpHeaders headers = createHeaderWithToken(userDTO, restTemplate);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(questionDTO, headers);
    }

    private HttpEntity<AnswerQuestionDTO> createHttpEntity(AnswerQuestionDTO answerDTO, CreateUserDTO userDTO){
        HttpHeaders headers = createHeaderWithToken(userDTO, restTemplate);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(answerDTO, headers);
    }

    @Test
    void Test11_WhenOwnerAnswersExistingQuestionReturnOK(){
        ResponseEntity<GetQuestionDTO> postBidResponse = restTemplate.exchange("/QandA/question", HttpMethod.POST, createHttpEntity(questionDTO, userDTOS.get(1)),
                GetQuestionDTO.class);
        assertEquals(HttpStatus.CREATED, postBidResponse.getStatusCode());
        GetQuestionDTO res = postBidResponse.getBody();

        assert res != null;

        ResponseEntity<String> answerResponse = restTemplate.exchange("/QandA/answer/"+res.getId(), HttpMethod.PATCH,
                createHttpEntity(new AnswerQuestionDTO("150000 pesos, un saludo"), userDTOS.get(0)),
                String.class);

        assertEquals(HttpStatus.CREATED, answerResponse.getStatusCode());
        assertTrue(answerResponse.hasBody());
        assertTrue(Objects.requireNonNull(answerResponse.getBody()).contains("150000 pesos, un saludo"));
    }

    @Test
    void Test22_WhenOwnerAnswersExistingQuestionOnClosedAuctionReturnBadRequest(){
        ResponseEntity<GetQuestionDTO> postBidResponse = restTemplate.exchange("/QandA/question",
                HttpMethod.POST, createHttpEntity(questionDTO, userDTOS.get(1)), GetQuestionDTO.class);
        assertEquals(HttpStatus.CREATED, postBidResponse.getStatusCode());

        Optional<Auction> a = auctionRepository.findById(1L);
        a.get().setStatus(AuctionStatus.FINISHED);
        auctionRepository.save(a.get());

        ResponseEntity<String> answerResponse = restTemplate.exchange("/QandA/answer/1", HttpMethod.PATCH,
                createHttpEntity(new AnswerQuestionDTO("150000 pesos, un saludo"), userDTOS.get(0)),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, answerResponse.getStatusCode());
        assertTrue(answerResponse.hasBody());
        System.out.println(answerResponse.getBody());
        assertTrue(Objects.requireNonNull(answerResponse.getBody()).contains("The auction is already closed"));
    }

    @Test
    void Test64_WhenOwnerDeletesAnswerReturnOK(){
        ResponseEntity<GetQuestionDTO> postBidResponse = restTemplate.exchange("/QandA/question",
                HttpMethod.POST, createHttpEntity(questionDTO, userDTOS.get(1)), GetQuestionDTO.class);
        assertEquals(HttpStatus.CREATED, postBidResponse.getStatusCode());

        GetQuestionDTO res = postBidResponse.getBody();

        assert res != null;
        ResponseEntity<String> answerResponse = restTemplate.exchange("/QandA/answer/"+res.getId(), HttpMethod.PATCH,
                createHttpEntity(new AnswerQuestionDTO("150000 pesos, un saludo"), userDTOS.get(0)),
                String.class);

        assertEquals(HttpStatus.CREATED, answerResponse.getStatusCode());
        assertTrue(answerResponse.hasBody());
        assertTrue(Objects.requireNonNull(answerResponse.getBody()).contains("150000 pesos, un saludo"));

        ResponseEntity<String> deleteResponse = restTemplate.exchange("/QandA/answer/"+res.getId(), HttpMethod.DELETE,
                createHttpEntity(new AnswerQuestionDTO("150000 pesos, un saludo"), userDTOS.get(0)),
                String.class);

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
    }

    @Test
    void Test66_WhenOwnerModifyAnswerReturnOK(){
        ResponseEntity<GetQuestionDTO> postBidResponse = restTemplate.exchange("/QandA/question",
                HttpMethod.POST, createHttpEntity(questionDTO, userDTOS.get(1)), GetQuestionDTO.class);
        assertEquals(HttpStatus.CREATED, postBidResponse.getStatusCode());

        GetQuestionDTO res = postBidResponse.getBody();

        assert res != null;
        ResponseEntity<String> answerResponse = restTemplate.exchange("/QandA/answer/"+res.getId(), HttpMethod.PATCH,
                createHttpEntity(new AnswerQuestionDTO("150000 pesos, un saludo"), userDTOS.get(0)),
                String.class);

        assertEquals(HttpStatus.CREATED, answerResponse.getStatusCode());
        assertTrue(answerResponse.hasBody());
        assertTrue(Objects.requireNonNull(answerResponse.getBody()).contains("150000 pesos, un saludo"));

        ResponseEntity<String> modifyResponse = restTemplate.exchange("/QandA/answer/"+res.getId(), HttpMethod.PATCH,
                createHttpEntity(new AnswerQuestionDTO("una banda de plata"), userDTOS.get(0)),
                String.class);

        assertEquals(HttpStatus.CREATED, modifyResponse.getStatusCode());
        assertTrue(modifyResponse.hasBody());
        assertTrue(Objects.requireNonNull(modifyResponse.getBody()).contains("una banda de plata"));
    }
}
