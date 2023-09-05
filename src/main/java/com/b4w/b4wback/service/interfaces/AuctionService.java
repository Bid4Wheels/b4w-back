package com.b4w.b4wback.service.interfaces;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.FilterAuctionDTO;
import com.b4w.b4wback.model.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuctionService {
    CreateAuctionDTO createAuction(CreateAuctionDTO createAuctionDTO);
    Page<Auction> getAuctionsFiltered(FilterAuctionDTO filter, Pageable pageable);
}
