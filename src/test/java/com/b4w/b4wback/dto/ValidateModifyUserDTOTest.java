package com.b4w.b4wback.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidateModifyUserDTOTest {
    private Validator validator;
    private ModifyUserDTO modifyUser;


    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        modifyUser = new ModifyUserDTO("Pedro", "Ramirez", "+5491112345678");
    }

    @Test
    void Test001_UserServiceWhenModifyingUserWithValidDataShouldModifyCorrectly(){
        Set<ConstraintViolation<ModifyUserDTO>> violations = validator.validate(modifyUser);
        assertEquals(0, violations.size());
    }

    @Test
    void Test002_UserServiceModifyUserWithNullNameShouldThrowDataIntegrityException(){
        modifyUser.setName(null);
        Set<ConstraintViolation<ModifyUserDTO>> violations = validator.validate(modifyUser);
        assertEquals(1, violations.size());
    }

    @Test
    void Test003_UserServiceModifyUserWithEmptyNameShouldThrowDataIntegrityException(){
        modifyUser.setName("");
        Set<ConstraintViolation<ModifyUserDTO>> violations = validator.validate(modifyUser);
        assertEquals(1, violations.size());
    }

    @Test
    void Test004_UserServiceModifyUserWithNullLastNameShouldThrowDataIntegrityException(){
        modifyUser.setLastName(null);
        Set<ConstraintViolation<ModifyUserDTO>> violations = validator.validate(modifyUser);
        assertEquals(1, violations.size());
    }

    @Test
    void Test005_UserServiceModifyUserWithEmptyLastNameShouldThrowDataIntegrityException(){
        modifyUser.setLastName("");
        Set<ConstraintViolation<ModifyUserDTO>> violations = validator.validate(modifyUser);
        assertEquals(1, violations.size());
    }

    @Test
    void Test006_UserServiceModifyUserWithNullPhoneNumberShouldThrowDataIntegrityException(){
        modifyUser.setPhoneNumber(null);
        Set<ConstraintViolation<ModifyUserDTO>> violations = validator.validate(modifyUser);
        assertEquals(1, violations.size());
    }

    @Test
    void Test007_UserServiceModifyUserWithEmptyPhoneNumberShouldThrowDataIntegrityException(){
        modifyUser.setPhoneNumber("");
        Set<ConstraintViolation<ModifyUserDTO>> violations = validator.validate(modifyUser);
        assertEquals(1, violations.size());
    }

    @Test
    void Test008_UserServiceModifyUserWithSmallPhoneNumberShouldThrowDataIntegrityException(){
        modifyUser.setPhoneNumber("+549111234567");
        Set<ConstraintViolation<ModifyUserDTO>> violations = validator.validate(modifyUser);
        assertEquals(1, violations.size());
    }
}
