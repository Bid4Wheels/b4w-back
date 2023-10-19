package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.UserReview.CreateUserReview;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.UserReviewType;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.model.UserReview;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.BidRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.repository.UserReviewRepository;
import com.b4w.b4wback.service.interfaces.ReviewUserService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Validated
public class ReviewUserServiceImp implements ReviewUserService {
    private final UserReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    public ReviewUserServiceImp(UserReviewRepository reviewRepository,
                                UserRepository userRepository,
                                AuctionRepository auctionRepository, BidRepository bidRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
    }

    @Override
    //TODO: make an accurate exception fot this
    public UserReview createUserReview(CreateUserReview userReviewDTO, long auctionId) {
        User reviewer = getEntity(userRepository, userReviewDTO.getReviewer(), "The reviewer id was not found");
        User reviewed = getEntity(userRepository, userReviewDTO.getReviewed(), "The reviewed id was not found");

        if (reviewer.getId() == reviewed.getId())
            throw new BadCredentialsException("Reviewer can't be the same as the reviewed");

        Auction auction = getEntity(auctionRepository, auctionId, "The auction with the given id was not found");
        if (auction.getStatus() != AuctionStatus.FINISHED)
            throw new BadCredentialsException("Auction not closed");

        UserReviewType reviewType = getReviewType(auction, reviewer, reviewed);

        Optional<Bid> winnerBidOp  = Optional.ofNullable(bidRepository.findTopByAuctionOrderByAmountDesc(auction));
        if (winnerBidOp.isEmpty()) throw new EntityNotFoundException("No winner bid found");
        Bid winnerBid = winnerBidOp.get();

        if (winnerBid.getBidder().getId() != reviewer.getId() && winnerBid.getBidder().getId() != reviewed.getId())
            throw new BadCredentialsException("Reviewer nor reviewed are the winner of the auction");

        return reviewRepository.save(UserReview.builder()
                .reviewer(reviewer)
                .reviewed(reviewed)
                .type(reviewType)
                .punctuation(userReviewDTO.getPunctuation())
                .review(userReviewDTO.getReview())
                .date(LocalDateTime.now()).build()
        );
    }

    private UserReviewType getReviewType(Auction auction, User reviewer, User reviewed){
        if (auction.getUser().getId() == reviewer.getId()) return UserReviewType.OWNER;
        else if (auction.getUser().getId() == reviewed.getId()) return UserReviewType.WINNER;
        else throw new BadCredentialsException("Reviewer nor reviewed are the owner of the auction");
    }
    private static <T> T getEntity(JpaRepository<T, Long> repository, long id, String errorMsg){
        Optional<T> entityOp = repository.findById(id);
        if (entityOp.isEmpty()) throw new EntityNotFoundException(errorMsg);
        return entityOp.get();
    }
}
