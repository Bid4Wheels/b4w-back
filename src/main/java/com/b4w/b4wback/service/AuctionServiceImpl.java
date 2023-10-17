package com.b4w.b4wback.service;


import com.b4w.b4wback.dto.AuctionDTO;
import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.GetAuctionDTO;
import com.b4w.b4wback.dto.FilterAuctionDTO;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.exception.AuctionExpiredException;
import com.b4w.b4wback.dto.*;

import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.exception.UrlAlreadySentException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.BidRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.*;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Validated
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    private final MailService mailService;
    private final UserService userService;

    private final S3Service s3Service;

    @Value("${aws.auction.objectKey}")
    private String auctionObjectKey;

    @Value("${expiration.time.image.url}")
    private Integer expirationTimeImageUrl;

    private final TagService tagService;

    private final JwtService jwtService;


    public AuctionServiceImpl(AuctionRepository auctionRepository, UserRepository userRepository, BidRepository bidRepository,
                              MailService mailService, UserService userService, S3Service s3Service, TagService tagService, JwtService jwtService) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
        this.mailService = mailService;
        this.userService=userService;
        this.s3Service=s3Service;
        this.tagService=tagService;
        this.jwtService=jwtService;
    }

    @Override
    public CreateAuctionDTO createAuction(CreateAuctionDTO createAuctionDTO)  {
        User user = userRepository.findById(createAuctionDTO.getUserId()).orElseThrow(()->new BadRequestParametersException("User with id "+createAuctionDTO.getUserId()+" not found"));
        Auction auction= new Auction(createAuctionDTO, tagService.getOrCreateTagsFromStringList(createAuctionDTO.getTags()));
        auction.setUser(user);
        return auctionRepository.save(auction).toDTO(createAuctionDTO);
    }

    @Override
    public GetAuctionDTO getAuctionById(long id) {
        Auction auction = auctionRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Auction with id "+id+" not found"));
        List<Bid> bids = bidRepository.getBidByAuction(auction);
        List<AuctionHigestBidDTO> top5 = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            if(bids.size()<=i)break;
            top5.add(AuctionHigestBidDTO.builder()
                    .amount(bids.get(bids.size() -1 -i).getAmount())
                    .userId(bids.get(bids.size()-1 -i).getBidder().getId())
                    .userName(bids.get(bids.size()-1 -i).getBidder().getUsername())
                    .userLastName(bids.get(bids.size()-1 -i).getBidder().getLastName())
                    .build());
            }
        return new GetAuctionDTO(auction.getAuctionToDTO(bidRepository,userService),createUrlsForDownloadingImages(id),top5);
    }
    @Override
    public Page<AuctionDTO> getAuctionsByUserId(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with " +userId+" found"));
        Page<Auction> auctions= auctionRepository.findByUser(user, pageable);
        List<AuctionDTO> auctionDTOS = new ArrayList<>();
        for (Auction auction : auctions){
            auctionDTOS.add((new AuctionDTO(auction)));
        }
        long totalElements= auctions.getTotalElements();
        List<AuctionDTO> auctionWithImages=new ArrayList<>();
        for (AuctionDTO auctionDTO : auctionDTOS) {
            String url=auctionObjectKey+auctionDTO.getId()+"/img0";
            auctionDTO.setFirstImageUrl(s3Service.generatePresignedDownloadImageUrl(url,expirationTimeImageUrl));
            auctionWithImages.add(auctionDTO);
            Bid topBid = bidRepository.findTopByAuctionOrderByAmountDesc(auctionRepository.findById(auctionDTO.getId()).
                    orElseThrow(()->new EntityNotFoundException("Auction with "+auctionDTO.getId()+" not found")));
            if (topBid == null){
                auctionDTO.setHighestBidAmount(auctionRepository.findById(auctionDTO.getId()).orElseThrow(()->new EntityNotFoundException("Auction with "+auctionDTO.getId()+" not found")).getBasePrice());
                continue;
            }
            auctionDTO.setHighestBidAmount(topBid.getAmount());
            auctionDTO.setCreatedAt(auctionDTO.getCreatedAt());
        }

        return new PageImpl<>(auctionWithImages,pageable,totalElements);
    }
    @Override
    public Page<AuctionDTO> getAuctionsFiltered(FilterAuctionDTO filter, Pageable pageable) {
        Page<AuctionDTO> auctionDTOPage=auctionRepository.findWithFilter(filter.getMilageMin(), filter.getMilageMax(),
                filter.getModelYearMin(), filter.getModelYearMax(),
                filter.getPriceMin(), filter.getPriceMax(),
                filter.getBrand(), filter.getColor(),
                filter.getGasType() != null? filter.getGasType().ordinal() : null,
                filter.getDoorsAmount(),
                filter.getGearShiftType() != null? filter.getGearShiftType().ordinal() : null,
                filter.getModel(),
                filter.getTags() != null? filter.getTags() : new ArrayList<>(),
                pageable);

        List<AuctionDTO> auctions=auctionDTOPage.getContent();
        long totalElements=auctionDTOPage.getTotalElements();
        List<AuctionDTO> auctionsWithImage=new ArrayList<>();

        for (AuctionDTO auction: auctions) {
            String url=auctionObjectKey+auction.getId()+"/img0";
            auction.setFirstImageUrl(s3Service.generatePresignedDownloadImageUrl(url,expirationTimeImageUrl));
            auctionsWithImage.add(auction);
            auction.setTagNames(auctionRepository.findTagsOfAuctionID(auction.getId()));
            auction.setCreatedAt(auction.getCreatedAt());
        }
        return new PageImpl<>(auctionsWithImage,pageable,totalElements);
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
    @Override
    public Page<AuctionDTO> getAuctionsEnding(Pageable pageable) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Page<AuctionDTO> auctions = auctionRepository.findUpcomingAuctions(currentDateTime, pageable);
        List<AuctionDTO> auctionDTOS = new ArrayList<>();
        for (AuctionDTO auction : auctions){
            Auction reusableAuction = auctionRepository.findById(auction.getId()).orElseThrow(() -> new EntityNotFoundException("Auction with "+auction.getId()+" not found"));
            auctionDTOS.add((new AuctionDTO(reusableAuction)));
        }
        long totalElements= auctions.getTotalElements();
        List<AuctionDTO> auctionWithImages=new ArrayList<>();
        for (AuctionDTO auctionDTO : auctionDTOS) {
            String url=auctionObjectKey+auctionDTO.getId()+"/img0";
            auctionDTO.setFirstImageUrl(s3Service.generatePresignedDownloadImageUrl(url,expirationTimeImageUrl));
            auctionWithImages.add(auctionDTO);
            Bid topBid = bidRepository.findTopByAuctionOrderByAmountDesc(auctionRepository.findById(auctionDTO.getId()).orElseThrow(()->new EntityNotFoundException("Auction with "+auctionDTO.getId()+" not found")));
            if (topBid == null){
                auctionDTO.setHighestBidAmount(auctionRepository.findById(auctionDTO.getId()).orElseThrow(()->new EntityNotFoundException("Auction with "+auctionDTO.getId()+" not found")).getBasePrice());
                continue;
            }
            auctionDTO.setHighestBidAmount(topBid.getAmount());
            auctionDTO.setCreatedAt(auctionDTO.getCreatedAt());
        }
        return new PageImpl<>(auctionDTOS,pageable,totalElements);
    }

    @Override
    public Page<AuctionDTO> getAuctionsNew(Pageable pageable) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Page<AuctionDTO> auctions = auctionRepository.findNewAuctions(currentDateTime, pageable);
        List<AuctionDTO> auctionDTOS = new ArrayList<>();
        for (AuctionDTO auction : auctions){
            Auction reusableAuction = auctionRepository.findById(auction.getId()).orElseThrow(() -> new EntityNotFoundException("Auction with "+auction.getId()+" not found")) ;
            auctionDTOS.add((new AuctionDTO(reusableAuction)));
        }
        long totalElements= auctions.getTotalElements();
        List<AuctionDTO> auctionWithImages=new ArrayList<>();
        for (AuctionDTO auctionDTO : auctionDTOS) {
            String url=auctionObjectKey+auctionDTO.getId()+"/img0";
            auctionDTO.setFirstImageUrl(s3Service.generatePresignedDownloadImageUrl(url,expirationTimeImageUrl));
            auctionWithImages.add(auctionDTO);
            Bid topBid = bidRepository.findTopByAuctionOrderByAmountDesc(auctionRepository.findById(auctionDTO.getId()).orElseThrow(()->new EntityNotFoundException("Auction with "+auctionDTO.getId()+" not found")));
            if (topBid == null){
                auctionDTO.setHighestBidAmount(auctionRepository.findById(auctionDTO.getId()).orElseThrow(()->new EntityNotFoundException("Auction with "+auctionDTO.getId()+" not found")).getBasePrice());
                continue;
            }
            auctionDTO.setHighestBidAmount(topBid.getAmount());
            auctionDTO.setCreatedAt(auctionDTO.getCreatedAt());
        }
        return new PageImpl<>(auctionDTOS,pageable,totalElements);
    }

    @Override
    public void deleteAuction(Long auctionID,String token) {
        String email = jwtService.extractUsername(token.substring(7));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));
        Auction userAuctionFound = auctionRepository.findAuctionByIdAndUserId(auctionID, user.getId());
        if (userAuctionFound != null) {
            LocalDateTime momentToDelete = LocalDateTime.now();
            if (!momentToDelete.isAfter(userAuctionFound.getDeadline())) {
                auctionRepository.delete(userAuctionFound);
            } else {
                throw new AuctionExpiredException("Auction expired in " + userAuctionFound.getDeadline());
            }
        } else {
            throw new EntityNotFoundException("Auction with id " + auctionID + " not found");
        }
    }

    @Override
    public Page<AuctionDTO> getAuctionsBiddedByUser ( long bidderId, Pageable pageable){
        User user = userRepository.findById(bidderId).orElseThrow(() -> new EntityNotFoundException("User with " + bidderId + " found"));
        Page<Auction> auctions = auctionRepository.findAuctionsByBidderIdOrderByDeadline(bidderId, pageable);
        List<AuctionDTO> auctionDTOS = new ArrayList<>();
        for (Auction auction : auctions)
            auctionDTOS.add((new AuctionDTO(auction)));

        long totalElements = auctions.getTotalElements();
        List<AuctionDTO> auctionWithImages = new ArrayList<>();
        for (AuctionDTO auctionDTO : auctionDTOS) {
            String url = auctionObjectKey + auctionDTO.getId() + "/img0";
            auctionDTO.setFirstImageUrl(s3Service.generatePresignedDownloadImageUrl(url, expirationTimeImageUrl));
            auctionWithImages.add(auctionDTO);
            Bid topBid = bidRepository.findTopByAuctionOrderByAmountDesc(auctionRepository.findById(auctionDTO.getId()).orElseThrow(() -> new BadRequestParametersException("Auction with " + auctionDTO.getId() + " not found")));
            if (topBid == null) {
                auctionDTO.setHighestBidAmount(auctionRepository.findById(auctionDTO.getId()).orElseThrow(() -> new EntityNotFoundException("Auction with " + auctionDTO.getId() + " not found")).getBasePrice());
                continue;
            }
            auctionDTO.setHighestBidAmount(topBid.getAmount());
            auctionDTO.setCreatedAt(auctionDTO.getCreatedAt());
        }
        return new PageImpl<>(auctionWithImages, pageable, totalElements);
    }

    @Transactional
    public void updateAuctionStatus(){
        List<Auction> auctions = auctionRepository.findAuctionByStatusAndDeadlineLessThan(AuctionStatus.OPEN, LocalDateTime.now());

        for (Auction auction : auctions) {
            if (auction.getBids().isEmpty()) auction.setStatus(AuctionStatus.FINISHED);
            else auction.setStatus(AuctionStatus.AWATINGDELIVERY);
            Bid winnerBid = bidRepository.findTopByAuctionOrderByAmountDesc(auction);
            mailService.endOfAuctionMails(auction, winnerBid, getLosersMails(auction, winnerBid));
        }

        auctionRepository.saveAll(auctions);
    }

    public String[] getLosersMails(Auction auction, Bid winner){
        List<Bid> bids = auction.getBids();
        if (bids.isEmpty() || bids.size() == 1) return new String[0];

        List<String> losers = new ArrayList<>();
        for (Bid bid : auction.getBids()) {
            if (bid.getBidder().getId() != winner.getBidder().getId() && !losers.contains(bid.getBidder().getEmail()))
                losers.add(bid.getBidder().getEmail());
        }
        return losers.toArray(new String[0]);
    }

}
