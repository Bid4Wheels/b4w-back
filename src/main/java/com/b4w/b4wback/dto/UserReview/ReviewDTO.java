package com.b4w.b4wback.dto.UserReview;

import com.b4w.b4wback.dto.UserDTO;
import com.b4w.b4wback.enums.UserReviewType;
import com.b4w.b4wback.model.UserReview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReviewDTO {
    String review;
    float rating;
    UserReviewType type;
    UserDTO reviewer;
    UserDTO reviewed;
    LocalDateTime createdAt;

    public ReviewDTO(UserReview userReview){
        review = userReview.getReview();
        rating = userReview.getPunctuation();
        type = userReview.getType();
        reviewer = new UserDTO(userReview.getReviewer());
        reviewed = new UserDTO(userReview.getReviewed());
        createdAt = userReview.getDate();
    }

    public ReviewDTO(UserReview userReview, UserDTO reviewer, UserDTO reviewed){
        review = userReview.getReview();
        rating = userReview.getPunctuation();
        type = userReview.getType();
        this.reviewer = reviewer;
        this.reviewed = reviewed;
        createdAt = userReview.getDate();
    }
}
