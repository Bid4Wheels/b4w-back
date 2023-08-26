package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.ChangePasswordDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.GetPasswordCodeDTO;
import com.b4w.b4wback.dto.PasswordChangerDTO;

import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    CreateUserDTO userDTO;
    PasswordChangerDTO passwordChangerDto;
    GetPasswordCodeDTO passwordCodeDTO;
    ChangePasswordDTO changePasswordDTO;



    @BeforeEach
    public void setup() {
        userDTO = new CreateUserDTO("Nico", "Borja", "bejero7623@dusyum.com",
                8493123, "1Afjfslkjfl");
        passwordChangerDto = new PasswordChangerDTO("bejero7623@dusyum.com");
        passwordCodeDTO = new GetPasswordCodeDTO("bejero7623@dusyum.com", 123456, true);
        changePasswordDTO = new ChangePasswordDTO("bejero7623@dusyum.com","1Afjfslkjfl");

    }

    @Test
    void Test001_UserServiceWhenReceiveCreatedUserDTOWithValidDTOShouldCreateUser() {
        User user = userService.createUser(userDTO);
        assertEquals(user.getId(), userRepository.getReferenceById(user.getId()).getId());
    }

    @Test
    void Test002_UserServiceWhenReceiveCreatedUserDTOWithNullNameShouldThrowDataIntegrityViolationException() {
        userDTO.setName(null);
        assertThrowsExactly(DataIntegrityViolationException.class, ()->userService.createUser(userDTO));
    }

    @Test
    void Test003_UserServiceWhenReceiveCreatedUserDTOWithNullLastNameShouldThrowDataIntegrityViolationException() {
        userDTO.setLastName(null);
        assertThrowsExactly(DataIntegrityViolationException.class, ()->userService.createUser(userDTO));
    }

    @Test
    void Test004_UserServiceWhenReceiveCreatedUserDTOWithNullEmailShouldThrowDataIntegrityViolationException() {
        userDTO.setEmail(null);
        assertThrowsExactly(DataIntegrityViolationException.class, ()->userService.createUser(userDTO));
    }

    @Test
    void Test005_UserServiceWhenReceiveCreatedUserDTOWithNullPhoneNumberShouldThrowDataIntegrityViolationException() {
        userDTO.setEmail(null);
        assertThrowsExactly(DataIntegrityViolationException.class, ()->userService.createUser(userDTO));
    }

    @Test
    void Test006_UserServiceWhenReceiveCreatedUserDTOWithNullPassWordShouldThrowDataIntegrityViolationException() {
        userDTO.setPassword(null);
        assertThrowsExactly(DataIntegrityViolationException.class, ()->userService.createUser(userDTO));
    }

    @Test
    void Test007_UserServiceWhenSearchUserByIdWithValidIdShouldReturnUserDTO() {
        User user = userService.createUser(userDTO);
        assertEquals(userDTO.getName(), userService.getUserById(user.getId()).getName());
        assertEquals(userDTO.getLastName(), userService.getUserById(user.getId()).getLastName());
        assertEquals(userDTO.getEmail(), userService.getUserById(user.getId()).getEmail());
        assertEquals(userDTO.getPhoneNumber(), userService.getUserById(user.getId()).getPhoneNumber());
    }

    @Test
    void Test008_UserServiceWhenSearchUserByIdWithInvalidIdShouldThrowEntityNotFoundException() {
        assertThrowsExactly(EntityNotFoundException.class, ()->userService.getUserById(1L));
    }

    @Test
    void Test009_UserServiceWhenRequestForAPasswordChangeWndUserNotFoundShouldThrowEntityNotFoundException() {
        assertThrowsExactly(EntityNotFoundException.class, ()->userService.createPasswordCodeForId(1L, passwordChangerDto));
    }

    @Test
    void Test010_UserServiceWhenRequestForAPasswordCodeShouldGeneratePasswordCode() {
        User user = userService.createUser(userDTO);
        userService.createPasswordCodeForId(user.getId(), passwordChangerDto);
        User user1 = userRepository.findByEmail(userDTO.getEmail()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        assertTrue(user1.getPasswordCode() != 0);
    }
    @Test
    void Test011_UserServiceWhenCheckForPasswordCodeAndUserNotFoundShouldThrowEntityNotFoundException() {
        assertThrowsExactly(EntityNotFoundException.class, ()->userService.checkPasswordCode(passwordCodeDTO));
    }
    @Test
    void Test012_UserServiceWhenComparingPasswordCodeAndEqualsShouldReturnPasswordCodeDTOWithTrue() {
        User user = userService.createUser(userDTO);
        Integer code = userService.createPasswordCodeForId(user.getId(), passwordChangerDto);
        assertNotNull(code);
        assertEquals(6, code.toString().length());
    }
    @Test
    void Test013_UserServiceWhenChangingPasswordAndUserNotFoundShouldThrowEntityNotFoundException() {
        assertThrowsExactly(EntityNotFoundException.class, ()->userService.changePassword(changePasswordDTO));
    }

    @Test
    void Test014_UserServiceWhenChangingPasswordShouldChangePassword() {
        User user = userService.createUser(userDTO);
        String pas = user.getPassword();
        userService.changePassword(changePasswordDTO);
        assertNotEquals(pas,changePasswordDTO.getPassword());
    }
}