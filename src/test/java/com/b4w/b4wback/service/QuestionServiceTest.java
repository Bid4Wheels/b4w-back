package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateBidDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.Question.CreateQuestionDTO;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.model.Question;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class QuestionServiceTest {
    @Autowired
    QuestionService questionService;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private UserRepository userRepository;

    private CreateQuestionDTO createQuestionDTO;
    private List<User> users;
    private Auction auction;


    @BeforeEach
    public void setup(){
        users = new ArrayList<>();
        users.add(userRepository.save(new User(new CreateUserDTO("Nico", "Pil", "pilo@gmail,com",
                "+5491112345678", "Aa1bcdefgh"))));
        users.add(userRepository.save(new User(new CreateUserDTO("Pablo", "Pil", "fsajkdf@gmail,com",
                "+5491112345678", "Aa1bcdefgh"))));
        users.add(userRepository.save(new User(new CreateUserDTO("Esteban", "Chiquito", "bejero@dusyum,com","+5491112345678",
                "1Afjfslkjfl"))));

        auction = new Auction(new CreateAuctionDTO(users.get(0).getId(), "A","text",
                LocalDateTime.of(2030, 10, 10, 10, 10), "Toyota",
                "A1", 1, 10000, GasType.DIESEL, 1990, "Blue", 4,
                GearShiftType.AUTOMATIC, null), null);
        auction.setUser(users.get(0));
        auction = auctionRepository.save(auction);
        createQuestionDTO = new CreateQuestionDTO("Hello world", auction.getId());
    }

    @Test
    void Test001_QuestionServiceCreateQuestionWithAllCorrectThenCreateQuestion() {
        Question question = questionService.createQuestion(createQuestionDTO, users.get(1).getId());

        assertEquals(createQuestionDTO.getQuestion(), question.getQuestion());
        assertEquals(createQuestionDTO.getAuctionId(), question.getAuction().getId());
        assertEquals(users.get(1).getId(), question.getAuthor().getId());
    }

    @Test
    void Test002_QuestionServiceCreateQuestionMultipleTimesForSameAuctionThenAllCorrect(){
        Question question1 = questionService.createQuestion(createQuestionDTO, users.get(1).getId());

        CreateQuestionDTO questionDTO2 = new CreateQuestionDTO("Hello", auction.getId());
        Question question2 = questionService.createQuestion(questionDTO2, users.get(2).getId());


        assertEquals(createQuestionDTO.getQuestion(), question1.getQuestion());
        assertEquals(createQuestionDTO.getAuctionId(), question1.getAuction().getId());
        assertEquals(users.get(1).getId(), question1.getAuthor().getId());

        assertEquals(questionDTO2.getQuestion(), question2.getQuestion());
        assertEquals(questionDTO2.getAuctionId(), question2.getAuction().getId());
        assertEquals(users.get(2).getId(), question2.getAuthor().getId());
    }

    @Test
    void Test003_QuestionServiceCreateQuestionOverNotOpenAuctionThenGetError(){
        auction = new Auction(new CreateAuctionDTO(users.get(0).getId(), "A","text",
                LocalDateTime.of(2020, 10, 10, 10, 10), "Toyota",
                "A1", 1, 10000, GasType.DIESEL, 1990, "Blue", 4,
                GearShiftType.AUTOMATIC, null), null);
        auction.setUser(users.get(0));
        auction = auctionRepository.save(auction);

        createQuestionDTO.setAuctionId(auction.getId());

        assertThrows(BadRequestParametersException.class,()->questionService.createQuestion(createQuestionDTO, users.get(1).getId()));
    }

    @Test
    void Test004_QuestionServiceCreateQuestionOverAuctionWithSameUser(){
        assertThrows(BadRequestParametersException.class,()->questionService.createQuestion(createQuestionDTO, users.get(0).getId()));
    }
}
