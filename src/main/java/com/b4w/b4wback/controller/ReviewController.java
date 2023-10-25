package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.UserReview.CreateUserReview;
import com.b4w.b4wback.dto.UserReview.ReviewDTO;
import com.b4w.b4wback.model.UserReview;
import com.b4w.b4wback.service.interfaces.JwtService;
import com.b4w.b4wback.service.interfaces.ReviewUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewUserService reviewService;
    private final JwtService jwtService;

    @PostMapping("/owner/{auctionId}")
    public ResponseEntity<ReviewDTO> createReviewForWinner(@RequestHeader("Authorization") String auth,
                                                           @PathVariable long auctionId,
                                                           @RequestBody @Valid CreateUserReview createReviewDTO){
        final String jwt = auth.substring(7);
        Long userId = jwtService.extractId(jwt);

        UserReview review = reviewService.createUserReviewOwner(createReviewDTO, auctionId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReviewDTO(review));
    }
}
