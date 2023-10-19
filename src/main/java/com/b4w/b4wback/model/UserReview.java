package com.b4w.b4wback.model;

import com.b4w.b4wback.enums.UserReviewType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserReview {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    private User reviewer;
    @ManyToOne
    private User reviewed;
    private UserReviewType type;
    private byte punctuation;
    private String review;
    private LocalDateTime date;
}

