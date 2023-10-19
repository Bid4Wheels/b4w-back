package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.BidNotificationDTO;
import com.b4w.b4wback.dto.CreateBidDTO;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.BidRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.BidService;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public BidServiceImpl(BidRepository bidRepository, UserRepository userRepository,
                          AuctionRepository auctionRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public Bid crateBid(CreateBidDTO bidDTO) {
        Optional<User> userOptional = userRepository.findById(bidDTO.getUserId());
        if (userOptional.isEmpty()) throw new EntityNotFoundException("User with Id "+ bidDTO.getUserId() +" not found");
        Optional<Auction> auctionOptional = auctionRepository.findById(bidDTO.getAuctionId());
        if (auctionOptional.isEmpty()) throw new EntityNotFoundException("Auction with Id "+ bidDTO.getAuctionId() +" not found");

        User user = userOptional.get();
        Auction auction = auctionOptional.get();

        if (auction.getStatus() != AuctionStatus.OPEN)
            throw new BadRequestParametersException("Auction with "+ bidDTO.getAuctionId()+" is already closed");

        if (auction.getUser().getId() == user.getId())
            throw new BadRequestParametersException("The author of the auction can't be the same of the bid");

        List<Bid> bids = bidRepository.getBidByAuction(auction);
        if (bids.size() > 0) {
            bids.forEach(o -> {
                if (o.getAmount() >= bidDTO.getAmount())
                    throw new BadRequestParametersException("There is an bid with a higher amount");
            });
        }else{
            if (auction.getBasePrice() > bidDTO.getAmount())
                throw new BadRequestParametersException("Base bid amount not reached");
        }
        simpMessagingTemplate.convertAndSend("/auction/" + auction.getId(),
                new BidNotificationDTO(bidDTO.getAmount(),user.getName(),user.getLastName()),new MessageHeaders(Collections.singletonMap(
                        MessageHeaders.CONTENT_TYPE,"application/json")));

        return bidRepository.save(new Bid(bidDTO.getAmount(), user, auction));
    }

    @Override
    public Bid getHighestBidByUserInAuction(Long userId, Long auctionId) {
        return bidRepository.findTopByBidderIdAndAuctionIdOrderByAmountDesc(userId, auctionId);
    }
}
