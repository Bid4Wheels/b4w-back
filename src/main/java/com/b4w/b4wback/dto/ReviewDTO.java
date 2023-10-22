package com.b4w.b4wback.dto;

import com.b4w.b4wback.enums.UserReviewType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
@AllArgsConstructor
@Getter
public class ReviewDTO {
    String review;
    float rating;
    UserReviewType type;
    UserDTO owner;
    UserDTO winner;
    LocalDateTime createdAt;
}
