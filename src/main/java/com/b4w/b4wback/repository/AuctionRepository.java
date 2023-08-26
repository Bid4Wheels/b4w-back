package com.b4w.b4wback.repository;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction,Long> {
    List<CreateAuctionDTO> findAllByUserId(Long id);
}
