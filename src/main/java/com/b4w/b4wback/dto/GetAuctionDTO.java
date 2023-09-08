package com.b4w.b4wback.dto;

import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import lombok.*;

import java.time.LocalDateTime;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetAuctionDTO {
    private String title;
    private String description;
    private LocalDateTime deadline;
    private int basePrice;
    private String brand;
    private String model;
    private AuctionStatus status;
    private int milage;
    private GasType gasType;
    private int modelYear;
    private String color;
    private int doorsAmount;
    private GearShiftType gearShiftType;
    private AuctionOwnerDTO auctionOwnerDTO;
    private AuctionHigestBidDTO auctionHigestBidDTO;
    //private List<String> tags;
}
