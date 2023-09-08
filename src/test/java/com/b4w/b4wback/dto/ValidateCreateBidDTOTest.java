package com.b4w.b4wback.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidateCreateBidDTOTest {
    private Validator validator;
    CreateBidDTO bidDTO;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        bidDTO = new CreateBidDTO(12345, 1111111111L, 1111111111L);
    }

    @Test
    void Test001_ValidateWhenCreatingCreateBidDTOWithAllFieldsAreNotNullShouldBeValid() {
        Set<ConstraintViolation<CreateBidDTO>> violations = validator.validate(bidDTO);
        assertEquals(0, violations.size());
    }

    @Test
    void Test002_ValidateWhenCreatingCreateBidDTOWithAllFieldsAreNotNullExceptAmountShouldThrowOneInvalid() {
        bidDTO.setAmount(null);
        Set<ConstraintViolation<CreateBidDTO>> violations = validator.validate(bidDTO);
        assertEquals(1, violations.size());
    }


    @Test
    void Test003_ValidateWhenCreatingCreateBidDTOWithAllFieldsAreNotNullExceptUserIdShouldThrowOneInvalid() {
        bidDTO.setUserId(null);
        Set<ConstraintViolation<CreateBidDTO>> violations = validator.validate(bidDTO);
        assertEquals(1, violations.size());
    }

    @Test
    void Test004_ValidateWhenCreatingCreateBidDTOWithAllFieldsAreNotNullExceptAuctionIdShouldThrowOneInvalid() {
        bidDTO.setAuctionId(null);
        Set<ConstraintViolation<CreateBidDTO>> violations = validator.validate(bidDTO);
        assertEquals(1, violations.size());
    }
}
