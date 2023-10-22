package com.b4w.b4wback.dto;

import com.b4w.b4wback.enums.UserReviewType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
@AllArgsConstructor
@Getter
public class ReviewDTO {
    @Size(max = 400, min = 10)
    String review;
    @DecimalMin(value = "1", message = "La puntuación mínima es 1")
    @DecimalMax(value = "5", message = "La puntuación máxima es 5")
    float rating;
    UserReviewType type;
    UserDTO owner;
    UserDTO winner;
    LocalDateTime createdAt;
}
