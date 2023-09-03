package com.b4w.b4wback.service.interfaces;

import com.b4w.b4wback.dto.AuctionDTO;
import com.b4w.b4wback.dto.CreateAuctionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AuctionService {
    CreateAuctionDTO createAuction(CreateAuctionDTO createAuctionDTO);

    Page<AuctionDTO> getAuctionsByUserId(Long userId, Pageable pageable);
}
