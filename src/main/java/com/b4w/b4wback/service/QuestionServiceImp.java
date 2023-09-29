package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.Question.CreateQuestionDTO;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Question;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.QuestionRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.time.LocalDateTime;


@Service
@Validated
public class QuestionServiceImp implements QuestionService {
    private final QuestionRepository questionRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    public QuestionServiceImp(QuestionRepository questionRepository, AuctionRepository auctionRepository, UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Question createQuestion(CreateQuestionDTO questionDTO, Long authorId) {
        Optional<Auction> auctionOptional = auctionRepository.findById(questionDTO.getAuctionId());
        if (auctionOptional.isEmpty()) throw new EntityNotFoundException("The auction with the given id was not found");
        Auction auction = auctionOptional.get();
        if (auction.getStatus() != AuctionStatus.OPEN)
            throw new BadRequestParametersException("The auction is already closed");
        if (auction.getUser().getId() == authorId)
            throw new BadRequestParametersException("The author of the auction can't be the same of the question");

        Optional<User> user = userRepository.findById(authorId);
        if (user.isEmpty()) throw new EntityNotFoundException("User with given id");

        return questionRepository.save(new Question(LocalDateTime.now(), questionDTO.getQuestion(), auction, user.get()));
    }
}
