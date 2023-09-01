package com.b4w.b4wback.repository;

import com.b4w.b4wback.dto.AuctionDTO;
import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction,Long> {
    List<CreateAuctionDTO> findAllByUserId(Long id);

    Page<AuctionDTO> findByUser(User user, Pageable pageable);
}
