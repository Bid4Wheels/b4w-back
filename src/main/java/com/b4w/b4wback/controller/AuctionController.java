package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.service.interfaces.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auction")
@RequiredArgsConstructor
public class AuctionController {
    private final AuctionService auctionService;
    @PostMapping()
    public ResponseEntity<CreateAuctionDTO> createUserAuction(@RequestBody @Valid CreateAuctionDTO createAuctionDTO){
        return ResponseEntity.ok().body(auctionService.createAuction(createAuctionDTO));
    }
}
