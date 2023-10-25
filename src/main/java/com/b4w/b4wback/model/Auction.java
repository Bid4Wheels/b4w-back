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

@Table(name = "auction")
@Entity
@NamedNativeQuery(
        name = "getAuctionDTOWithFilter",
        query = "SELECT DISTINCT" +
                " a.id AS id," +
                " a.title AS title," +
                " a.deadline AS deadline," +
                " a.created_at AS created_at," +
                " a.status AS status," +
                " COALESCE(( SELECT MAX(b.amount) FROM bid b WHERE b.auction_id = a.id), a.base_price) AS highestBidAmount" +
                " FROM" +
                " auction a" +
                " LEFT JOIN" +
                " auction_tag at ON a.id = at.auction_id" +
                " LEFT JOIN" +
                " tag t ON at.tag_id = t.id" +
                " WHERE" +
                " (:tagsIds IS NULL OR" +
                " (SELECT COUNT( DISTINCT t.id) FROM tag t WHERE t.id IN :tagsIds) = " +
                " (SELECT COUNT( DISTINCT at.tag_id) FROM auction_tag at WHERE at.auction_id = a.id AND at.tag_id IN :tagsIds) OR " +
                " (SELECT COUNT( DISTINCT t.id) FROM tag t WHERE t.id IN :tagsIds) = 0) " +
                " AND (:milageMin IS NULL OR a.milage >= :milageMin) " +
                " AND (:milageMax IS NULL OR a.milage <= :milageMax) " +
                " AND (:modelYearMin IS NULL OR a.model_year >= :modelYearMin) " +
                " AND (:modelYearMax IS NULL OR a.model_year <= :modelYearMax) " +
                " AND (:brand IS NULL OR LOWER(a.brand) = LOWER(:brand)) " +
                " AND (:color IS NULL OR LOWER(a.color) = LOWER(:color)) " +
                " AND (:gasType IS NULL OR a.gas_type = :gasType) " +
                " AND (:doorsAmount IS NULL OR a.doors_amount = :doorsAmount) " +
                " AND (:gearShiftType IS NULL OR a.gear_shift_type = :gearShiftType) " +
                " AND (:model IS NULL OR LOWER(a.model) = LOWER(:model)) " +
                " AND (:priceMin IS NULL OR COALESCE (( SELECT MAX(b.amount) FROM bid b WHERE b.auction_id = a.id), a.base_price) >= :priceMin) " +
                " AND (:priceMax IS NULL OR COALESCE (( SELECT MAX(b.amount) FROM bid b WHERE b.auction_id = a.id), a.base_price) <= :priceMax) " +
                " AND a.status = 0",
        resultSetMapping = "sqlConstructor"
)
@SqlResultSetMapping(
        name = "sqlConstructor",
        classes = @ConstructorResult(
                targetClass = AuctionDTO.class,
                columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "title", type = String.class),
                        @ColumnResult(name = "created_at", type = LocalDateTime.class),
                        @ColumnResult(name = "deadline", type = LocalDateTime.class),
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
    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL)
    private List<Question> questions;
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

        return GetAuctionDTO.builder().title(this.getTitle()).description(this.getDescription()).createdAt(this.getCreatedAt()).deadline(this.getDeadline()).basePrice(this.getBasePrice()).
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
