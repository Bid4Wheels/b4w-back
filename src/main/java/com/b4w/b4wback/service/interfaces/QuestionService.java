package com.b4w.b4wback.service.interfaces;


import com.b4w.b4wback.dto.Question.CreateQuestionDTO;
import com.b4w.b4wback.model.Question;


public interface QuestionService {
    Question createQuestion(CreateQuestionDTO questionDTO, Long authorId);
}
