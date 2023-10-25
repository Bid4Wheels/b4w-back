package com.b4w.b4wback.service.interfaces;

import com.b4w.b4wback.dto.UserReview.CreateUserReview;
import com.b4w.b4wback.model.UserReview;

public interface ReviewUserService {
    UserReview createUserReviewOwner(CreateUserReview userReviewDTO, long auctionId, long userId);
}
