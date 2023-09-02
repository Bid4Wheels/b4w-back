package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateBidDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.service.interfaces.AuctionService;
import com.b4w.b4wback.service.interfaces.BidService;
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
    AuctionService auctionService;

    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    UserService userService;

    @Autowired
    BidService bidService;
    @BeforeEach
    public void setup() {
        CreateUserDTO userDTO = new CreateUserDTO("Nico", "Borja", "bejero7623@dusyum.com",
                "+5491112345678", "1Afjfslkjfl");
        CreateUserDTO userDTO2 = new CreateUserDTO("Esteban", "Chiquito", "bejero@dusyum,com","+5491112345678",
                "1Afjfslkjfl");
        userService.createUser(userDTO);
        userService.createUser(userDTO2);
    }


    @Test
    void Test001_AuctionServiceWhenReceiveCreatedAuctionDTOWithValidDTOShouldReturnCreateAuctionDTO() {
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC);
        CreateAuctionDTO auctionCreated=auctionService.createAuction(auctionDTO);
        assertEquals(auctionCreated,auctionDTO);
    }

    @Test
    void Test002_AuctionServiceWhenUserCreatesMoreThanOneValidAuctionShouldUserHaveMoreThanOneAuctions() {
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC);

        CreateAuctionDTO auctionDTO2=new CreateAuctionDTO(1L,"Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Ford",
                "Focus",120000,10000, GasType.HYBRID,2022,"Blue",4, GearShiftType.MANUAL);
        auctionService.createAuction(auctionDTO);
        auctionService.createAuction(auctionDTO2);
        assertEquals(2,auctionRepository.findAllByUserId(1L).size());
    }

    @Test
    void Test003_AuctionServiceWhenUserCreatesAuctionWithInvalidUserIdShouldThrowException() {
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(3L,"Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC);
        assertThrows(BadRequestParametersException.class,()->auctionService.createAuction(auctionDTO));
    }

    @Test
    void Test004_AuctionServiceCheckAllFieldsOfAuctionCreatedShouldReturnEquals(){
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil","text",
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

    @Test
    void Test005_AuctionServiceWhenGetAuctionByIdShouldReturnGetAuctionDTO(){
        CreateAuctionDTO auctionDTO=new CreateAuctionDTO(1L,"Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC);
        CreateBidDTO bidDto=new CreateBidDTO(150000,2L,1L);
        auctionService.createAuction(auctionDTO);
        bidService.crateBid(bidDto);
        assertEquals(auctionDTO.getTitle(),auctionService.getAuctionById(1L).getTitle());
        assertEquals(auctionDTO.getDescription(),auctionService.getAuctionById(1L).getDescription());
        assertEquals(auctionDTO.getDeadline(),auctionService.getAuctionById(1L).getDeadline());
        assertEquals(auctionDTO.getBasePrice(),auctionService.getAuctionById(1L).getBasePrice());
        assertEquals(auctionDTO.getBrand(),auctionService.getAuctionById(1L).getBrand());
        assertEquals(auctionDTO.getModel(),auctionService.getAuctionById(1L).getModel());
        assertEquals(auctionDTO.getStatus(),auctionService.getAuctionById(1L).getStatus());
        assertEquals(auctionDTO.getMilage(),auctionService.getAuctionById(1L).getMilage());
        assertEquals(auctionDTO.getGasType(),auctionService.getAuctionById(1L).getGasType());
        assertEquals(auctionDTO.getModelYear(),auctionService.getAuctionById(1L).getModelYear());
        assertEquals(auctionDTO.getColor(),auctionService.getAuctionById(1L).getColor());
        assertEquals(auctionDTO.getDoorsAmount(),auctionService.getAuctionById(1L).getDoorsAmount());
        assertEquals(auctionDTO.getGearShiftType(),auctionService.getAuctionById(1L).getGearShiftType());
        assertEquals(auctionDTO.getUserId(),auctionService.getAuctionById(1L).getAuctionOwnerDTO().getId());

        assertEquals(bidDto.getAmount(),auctionService.getAuctionById(1L).getAuctionHigestBidDTO().getAmount());
        assertEquals(bidDto.getUserId(),auctionService.getAuctionById(1L).getAuctionHigestBidDTO().getUserId());
        assertEquals("Esteban",auctionService.getAuctionById(1L).getAuctionHigestBidDTO().getUserName());
        assertEquals("Chiquito",auctionService.getAuctionById(1L).getAuctionHigestBidDTO().getUserLastName());

        assertEquals(1L,auctionService.getAuctionById(1L).getAuctionOwnerDTO().getId());
        assertEquals("Nico",auctionService.getAuctionById(1L).getAuctionOwnerDTO().getName());
        assertEquals("Borja",auctionService.getAuctionById(1L).getAuctionOwnerDTO().getLastName());

    }

    @Test
    void Test006_AuctionServiceWhenGetAuctionByIdWithInvalidIdShouldThrowException(){
        assertThrows(EntityNotFoundException.class,()->auctionService.getAuctionById(1L));
    }

}
