package com.b4w.b4wback.model;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.repository.BidRepository;
import com.b4w.b4wback.service.interfaces.UserService;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@NamedNativeQuery(
        name = "getAuctionDTOWithFilter",
        query = "SELECT DISTINCT auction.id AS id, auction.title AS title, auction.deadline AS deadline, auction.status AS status," +
                "COALESCE((SELECT MAX(bid.amount) FROM Bid bid WHERE bid.auction_id = auction.id), auction.base_price) AS highestBidAmount " +
                "FROM Auction auction " +
                "LEFT JOIN auction_tag at ON auction.id = at.auction_id WHERE" +
                ":tagsIds IS NULL OR at.tag_id IN :tagsIds AND " +
                ":milageMin IS NULL OR auction.milage >= :milageMin AND " +
                ":milageMax IS NULL OR auction.milage <= :milageMax AND " +
                ":modelYearMin IS NULL OR auction.model_year >= :modelYearMin AND " +
                ":modelYearMax IS NULL OR auction.model_year <= :modelYearMax AND " +
                ":brand IS NULL OR lower(auction.brand) = lower(:brand) AND " +
                ":color IS NULL OR lower(auction.color) = lower(:color) AND " +
                ":gasType IS NULL OR auction.gas_type = :gasType AND " +
                ":doorsAmount IS NULL OR auction.doors_amount = :doorsAmount AND " +
                ":gearShiftType IS NULL OR auction.gear_shift_type = :gearShiftType AND " +
                ":model IS NULL OR lower(auction.model) = lower(:model) AND " +
                "(:priceMin IS NULL OR COALESCE((SELECT " +
                    "MAX(bid.amount) FROM Bid bid WHERE bid.auction_id = auction.id), auction.base_price " +
                ") >= :priceMin) AND" +
                "(:priceMax IS NULL OR COALESCE((SELECT " +
                    "MAX(bid.amount) FROM Bid bid WHERE bid.auction_id = auction.id), auction.base_price " +
                ") <= :priceMax) AND " +
                "auction.status = 0",
        resultSetMapping = "sqlConstructor"
)
@SqlResultSetMapping(
        name = "sqlConstructor",
        classes = @ConstructorResult(
                targetClass = AuctionDTO.class,
                columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "title", type = String.class),
                        @ColumnResult(name = "deadLine", type = LocalDateTime.class),
                        @ColumnResult(name = "status", type = AuctionStatus.class),
                        @ColumnResult(name = "highestBidAmount", type = Integer.class)
                }
        )
)
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

    private boolean alreadySentImageUrl;
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
        this.alreadySentImageUrl=false;
        this.tags = tags;
    }

    public AuctionStatus getStatus(){
        if (status == AuctionStatus.OPEN)
            if (LocalDateTime.now().isAfter(deadline))
                return AuctionStatus.FINISHED;

        return status;
    }
    public GetAuctionDTO getAuctionToDTO(BidRepository bidRepository, UserService userService){
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
                        .profilePicture(userService.createUrlForDownloadingImage(user.getId()))
                        .build())
                .auctionHigestBidDTO(auctionHigestBidDTO)
                .tags(tags)
                .build();
    }

    public CreateAuctionDTO toDTO( CreateAuctionDTO auctionDTO){
        return new CreateAuctionDTO(this.getId(), auctionDTO);
    }
}
