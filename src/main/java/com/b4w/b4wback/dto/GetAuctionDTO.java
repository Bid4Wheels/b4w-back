package com.b4w.b4wback.dto;

import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.b4w.b4wback.model.Tag;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
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
    private List<Tag> tags;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> auctionImageUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AuctionHigestBidDTO> topBids;

    public GetAuctionDTO(GetAuctionDTO getAuctionDTO,List<String> auctionImageUrl, List<AuctionHigestBidDTO> topBids){
        this.title=getAuctionDTO.getTitle();
        this.description=getAuctionDTO.getDescription();
        this.deadline=getAuctionDTO.getDeadline();
        this.basePrice=getAuctionDTO.getBasePrice();
        this.brand=getAuctionDTO.getBrand();
        this.model=getAuctionDTO.getModel();
        this.status=getAuctionDTO.getStatus();
        this.milage=getAuctionDTO.getMilage();
        this.gasType=getAuctionDTO.getGasType();
        this.modelYear=getAuctionDTO.getModelYear();
        this.color=getAuctionDTO.getColor();
        this.doorsAmount=getAuctionDTO.getDoorsAmount();
        this.gearShiftType=getAuctionDTO.getGearShiftType();
        this.auctionOwnerDTO=getAuctionDTO.getAuctionOwnerDTO();
        this.tags=getAuctionDTO.getTags();
        this.auctionImageUrl=auctionImageUrl;
        this.topBids=topBids;
    }
}
