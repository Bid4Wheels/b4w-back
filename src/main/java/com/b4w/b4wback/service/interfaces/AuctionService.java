package com.b4w.b4wback.service.interfaces;

import com.b4w.b4wback.dto.AuctionDTO;
import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.GetAuctionDTO;
import com.b4w.b4wback.dto.FilterAuctionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuctionService {
    CreateAuctionDTO createAuction(CreateAuctionDTO createAuctionDTO);

    GetAuctionDTO getAuctionById(long id);

    Page<AuctionDTO> getAuctionsByUserId(Long userId, Pageable pageable);

    Page<AuctionDTO> getAuctionsFiltered(FilterAuctionDTO filter, Pageable pageable);

    List<String> createUrlsForUploadingImages(long auctionId);

    List<String> createUrlsForDownloadingImages(long auctionId);

    Page<AuctionDTO> getAuctionsEnding(Pageable pageable);

    Page<AuctionDTO> getAuctionsNew(Pageable pageable);


    void deleteAuction(Long auctionID,String token);
    Page<AuctionDTO> getAuctionsBiddedByUser(long bidderId, Pageable pageable);

    void finishAuction(Long auctionID, Long userId);
}
