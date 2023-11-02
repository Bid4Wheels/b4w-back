package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateReviewDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.UserReview.CreateUserReview;
import com.b4w.b4wback.dto.UserReview.ReviewDTO;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.enums.UserReviewType;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.model.*;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.repository.UserReviewRepository;
import com.b4w.b4wback.service.interfaces.JwtService;
import com.b4w.b4wback.service.interfaces.ReviewUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ReviewServiceTest {

    private List<User> users;
    private Auction auction;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private ReviewUserService reviewService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserReviewRepository reviewRepository;

    @BeforeEach
    public void setup(){
        users = new ArrayList<>();
        users.add(userRepository.save(new User(new CreateUserDTO("Nico", "Pil", "pilo@gmail,com",
                "+5491112345678", "Aa1bcdefgh"))));
        users.add(userRepository.save(new User(new CreateUserDTO("Pablo", "Pil", "fsajkdf@gmail,com",
                "+5491112345678", "Aa1bcdefgh"))));
        users.add(userRepository.save(new User(new CreateUserDTO("Esteban", "Chiquito", "bejero@dusyum,com","+5491112345678",
                "1Afjfslkjfl"))));

        auction = new Auction(new CreateAuctionDTO(users.get(0).getId(), "A","text",
                LocalDateTime.of(2030, 10, 10, 10, 10), "Toyota",
                "A1", 1, 10000, GasType.DIESEL, 1990, "Blue", 4,
                GearShiftType.AUTOMATIC, null), null);
        auction.setUser(users.get(0));
        List<Bid> bids = new ArrayList<>();
        bids.add(new Bid(10000, users.get(1), auction));
        auction.setBids(bids);
        auction = auctionRepository.save(auction);
    }


    @Test
    void Test001_ReviewServiceCreateReviewForWinnerShouldReturnReviewDTO(){
        auction.setStatus(AuctionStatus.FINISHED);
        auction = auctionRepository.save(auction);
        CreateReviewDTO createReviewDTO = new CreateReviewDTO("Muy buen comprador", 4.5f);

        String token = jwtService.generateToken(users.get(0).getEmail(), users.get(0).getId());
        ReviewDTO reviewDTO = reviewService.createReviewForWinner(createReviewDTO, auction.getId(),"Bearer " +token);


        assertEquals(createReviewDTO.getReview(), reviewDTO.getReview());
        assertEquals(createReviewDTO.getRating(), reviewDTO.getRating());
        assertEquals(UserReviewType.WINNER, reviewDTO.getType());
        assertEquals(users.get(1).getId(), reviewDTO.getReviewed().getId());
        assertEquals(users.get(0).getId(), reviewDTO.getReviewer().getId());

        UserReview review = reviewRepository.findById(1L).orElseThrow(() -> new EntityNotFoundException("Review not found"));
        assertEquals(createReviewDTO.getReview(), review.getReview());
        assertEquals(createReviewDTO.getRating(), review.getPunctuation());
        assertEquals(UserReviewType.WINNER, review.getType());
        assertEquals(users.get(0).getId(), review.getReviewer().getId());
        assertEquals(users.get(1).getId(), review.getReviewed().getId());
    }

    @Test
    void Test002_ReviewServiceCreateReviewForWinnerWhenIsNotFinishedShouldThrowException(){
        auction = auctionRepository.save(auction);
        CreateReviewDTO createReviewDTO = new CreateReviewDTO("Muy buen comprador", 5);
        String token = jwtService.generateToken(users.get(0).getEmail(), users.get(0).getId());
        try {
            reviewService.createReviewForWinner(createReviewDTO, auction.getId(), token);
        } catch (BadRequestParametersException e) {
            assertEquals("Auction is not finished or you are not the owner", e.getMessage());
        }
    }
    @Test
    void Test003_ReviewServiceCreateReviewForOwnerShouldReturnReviewDTO(){
        auction.setStatus(AuctionStatus.FINISHED);
        auction = auctionRepository.save(auction);
        CreateUserReview createReviewDTO = new CreateUserReview(4, "Very bad service");

        UserReview review = reviewService.createUserReviewOwner(createReviewDTO, auction.getId(), users.get(1).getId());
        assertEquals(createReviewDTO.getReview(), review.getReview());
        assertEquals(createReviewDTO.getRating(), review.getPunctuation());
        assertEquals(UserReviewType.OWNER, review.getType());
        assertEquals(users.get(1).getId(), review.getReviewer().getId());
        assertEquals(users.get(0).getId(), review.getReviewed().getId());
    }

    @Test
    void Test004_ReviewServiceCreateReviewForOwnerWhenIsNotFinishedShouldThrowException(){
        auction = auctionRepository.save(auction);
        CreateUserReview createReviewDTO = new CreateUserReview(4.6f, "Muy malo");
        assertThrows(BadCredentialsException.class, ()->
                reviewService.createUserReviewOwner(createReviewDTO, auction.getId(), users.get(1).getId()));
    }

    @Test
    void Test005_ReviewServiceGetFilteredReviewsOfNoReviewsThenEmptyList() {
        List<ReviewDTO> reviews = reviewService.getReviewsFiltered(users.get(0).getId(), 0f);
        assertTrue(reviews.isEmpty());
    }

    @Test
    void Test006_ReviewServiceGetFilteredReviewsThenReturn2() {
        auction.setStatus(AuctionStatus.FINISHED);
        auction = auctionRepository.save(auction);
        CreateUserReview createReviewDTO = new CreateUserReview(4.5f, "Muy malo");
        reviewService.createUserReviewOwner(createReviewDTO, auction.getId(), users.get(1).getId());

        auction = auctionRepository.save(auction);
        createReviewDTO = new CreateUserReview(3.5f, "Muy malo");
        reviewService.createUserReviewOwner(createReviewDTO, auction.getId(), users.get(1).getId());

        auction = auctionRepository.save(auction);
        createReviewDTO = new CreateUserReview(2.5f, "Muy malo");
        reviewService.createUserReviewOwner(createReviewDTO, auction.getId(), users.get(1).getId());

        List<ReviewDTO> filteredReviews = reviewService.getReviewsFiltered(users.get(0).getId(), 3.5f);

        assertEquals(2, filteredReviews.size());
    }

    @Test
    void Test007_ReviewServiceGetReviewsShouldReturnListOfReviewDTO(){
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
        reviewService.createUserReviewOwner(createReviewDTO, auction.getId(), users.get(1).getId());
        auction2.setUser(users.get(0));
        List<Bid> bids = new ArrayList<>();
        bids.add(new Bid(10000, user, auction2));
        auction2.setBids(bids);
        auctionRepository.save(auction2);
        reviewService.createUserReviewOwner(createReviewDTO, auction2.getId(), user.getId());
        List<com.b4w.b4wback.dto.UserReview.ReviewDTO> reviews = reviewService.getReviews(users.get(0).getId());
        assertEquals(2, reviews.size());
    }
}
