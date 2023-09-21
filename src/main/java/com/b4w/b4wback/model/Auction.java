package com.b4w.b4wback.model;

import com.b4w.b4wback.dto.AuctionDTO;
import com.b4w.b4wback.dto.AuctionOwnerDTO;
import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.GetAuctionDTO;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.repository.BidRepository;
import com.b4w.b4wback.service.interfaces.UserService;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@NamedNativeQuery(
        name = "getAuctionDTOWithFilter",
        query = "SELECT DISTINCT auction.id AS id, auction.title AS title, auction.deadline AS deadline, auction.status AS status," +
                "COALESCE((SELECT MAX(bid.amount) FROM Bid bid WHERE bid.auction_id = auction.id), auction.base_price) AS highestBidAmount " +
                "FROM Auction auction WHERE " +
                "(:tagsIds IS NULL OR " +
                    "(SELECT COUNT(DISTINCT tag.id) FROM Tag tag WHERE tag.id IN :tagsIds) = (" +
                        "SELECT COUNT(DISTINCT auct_t.tag_id) FROM Auction_tag auct_t " +
                            "WHERE auct_t.auction_id = auction.id AND " +
                            "auct_t.tag_id IN :tagsIds) OR " +
                    "(SELECT COUNT(DISTINCT tag.id) FROM Tag tag WHERE tag.id IN :tagsIds) = 0) AND " +
                "(:milageMin IS NULL OR auction.milage >= :milageMin) AND " +
                "(:milageMax IS NULL OR auction.milage <= :milageMax) AND " +
                "(:modelYearMin IS NULL OR auction.model_year >= :modelYearMin) AND " +
                "(:modelYearMax IS NULL OR auction.model_year <= :modelYearMax) AND " +
                "(:brand IS NULL OR lower(auction.brand) = lower(:brand)) AND " +
                "(:color IS NULL OR lower(auction.color) = lower(:color)) AND " +
                "(:gasType IS NULL OR auction.gas_type = :gasType) AND " +
                "(:doorsAmount IS NULL OR auction.doors_amount = :doorsAmount) AND " +
                "(:gearShiftType IS NULL OR auction.gear_shift_type = :gearShiftType) AND " +
                "(:model IS NULL OR lower(auction.model) = lower(:model)) AND " +
                "(:priceMin IS NULL OR COALESCE((SELECT " +
                    "MAX(bid.amount) FROM Bid bid WHERE bid.auction_id = auction.id), auction.base_price " +
                ") >= :priceMin) AND " +
                "(:priceMax IS NULL OR COALESCE((SELECT " +
                    "MAX(bid.amount) FROM Bid bid WHERE bid.auction_id = auction.id), auction.base_price " +
                ") <= :priceMax) AND " +
                "(auction.status = 0)",
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

    @OneToMany(mappedBy = "auction",cascade = CascadeType.ALL)
    private List<Bid> bids;
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

        return GetAuctionDTO.builder().title(this.getTitle()).description(this.getDescription()).deadline(this.getDeadline()).basePrice(this.getBasePrice()).
                brand(this.getBrand()).model(this.getModel()).status(this.getStatus()).milage(this.getMilage()).gasType(this.getGasType())
                .modelYear(this.getModelYear()).color(this.getColor()).doorsAmount(this.getDoorsAmount()).gearShiftType(this.getGearShiftType())
                .auctionOwnerDTO(AuctionOwnerDTO.builder()
                        .name(this.getUser().getName())
                        .id(this.getUser().getId())
                        .lastName(this.getUser().getLastName())
                        .profilePicture(userService.createUrlForDownloadingImage(user.getId()))
                        .build())
                .tags(tags)
                .build();
    }

    public CreateAuctionDTO toDTO( CreateAuctionDTO auctionDTO){
        return new CreateAuctionDTO(this.getId(), auctionDTO);
    }

    public List<String> getTagNames(){
        List<String> tagNames = new ArrayList<>();
        for (Tag tag : tags) {
            tagNames.add(tag.getTagName());
        }
        return tagNames;
    }
}
