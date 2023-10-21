package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateReviewDTO;
import com.b4w.b4wback.service.interfaces.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/winner/{auctionId}")
    public ResponseEntity<?> createReviewForWinner(@RequestBody @Valid CreateReviewDTO createReviewDTO, @PathVariable long auctionId, @RequestHeader("Authorization") String token){
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReviewForWinner(createReviewDTO, auctionId, token));
    }
}
