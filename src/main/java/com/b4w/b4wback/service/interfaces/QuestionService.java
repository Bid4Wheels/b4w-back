package com.b4w.b4wback.service.interfaces;


import com.b4w.b4wback.dto.Question.CreateQuestionDTO;
import com.b4w.b4wback.dto.Question.GetQuestionDTO;
import com.b4w.b4wback.model.Question;


public interface QuestionService {
    GetQuestionDTO createQuestion(CreateQuestionDTO questionDTO, Long authorId);

    void deleteQuestion(Long questionId, Long userId);
}
