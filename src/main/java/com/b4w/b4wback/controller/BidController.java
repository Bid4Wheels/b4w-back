package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.CreateOfferDTO;
import com.b4w.b4wback.model.Offer;
import com.b4w.b4wback.service.interfaces.OfferService;
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
    private final OfferService offerService;

    public BidController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping()
    public ResponseEntity<?> createUserAuction(@RequestBody @Valid CreateOfferDTO offerDTO){
        offerService.CrateOffer(offerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
