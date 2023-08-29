package com.b4w.b4wback.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidateCreateOfferDTOTest {
    private Validator validator;
    CreateOfferDTO offerDTO;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        offerDTO = new CreateOfferDTO(12345, 1111111111L, 1111111111L);
    }

    @Test
    void Test001_ValidateWhenCreatingCreateOfferDTOWithAllFieldsAreNotNullShouldBeValid() {
        Set<ConstraintViolation<CreateOfferDTO>> violations = validator.validate(offerDTO);
        assertEquals(0, violations.size());
    }

    @Test
    void Test002_ValidateWhenCreatingCreateOfferDTOWithAllFieldsAreNotNullExceptAmountShouldThrowOneInvalid() {
        offerDTO.setAmount(null);
        Set<ConstraintViolation<CreateOfferDTO>> violations = validator.validate(offerDTO);
        assertEquals(1, violations.size());
    }


    @Test
    void Test003_ValidateWhenCreatingCreateOfferDTOWithAllFieldsAreNotNullExceptUserIdShouldThrowOneInvalid() {
        offerDTO.setUserId(null);
        Set<ConstraintViolation<CreateOfferDTO>> violations = validator.validate(offerDTO);
        assertEquals(1, violations.size());
    }

    @Test
    void Test004_ValidateWhenCreatingCreateOfferDTOWithAllFieldsAreNotNullExceptAuctionIdShouldThrowOneInvalid() {
        offerDTO.setAuctionId(null);
        Set<ConstraintViolation<CreateOfferDTO>> violations = validator.validate(offerDTO);
        assertEquals(1, violations.size());
    }
}
