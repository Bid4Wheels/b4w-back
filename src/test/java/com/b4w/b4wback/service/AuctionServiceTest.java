package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.exception.UrlAlreadySentException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.AuctionService;
import com.b4w.b4wback.service.interfaces.BidService;
import com.b4w.b4wback.service.interfaces.UserService;
import com.b4w.b4wback.util.AuctionGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
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

    @Autowired
    UserRepository userRepository;



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
        CreateAuctionDTO auctionDTOWithID=new CreateAuctionDTO(1L,new CreateAuctionDTO(1L,"Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",4, GearShiftType.AUTOMATIC));
        CreateAuctionDTO auctionCreated=auctionService.createAuction(auctionDTOWithID);
        assertEquals(auctionCreated.getAuctionId(),auctionDTOWithID.getAuctionId());
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
        assertEquals(2,auctionRepository.findByUser(userRepository.findById(1L).get(),null).getSize());
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
        //Improve perfomance of this test.
        GetAuctionDTO auction=auctionService.getAuctionById(1L);
        assertEquals(auctionDTO.getTitle(),auction.getTitle());
        assertEquals(auctionDTO.getDescription(),auction.getDescription());
        assertEquals(auctionDTO.getDeadline(),auction.getDeadline());
        assertEquals(auctionDTO.getBasePrice(),auction.getBasePrice());
        assertEquals(auctionDTO.getBrand(),auction.getBrand());
        assertEquals(auctionDTO.getModel(),auction.getModel());
        assertEquals(auctionDTO.getStatus(),auction.getStatus());
        assertEquals(auctionDTO.getMilage(),auction.getMilage());
        assertEquals(auctionDTO.getGasType(),auction.getGasType());
        assertEquals(auctionDTO.getModelYear(),auction.getModelYear());
        assertEquals(auctionDTO.getColor(),auction.getColor());
        assertEquals(auctionDTO.getDoorsAmount(),auction.getDoorsAmount());
        assertEquals(auctionDTO.getGearShiftType(),auction.getGearShiftType());
        assertEquals(auctionDTO.getUserId(),auction.getAuctionOwnerDTO().getId());

        assertEquals(bidDto.getAmount(),auction.getAuctionHigestBidDTO().getAmount());
        assertEquals(bidDto.getUserId(),auction.getAuctionHigestBidDTO().getUserId());
        assertEquals("Esteban",auction.getAuctionHigestBidDTO().getUserName());
        assertEquals("Chiquito",auction.getAuctionHigestBidDTO().getUserLastName());

        assertEquals(1L,auction.getAuctionOwnerDTO().getId());
        assertEquals("Nico",auction.getAuctionOwnerDTO().getName());
        assertEquals("Borja",auction.getAuctionOwnerDTO().getLastName());

    }

    @Test
    void Test006_AuctionServiceWhenGetAuctionByIdWithInvalidIdShouldThrowException(){
        assertThrows(EntityNotFoundException.class,()->auctionService.getAuctionById(1L));
    }


    @Test
    void Test005_AuctionServiceWhenGetAuctionsByUserIdShouldReturnAListOfAuctions() {
        CreateAuctionDTO auctionDTO = new CreateAuctionDTO(1L, "Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0), "Toyota",
                "Corolla", 150000, 30000, GasType.GASOLINE, 2022, "Silver", 4, GearShiftType.AUTOMATIC);

        CreateAuctionDTO auctionDTO2 = new CreateAuctionDTO(1L, "Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0), "Ford",
                "Focus", 120000, 10000, GasType.HYBRID, 2022, "Blue", 4, GearShiftType.MANUAL);

        auctionService.createAuction(auctionDTO);
        auctionService.createAuction(auctionDTO2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<AuctionDTO> actualAuctions = auctionService.getAuctionsByUserId(1L, pageable);
        assertNotNull(actualAuctions, "Auction list for the user should not be null");

        List<AuctionDTO> auctionList = actualAuctions.getContent();

        AuctionDTO firstAuction = auctionList.get(0);
        assertEquals(1L, firstAuction.getId(), "Expected auction ID to match");
        assertEquals("Subasta de automovil", firstAuction.getTitle(), "Expected auction title to match");
        assertEquals(LocalDateTime.of(2030, 8, 27, 2, 11, 0), firstAuction.getDeadline(), "Expected auction deadline to match");
        assertEquals(150000, firstAuction.getHighestBidAmount(), "Expected highest bid amount to match");
        assertEquals( "OPEN", firstAuction.getStatus().toString(), "Expected auction status to match");

        AuctionDTO secondAuction = auctionList.get(1);
        assertEquals(2L, secondAuction.getId(), "Expected auction ID to match");
        assertEquals("Subasta de automovil", secondAuction.getTitle(), "Expected auction title to match");
        assertEquals(LocalDateTime.of(2030, 8, 27, 2, 11, 0), secondAuction.getDeadline(), "Expected auction deadline to match");
        assertEquals(120000, secondAuction.getHighestBidAmount(), "Expected highest bid amount to match");
        assertEquals( "OPEN", secondAuction.getStatus().toString(), "Expected auction status to match");


    }

    @Test
    void Test006_AuctionServiceWhenGetAuctionsByIdAndUserHasNoAuctionsShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuctionDTO> actualAuctions = auctionService.getAuctionsByUserId(1L,pageable);
        List<AuctionDTO> auctionList = actualAuctions.getContent();
        assertEquals(0, auctionList.size(), "Expected auction list to be empty");
    }

    @Test
    void Test007_AuctionServiceWhenGetAuctionsByIdAndUserNotExistsShouldThrowException() {
        Pageable pageable = PageRequest.of(0, 10);
        assertThrows(EntityNotFoundException.class, () -> auctionService.getAuctionsByUserId(3L, pageable));
    }

    @Test
    void Test008_AuctionServiceWhenBidForAnAuctionAndGetAuctionsByIdShouldReturnAuctionWithHighestBidAmount() {
        CreateAuctionDTO auctionDTO = new CreateAuctionDTO(1L, "Subasta de automovil", "text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0), "Toyota",
                "Corolla", 150000, 30000, GasType.GASOLINE, 2022, "Silver", 4, GearShiftType.AUTOMATIC);

        auctionService.createAuction(auctionDTO);

        bidService.crateBid(CreateBidDTO.builder().auctionId(1L).amount(150000).userId(2L).build());

        Pageable pageable = PageRequest.of(0, 10);

        Page<AuctionDTO> actualAuctions = auctionService.getAuctionsByUserId(1L, pageable);

        List<AuctionDTO> auctionList = actualAuctions.getContent();

        AuctionDTO firstAuction = auctionList.get(0);
        assertEquals(150000, firstAuction.getHighestBidAmount(), "Expected auction ID to match");
    }

    @Test
    void Test009_AuctionServiceWhenGetAuctionsByIdShouldReturnAuctionWithHigestBidAmountEqualsBasePrice() {
        CreateAuctionDTO auctionDTO = new CreateAuctionDTO(1L, "Subasta de automovil", "text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0), "Toyota",
                "Corolla", 150000, 30000, GasType.GASOLINE, 2022, "Silver", 4, GearShiftType.AUTOMATIC);

        auctionService.createAuction(auctionDTO);

        Pageable pageable = PageRequest.of(0, 10);

        Page<AuctionDTO> actualAuctions = auctionService.getAuctionsByUserId(1L, pageable);

        List<AuctionDTO> auctionList = actualAuctions.getContent();

        AuctionDTO firstAuction = auctionList.get(0);
        assertEquals(150000, firstAuction.getHighestBidAmount(), "Expected auction ID to match");
    }

    @Test
    void Test010_AuctionServiceWhenFilterAllAuctionsThenGetAll(){
        List<Auction> auctions = new AuctionGenerator(userRepository).generateAndSaveListOfAuctions(100, auctionRepository);
        Page<AuctionDTO> page = auctionService.getAuctionsFiltered(FilterAuctionDTO.builder().build(), Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test011_AuctionServiceWhenFilterAllAuctionsWithMilageThenGetFew(){
        List<Auction> auctions = new AuctionGenerator(userRepository)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        int min = 5000;
        int max = 50000;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setMilageMin(min);
        filter.setMilageMax(max);

        auctions = auctions.stream().filter(a->(a.getMilage() >= min && a.getMilage() <= max)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test012_AuctionServiceWhenFilterAllAuctionsWithModelYearThenGetFew(){
        List<Auction> auctions = new AuctionGenerator(userRepository)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        int min = 2000;
        int max = 2010;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setModelYearMin(min);
        filter.setModelYearMax(max);

        auctions = auctions.stream().filter(a->(a.getModelYear() >= min && a.getModelYear() <= max)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test013_AuctionServiceWhenFilterAllAuctionsWithPriceAndNoBidsThenGetFew(){
        List<Auction> auctions = new AuctionGenerator(userRepository)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        int min = 2000;
        int max = 5000;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setPriceMin(min);
        filter.setPriceMax(max);

        auctions = auctions.stream().filter(a->(a.getBasePrice() >= min && a.getBasePrice() <= max)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test014_AuctionServiceWhenFilterAllAuctionsWithBrandThenGetFew(){
        List<Auction> auctions = new AuctionGenerator(userRepository)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        String value = "Toyota";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setBrand(value);

        auctions = auctions.stream().filter(a-> a.getBrand().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test015_AuctionServiceWhenFilterAllAuctionsWithColorThenGetFew(){
        List<Auction> auctions = new AuctionGenerator(userRepository)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        String value = "Color";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setColor(value);

        auctions = auctions.stream().filter(a-> a.getColor().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test016_AuctionServiceWhenFilterAllAuctionsWithGasTypeThenGetFew(){
        List<Auction> auctions = new AuctionGenerator(userRepository)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        GasType value = GasType.DIESEL;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setGasType(value);

        auctions = auctions.stream().filter(a-> a.getGasType().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test017_AuctionServiceWhenFilterAllAuctionsWithGearshiftTypeThenGetFew(){
        List<Auction> auctions = new AuctionGenerator(userRepository)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        GearShiftType value = GearShiftType.MANUAL;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setGearShiftType(value);

        auctions = auctions.stream().filter(a-> a.getGearShiftType().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test018_AuctionServiceWhenFilterAllAuctionsWithModeThenGetFew(){
        List<Auction> auctions = new AuctionGenerator(userRepository)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        String value = "2";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setModel(value);

        auctions = auctions.stream().filter(a-> a.getModel().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test019_AuctionServiceWhenFilterAllAuctionsWithDoorsAmountThenGetFew(){
        List<Auction> auctions = new AuctionGenerator(userRepository)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        Integer value = 4;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setDoorsAmount(value);

        auctions = auctions.stream().filter(a-> a.getDoorsAmount().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test020_AuctionServiceWhenCreateAuctionUrlForUploadingImageWithAnExistingAuctionShouldReturnValidUrls(){
        Auction auction = new AuctionGenerator(userRepository).generateRandomAuction();
        auctionRepository.save(auction);
        List<String> urls=auctionService.createUrlsForUploadingImages(1);
        assertEquals(urls.size(),7);
    }

    @Test
    void Test021_AuctionServiceWhenCreateAuctionUrlForUploadingImageWithAnExistingAuctionAndAlreadySentImageUrlShouldReturnUrlAlreadySentException(){
        Auction auction = new AuctionGenerator(userRepository).generateRandomAuction();
        auctionRepository.save(auction);
        auctionService.createUrlsForUploadingImages(1);
        //Making the same request, should return exception.
        assertThrows(UrlAlreadySentException.class, () -> auctionService.createUrlsForUploadingImages(1));
    }
    @Test
    void Test022_AuctionServiceWhenCreateAuctionUrlForDownloadingImageWithAnExistingAuctionUrls(){
        Auction auction = new AuctionGenerator(userRepository).generateRandomAuction();
        auctionRepository.save(auction);
        List<String> urls=auctionService.createUrlsForUploadingImages(1);
        List<String> downloadUrls=auctionService.createUrlsForDownloadingImages(1);
        assertEquals(urls.size(),downloadUrls.size());
    }

}
