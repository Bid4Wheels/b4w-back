package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.BidDTO;
import com.b4w.b4wback.dto.CreateBidDTO;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.service.interfaces.BidService;
import com.b4w.b4wback.service.interfaces.JwtService;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bid")
public class BidController {
    private final BidService bidService;
    private final JwtService jwtService;

    public BidController(BidService bidService, JwtService jwtService) {

        this.bidService = bidService;
        this.jwtService = jwtService;
    }

    @PostMapping()
    public ResponseEntity<BidDTO> createUserAuction(@RequestBody @Valid CreateBidDTO bidDTO){
        Bid bid = bidService.crateBid(bidDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BidDTO(bid));
    }
}
