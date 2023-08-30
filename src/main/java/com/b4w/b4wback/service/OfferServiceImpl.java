package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateOfferDTO;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Offer;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.OfferRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.OfferService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class OfferServiceImpl implements OfferService {
    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;

    public OfferServiceImpl(OfferRepository offerRepository, UserRepository userRepository,
                            AuctionRepository auctionRepository) {
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
    }

    @Override
    public void CrateOffer(CreateOfferDTO offerDTO) {
        User user = userRepository.getReferenceById(offerDTO.getUserId());
        Auction auction = auctionRepository.getReferenceById(offerDTO.getAuctionId());

        if (auction.getUser().getId() == user.getId())
            throw new BadRequestParametersException("User can't bid in own auctions");

        offerRepository.save(new Offer(offerDTO.getAmount(), user, auction));
    }
}
