package com.b4w.b4wback.repository;

import com.b4w.b4wback.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    public List<Offer> getOfferByAuction(long auctionId);
}
