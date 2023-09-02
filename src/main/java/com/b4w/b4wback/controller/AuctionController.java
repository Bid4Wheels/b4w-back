package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.GetAuctionDTO;
import com.b4w.b4wback.service.interfaces.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        return new ResponseEntity<GetAuctionDTO>(auctionService.getAuctionById(id),HttpStatus.OK);
    }
}
