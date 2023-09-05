package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.FilterAuctionDTO;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.AuctionService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


@Service
@Validated
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    public AuctionServiceImpl(AuctionRepository auctionRepository, UserRepository userRepository) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
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
    public Page<Auction> getAuctionsFiltered(FilterAuctionDTO filter, Pageable pageable) {
        return auctionRepository.findWithFilter(filter.getMilageMin(), filter.getMilageMax(),
                filter.getModelYearMin(), filter.getModelYearMax(),
                filter.getPriceMin(), filter.getPriceMax(),
                filter.getBrand(), filter.getColor(), filter.getGasType(), filter.getDoorsAmount(),
                filter.getGearShiftType(), filter.getModel(), pageable);
    }

}
