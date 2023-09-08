package com.b4w.b4wback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class AuctionHigestBidDTO {
    private Integer amount;
    private long userId;
    private String userName;
    private String userLastName;
}
