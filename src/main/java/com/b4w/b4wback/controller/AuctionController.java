package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.service.interfaces.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
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
    @GetMapping("user/{userId}")
    public ResponseEntity<?> getAuctionsByUserId(@PathVariable long userId){
        val auctions = auctionService.getAuctionsByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(auctions);
    }
}
