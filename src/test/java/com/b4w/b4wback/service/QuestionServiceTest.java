package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.Question.*;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.QuestionRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.QuestionService;
import jakarta.mail.MessagingException;
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

    @Autowired
    private QuestionRepository questionRepository;
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
    void Test001_QuestionServiceCreateQuestionWithAllCorrectThenCreateQuestion() throws MessagingException {
        GetQuestionDTO question = questionService.createQuestion(createQuestionDTO, users.get(1).getId());

        assertEquals(createQuestionDTO.getQuestion(), question.getQuestion());
        assertEquals(users.get(1).getEmail(), question.getUser().getEmail());
    }

    @Test
    void Test002_QuestionServiceCreateQuestionMultipleTimesForSameAuctionThenAllCorrect() throws MessagingException {
        GetQuestionDTO question1 = questionService.createQuestion(createQuestionDTO, users.get(1).getId());

        CreateQuestionDTO questionDTO2 = new CreateQuestionDTO("Hello", auction.getId());
        GetQuestionDTO question2 = questionService.createQuestion(questionDTO2, users.get(2).getId());


        assertEquals(createQuestionDTO.getQuestion(), question1.getQuestion());
        assertEquals(users.get(1).getEmail(), question1.getUser().getEmail());


        assertEquals(questionDTO2.getQuestion(), question2.getQuestion());
        assertEquals(users.get(2).getEmail(), question2.getUser().getEmail());
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

    @Test
    void Test005_QuestionServiceAnswerQuestionWithAllCorrectThenAnswerQuestion() throws MessagingException {
        GetQuestionDTO question = questionService.createQuestion(createQuestionDTO, users.get(1).getId());

        String answer ="sale 150000, un saludo";

        AnswerQuestionDTO answerCheck = new AnswerQuestionDTO(answer);

        GetAnswerDTO answerDTO = questionService.answerQuestion(users.get(0).getId(), answerCheck, question.getId());

        assertEquals(answer, answerDTO.getAnswer());
    }

    @Test
    void Test006_QuestionServiceAnswerQuestionWhenAlreadyAnswerShouldOverwriteAnswer() throws MessagingException {
        GetQuestionDTO question = questionService.createQuestion(createQuestionDTO, users.get(1).getId());

        String answer ="sale 150000, un saludo";

        AnswerQuestionDTO answerCheck = new AnswerQuestionDTO(answer);

        GetAnswerDTO answerDTO = questionService.answerQuestion(users.get(0).getId(), answerCheck, question.getId());

        assertEquals(answer, answerDTO.getAnswer());

        String answer2 ="sale 603784, un saludo";

        AnswerQuestionDTO answerCheck2 = new AnswerQuestionDTO(answer2);

        GetAnswerDTO answerDTO2 = questionService.answerQuestion(users.get(0).getId(), answerCheck2, question.getId());

        assertEquals(answer2, answerDTO2.getAnswer());
    }

    @Test
    void Test007_QuestionServiceAnswerQuestionWhenUserIsNotOwnerOfAuctionReturnBadRequest() throws MessagingException {
        GetQuestionDTO question = questionService.createQuestion(createQuestionDTO, users.get(1).getId());

        String answer ="sale 150000, un saludo";

        AnswerQuestionDTO answerCheck = new AnswerQuestionDTO(answer);

        assertThrows(BadRequestParametersException.class,()->questionService.answerQuestion(users.get(1).getId(), answerCheck, question.getId()));
    }

    @Test
    void Test008_QuestionServiceAnswerQuestionWhenAuctionIsNotOpenReturnBadRequest() throws MessagingException {
        auction = new Auction(new CreateAuctionDTO(users.get(0).getId(), "A","text",
                LocalDateTime.of(2025, 10, 10, 10, 10), "Toyota",
                "A1", 1, 10000, GasType.DIESEL, 1990, "Blue", 4,
                GearShiftType.AUTOMATIC, null), null);
        auction.setUser(users.get(0));
        auction = auctionRepository.save(auction);

        createQuestionDTO.setAuctionId(auction.getId());

        GetQuestionDTO question = questionService.createQuestion(createQuestionDTO, users.get(1).getId());

        auction.setStatus(AuctionStatus.FINISHED);
        auctionRepository.save(auction);

        String answer ="sale 150000, un saludo";

        AnswerQuestionDTO answerCheck = new AnswerQuestionDTO(answer);

        assertThrows(BadRequestParametersException.class,()->questionService.answerQuestion(users.get(0).getId(), answerCheck, question.getId()));
    }

    @Test
    void Test009_QuestionServiceGetQandAWhenAuctionHasNoQuestionsReturnEmptyList(){
        List<GetQandADTO> list = questionService.getQandA(auction.getId());

        assertTrue(list.isEmpty());
    }

    @Test
    void Test010_QuestionServiceGetQandAWhenAuctionHasQuestionsReturnList() throws MessagingException {
        GetQuestionDTO question = questionService.createQuestion(createQuestionDTO, users.get(1).getId());

        List<GetQandADTO> list = questionService.getQandA(auction.getId());

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        assertEquals(question.getQuestion(), list.get(0).getQuestion().getQuestion());
        //mutable URL because it contains time
        //assertEquals(question.getUser().getImgURL(), list.get(0).getUser().getImgURL());
    }


    @Test
    void Test011_QuestionServiceDeleteQuestionWhenNotExists(){
        assertThrows(EntityNotFoundException.class,()-> questionService.deleteQuestion(1L,1L));
    }

    @Test
    void Test012_QuestionServiceDeleteQuestionWhenAuctionIsClosed() throws MessagingException {
        questionService.createQuestion(createQuestionDTO, users.get(1).getId());
        auction.setStatus(AuctionStatus.FINISHED);
        auctionRepository.save(auction);
        assertThrows(BadRequestParametersException.class,()-> questionService.deleteQuestion(1L,1L));
    }

    @Test
    void Test013_QuestionServiceDeleteQuestionWhenQuestionHasAnswer() throws MessagingException {
        GetQuestionDTO question = questionService.createQuestion(createQuestionDTO, users.get(1).getId());
        String answer ="sale 150000, un saludo";
        AnswerQuestionDTO answerCheck = new AnswerQuestionDTO(answer);
        questionService.answerQuestion(users.get(0).getId(), answerCheck, question.getId());
        assertThrows(BadRequestParametersException.class,()-> questionService.deleteQuestion(1L,1L));
    }

    @Test
    void Test013_QuestionServiceDeleteAnswerWithAllOk() throws MessagingException {
        GetQuestionDTO question = questionService.createQuestion(createQuestionDTO, users.get(1).getId());
        String answer ="sale 150000, un saludo";
        AnswerQuestionDTO answerCheck = new AnswerQuestionDTO(answer);
        questionService.answerQuestion(users.get(0).getId(), answerCheck, question.getId());
        questionService.deleteAnswer(1L,1L);
        assertNull(questionRepository.findById(1L).get().getAnswer());
    }

}
