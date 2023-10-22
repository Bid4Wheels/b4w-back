package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateReviewDTO;
import com.b4w.b4wback.dto.ReviewDTO;
import com.b4w.b4wback.dto.UserDTO;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.UserReviewType;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.BidRepository;
import com.b4w.b4wback.service.interfaces.JwtService;
import com.b4w.b4wback.service.interfaces.ReviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service

public class ReviewServiceImpl implements ReviewService {
    private final AuctionRepository auctionRepository;
    private final JwtService jwtService;
    private final BidRepository bidRepository;
    public ReviewServiceImpl(AuctionRepository auctionRepository, JwtService jwtService, BidRepository bidRepository) {
        this.auctionRepository = auctionRepository;
        this.jwtService = jwtService;
        this.bidRepository = bidRepository;
    }
    @Override
    public ReviewDTO createReviewForWinner(CreateReviewDTO createReviewDTO, long auctionId,String token) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new RuntimeException("Auction not found"));
        if(auction.getStatus() == AuctionStatus.FINISHED && auction.getUser().getUsername().equals(jwtService.extractUsername(token.substring(7)))){
            Bid winnerBid = bidRepository.findTopByAuctionOrderByAmountDesc(auction);
            User winner = winnerBid.getBidder();
            User owner = auction.getUser();
            return new ReviewDTO(createReviewDTO.getReview(), createReviewDTO.getRating(), UserReviewType.OWNER, UserDTO.builder()
                    .id(owner.getId()).name(owner.getName()).lastName(owner.getLastName()).email(owner.getEmail()).build(), UserDTO.builder()
                    .id(winner.getId()).name(winner.getName()).lastName(winner.getLastName()).email(winner.getEmail()).build(), LocalDateTime.now());
        }
        throw new BadRequestParametersException("Auction is not finished or you are not the owner");
   }
}
