package com.b4w.b4wback.service;


import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateBidDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.TagRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.BidService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class BidServiceTest {
    @Autowired
    private BidService bidService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private TagRepository tagRepository;

    private List<User> users;
    private Auction auction;

    @BeforeEach
    public void setup(){
        users = new ArrayList<>();
        users.add(userRepository.save(new User(new CreateUserDTO("Nico", "Pil", "pilo@gmail,com",
                "+5491112345678", "Aa1bcdefgh"))));
        users.add(userRepository.save(new User(new CreateUserDTO("Pablo", "Pil", "fsajkdf@gmail,com",
                "+5491112345678", "Aa1bcdefgh"))));
        users.add(userRepository.save(new User(new CreateUserDTO("Rogelio", "Pil", "asdfasfd@gmail,com",
                "+5491112345678", "Aa1bcdefgh"))));

        auction = new Auction(new CreateAuctionDTO(users.get(0).getId(), "A","text",
                LocalDateTime.of(2030, 10, 10, 10, 10), "Toyota",
                "A1", 1, 10000, GasType.DIESEL, 1990, "Blue", 4,
                GearShiftType.AUTOMATIC, null), null);
        auction.setUser(users.get(0));
        auction = auctionRepository.save(auction);
    }

    @Test
    void Test001_BidServiceWhenReceiveCreatedBidDTOWithValidDTOShouldCreateBid() {
        Bid bid = bidService.crateBid(new CreateBidDTO(auction.getBasePrice()+1, users.get(1).getId(), auction.getId()));

        assertEquals(auction.getBasePrice()+1, bid.getAmount());
        assertEquals(users.get(1).getId(), bid.getBidder().getId());
        assertEquals(auction.getId(), bid.getAuction().getId());
        assertNotNull(bid.getDate());
    }

    @Test
    void Test002_BidServiceWhenReceiveCreatedBidDTOWithBidderEqualAuctionerShouldThrowBadRequestParametersException(){
        assertThrows(BadRequestParametersException.class,
                ()->bidService.crateBid(new CreateBidDTO(10, users.get(0).getId(), auction.getId())));
    }

    @Test
    void Test003_BidServiceWhenReceiveCreatedBidDTOWithAuctionFinishedShouldThrowBadRequestParametersException(){
        auction.setStatus(AuctionStatus.FINISHED);
        auctionRepository.save(auction);
        assertThrows(BadRequestParametersException.class,
                ()->bidService.crateBid(new CreateBidDTO(10, users.get(1).getId(), auction.getId())));
    }

    @Test
    void Test004_BidServiceWhenReceiveCreatedBidDTOWithAuctionAwaitingDeliveryShouldThrowBadRequestParametersException(){
        auction.setStatus(AuctionStatus.AWATINGDELIVERY);
        auctionRepository.save(auction);
        assertThrows(BadRequestParametersException.class,
                ()->bidService.crateBid(new CreateBidDTO(10, users.get(1).getId(), auction.getId())));
    }

    @Test
    void Test005_BidServiceWhenReceiveCreateBidDTOWithLowerBidAmountShouldBadRequestParametersException(){
        bidService.crateBid(new CreateBidDTO(10, users.get(1).getId(), auction.getId()));
        assertThrows(BadRequestParametersException.class,
                ()->bidService.crateBid(new CreateBidDTO(5, users.get(2).getId(), auction.getId())));
    }

    @Test
    void Test006_BidServiceWhenReceiveCreateBidDTOWithSameBidAmountShouldBadRequestParametersException(){
        bidService.crateBid(new CreateBidDTO(10, users.get(1).getId(), auction.getId()));
        assertThrows(BadRequestParametersException.class,
                ()->bidService.crateBid(new CreateBidDTO(10, users.get(2).getId(), auction.getId())));
    }

    @Test
    void Test007_BidServiceWhenReceiveCreatedBidDTOWithBidAmountLowerThanBaseShouldThrowBadRequestParametersException(){
        assertThrows(BadRequestParametersException.class,
                ()->bidService.crateBid(new CreateBidDTO(0, users.get(0).getId(), auction.getId())));
    }

    @Test
    void Test008_BidServiceWhenGetHighestBidOfUserInAuctionShouldReturnNull(){
        assertNull(bidService.getHighestBidByUserInAuction(users.get(1).getId(), auction.getId()));
    }

    @Test
    void Test009_BidServiceWhenGetHighestBidOfUserInAuctionWithOneBidShouldReturnBid(){
        int bidAmount = 100;
        Long userId = users.get(1).getId();
        Long auctionId = auction.getId();

        bidService.crateBid(new CreateBidDTO(bidAmount, userId, auctionId));
        Bid bid = bidService.getHighestBidByUserInAuction(userId, auctionId);

        assertNotNull(bid);
        assertEquals(userId, bid.getBidder().getId());
        assertEquals(auctionId, bid.getAuction().getId());
        assertEquals(bidAmount, bid.getAmount());
    }

    @Test
    void Test010_BidServiceWhenGetHighestBidOfUserInAuctionWith2BidsShouldReturnHighestBid(){
        int bidAmount = 100;
        Long userId = users.get(1).getId();
        Long auctionId = auction.getId();

        bidService.crateBid(new CreateBidDTO(bidAmount, userId, auctionId));
        bidAmount = 200;
        bidService.crateBid(new CreateBidDTO(bidAmount, userId, auctionId));
        Bid bid = bidService.getHighestBidByUserInAuction(userId, auctionId);

        assertNotNull(bid);
        assertEquals(userId, bid.getBidder().getId());
        assertEquals(auctionId, bid.getAuction().getId());
        assertEquals(bidAmount, bid.getAmount());
    }

    @Test
    void Test0011_BidServiceWhenGetHighestBidOfUserInAuctionWith2BidsAndOtherUserBidShouldReturnHighestBid(){
        int bidAmount = 100;
        Long userId = users.get(1).getId();
        Long auctionId = auction.getId();

        bidService.crateBid(new CreateBidDTO(bidAmount, userId, auctionId));
        bidAmount = 200;
        bidService.crateBid(new CreateBidDTO(bidAmount, userId, auctionId));

        bidService.crateBid(new CreateBidDTO(300, users.get(2).getId(), auctionId));

        Bid bid = bidService.getHighestBidByUserInAuction(userId, auctionId);

        assertNotNull(bid);
        assertEquals(userId, bid.getBidder().getId());
        assertEquals(auctionId, bid.getAuction().getId());
        assertEquals(bidAmount, bid.getAmount());
    }
}
