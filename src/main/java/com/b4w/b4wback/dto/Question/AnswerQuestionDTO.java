package com.b4w.b4wback.dto.Question;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerQuestionDTO {
    @Size(max = 400, min = 10)
    private String answer;
}
