package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateReviewDTO;
import com.b4w.b4wback.dto.UserReview.ReviewDTO;
import com.b4w.b4wback.dto.UserReview.CreateUserReview;
import com.b4w.b4wback.model.UserReview;
import com.b4w.b4wback.service.interfaces.JwtService;
import com.b4w.b4wback.service.interfaces.ReviewUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewUserService reviewService;
    private final JwtService jwtService;

    @PostMapping("/winner/{auctionId}")
    public ResponseEntity<?> createReviewForWinner(@RequestBody @Valid CreateReviewDTO createReviewDTO, @PathVariable long auctionId, @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReviewForWinner(createReviewDTO, auctionId, token));
    }

    @PostMapping("/owner/{auctionId}")
    public ResponseEntity<ReviewDTO> createReviewForOwner(@RequestHeader("Authorization") String auth,
                                                           @PathVariable long auctionId,
                                                           @RequestBody @Valid CreateUserReview createReviewDTO){
        final String jwt = auth.substring(7);
        Long userId = jwtService.extractId(jwt);

        UserReview review = reviewService.createUserReviewOwner(createReviewDTO, auctionId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReviewDTO(review));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ReviewDTO>> getFilteredReviews(@RequestHeader("Authorization") String auth,
                                                              @RequestParam float rate,
                                                              @RequestParam Long userId){
        List<ReviewDTO> reviews = reviewService.getReviewsFiltered(userId, rate);
        return ResponseEntity.status(HttpStatus.OK).body(reviews);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getReviews(@PathVariable long userId){
        return ResponseEntity.ok(reviewService.getReviews(userId));
    }
}