package com.b4w.b4wback.service.interfaces;


import com.b4w.b4wback.dto.Question.AnswerQuestionDTO;
import com.b4w.b4wback.dto.Question.CreateQuestionDTO;
import com.b4w.b4wback.dto.Question.GetQuestionDTO;


public interface QuestionService {
    GetQuestionDTO createQuestion(CreateQuestionDTO questionDTO, Long authorId);

    void answerQuestion(Long userId, AnswerQuestionDTO answer, Long idQuestion);
}
