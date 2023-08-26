package com.b4w.b4wback.repository;

import com.b4w.b4wback.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction,Long> {
}
