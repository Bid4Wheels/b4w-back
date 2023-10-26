package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateReviewDTO;
import com.b4w.b4wback.dto.ReviewDTO;
import com.b4w.b4wback.dto.UserDTO;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.UserReviewType;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.model.Review;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.BidRepository;
import com.b4w.b4wback.repository.ReviewRepository;
import com.b4w.b4wback.service.interfaces.JwtService;
import com.b4w.b4wback.service.interfaces.ReviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service

public class ReviewServiceImpl implements ReviewService {
    private final AuctionRepository auctionRepository;
    private final JwtService jwtService;
    private final BidRepository bidRepository;
    private final ReviewRepository reviewRepository;
    public ReviewServiceImpl(AuctionRepository auctionRepository, JwtService jwtService, BidRepository bidRepository, ReviewRepository reviewRepository) {
        this.auctionRepository = auctionRepository;
        this.jwtService = jwtService;
        this.bidRepository = bidRepository;
        this.reviewRepository = reviewRepository;
    }
    @Override
    public ReviewDTO createReviewForWinner(CreateReviewDTO createReviewDTO, long auctionId,String token) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new RuntimeException("Auction not found"));
        if(auction.getStatus() == AuctionStatus.FINISHED && auction.getUser().getUsername().equals(jwtService.extractUsername(token.substring(7)))){
            Bid winnerBid = bidRepository.findTopByAuctionOrderByAmountDesc(auction);
            User winner = winnerBid.getBidder();
            User owner = auction.getUser();
            Review savedreview = reviewRepository.save(new Review(createReviewDTO.getReview(), createReviewDTO.getRating(), UserReviewType.OWNER, owner, winner, LocalDateTime.now()));
            return new ReviewDTO(savedreview.getReview(), savedreview.getRating(), savedreview.getType(),
                    UserDTO.builder().id(savedreview.getOwner().getId()).name(savedreview.getOwner().getName()).lastName(savedreview.getOwner().getLastName()).email(savedreview.getOwner().getEmail()).build(),
                    UserDTO.builder().id(savedreview.getWinner().getId()).name(savedreview.getWinner().getName()).lastName(savedreview.getWinner().getLastName()).email(savedreview.getWinner().getEmail()).build(),
                    savedreview.getCreatedAt());
        }
        throw new BadRequestParametersException("Auction is not finished or you are not the owner");
   }
}
