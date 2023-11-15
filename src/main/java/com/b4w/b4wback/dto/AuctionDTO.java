package com.b4w.b4wback.dto;

import com.b4w.b4wback.enums.AuctionStatus;
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
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private int highestBidAmount;
    private AuctionStatus status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String firstImageUrl;
    private List<String> tagNames;
    public AuctionDTO(Long id, String title, LocalDateTime deadline,LocalDateTime createdAt,  AuctionStatus status, Integer highestBidAmount) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.deadline = deadline.minusHours(3);
        this.highestBidAmount = highestBidAmount;
        this.status = status;
    }

    public AuctionDTO(Auction auction){
        this.id = auction.getId();
        this.title = auction.getTitle();
        this.createdAt = auction.getCreatedAt();
        this.deadline = auction.getDeadline().minusHours(3);
        this.highestBidAmount = auction.getHighestBidAmount();
        this.status = auction.getStatus();
        this.tagNames = auction.getTagNames();
    }
}
