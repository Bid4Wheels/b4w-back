package com.b4w.b4wback.dto;

import com.b4w.b4wback.enums.AuctionStatus;
import lombok.Builder;
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
    private List<String> tagAuctions;

    public AuctionDTO(Long id, String title, LocalDateTime deadline,  AuctionStatus status, Integer highestBidAmount, List<String> tagAuctions) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.highestBidAmount = highestBidAmount;
        this.status = status;
        this.tagAuctions = tagAuctions;
    }
}
