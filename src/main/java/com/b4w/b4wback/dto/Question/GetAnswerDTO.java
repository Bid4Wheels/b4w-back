package com.b4w.b4wback.dto.Question;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
@AllArgsConstructor
@Getter
public class GetAnswerDTO {
    private LocalDateTime timeOfAnswer;
    private String answer;
}
