package com.b4w.b4wback.service.interfaces;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.GetAuctionDTO;

public interface AuctionService {
    CreateAuctionDTO createAuction(CreateAuctionDTO createAuctionDTO);
    GetAuctionDTO getAuctionById(long id);
}
