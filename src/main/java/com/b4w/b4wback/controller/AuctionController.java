package com.b4w.b4wback.controller;

import com.b4w.b4wback.dto.AuctionDTO;
import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.FilterAuctionDTO;
import com.b4w.b4wback.dto.GetAuctionDTO;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.service.interfaces.AuctionService;
import com.b4w.b4wback.service.interfaces.BidService;
import com.b4w.b4wback.service.interfaces.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auction")
@RequiredArgsConstructor
public class AuctionController {
    private final AuctionService auctionService;
    private final JwtService jwtService;
    private final BidService bidService;
    @PostMapping()
    public ResponseEntity<CreateAuctionDTO> createUserAuction(@RequestBody @Valid CreateAuctionDTO createAuctionDTO){
        return new ResponseEntity<>(auctionService.createAuction(createAuctionDTO), HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getAuctionById(
            @RequestHeader(HttpHeaders.AUTHORIZATION)  String auth,
            @PathVariable long id){
        GetAuctionDTO auctionDTO = auctionService.getAuctionById(id);

        final String jwt = auth.substring(7);
        Long userId = jwtService.extractId(jwt);

        Bid highestBid = bidService.getHighestBidByUserInAuction(userId, id);
        if (highestBid != null) auctionDTO.setMyHighestBid(highestBid.getAmount());
        return new ResponseEntity<>(auctionDTO, HttpStatus.OK);
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
    public ResponseEntity<?> generateAuctionImageUrl(@PathVariable long auctionId){
        return ResponseEntity.status(HttpStatus.OK).body(auctionService.createUrlsForUploadingImages(auctionId));
    }

    @GetMapping("/ending")
    public ResponseEntity<Page<AuctionDTO>> getAuctionsEnding(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(auctionService.getAuctionsEnding(pageable));
    }

    @GetMapping("/new")
    public ResponseEntity<Page<AuctionDTO>> getAuctionsNew(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(auctionService.getAuctionsNew(pageable));
    }


    @DeleteMapping("/{auctionID}")
    public ResponseEntity<?> deleteAuctionById(@RequestHeader("Authorization") String token,@PathVariable Long auctionID) {
        auctionService.deleteAuction(auctionID, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/bidder/{bidderId}")
    public ResponseEntity<Page<AuctionDTO>> getAuctionsBiddedByUser(@PathVariable long bidderId, Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(auctionService.getAuctionsBiddedByUser(bidderId, pageable));
    }
}
