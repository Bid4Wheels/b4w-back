package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.AuctionDTO;
import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.FilterAuctionDTO;
import com.b4w.b4wback.service.interfaces.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auction")
@RequiredArgsConstructor
public class AuctionController {
    private final AuctionService auctionService;
    @PostMapping()
    public ResponseEntity<CreateAuctionDTO> createUserAuction(@RequestBody @Valid CreateAuctionDTO createAuctionDTO){
        return new ResponseEntity<>(auctionService.createAuction(createAuctionDTO), HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getAuctionById(@PathVariable long id){
        return new ResponseEntity<>(auctionService.getAuctionById(id),HttpStatus.OK);
    }
    @GetMapping("user/{userId}")
    public ResponseEntity<?> getAuctionsByUserId(@PathVariable long userId, Pageable pageable){
        val auctions = auctionService.getAuctionsByUserId(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(auctions);
    }

    @PostMapping("/filter")
    public ResponseEntity<Page<AuctionDTO>> getAuctionsFiltered(@RequestBody FilterAuctionDTO filter, Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(auctionService.getAuctionsFiltered(filter, pageable));
    }

    @PostMapping("/image-url/{auctionId}")
    public ResponseEntity<?> getAuctionImageUrl(@PathVariable long auctionId){
        return ResponseEntity.status(HttpStatus.OK).body(auctionService.createUrlsForUploadingImages(auctionId));
    }
}
