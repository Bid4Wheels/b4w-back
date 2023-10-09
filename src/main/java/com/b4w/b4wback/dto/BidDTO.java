package com.b4w.b4wback.dto;

import com.b4w.b4wback.model.Bid;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BidDTO {
    private long id;
    private LocalDateTime time;
    private int amount;
    private long userId;
    private long auctionId;

    public BidDTO(Bid bid){
        id = bid.getId();
        time = bid.getDate();
        amount = bid.getAmount();
        userId = bid.getBidder().getId();
        auctionId = bid.getAuction().getId();
    }
}
