package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.BidDTO;
import com.b4w.b4wback.dto.CreateBidDTO;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.service.interfaces.BidService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bid")
public class BidController {
    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping()
    public ResponseEntity<BidDTO> createUserAuction(@RequestBody @Valid CreateBidDTO bidDTO){
        Bid bid = bidService.crateBid(bidDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BidDTO(bid));
    }
}
