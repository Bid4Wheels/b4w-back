package com.b4w.b4wback.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidFloatValuesValidator implements ConstraintValidator<ValidRatingValues, Float> {
    @Override
    public boolean isValid(Float value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return value % 0.5 == 0 && value >= 1.0 && value <= 5.0;
    }
}

