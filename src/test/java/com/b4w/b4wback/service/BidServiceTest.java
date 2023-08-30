package com.b4w.b4wback.service;


import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateBidDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
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

        auction = new Auction(new CreateAuctionDTO(users.get(0).getId(), "A", LocalDateTime.now(), "Toyota",
                "A1", 100, 10000, GasType.DIESEL, 1990, "Blue", 4,
                GearShiftType.AUTOMATIC));
        auction.setUser(users.get(0));
        auction = auctionRepository.save(auction);
    }

    @Test
    void Test001_BidServiceWhenReceiveCreatedBidDTOWithValidDTOShouldCreateBid() {
        bidService.CrateBid(new CreateBidDTO(10, users.get(1).getId(), auction.getId()));
    }

    @Test
    void Test002_BidServiceWhenReceiveCreatedBidDTOWithBidderEqualAuctionerShouldThrowBadRequestParametersException(){
        assertThrows(BadRequestParametersException.class,
                ()->bidService.CrateBid(new CreateBidDTO(10, users.get(0).getId(), auction.getId())));
    }

    @Test
    void Test003_BidServiceWhenReceiveCreatedBidDTOWithAuctionFinishedShouldThrowBadRequestParametersException(){
        auction.setStatus(AuctionStatus.FINISHED);
        auctionRepository.save(auction);
        assertThrows(BadRequestParametersException.class,
                ()->bidService.CrateBid(new CreateBidDTO(10, users.get(1).getId(), auction.getId())));
    }

    @Test
    void Test004_BidServiceWhenReceiveCreatedBidDTOWithAuctionAwaitingDeliveryShouldThrowBadRequestParametersException(){
        auction.setStatus(AuctionStatus.AWATINGDELIVERY);
        auctionRepository.save(auction);
        assertThrows(BadRequestParametersException.class,
                ()->bidService.CrateBid(new CreateBidDTO(10, users.get(1).getId(), auction.getId())));
    }

    @Test
    void Test005_BidServiceWhenReceiveCreateBidDTOWithLowerBidAmountShouldBadRequestParametersException(){
        bidService.CrateBid(new CreateBidDTO(10, users.get(1).getId(), auction.getId()));
        assertThrows(BadRequestParametersException.class,
                ()->bidService.CrateBid(new CreateBidDTO(5, users.get(2).getId(), auction.getId())));
    }
}
