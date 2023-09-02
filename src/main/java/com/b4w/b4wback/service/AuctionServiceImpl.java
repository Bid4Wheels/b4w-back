package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.AuctionHigestBidDTO;
import com.b4w.b4wback.dto.AuctionOwnerDTO;
import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.GetAuctionDTO;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.BidRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.AuctionService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


@Service
@Validated
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    public AuctionServiceImpl(AuctionRepository auctionRepository, UserRepository userRepository, BidRepository bidRepository) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
    }

    @Override
    public CreateAuctionDTO createAuction(CreateAuctionDTO createAuctionDTO)  {
        User user = userRepository.findById(createAuctionDTO.getUserId()).orElseThrow(()->new BadRequestParametersException("User with id "+createAuctionDTO.getUserId()+" not found"));
        Auction auction=new Auction(createAuctionDTO);
        auction.setUser(user);
        auctionRepository.save(auction);
        return createAuctionDTO;
    }

    @Override
    public GetAuctionDTO getAuctionById(long id) {
        Auction auction = auctionRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Auction with id "+id+" not found"));
        return GetAuctionDTO.builder().title(auction.getTitle()).description(auction.getDescription()).deadline(auction.getDeadline()).basePrice(auction.getBasePrice()).
                brand(auction.getBrand()).model(auction.getModel()).status(auction.getStatus()).milage(auction.getMilage()).gasType(auction.getGasType())
                .modelYear(auction.getModelYear()).color(auction.getColor()).doorsAmount(auction.getDoorsAmount()).gearShiftType(auction.getGearShiftType())
                .auctionOwnerDTO(AuctionOwnerDTO.builder()
                        .name(auction.getUser().getName())
                        .id(auction.getUser().getId())
                        .lastName(auction.getUser().getLastName()).build())
                .auctionHigestBidDTO(AuctionHigestBidDTO.builder()
                        .amount(bidRepository.findTopByAuctionOrderByAmountDesc(auction).getAmount())
                        .userId(bidRepository.findTopByAuctionOrderByAmountDesc(auction).getBidder().getId())
                        .userName(bidRepository.findTopByAuctionOrderByAmountDesc(auction).getBidder().getName())
                        .userLastName(bidRepository.findTopByAuctionOrderByAmountDesc(auction).getBidder().getLastName()).build())
                .build();
    }
}
