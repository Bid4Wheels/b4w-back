package com.b4w.b4wback.service.interfaces;

import com.b4w.b4wback.dto.CreateReviewDTO;
import com.b4w.b4wback.dto.ReviewDTO;

public interface ReviewService {
    ReviewDTO createReviewForWinner(CreateReviewDTO createReviewDTO, long auctionId, String token);
}
