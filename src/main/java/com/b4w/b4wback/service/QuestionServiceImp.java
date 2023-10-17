package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.Question.AnswerQuestionDTO;
import com.b4w.b4wback.dto.Question.CreateQuestionDTO;
import com.b4w.b4wback.dto.Question.GetQandADTO;
import com.b4w.b4wback.dto.Question.GetAnswerDTO;
import com.b4w.b4wback.dto.Question.GetQuestionDTO;
import com.b4w.b4wback.dto.UserDTO;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Question;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.QuestionRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.MailService;
import com.b4w.b4wback.service.interfaces.QuestionService;
import com.b4w.b4wback.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;


@Service
@Validated
public class QuestionServiceImp implements QuestionService {
    private final QuestionRepository questionRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MailService mailService;
    @Value("${sendMail.Boolean.Value}")
    private boolean sendMail;

    public QuestionServiceImp(QuestionRepository questionRepository, AuctionRepository auctionRepository, UserRepository userRepository, UserService userService, MailService mailService) {
        this.questionRepository = questionRepository;
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    @Override
    public GetQuestionDTO createQuestion(CreateQuestionDTO questionDTO, Long authorId) {
        Optional<Auction> auctionOptional = auctionRepository.findById(questionDTO.getAuctionId());
        if (auctionOptional.isEmpty()) throw new EntityNotFoundException("The auction with the given id was not found");
        Auction auction = auctionOptional.get();
        if (auction.getStatus() != AuctionStatus.OPEN)
            throw new BadRequestParametersException("The auction is already closed");
        if (auction.getUser().getId() == authorId)
            throw new BadRequestParametersException("The author of the auction can't be the same of the question");

        Optional<User> user = userRepository.findById(authorId);
        if (user.isEmpty()) throw new EntityNotFoundException("User with given id");

        Question question = questionRepository.save(
                new Question(LocalDateTime.now(), questionDTO.getQuestion(), auction, user.get()));

        UserDTO userDTO = UserDTO.builder().id(user.get().getId()).name(question.getAuthor().getName())
                .lastName(question.getAuthor().getLastName())
                .email(question.getAuthor().getEmail())
                .phoneNumber(question.getAuthor().getPhoneNumber())
                .imgURL(userService.createUrlForDownloadingImage(authorId))
                .build();
        if(sendMail)
            mailService.sendQuestionMail(auction.getUser().getEmail(), userDTO.getName(), auction.getTitle(), question.getQuestion());
        return new GetQuestionDTO(question.getId(), question.getTimeOfQuestion(), question.getQuestion(), userDTO);
    }

    @Override
    public List<GetQandADTO> getQandA(long auctionId) {
        List<GetQandADTO> list = new ArrayList<>();
        List<Question> questions = questionRepository.getQuestionByAuctionId(auctionId);
        for (Question question : questions) {
            UserDTO userDTOQ = UserDTO.builder().id(question.getAuthor().getId())
                    .name(question.getAuthor().getName())
                    .lastName(question.getAuthor().getLastName())
                    .imgURL(userService.createUrlForDownloadingImage(question.getAuthor().getId()))
                    .build();

            GetQandADTO getQandADTO = new GetQandADTO(
                    new GetQuestionDTO(question),
                    new GetAnswerDTO(question.getTimeOfAnswer(), question.getAnswer()),
                    userDTOQ);
            list.add(getQandADTO);
            }
        return list;
    }


    @Override
    public void deleteQuestion(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new EntityNotFoundException("Question with given id was not found"));

        if (question.getAuthor().getId() != userId)
            throw new BadRequestParametersException("The user is not the author of the question");
        if (question.getAuction().getStatus() == AuctionStatus.OPEN) {
            if (question.getAnswer()==null){
                questionRepository.deleteById(questionId);
            }
            else{
                throw new BadRequestParametersException("The question has already been answered");
            }
        } else {
            throw new BadRequestParametersException("The auction is not opened");
        }
    }
    public GetAnswerDTO answerQuestion(Long userId, AnswerQuestionDTO answer, Long idQuestion) {
        Optional<Question> questionOptional = questionRepository.findById(idQuestion);
        if (questionOptional.isEmpty()) throw new EntityNotFoundException("Question with given id was not found");
        Auction auction = questionOptional.get().getAuction();
        if (auction.getUser().getId() != userId)
            throw new BadRequestParametersException("The user is not the owner of the auction");
        if (auction.getStatus() != AuctionStatus.OPEN)
            throw new BadRequestParametersException("The auction is already closed");
        Question question = questionOptional.get();
        question.setAnswer(answer.getAnswer());
        question.setTimeOfAnswer(LocalDateTime.now());
        questionRepository.save(questionOptional.get());
        if(sendMail)
            mailService.sendMail(question.getAuthor().getEmail(), question.getAuction().getUser().getName() + " replied to your question of auction: " + question.getAuction().getTitle(), "your question: " + question.getQuestion() + "\n" + "answer: " + question.getAnswer());
        return new GetAnswerDTO(questionOptional.get().getTimeOfAnswer(), questionOptional.get().getAnswer());
    }

    @Override
    public void deleteAnswer(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new EntityNotFoundException("Question with given id was not found"));
        Auction auction = question.getAuction();
        if (auction.getUser().getId() != userId)
            throw new BadRequestParametersException("The user is not the owner of the auction");
        if (auction.getStatus() != AuctionStatus.OPEN)
            throw new BadRequestParametersException("The auction is already closed");
        question.setAnswer(null);
        question.setTimeOfAnswer(null);
        questionRepository.save(question);
    }
}
