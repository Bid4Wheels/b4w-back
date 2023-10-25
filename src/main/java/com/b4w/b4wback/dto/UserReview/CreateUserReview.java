package com.b4w.b4wback.dto.UserReview;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;


@Getter
@AllArgsConstructor
public class CreateUserReview {
    private Long reviewed;
    @Range(min = 1, max = 5)
    private float punctuation;
    @Size(min = 10, max = 400)
    private String review;
}
