package com.b4w.b4wback.service;

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
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;

    public BidServiceImpl(BidRepository bidRepository, UserRepository userRepository,
                          AuctionRepository auctionRepository) {
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
    }

    @Override
    public Bid crateBid(CreateBidDTO bidDTO) {
        Optional<User> userOptional = userRepository.findById(bidDTO.getUserId());
        if (userOptional.isEmpty()) throw new EntityNotFoundException("The user could not be found");
        Optional<Auction> auctionOptional = auctionRepository.findById(bidDTO.getAuctionId());
        if (auctionOptional.isEmpty()) throw new EntityNotFoundException("The auction could not be found");

        User user = userOptional.get();
        Auction auction = auctionOptional.get();

        if (auction.getStatus() != AuctionStatus.OPEN)
            throw new BadRequestParametersException("The auction is not open");

        if (auction.getUser().getId() == user.getId())
            throw new BadRequestParametersException("User can't bid in own auctions");

        List<Bid> bids = bidRepository.getBidByAuction(auction);
        bids.forEach(o->{
            if (o.getAmount() >= bidDTO.getAmount())
                throw new BadRequestParametersException("There is an bid with a higher amount");
        });


        return bidRepository.save(new Bid(bidDTO.getAmount(), user, auction));
    }
}
