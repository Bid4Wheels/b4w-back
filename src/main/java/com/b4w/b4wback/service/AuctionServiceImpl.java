package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.AuctionDTO;
import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.GetAuctionDTO;
import com.b4w.b4wback.dto.FilterAuctionDTO;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.exception.UrlAlreadySentException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.BidRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.AuctionService;
import com.b4w.b4wback.service.interfaces.S3Service;
import com.b4w.b4wback.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;


@Service
@Validated
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    private final UserService userService;

    private final S3Service s3Service;

    @Value("${aws.auction.objectKey}")
    private String auctionObjectKey;

    @Value("${expiration.time.image.url}")
    private Integer expirationTimeImageUrl;
    public AuctionServiceImpl(AuctionRepository auctionRepository, UserRepository userRepository, BidRepository bidRepository,UserService userService,S3Service s3Service) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
        this.userService=userService;
        this.s3Service=s3Service;
    }

    @Override
    public CreateAuctionDTO createAuction(CreateAuctionDTO createAuctionDTO)  {
        User user = userRepository.findById(createAuctionDTO.getUserId()).orElseThrow(()->new BadRequestParametersException("User with id "+createAuctionDTO.getUserId()+" not found"));
        Auction auction=new Auction(createAuctionDTO);
        auction.setUser(user);
        return auctionRepository.save(auction).toDTO(createAuctionDTO);
    }

    @Override
    public GetAuctionDTO getAuctionById(long id) {
        Auction auction = auctionRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Auction with id "+id+" not found"));
        return new GetAuctionDTO(auction.getAuctionToDTO(bidRepository,userService),createUrlsForDownloadingImages(id));
    }
    @Override
    public Page<AuctionDTO> getAuctionsByUserId(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Page<AuctionDTO> auctions= auctionRepository.findByUser(user, pageable);
        List<AuctionDTO> auctionDTOS = auctions.getContent();
        for (AuctionDTO auctionDTO : auctionDTOS) {
            Bid topBid = bidRepository.findTopByAuctionOrderByAmountDesc(auctionRepository.findById(auctionDTO.getId()).orElseThrow(()->new EntityNotFoundException("Auction not found")));
            if (topBid == null){
                auctionDTO.setHighestBidAmount(auctionRepository.findById(auctionDTO.getId()).orElseThrow(()->new EntityNotFoundException("Auction not found")).getBasePrice());
                continue;
            }
            auctionDTO.setHighestBidAmount(topBid.getAmount());
        }
        return  auctions;
    }
    @Override
    public Page<AuctionDTO> getAuctionsFiltered(FilterAuctionDTO filter, Pageable pageable) {
        return auctionRepository.findWithFilter(filter.getMilageMin(), filter.getMilageMax(),
                filter.getModelYearMin(), filter.getModelYearMax(),
                filter.getPriceMin(), filter.getPriceMax(),
                filter.getBrand(), filter.getColor(), filter.getGasType(), filter.getDoorsAmount(),
                filter.getGearShiftType(), filter.getModel(), pageable);
    }

    @Override
    public List<String> createUrlsForUploadingImages(long auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(()->new EntityNotFoundException("Auction with id "+auctionId+" not found"));
        if (!auction.isAlreadySentImageUrl()){
            List<String> auctionImageUrls=new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                String url= auctionObjectKey+auctionId+"/img"+i;
                auctionImageUrls.add(s3Service.generatePresignedUploadImageUrl(url,expirationTimeImageUrl));
            }
            auction.setAlreadySentImageUrl(true);
            auctionRepository.save(auction);
            return auctionImageUrls;
        }
        throw new UrlAlreadySentException("Auction Urls already sent");
    }

    @Override
    public List<String> createUrlsForDownloadingImages(long auctionId) {
        List<String> auctionImageUrls=new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            String url= auctionObjectKey+auctionId+"/img"+i;
            auctionImageUrls.add(s3Service.generatePresignedDownloadImageUrl(url,expirationTimeImageUrl));
        }
        return auctionImageUrls;
    }
}
