package com.b4w.b4wback.dto.Question;

import com.b4w.b4wback.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetQandADTO {
    private GetQuestionDTO question;
    private GetAnswerDTO answer;
}
