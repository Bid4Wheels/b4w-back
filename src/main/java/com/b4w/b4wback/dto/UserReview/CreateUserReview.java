package com.b4w.b4wback.dto.UserReview;

import com.b4w.b4wback.enums.UserReviewType;
import com.b4w.b4wback.model.User;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;



public class CreateUserReview {
    private User reviewer;
    private User reviewed;
    private UserReviewType type;
    @Range(min = 1, max = 5)
    private byte punctuation;
    @Size(min = 10, max = 400)
    private String review;
}
