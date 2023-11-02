package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.dto.UserReview.CreateUserReview;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.AuctionService;
import com.b4w.b4wback.service.interfaces.ReviewUserService;
import com.b4w.b4wback.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.b4w.b4wback.util.HttpEntityCreator.authenticateAndGetToken;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private AuctionService auctionService;
    CreateUserDTO userDTO;
    PasswordChangerDTO passwordChangerDto;
    GetPasswordCodeDTO passwordCodeDTO;
    ChangePasswordDTO changePasswordDTO;
    ModifyUserDTO modUser;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private ReviewUserService reviewUserService;
    @BeforeEach
    public void setup() {
        userDTO = new CreateUserDTO("Nico", "Borja", "bejero7623@dusyum.com",
                "+5491154964341", "1Afjfslkjfl");
        modUser = new ModifyUserDTO("Pedro", "Ramirez", "+5491112345678");
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
    void Test010_UserServiceWhenRequestForAPasswordChangeWndUserNotFoundShouldThrowEntityNotFoundException() {
        assertThrowsExactly(EntityNotFoundException.class, ()->userService.createPasswordCodeForId( passwordChangerDto));
    }

    @Test
    void Test011_UserServiceWhenRequestForAPasswordCodeShouldGeneratePasswordCode() {
        userService.createUser(userDTO);
        userService.createPasswordCodeForId(passwordChangerDto);
        User user1 = userRepository.findByEmail(userDTO.getEmail()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        assertTrue(user1.getPasswordCode() != 0);
    }
    @Test
    void Test012_UserServiceWhenCheckForPasswordCodeAndUserNotFoundShouldThrowEntityNotFoundException() {
        assertThrowsExactly(EntityNotFoundException.class, ()->userService.checkPasswordCode(passwordCodeDTO));
    }
    @Test
    void Test013_UserServiceWhenCreatingPasswordCodeShouldReturnPasswordCodeCorrectly() {
        userService.createUser(userDTO);
        Integer code = userService.createPasswordCodeForId(passwordChangerDto);
        assertNotNull(code);
        assertEquals(6, code.toString().length());
    }
    @Test
    void Test014_UserServiceWhenChangingPasswordAndUserNotFoundShouldThrowEntityNotFoundException() {
        assertThrowsExactly(EntityNotFoundException.class, ()->userService.changePassword(changePasswordDTO));
    }
    @Test
    void Test015_UserServiceWhenModifyingUserWithValidDataShouldModifyCorrectly(){
        User user = userService.createUser(userDTO);

        userService.modifyUser(user.getId(), modUser);

        UserDTO modifiedUser = userService.getUserById(user.getId());

        assertEquals(modUser.getName(), modifiedUser.getName());
        assertEquals(modUser.getLastName(), modifiedUser.getLastName());
        assertEquals(modUser.getPhoneNumber(), modifiedUser.getPhoneNumber());
    }

    @Test
    void Test016_UserServiceWhenChangingPasswordShouldChangePassword() {
        User user = userService.createUser(userDTO);
        String pas = user.getPassword();
        userService.changePassword(changePasswordDTO);
        assertNotEquals(pas,changePasswordDTO.getPassword());
    }

    @Test
    void Test017_UserServiceWhenCheckingPasswordCodeAndDifferentShouldReturnExceptionPasswordCodeDoesNotMatch(){
        userService.createUser(userDTO);
        userService.createPasswordCodeForId(passwordChangerDto);
        passwordCodeDTO.setPasswordCode(123457);
        assertThrowsExactly(BadRequestParametersException.class, ()->userService.checkPasswordCode(passwordCodeDTO));
    }
    @Test
    void Test018_UserServiceModifyUserWithNullNameShouldThrowDataIntegrityException(){
        User user = userService.createUser(userDTO);

        modUser.setName(null);

        assertThrowsExactly(DataIntegrityViolationException.class,()->userService.modifyUser(user.getId(), modUser));
    }

    @Test
    void Test019_UserServiceModifyUserWithNullLastNameShouldThrowDataIntegrityException(){
        User user = userService.createUser(userDTO);

        modUser.setLastName(null);

        assertThrowsExactly(DataIntegrityViolationException.class,()->userService.modifyUser(user.getId(), modUser));
    }

    @Test
    void Test020_UserServiceModifyUserWithNullPhoneNumberShouldThrowDataIntegrityException(){
        User user = userService.createUser(userDTO);

        modUser.setPhoneNumber(null);

        assertThrowsExactly(DataIntegrityViolationException.class,()->userService.modifyUser(user.getId(), modUser));
    }

    @Test
    void Test021_UserServiceModifyNonExistentUserShouldThrowEntityNotFoundException(){
        modUser.setPhoneNumber(null);

        assertThrowsExactly(EntityNotFoundException.class,()->userService.modifyUser(432189507, modUser));
    }

    @Test
    void Test022_UserServiceWhenDeleteUserShouldChangeNameToDeleted() {
        User user = userService.createUser(userDTO);
        SignInRequest signInRequest = new SignInRequest(userDTO.getEmail(), "1Afjfslkjfl");
        String token = authenticateAndGetToken(signInRequest, restTemplate);
        userService.deleteUser("Bearer " + token);
        assertEquals("Deleted", userRepository.findById(user.getId()).get().getName());
        assertEquals("Deleted", userRepository.findById(user.getId()).get().getLastName());
        assertEquals("Deleted", userRepository.findById(user.getId()).get().getPhoneNumber());
    }

    @Test
    void Test023_UserServiceWhenDeleteUserShouldDeleteAllAuctions(){
        userService.createUser(userDTO);
        SignInRequest signInRequest = new SignInRequest(userDTO.getEmail(), "1Afjfslkjfl");
        String token = authenticateAndGetToken(signInRequest, restTemplate);
        CreateAuctionDTO auctionDTO = new CreateAuctionDTO(1L,"Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",
                4, GearShiftType.AUTOMATIC, null);
        auctionService.createAuction(auctionDTO);
        auctionService.getAuctionById(1L);
        userService.deleteUser("Bearer " + token);
        assertThrowsExactly(EntityNotFoundException.class, ()->auctionService.getAuctionById(1L));
    }

    @Test
    void Test024_UserServiceGetShouldReturnRating(){
        List<User>users = new ArrayList<>();
        users.add(userRepository.save(new User(new CreateUserDTO("Nico", "Pil", "pilo@gmail,com",
                "+5491112345678", "Aa1bcdefgh"))));
        users.add(userRepository.save(new User(new CreateUserDTO("Pablo", "Pil", "fsajkdf@gmail,com",
                "+5491112345678", "Aa1bcdefgh"))));
        users.add(userRepository.save(new User(new CreateUserDTO("Esteban", "Chiquito", "bejero@dusyum,com","+5491112345678",
                "1Afjfslkjfl"))));

        Auction auction = new Auction(new CreateAuctionDTO(users.get(0).getId(), "A","text",
                LocalDateTime.of(2030, 10, 10, 10, 10), "Toyota",
                "A1", 1, 10000, GasType.DIESEL, 1990, "Blue", 4,
                GearShiftType.AUTOMATIC, null), null);
        auction.setUser(users.get(0));
        List<Bid> bids = new ArrayList<>();
        bids.add(new Bid(10000, users.get(1), auction));
        auction.setBids(bids);
        auction = auctionRepository.save(auction);
        auction.setStatus(AuctionStatus.FINISHED);
        auction = auctionRepository.save(auction);
        Auction auction2 = new Auction(new CreateAuctionDTO(users.get(0).getId(), "A","text",
                LocalDateTime.of(2030, 10, 10, 10, 10), "Toyota",
                "A1", 1, 120000, GasType.DIESEL, 1990, "Blue", 4,
                GearShiftType.AUTOMATIC, null), null);
        auctionRepository.save(auction2);
        auction2.setStatus(AuctionStatus.FINISHED);
        auction2 = auctionRepository.save(auction2);
        User user= userRepository.save(new User(new CreateUserDTO("Mark", "Pil", "pliiii@gmail,com",
                "+5491112345678", "dsadsadsadsadada")));
        CreateUserReview createReviewDTO = new CreateUserReview(4.6f, "Muy malo");
        reviewUserService.createUserReviewOwner(createReviewDTO, auction.getId(), users.get(1).getId());
        auction2.setUser(users.get(0));
        bids.add(new Bid(10000, user, auction2));
        auction2.setBids(bids);
        auctionRepository.save(auction2);
        CreateUserReview createReviewDTO2 = new CreateUserReview(1.4f, "El peor auto que vi en mi vida");
        reviewUserService.createUserReviewOwner(createReviewDTO2, auction2.getId(), user.getId());
        assertEquals(3.0f, userService.getUserById(users.get(0).getId()).getRating());
    }
}