package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.ChangePasswordDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.GetPasswordCodeDTO;
import com.b4w.b4wback.dto.PasswordChangerDTO;

import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.dto.ModifyUserDTO;
import com.b4w.b4wback.dto.UserDTO;
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
    ModifyUserDTO modUser;

    @BeforeEach
    public void setup() {
        userDTO = new CreateUserDTO("Nico", "Borja", "bejero7623@dusyum.com",
                "+5491154964341", "1Afjfslkjfl");
        modUser = new ModifyUserDTO("Pedro", "Ramirez", "+5491112345678");
                8493123, "1Afjfslkjfl");
        passwordChangerDto = new PasswordChangerDTO("bejero7623@dusyum.com");
        passwordCodeDTO = new GetPasswordCodeDTO("bejero7623@dusyum.com", 123456);
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
    void Test005_UserServiceWhenCreatingUserWithSameMailShouldThrowDataIntegrityViolationException(){
        userService.createUser(userDTO);
        assertThrowsExactly(DataIntegrityViolationException.class, ()->userService.createUser(userDTO));
    }

    @Test
    void Test006_UserServiceWhenReceiveCreatedUserDTOWithNullPhoneNumberShouldThrowDataIntegrityViolationException() {
        userDTO.setPhoneNumber(null);
        assertThrowsExactly(DataIntegrityViolationException.class, ()->userService.createUser(userDTO));
    }

    @Test
    void Test007_UserServiceWhenReceiveCreatedUserDTOWithNullPassWordShouldThrowDataIntegrityViolationException() {
        userDTO.setPassword(null);
        assertThrowsExactly(DataIntegrityViolationException.class, ()->userService.createUser(userDTO));
    }

    @Test
    void Test008_UserServiceWhenSearchUserByIdWithValidIdShouldReturnUserDTO() {
        User user = userService.createUser(userDTO);
        assertEquals(userDTO.getName(), userService.getUserById(user.getId()).getName());
        assertEquals(userDTO.getLastName(), userService.getUserById(user.getId()).getLastName());
        assertEquals(userDTO.getEmail(), userService.getUserById(user.getId()).getEmail());
        assertEquals(userDTO.getPhoneNumber(), userService.getUserById(user.getId()).getPhoneNumber());
    }

    @Test
    void Test009_UserServiceWhenSearchUserByIdWithInvalidIdShouldThrowEntityNotFoundException() {
        assertThrowsExactly(EntityNotFoundException.class, ()->userService.getUserById(1L));
    }

    @Test
    void Test009_UserServiceWhenRequestForAPasswordChangeWndUserNotFoundShouldThrowEntityNotFoundException() {
        assertThrowsExactly(EntityNotFoundException.class, ()->userService.createPasswordCodeForId( passwordChangerDto));
    }

    @Test
    void Test010_UserServiceWhenRequestForAPasswordCodeShouldGeneratePasswordCode() {
        userService.createUser(userDTO);
        userService.createPasswordCodeForId(passwordChangerDto);
        User user1 = userRepository.findByEmail(userDTO.getEmail()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        assertTrue(user1.getPasswordCode() != 0);
    }
    @Test
    void Test011_UserServiceWhenCheckForPasswordCodeAndUserNotFoundShouldThrowEntityNotFoundException() {
        assertThrowsExactly(EntityNotFoundException.class, ()->userService.checkPasswordCode(passwordCodeDTO));
    }
    @Test
    void Test012_UserServiceWhenCreatingPasswordCodeShouldReturnPasswordCodeCorrectly() {
        userService.createUser(userDTO);
        Integer code = userService.createPasswordCodeForId(passwordChangerDto);
        assertNotNull(code);
        assertEquals(6, code.toString().length());
    }
    @Test
    void Test013_UserServiceWhenChangingPasswordAndUserNotFoundShouldThrowEntityNotFoundException() {
        assertThrowsExactly(EntityNotFoundException.class, ()->userService.changePassword(changePasswordDTO));
    }
    @Test
    void Test010_UserServiceWhenModifyingUserWithValidDataShouldModifyCorrectly(){
        User user = userService.createUser(userDTO);

        userService.modifyUser(user.getId(), modUser);

        UserDTO modifiedUser = userService.getUserById(user.getId());

        assertEquals(modUser.getName(), modifiedUser.getName());
        assertEquals(modUser.getLastName(), modifiedUser.getLastName());
        assertEquals(modUser.getPhoneNumber(), modifiedUser.getPhoneNumber());
    }

    @Test
    void Test014_UserServiceWhenChangingPasswordShouldChangePassword() {
        User user = userService.createUser(userDTO);
        String pas = user.getPassword();
        userService.changePassword(changePasswordDTO);
        assertNotEquals(pas,changePasswordDTO.getPassword());
    }

    @Test
    void Test015_UserServiceWhenCheckingPasswordCodeAndDifferentShouldReturnExceptionPasswordCodeDoesNotMatch(){
        userService.createUser(userDTO);
        userService.createPasswordCodeForId(passwordChangerDto);
        passwordCodeDTO.setPasswordCode(123457);
        assertThrowsExactly(BadRequestParametersException.class, ()->userService.checkPasswordCode(passwordCodeDTO));
    }
    @Test
    void Test011_UserServiceModifyUserWithNullNameShouldThrowDataIntegrityException(){
        User user = userService.createUser(userDTO);

        modUser.setName(null);

        assertThrowsExactly(DataIntegrityViolationException.class,()->userService.modifyUser(user.getId(), modUser));
    }

    @Test
    void Test012_UserServiceModifyUserWithNullLastNameShouldThrowDataIntegrityException(){
        User user = userService.createUser(userDTO);

        modUser.setLastName(null);

        assertThrowsExactly(DataIntegrityViolationException.class,()->userService.modifyUser(user.getId(), modUser));
    }

    @Test
    void Test013_UserServiceModifyUserWithNullPhoneNumberShouldThrowDataIntegrityException(){
        User user = userService.createUser(userDTO);

        modUser.setPhoneNumber(null);

        assertThrowsExactly(DataIntegrityViolationException.class,()->userService.modifyUser(user.getId(), modUser));
    }

    @Test
    void Test014_UserServiceModifyNonExistentUserShouldThrowEntityNotFoundException(){
        modUser.setPhoneNumber(null);

        assertThrowsExactly(EntityNotFoundException.class,()->userService.modifyUser(432189507, modUser));
    }
}