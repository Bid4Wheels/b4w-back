package com.b4w.b4wback.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidateCreateUserDTOTest {
    private Validator validator;
    CreateUserDTO userDTO;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userDTO = new CreateUserDTO("Nico", "Borja", "bejero7623@dusyum.com",
                8493123, "1Afjfslkjfl");
    }

    @Test
    void Test001_ValidateWhenCreatingCreateUserDTOWithAllFieldsAreNotNullAndPasswordFormatValidShouldBeValid(){
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);
        assertEquals(0, violations.size());
    }

    @Test
    void Test002_ValidateWhenCreatingCreateUserDTOWithAllFieldsAreNotNullExceptNameAndPasswordFormatValidShouldThrowOneInvalid(){
        userDTO.setName(null);
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
    }

    @Test
    void Test003_ValidateWhenCreatingCreateUserDTOWithAllFieldsAreNotNullExceptNameWithEmptyStringAndPasswordFormatValidShouldThrowOneInvalid(){
        userDTO.setName("");
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
    }

    @Test
    void Test004_ValidateWhenCreatingCreateUserDTOWithAllFieldsAreNotNullExceptLastNameAndPasswordFormatValidShouldThrowOneInvalid(){
        userDTO.setLastName(null);
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
    }

    @Test
    void Test003_ValidateWhenCreatingCreateUserDTOWithAllFieldsAreNotNullExceptLastNameWithEmptyStringAndPasswordFormatValidShouldThrowOneInvalid(){
        userDTO.setLastName("");
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
    }

    @Test
    void Test004_ValidateWhenCreatingCreateUserDTOWithAllFieldsAreNotNullExceptPhoneAndPasswordFormatValidShouldThrowOneInvalid() {
        userDTO.setPhoneNumber(null);
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
    }

    @Test
    void Test005_ValidateWhenCreatingCreateUserDTOWithAllFieldsAreNotNullExceptEmailAndPasswordFormatValidShouldThrowOneInvalid(){
        userDTO.setEmail(null);
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
    }

    @Test
    void Test006_ValidateWhenCreatingCreateUserDTOWithAllFieldsAreNotNullPasswordFormatValidAndEmailNotContainingArrobaShouldThrowOneInvalid(){
        userDTO.setEmail("borjagmail.com");
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
    }

    @Test
    void Test007_ValidateWhenCreatingCreateUserDTOWithAllFieldsAreNotNullPasswordFormatValidAndEmailNotContainingDomainPointShouldThrowOneInvalid(){
        userDTO.setEmail("borja@gmailcom");
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
    }

    @Test
    void Test008_ValidateWhenCreatingCreateUserDTOWithAllFieldsAreNotNullPasswordFormatValidAndEmailNotContainingDomainEmptyShouldThrowOneInvalid(){
        userDTO.setEmail("borja@gmail.");
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
    }

    @Test
    void Test009_ValidateWhenCreatingCreateUserDTOWithAllFieldsAreNotNullExceptPasswordShouldThrowOneInvalid(){
        userDTO.setPassword(null);
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
    }

    @Test
    void Test010_ValidateWhenCreatingCreateUserDTOWithAllFieldsAreNotNullSmallPasswordShouldThrowOneInvalid(){
        userDTO.setPassword("1Absdfg");
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
    }

    @Test
    void Test010_ValidateWhenCreatingCreateUserDTOWithAllFieldsAreNotNullPasswordMissingUpperCaseShouldThrowOneInvalid(){
        userDTO.setPassword("1qbsdfgf");
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
    }

    @Test
    void Test010_ValidateWhenCreatingCreateUserDTOWithAllFieldsAreNotNullPasswordMissingLowerCaseShouldThrowOneInvalid(){
        userDTO.setPassword("1qbsdfgf");
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
    }

    @Test
    void Test011_ValidateWhenCreatingCreateUserDTOWithAllFieldsAreNotNullPasswordMissingNumberShouldThrowOneInvalid(){
        userDTO.setPassword("Aqbsdfgf");
        Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(userDTO);

        assertEquals(1, violations.size());
    }
}
