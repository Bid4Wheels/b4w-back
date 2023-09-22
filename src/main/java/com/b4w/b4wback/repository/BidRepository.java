package com.b4w.b4wback.repository;

import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {
    public List<Bid> getBidByAuction(Auction auction);

    Bid findTopByAuctionOrderByAmountDesc(Auction auction);

    Bid findTopByBidderIdAndAuctionIdOrderByAmountDesc(Long userId, Long auctionId);
    @Query("SELECT b FROM Bid b WHERE b.bidder.id = ?1 AND b.auction.status = ?2")
    List<Bid> findAllByUserAndStatusOpen(Long userId, AuctionStatus status);
}
