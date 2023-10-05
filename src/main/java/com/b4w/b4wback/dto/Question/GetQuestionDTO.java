package com.b4w.b4wback.dto.Question;

import com.b4w.b4wback.dto.UserDTO;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.User;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class GetQuestionDTO {
    private Long id;
    private LocalDateTime timeOfQuestion;
    private String question;
    private UserDTO user;
}
