package com.b4w.b4wback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime timeOfQuestion;
    private String question;

    @ManyToOne
    private Auction auction;
    @ManyToOne
    private User author;

    public Question(LocalDateTime time, String question, Auction auction, User user) {
        timeOfQuestion = time;
        this.question = question;
        this.auction = auction;
        this.author = user;
    }
}
