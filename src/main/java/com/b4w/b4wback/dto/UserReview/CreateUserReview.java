package com.b4w.b4wback.dto.UserReview;

import com.b4w.b4wback.validation.ValidRatingValues;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;


@Getter
@AllArgsConstructor
public class CreateUserReview {
    @ValidRatingValues
    private float rating;
    @Size(min = 10, max = 400)
    private String review;
}
