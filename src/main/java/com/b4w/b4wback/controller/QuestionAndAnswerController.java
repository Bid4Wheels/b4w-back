package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.Question.AnswerQuestionDTO;
import com.b4w.b4wback.dto.Question.CreateQuestionDTO;
import com.b4w.b4wback.dto.Question.GetQandADTO;
import com.b4w.b4wback.dto.Question.GetAnswerDTO;
import com.b4w.b4wback.dto.Question.GetQuestionDTO;
import com.b4w.b4wback.service.interfaces.JwtService;
import com.b4w.b4wback.service.interfaces.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/QandA")
public class QuestionAndAnswerController {
    private final QuestionService questionService;
    private final JwtService jwtService;


    public QuestionAndAnswerController(QuestionService questionService, JwtService jwtService) {
        this.questionService = questionService;
        this.jwtService = jwtService;
    }

    @PostMapping("/question")
    public ResponseEntity<GetQuestionDTO> createUserAuction(@RequestHeader("Authorization") String auth,  @RequestBody @Valid CreateQuestionDTO questionDTO){
        final String jwt = auth.substring(7);
        Long userId = jwtService.extractId(jwt);

        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(questionDTO, userId));
    }


    @GetMapping("/{auctionId}")
    public ResponseEntity<GetQandADTO> getQandA(@PathVariable long auctionId){
        return ResponseEntity.status(HttpStatus.OK).body(questionService.getQandA(auctionId));
    }

    @PatchMapping("/answer/{id}")
    public ResponseEntity<GetAnswerDTO> answerQuestion(@RequestHeader("Authorization") String token, @RequestBody @Valid AnswerQuestionDTO answer, @PathVariable Long id){
        final String jwt = token.substring(7);
        Long userId = jwtService.extractId(jwt);

        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.answerQuestion(userId, answer, id));
    }
}
