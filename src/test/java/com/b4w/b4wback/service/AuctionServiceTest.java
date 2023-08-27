package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.service.interfaces.AuctionService;
import com.b4w.b4wback.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest()
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuctionServiceTest {
    @Autowired
    AuctionService auctionService;;

    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    UserService userService;
    @BeforeEach
    public void setup() {
        CreateUserDTO userDTO = new CreateUserDTO("Nico", "Borja", "bejero7623@dusyum.com",
                8493123, "1Afjfslkjfl");
        userService.createUser(userDTO);
    }


    @Test
    void Test001_AuctionServiceWhenReceiveCreatedAuctionDTOWithValidDTOShouldReturnCreateAuctionDTO() {
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC);
        CreateAuctionDTO auctionCreated=auctionService.createAuction(auctionDTO);
        assertEquals(auctionCreated,auctionDTO);
    }

    @Test
    void Test002_AuctionServiceWhenUserCreatesMoreThanOneValidAuctionShouldUserHaveMoreThanOneAuctions() {
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC);

        CreateAuctionDTO auctionDTO2=new CreateAuctionDTO(1L,"Subasta de automovil",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Ford",
                "Focus",120000,10000, GasType.HYBRID,2022,"Blue",4, GearShiftType.MANUAL);
        auctionService.createAuction(auctionDTO);
        auctionService.createAuction(auctionDTO2);
        assertEquals(2,auctionRepository.findAllByUserId(1L).size());
    }

    @Test
    void Test003_AuctionServiceWhenUserCreatesAuctionWithInvalidUserIdShouldThrowException() {
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(2L,"Subasta de automovil",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC);
        assertThrows(BadRequestParametersException.class,()->auctionService.createAuction(auctionDTO));
    }

    @Test
    void Test004_AuctionServiceCheckAllFieldsOfAuctionCreatedShouldReturnEquals(){
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC);
        auctionService.createAuction(auctionDTO);
        //Already tested in Test001
        Auction auction=auctionRepository.findById(1L).get();
        assertEquals(auctionDTO.getBasePrice(),auction.getBasePrice());
        assertEquals(auctionDTO.getBrand(),auction.getBrand());
        assertEquals(auctionDTO.getColor(),auction.getColor());
        assertEquals(auctionDTO.getDeadline(),auction.getDeadline());
        assertEquals(auctionDTO.getDescription(),auction.getDescription());
        assertEquals(auctionDTO.getDoorsAmount(),auction.getDoorsAmount());
        assertEquals(auctionDTO.getGasType(),auction.getGasType());
        assertEquals(auctionDTO.getGearShiftType(),auction.getGearShiftType());
        assertEquals(auctionDTO.getMilage(),auction.getMilage());
        assertEquals(auctionDTO.getModel(),auction.getModel());
        assertEquals(auctionDTO.getModelYear(),auction.getModelYear());
        assertEquals(auctionDTO.getStatus(),auction.getStatus());
        assertEquals(auction.getUser().getId(),auctionDTO.getUserId());
    }
}
