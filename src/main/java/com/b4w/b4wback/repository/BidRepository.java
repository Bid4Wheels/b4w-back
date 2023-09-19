package com.b4w.b4wback.repository;

import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {
    public List<Bid> getBidByAuction(Auction auction);

    Bid findTopByAuctionOrderByAmountDesc(Auction auction);

    Bid findTopByBidderIdAndAuctionIdOrderByAmountDesc(Long userId, Long auctionId);
}
