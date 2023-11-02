package com.b4w.b4wback.dto;

import com.b4w.b4wback.validation.ValidRatingValues;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateReviewDTO {
    @Size(max = 400, min = 10)
    String review;

   @ValidRatingValues
    float rating;
}
