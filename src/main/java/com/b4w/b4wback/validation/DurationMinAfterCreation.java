package com.b4w.b4wback.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DurationMinValidator.class)
public @interface DurationMinAfterCreation {
    String message() default "Deadline must be at least 24 hours after the creation.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
