package com.b4w.b4wback.dto;

import com.b4w.b4wback.enums.AuctionStatus;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import com.b4w.b4wback.model.Auction;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AuctionDTO {
    private Long id;
    private String title;
    private LocalDateTime deadline;
    private int highestBidAmount;
    private AuctionStatus status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String firstImageUrl;
    private List<String> tagNames;
    public AuctionDTO(Long id, String title, LocalDateTime deadline,  AuctionStatus status, Integer highestBidAmount) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.highestBidAmount = highestBidAmount;
        this.status = status;
    }

    public AuctionDTO(Auction auction){
        this.id = auction.getId();
        this.title = auction.getTitle();
        this.deadline = auction.getDeadline();
        this.highestBidAmount = auction.getHighestBidAmount();
        this.status = auction.getStatus();
        this.tagNames = auction.getTagNames();
    }
}
