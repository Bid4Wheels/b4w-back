package com.b4w.b4wback.service.interfaces;

import com.b4w.b4wback.dto.CreateBidDTO;
import com.b4w.b4wback.model.Bid;

public interface BidService {
    Bid crateBid(CreateBidDTO createBidDTO);

    Bid getHighestBidByUserInAuction(Long userId, Long auctionId);
}
