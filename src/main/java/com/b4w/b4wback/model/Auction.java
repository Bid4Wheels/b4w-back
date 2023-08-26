package com.b4w.b4wback.model;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

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

    public Auction (CreateAuctionDTO createAuctionDTO){
        this.description = createAuctionDTO.getDescription();
        this.deadline = createAuctionDTO.getDeadline();
        this.createdAt = LocalDateTime.now();
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
    }
}
