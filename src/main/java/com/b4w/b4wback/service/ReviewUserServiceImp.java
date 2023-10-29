package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateReviewDTO;
import com.b4w.b4wback.dto.ReviewDTO;
import com.b4w.b4wback.dto.UserDTO;
import com.b4w.b4wback.dto.UserReview.CreateUserReview;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.UserReviewType;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.model.*;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.BidRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.repository.UserReviewRepository;
import com.b4w.b4wback.service.interfaces.JwtService;
import com.b4w.b4wback.service.interfaces.ReviewUserService;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Validated
@AllArgsConstructor
public class ReviewUserServiceImp implements ReviewUserService {
    private final UserReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final JwtService jwtService;


    @Override
    public ReviewDTO createReviewForWinner(CreateReviewDTO createReviewDTO, long auctionId, String token) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new RuntimeException("Auction not found"));
        if(auction.getStatus() == AuctionStatus.FINISHED && auction.getUser().getUsername().equals(jwtService.extractUsername(token.substring(7)))){
            Bid winnerBid = bidRepository.findTopByAuctionOrderByAmountDesc(auction);
            User winner = winnerBid.getBidder();
            User owner = auction.getUser();


            UserReview savedReview = reviewRepository.save(UserReview.builder()
                    .reviewer(owner)
                    .reviewed(winner)
                    .review(createReviewDTO.getReview())
                    .punctuation(createReviewDTO.getRating())
                    .date(LocalDateTime.now())
                    .type(UserReviewType.WINNER)
                    .build());
            //Review savedreview = reviewRepository.save(new Review(createReviewDTO.getReview(), createReviewDTO.getRating(), UserReviewType.OWNER, owner, winner, LocalDateTime.now()));
            return new ReviewDTO(savedReview.getReview(), savedReview.getPunctuation(), savedReview.getType(),
                    new UserDTO(savedReview.getReviewer()),
                    new UserDTO(savedReview.getReviewed()),
                    savedReview.getDate());
        }
        throw new BadRequestParametersException("Auction is not finished or you are not the owner");
    }

    @Override
    //TODO: make an accurate exception fot this
    public UserReview createUserReviewOwner(CreateUserReview userReviewDTO, long auctionId, long reviewerId) {
        User reviewer = getEntity(userRepository, reviewerId, "The reviewer id was not found");

        Auction auction = getEntity(auctionRepository, auctionId, "The auction with the given id was not found");
        if (auction.getStatus() != AuctionStatus.FINISHED)
            throw new BadCredentialsException("Auction not closed");

        User reviewed = auction.getUser();

        if (reviewer.getId() == reviewed.getId())
            throw new BadCredentialsException("Reviewer can't be the same as the reviewed");

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
        if (auction.getUser().getId() == reviewer.getId()) return UserReviewType.WINNER;
        else if (auction.getUser().getId() == reviewed.getId()) return UserReviewType.OWNER;
        else throw new BadCredentialsException("Reviewer nor reviewed are the owner of the auction");
    }
    private static <T> T getEntity(JpaRepository<T, Long> repository, long id, String errorMsg){
        Optional<T> entityOp = repository.findById(id);
        if (entityOp.isEmpty()) throw new EntityNotFoundException(errorMsg);
        return entityOp.get();
    }
}
