package com.b4w.b4wback.model;

import com.b4w.b4wback.dto.AuctionHigestBidDTO;
import com.b4w.b4wback.dto.AuctionOwnerDTO;
import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.GetAuctionDTO;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.repository.BidRepository;
import com.b4w.b4wback.service.interfaces.S3Service;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private Integer basePrice;
    @Column(nullable = false)
    private AuctionStatus status;
    @Column(nullable = false)
    private Integer milage;
    @Column(nullable = false)
    private GasType gasType;
    @Column(nullable = false)
    private Integer modelYear;
    @Column(nullable = false)
    private String color;
    @Column(nullable = false)
    private Integer doorsAmount;
    @Column(nullable = false)
    private GearShiftType gearShiftType;

    private int highestBidAmount;

    @ManyToOne()
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "auction_tag",
            joinColumns = @JoinColumn(name = "auction_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;


    public Auction (CreateAuctionDTO createAuctionDTO, List<Tag> tags){
        this.title = createAuctionDTO.getTitle();
        this.description = createAuctionDTO.getDescription();
        this.deadline = createAuctionDTO.getDeadline();
        this.createdAt = createAuctionDTO.getCreatedAt();
        this.brand = createAuctionDTO.getBrand();
        this.model = createAuctionDTO.getModel();
        this.basePrice = createAuctionDTO.getBasePrice();
        this.status = AuctionStatus.OPEN;
        this.milage = createAuctionDTO.getMilage();
        this.gasType = createAuctionDTO.getGasType();
        this.modelYear = createAuctionDTO.getModelYear();
        this.color = createAuctionDTO.getColor();
        this.doorsAmount = createAuctionDTO.getDoorsAmount();
        this.gearShiftType = createAuctionDTO.getGearShiftType();

        this.tags = tags;
    }

    public AuctionStatus getStatus(){
        if (status == AuctionStatus.OPEN)
            if (LocalDateTime.now().isAfter(deadline))
                return AuctionStatus.FINISHED;

        return status;
    }
    public GetAuctionDTO getAuctionToDTO(BidRepository bidRepository, S3Service s3Service){
        Bid topBid = bidRepository.findTopByAuctionOrderByAmountDesc(this);


        AuctionHigestBidDTO auctionHigestBidDTO = null;

        if(topBid != null)
            auctionHigestBidDTO = AuctionHigestBidDTO.builder()
                    .amount(topBid.getAmount())
                    .userId(topBid.getBidder().getId())
                    .userName(topBid.getBidder().getName())
                    .userLastName(topBid.getBidder().getLastName()).build();

        return GetAuctionDTO.builder().title(this.getTitle()).description(this.getDescription()).deadline(this.getDeadline()).basePrice(this.getBasePrice()).
                brand(this.getBrand()).model(this.getModel()).status(this.getStatus()).milage(this.getMilage()).gasType(this.getGasType())
                .modelYear(this.getModelYear()).color(this.getColor()).doorsAmount(this.getDoorsAmount()).gearShiftType(this.getGearShiftType())
                .auctionOwnerDTO(AuctionOwnerDTO.builder()
                        .name(this.getUser().getName())
                        .id(this.getUser().getId())
                        .lastName(this.getUser().getLastName())
                        .profilePicture(s3Service.getDownloadURL(this.getUser().getId()))
                        .build())
                .auctionHigestBidDTO(auctionHigestBidDTO)
                .tags(tags)
                .build();
    }
}
