package com.b4w.b4wback.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
/**
 *This validation is used to validate if the deadline is at least 24 hours after the creation of the auction.
  I made it because {@DurationMin} from  org.hibernate.validator.constraints.time; was not working.
 **/

public class DurationMinValidator implements ConstraintValidator<DurationMinAfterCreation, LocalDateTime> {
    private static final Integer MINIMUM_HOURS_TO_FINISH_AUCTION=24;
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minDeadline = now.plusHours(MINIMUM_HOURS_TO_FINISH_AUCTION);

        return value.isAfter(minDeadline);
    }

    @Override
    public void initialize(DurationMinAfterCreation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
