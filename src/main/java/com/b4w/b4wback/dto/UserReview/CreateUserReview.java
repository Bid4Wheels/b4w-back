package com.b4w.b4wback.dto.UserReview;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;


@Getter
public class CreateUserReview {
    private Long reviewer;
    private Long reviewed;
    @Range(min = 1, max = 5)
    private byte punctuation;
    @Size(min = 10, max = 400)
    private String review;
}
