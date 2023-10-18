package com.b4w.b4wback.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime timeOfQuestion;
    private String question;
    private String answer;
    private LocalDateTime timeOfAnswer;

    @ManyToOne
    private Auction auction;
    @ManyToOne
    private User author;

    public Question(LocalDateTime time, String question, Auction auction, User user) {
        timeOfQuestion = time;
        this.question = question;
        this.auction = auction;
        this.author = user;
        this.answer = null;
        this.timeOfAnswer = null;
    }

}
