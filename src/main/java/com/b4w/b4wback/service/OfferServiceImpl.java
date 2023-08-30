package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateOfferDTO;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Offer;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.OfferRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.OfferService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

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
        Optional<User> userOptional = userRepository.findById(offerDTO.getUserId());
        if (userOptional.isEmpty()) throw new EntityNotFoundException("The user could not be found");
        Optional<Auction> auctionOptional = auctionRepository.findById(offerDTO.getAuctionId());
        if (auctionOptional.isEmpty()) throw new EntityNotFoundException("The auction could not be found");

        User user = userOptional.get();
        Auction auction = auctionOptional.get();

        if (auction.getStatus() != AuctionStatus.OPEN)
            throw new BadRequestParametersException("The auction is not open");

        if (auction.getUser().getId() == user.getId())
            throw new BadRequestParametersException("User can't bid in own auctions");

        List<Offer> offers = offerRepository.getOfferByAuction(auction);
        offers.forEach(o->{
            if (o.getAmount() > offerDTO.getAmount())
                throw new BadRequestParametersException("There is an offer with a higher amount");
        });


        offerRepository.save(new Offer(offerDTO.getAmount(), user, auction));
    }
}
