package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateReviewDTO;
import com.b4w.b4wback.dto.ReviewDTO;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.service.interfaces.JwtService;
import com.b4w.b4wback.service.interfaces.ReviewService;
import org.springframework.stereotype.Service;

@Service

public class ReviewServiceImpl implements ReviewService {
    private final AuctionRepository auctionRepository;
    private final JwtService jwtService;
    public ReviewServiceImpl(AuctionRepository auctionRepository, JwtService jwtService) {
        this.auctionRepository = auctionRepository;
        this.jwtService = jwtService;
    }
    @Override
    public ReviewDTO createReviewForWinner(CreateReviewDTO createReviewDTO, long auctionId,String token) {
//        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new RuntimeException("Auction not found"));
//        if(auction.getStatus() == AuctionStatus.FINISHED && auction.getUser().getUsername().equals(jwtService.extractUsername(token))){
//            auction.getBids()
//            return new ReviewDTO(createReviewDTO.getReview(),createReviewDTO.getRating(),auction.getUser());
//        }
//
   }

}
