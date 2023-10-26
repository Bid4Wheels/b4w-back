package com.b4w.b4wback.service.interfaces;

import com.b4w.b4wback.dto.CreateReviewDTO;
import com.b4w.b4wback.dto.ReviewDTO;
import com.b4w.b4wback.dto.UserReview.CreateUserReview;
import com.b4w.b4wback.model.UserReview;

public interface ReviewUserService {
    ReviewDTO createReviewForWinner(CreateReviewDTO createReviewDTO, long auctionId, String token);

    UserReview createUserReviewOwner(CreateUserReview userReviewDTO, long auctionId, long userId);
}
