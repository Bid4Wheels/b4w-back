package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.Question.CreateQuestionDTO;
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
    private final UserService userService;
    private final JwtService jwtService;


    public QuestionAndAnswerController(QuestionService questionService, UserService userService, JwtService jwtService) {
        this.questionService = questionService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping()
    public ResponseEntity<GetQuestionDTO> createUserAuction(@RequestHeader("Authorization") String auth,  @RequestBody @Valid CreateQuestionDTO questionDTO){
        final String jwt = auth.substring(7);
        Long userId = jwtService.extractId(jwt);

        Question question = questionService.createQuestion(questionDTO, userId);
        UserDTO userDTO = UserDTO.builder().name(question.getAuthor().getName())
                .lastName(question.getAuthor().getLastName())
                .email(question.getAuthor().getEmail())
                .phoneNumber(question.getAuthor().getPhoneNumber())
                .imgURL(userService.createUrlForDownloadingImage(userId))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(new GetQuestionDTO(question.getId(),
                question.getTimeOfQuestion(), question.getQuestion(), userDTO));
    }
}
