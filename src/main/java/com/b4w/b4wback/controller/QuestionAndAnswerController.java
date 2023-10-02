package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.Question.CreateQuestionDTO;
import com.b4w.b4wback.dto.Question.GetQandADTO;
import com.b4w.b4wback.dto.Question.GetQuestionDTO;
import com.b4w.b4wback.dto.UserDTO;
import com.b4w.b4wback.model.Question;
import com.b4w.b4wback.service.interfaces.JwtService;
import com.b4w.b4wback.service.interfaces.QuestionService;
import com.b4w.b4wback.service.interfaces.UserService;
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
}
