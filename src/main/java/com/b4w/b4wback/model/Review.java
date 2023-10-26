package com.b4w.b4wback.model;

import com.b4w.b4wback.enums.UserReviewType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Setter
@Getter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Size(max = 400, min = 10)
    String review;
    @DecimalMin(value = "1", message = "La puntuación mínima es 1")
    @DecimalMax(value = "5", message = "La puntuación máxima es 5")
    float rating;
    UserReviewType type;

    @OneToOne
    User owner;
    @OneToOne
    User winner;

    LocalDateTime createdAt;

    public Review(String review, float rating, UserReviewType userReviewType, User owner, User winner, LocalDateTime now) {
        this.review = review;
        this.rating = rating;
        this.type = userReviewType;
        this.owner = owner;
        this.winner = winner;
        this.createdAt = now;
    }
}
