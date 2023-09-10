package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Tag;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.TagRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.AuctionService;
import com.b4w.b4wback.service.interfaces.BidService;
import com.b4w.b4wback.service.interfaces.TagService;
import com.b4w.b4wback.service.interfaces.UserService;
import com.b4w.b4wback.util.AuctionGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Autowired
    private TagService tagService;
    @Autowired
    private TagRepository tagRepository;

    private CreateAuctionDTO auctionDTO;


    @BeforeEach
    public void setup() {
        CreateUserDTO userDTO = new CreateUserDTO("Nico", "Borja", "bejero7623@dusyum.com",
                "+5491112345678", "1Afjfslkjfl");
        CreateUserDTO userDTO2 = new CreateUserDTO("Esteban", "Chiquito", "bejero@dusyum,com","+5491112345678",
                "1Afjfslkjfl");
        userService.createUser(userDTO);
        userService.createUser(userDTO2);

        auctionDTO = new CreateAuctionDTO(1L,"Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",
                4, GearShiftType.AUTOMATIC, null);
    }


    @Test
    void Test001_AuctionServiceWhenReceiveCreatedAuctionDTOWithValidDTOShouldReturnCreateAuctionDTO() {
        CreateAuctionDTO auctionCreated=auctionService.createAuction(auctionDTO);
        assertEquals(auctionCreated,auctionDTO);
    }

    @Test
    void Test002_AuctionServiceWhenUserCreatesMoreThanOneValidAuctionShouldUserHaveMoreThanOneAuctions() {
        CreateAuctionDTO auctionDTO2=new CreateAuctionDTO(1L,"Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Ford",
                "Focus",120000,10000, GasType.HYBRID,2022,"Blue",
                4, GearShiftType.MANUAL, null);
        auctionService.createAuction(auctionDTO);
        auctionService.createAuction(auctionDTO2);
        assertEquals(2,auctionRepository.findAllByUserId(1L).size());
    }

    @Test
    void Test003_AuctionServiceWhenUserCreatesAuctionWithInvalidUserIdShouldThrowException() {
        auctionDTO.setUserId(3L);
        assertThrows(BadRequestParametersException.class,()->auctionService.createAuction(auctionDTO));
    }

    @Test
    void Test004_AuctionServiceCheckAllFieldsOfAuctionCreatedShouldReturnEquals(){
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
        CreateBidDTO bidDto=new CreateBidDTO(150000,2L,1L);
        auctionService.createAuction(auctionDTO);
        bidService.crateBid(bidDto);
        GetAuctionDTO auction = auctionService.getAuctionById(1L);
        assertEquals(auctionDTO.getTitle(), auction.getTitle());
        assertEquals(auctionDTO.getDescription(), auction.getDescription());
        assertEquals(auctionDTO.getDeadline(), auction.getDeadline());
        assertEquals(auctionDTO.getBasePrice(), auction.getBasePrice());
        assertEquals(auctionDTO.getBrand(), auction.getBrand());
        assertEquals(auctionDTO.getModel(), auction.getModel());
        assertEquals(auctionDTO.getStatus(), auction.getStatus());
        assertEquals(auctionDTO.getMilage(), auction.getMilage());
        assertEquals(auctionDTO.getGasType(), auction.getGasType());
        assertEquals(auctionDTO.getModelYear(), auction.getModelYear());
        assertEquals(auctionDTO.getColor(), auction.getColor());
        assertEquals(auctionDTO.getDoorsAmount(), auction.getDoorsAmount());
        assertEquals(auctionDTO.getGearShiftType(), auction.getGearShiftType());
        assertEquals(auctionDTO.getUserId(), auction.getAuctionOwnerDTO().getId());

        assertEquals(bidDto.getAmount(), auction.getAuctionHigestBidDTO().getAmount());
        assertEquals(bidDto.getUserId(), auction.getAuctionHigestBidDTO().getUserId());
        assertEquals("Esteban", auction.getAuctionHigestBidDTO().getUserName());
        assertEquals("Chiquito", auction.getAuctionHigestBidDTO().getUserLastName());

        assertEquals(1L, auction.getAuctionOwnerDTO().getId());
        assertEquals("Nico", auction.getAuctionOwnerDTO().getName());
        assertEquals("Borja", auction.getAuctionOwnerDTO().getLastName());

    }

    @Test
    void Test006_AuctionServiceWhenGetAuctionByIdWithInvalidIdShouldThrowException(){
        assertThrows(EntityNotFoundException.class,()->auctionService.getAuctionById(1L));
    }


    @Test
    void Test005_AuctionServiceWhenGetAuctionsByUserIdShouldReturnAListOfAuctions() {
        auctionDTO = new CreateAuctionDTO(1L, "Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0), "Toyota",
                "Corolla", 150000, 30000, GasType.GASOLINE, 2022, "Silver",
                4, GearShiftType.AUTOMATIC, null);

        CreateAuctionDTO auctionDTO2 = new CreateAuctionDTO(1L, "Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0), "Ford",
                "Focus", 120000, 10000, GasType.HYBRID, 2022, "Blue",
                4, GearShiftType.MANUAL, null);

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
                "Corolla", 150000, 30000, GasType.GASOLINE, 2022, "Silver",
                4, GearShiftType.AUTOMATIC, null);

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
                "Corolla", 150000, 30000, GasType.GASOLINE, 2022, "Silver",
                4, GearShiftType.AUTOMATIC, null);

        auctionService.createAuction(auctionDTO);

        Pageable pageable = PageRequest.of(0, 10);

        Page<AuctionDTO> actualAuctions = auctionService.getAuctionsByUserId(1L, pageable);

        List<AuctionDTO> auctionList = actualAuctions.getContent();

        AuctionDTO firstAuction = auctionList.get(0);
        assertEquals(150000, firstAuction.getHighestBidAmount(), "Expected auction ID to match");
    }

    @Test
    void Test010_AuctionServiceWhenFilterAllAuctionsThenGetAll() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
                .generateAndSaveListOfAuctions(100, auctionRepository);
        Page<AuctionDTO> page = auctionService.getAuctionsFiltered(FilterAuctionDTO.builder().build(), Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test011_AuctionServiceWhenFilterAllAuctionsWithMilageThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
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
    void Test012_AuctionServiceWhenFilterAllAuctionsWithModelYearThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
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
    void Test013_AuctionServiceWhenFilterAllAuctionsWithPriceAndNoBidsThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
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
    void Test014_AuctionServiceWhenFilterAllAuctionsWithBrandThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
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
    void Test015_AuctionServiceWhenFilterAllAuctionsWithColorThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
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
    void Test016_AuctionServiceWhenFilterAllAuctionsWithGasTypeThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
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
    void Test017_AuctionServiceWhenFilterAllAuctionsWithGearshiftTypeThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
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
    void Test018_AuctionServiceWhenFilterAllAuctionsWithModeThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
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
    void Test019_AuctionServiceWhenFilterAllAuctionsWithDoorsAmountThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
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
    void Test020_AuctionServiceWhenCreateAuctionWithNonExistentTagsThenGetAuctionWithAllItsTags(){
        List<String> tags = List.of("Tag1", "Tag2", "Tag3", "Tag4", "Tag5");
        auctionDTO.setTags(tags);
        auctionService.createAuction(auctionDTO);

        GetAuctionDTO auction = auctionService.getAuctionById(1L);

        tags = tags.stream().map(String::toLowerCase).toList();

        List<Tag> existingTags = tagRepository.findAll();
        List<String> existingTagsNames = new ArrayList<>();
        existingTags.forEach(t->existingTagsNames.add(t.getTagName()));
        assertTrue(existingTagsNames.containsAll(tags));

        List<String> auctionTags = new ArrayList<>();
        auction.getTags().forEach(t->auctionTags.add(t.getTagName()));
        assertTrue(auctionTags.containsAll(tags));
    }

    @Test
    void Test021_AuctionServiceWhenCreateAuctionWithSomeExistentTagsThenGetAuctionWithAllItsTags(){
        List<String> existingTags = new ArrayList<>(List.of("tag1", "tag2", "tag3"));
        tagRepository.saveAll(existingTags.stream().map(Tag::new).toList());

        List<String> tags = new ArrayList<>(List.of("Tag4", "Tag5"));
        for (String existingTag : existingTags) tags.add(existingTag);

        auctionDTO.setTags(tags);
        auctionService.createAuction(auctionDTO);

        GetAuctionDTO auction = auctionService.getAuctionById(1L);

        tags = tags.stream().map(String::toLowerCase).toList();

        List<Tag> savedTags = tagRepository.findAll();
        List<String> savedTagsNames = new ArrayList<>();
        savedTags.forEach(t->savedTagsNames.add(t.getTagName()));
        assertTrue(savedTagsNames.containsAll(tags));

        //has all it's tags
        List<String> auctionTags = new ArrayList<>();
        auction.getTags().forEach(t->auctionTags.add(t.getTagName()));
        assertTrue(auctionTags.containsAll(tags));
    }

    @Test
    void Test022_AuctionServiceWhenCreateAuctionWithAllExistentTagsThenGetAuctionWithAllItsTags(){
        List<String> tags = List.of("Tag1", "Tag2", "Tag3", "Tag4", "Tag5");
        tagRepository.saveAll(tags.stream().map(Tag::new).toList());

        auctionDTO.setTags(tags);
        auctionService.createAuction(auctionDTO);

        GetAuctionDTO auction = auctionService.getAuctionById(1L);

        tags = tags.stream().map(String::toLowerCase).toList();

        List<Tag> existingTags = tagRepository.findAll();
        List<String> existingTagsNames = new ArrayList<>();
        existingTags.forEach(t->existingTagsNames.add(t.getTagName()));
        assertTrue(existingTagsNames.containsAll(tags));

        List<String> auctionTags = new ArrayList<>();
        auction.getTags().forEach(t->auctionTags.add(t.getTagName()));
        assertTrue(auctionTags.containsAll(tags));
    }
}
