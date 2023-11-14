package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.*;
import com.b4w.b4wback.dto.auth.SignInRequest;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.exception.AuctionExpiredException;
import com.b4w.b4wback.exception.BadRequestParametersException;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.exception.UrlAlreadySentException;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Tag;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.BidRepository;
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

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.b4w.b4wback.util.HttpEntityCreator.authenticateAndGetToken;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private BidRepository bidRepository;
    @BeforeEach
    public void setup() {
        CreateUserDTO userDTO= new CreateUserDTO("Nico", "Borja", "bejero7623@dusyum.com",
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
        CreateAuctionDTO auctionDTOWithID = new CreateAuctionDTO(1L, new CreateAuctionDTO(1L, "Subasta de automovil", "text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0), "Toyota",
                "Corolla", 150000, 30000, GasType.GASOLINE, 2022, "Silver", 4, GearShiftType.AUTOMATIC, null));
        CreateAuctionDTO auctionCreated = auctionService.createAuction(auctionDTOWithID);
        assertEquals(auctionCreated.getAuctionId(), auctionDTOWithID.getAuctionId());
    }

    @Test
    void Test002_AuctionServiceWhenUserCreatesMoreThanOneValidAuctionShouldUserHaveMoreThanOneAuctions() {
        CreateAuctionDTO auctionDTO2=new CreateAuctionDTO(1L,"Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Ford",
                "Focus",120000,10000, GasType.HYBRID,2022,"Blue",
                4, GearShiftType.MANUAL, null);
        auctionService.createAuction(auctionDTO);
        auctionService.createAuction(auctionDTO2);
        assertEquals(2,auctionRepository.findByUser(userRepository.findById(1L).get(),null).getSize());
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


        assertEquals(1L,auction.getAuctionOwnerDTO().getId());
        assertEquals("Nico",auction.getAuctionOwnerDTO().getName());
        assertEquals("Borja",auction.getAuctionOwnerDTO().getLastName());

    }

    @Test
    void Test006_AuctionServiceWhenGetAuctionByIdWithInvalidIdShouldThrowException(){
        assertThrows(EntityNotFoundException.class,()->auctionService.getAuctionById(1L));
    }


    @Test
    void Test007_AuctionServiceWhenGetAuctionsByUserIdShouldReturnAListOfAuctions() {
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
    void Test008_AuctionServiceWhenGetAuctionsByIdAndUserHasNoAuctionsShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuctionDTO> actualAuctions = auctionService.getAuctionsByUserId(1L,pageable);
        List<AuctionDTO> auctionList = actualAuctions.getContent();
        assertEquals(0, auctionList.size(), "Expected auction list to be empty");
    }

    @Test
    void Test009_AuctionServiceWhenGetAuctionsByIdAndUserNotExistsShouldThrowException() {
        Pageable pageable = PageRequest.of(0, 10);
        assertThrows(EntityNotFoundException.class, () -> auctionService.getAuctionsByUserId(3L, pageable));
    }

    @Test
    void Test010_AuctionServiceWhenBidForAnAuctionAndGetAuctionsByIdShouldReturnAuctionWithHighestBidAmount() {
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
    void Test011_AuctionServiceWhenGetAuctionsByIdShouldReturnAuctionWithHigestBidAmountEqualsBasePrice() {
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

    /**
    @Test
    void Test012_AuctionServiceWhenFilterAllAuctionsThenGetAll() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        List<Tag> tags = tagService.getAllTags();
        List<Long> tagsList = tags.stream().map(Tag::getId).toList();

        Page<AuctionDTO> page = auctionService.getAuctionsFiltered(
                FilterAuctionDTO.builder().tags(tagsList).build(),
                Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test013_AuctionServiceWhenFilterAllAuctionsWithMilageThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        List<Tag> tags = tagService.getAllTags();
        List<Long> tagsList = tags.stream().map(Tag::getId).toList();

        int min = 5000;
        int max = 50000;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().tags(tagsList).build();
        filter.setMilageMin(min);
        filter.setMilageMax(max);

        auctions = auctions.stream().filter(a->(a.getMilage() >= min && a.getMilage() <= max)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }


    @Test
    void Test014_AuctionServiceWhenFilterAllAuctionsWithModelYearThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        List<Tag> tags = tagService.getAllTags();
        List<Long> tagsList = tags.stream().map(Tag::getId).toList();

        int min = 2000;
        int max = 2010;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().tags(tagsList).build();
        filter.setModelYearMin(min);
        filter.setModelYearMax(max);

        auctions = auctions.stream().filter(a->(a.getModelYear() >= min && a.getModelYear() <= max)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test015_AuctionServiceWhenFilterAllAuctionsWithPriceAndNoBidsThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        List<Tag> tags = tagService.getAllTags();
        List<Long> tagsList = tags.stream().map(Tag::getId).toList();

        int min = 2000;
        int max = 5000;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().tags(tagsList).build();
        filter.setPriceMin(min);
        filter.setPriceMax(max);

        auctions = auctions.stream().filter(a->(a.getBasePrice() >= min && a.getBasePrice() <= max)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }



    @Test
    void Test016_AuctionServiceWhenFilterAllAuctionsWithBrandThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        List<Tag> tags = tagService.getAllTags();
        List<Long> tagsList = tags.stream().map(Tag::getId).toList();

        String value = "Toyota";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().tags(tagsList).build();
        filter.setBrand(value);

        auctions = auctions.stream().filter(a-> a.getBrand().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }


    @Test
    void Test017_AuctionServiceWhenFilterAllAuctionsWithColorThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        List<Tag> tags = tagService.getAllTags();
        List<Long> tagsList = tags.stream().map(Tag::getId).toList();

        String value = "Color";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().tags(tagsList).build();
        filter.setColor(value);

        auctions = auctions.stream().filter(a-> a.getColor().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }


    @Test
    void Test018_AuctionServiceWhenFilterAllAuctionsWithGasTypeThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        List<Tag> tags = tagService.getAllTags();
        List<Long> tagsList = tags.stream().map(Tag::getId).toList();

        GasType value = GasType.DIESEL;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().tags(tagsList).build();
        filter.setGasType(value);

        auctions = auctions.stream().filter(a-> a.getGasType().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }


    @Test
    void Test019_AuctionServiceWhenFilterAllAuctionsWithGearshiftTypeThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        List<Tag> tags = tagService.getAllTags();
        List<Long> tagsList = tags.stream().map(Tag::getId).toList();

        GearShiftType value = GearShiftType.MANUAL;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().tags(tagsList).build();
        filter.setGearShiftType(value);

        auctions = auctions.stream().filter(a-> a.getGearShiftType().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test020_AuctionServiceWhenFilterAllAuctionsWithModeThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        List<Tag> tags = tagService.getAllTags();
        List<Long> tagsList = tags.stream().map(Tag::getId).toList();

        String value = "2";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().tags(tagsList).build();
        filter.setModel(value);

        auctions = auctions.stream().filter(a-> a.getModel().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }



    @Test
    void Test021_AuctionServiceWhenFilterAllAuctionsWithDoorsAmountThenGetFew() throws Exception {
        List<Auction> auctions = new AuctionGenerator(userRepository, tagService)
                .generateAndSaveListOfAuctions(100, auctionRepository);

        List<Tag> tags = tagService.getAllTags();
        List<Long> tagsList = tags.stream().map(Tag::getId).toList();

        Integer value = 4;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().tags(tagsList).build();
        filter.setDoorsAmount(value);

        auctions = auctions.stream().filter(a-> a.getDoorsAmount().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }
     **/

    @Test
    void Test022_AuctionServiceWhenCreateAuctionUrlForUploadingImageWithAnExistingAuctionShouldReturnValidUrls() throws Exception {
        Auction auction = new AuctionGenerator(userRepository,tagService).generateRandomAuction();
        auctionRepository.save(auction);
        List<String> urls=auctionService.createUrlsForUploadingImages(1);
        assertEquals(urls.size(),7);
    }

    @Test
    void Test023_AuctionServiceWhenCreateAuctionUrlForUploadingImageWithAnExistingAuctionAndAlreadySentImageUrlShouldReturnUrlAlreadySentException() throws Exception {
        Auction auction = new AuctionGenerator(userRepository,tagService).generateRandomAuction();
        auctionRepository.save(auction);
        auctionService.createUrlsForUploadingImages(1);
        //Making the same request, should return exception.
        assertThrows(UrlAlreadySentException.class, () -> auctionService.createUrlsForUploadingImages(1));
    }
    @Test
    void Test024_AuctionServiceWhenCreateAuctionUrlForDownloadingImageWithAnExistingAuctionUrls() throws Exception {
        Auction auction = new AuctionGenerator(userRepository,tagService).generateRandomAuction();
        auctionRepository.save(auction);
        List<String> urls=auctionService.createUrlsForUploadingImages(1);
        List<String> downloadUrls=auctionService.createUrlsForDownloadingImages(1);
        assertEquals(urls.size(),downloadUrls.size());
    }
    @Test
    void Test025_AuctionServiceWhenCreateAuctionWithNonExistentTagsThenGetAuctionWithAllItsTags(){
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
    void Test026_AuctionServiceWhenCreateAuctionWithSomeExistentTagsThenGetAuctionWithAllItsTags(){
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
    void Test027_AuctionServiceWhenCreateAuctionWithAllExistentTagsThenGetAuctionWithAllItsTags(){
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

    @Test
    void Test028_AuctionServiceWhenGetAuctionByUSerIDAndHasTagsShouldReturnAAuctionWithAListWithAllTags(){
        List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5");
        tagRepository.saveAll(tags.stream().map(Tag::new).toList());

        auctionDTO.setTags(tags);

        auctionService.createAuction(auctionDTO);

        Pageable pageable = PageRequest.of(0, 10);

        Page<AuctionDTO> actualAuctions = auctionService.getAuctionsByUserId(1L, pageable);

        List<AuctionDTO> auctionList = actualAuctions.getContent();

        AuctionDTO firstAuction = auctionList.get(0);
        assertEquals(1L, firstAuction.getId(), "Expected auction ID to match");
        assertEquals("Subasta de automovil", firstAuction.getTitle(), "Expected auction title to match");
        assertEquals(LocalDateTime.of(2030, 8, 27, 2, 11, 0), firstAuction.getDeadline(), "Expected auction deadline to match");
        assertEquals(150000, firstAuction.getHighestBidAmount(), "Expected highest bid amount to match");
        assertEquals( "OPEN", firstAuction.getStatus().toString(), "Expected auction status to match");
        assertEquals(tags.size(), firstAuction.getTagNames().size(), "Expected auction tags to match");
        assertEquals(tags, firstAuction.getTagNames(), "Expected auction tags to match");
    }

    @Test
    void Test029_AuctionServiceWhenGetAuctionByUSerIDAndHasNoTagsShouldReturnAAuctionWithAListWithAllTags(){
        auctionService.createAuction(auctionDTO);

        Pageable pageable = PageRequest.of(0, 10);

        Page<AuctionDTO> actualAuctions = auctionService.getAuctionsByUserId(1L, pageable);

        List<AuctionDTO> auctionList = actualAuctions.getContent();

        AuctionDTO firstAuction = auctionList.get(0);
        assertEquals(1L, firstAuction.getId(), "Expected auction ID to match");
        assertEquals("Subasta de automovil", firstAuction.getTitle(), "Expected auction title to match");
        assertEquals(LocalDateTime.of(2030, 8, 27, 2, 11, 0), firstAuction.getDeadline(), "Expected auction deadline to match");
        assertEquals(150000, firstAuction.getHighestBidAmount(), "Expected highest bid amount to match");
        assertEquals( "OPEN", firstAuction.getStatus().toString(), "Expected auction status to match");
        assertEquals(0, firstAuction.getTagNames().size(), "Expected auction tags to match");
    }

    @Test
    void Test030_AuctionServiceWhenGetAuctionByIDAndHasTagsShouldReturnAAuctionWithAListWithAllTags(){
        List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5");
        tagRepository.saveAll(tags.stream().map(Tag::new).toList());

        auctionDTO.setTags(tags);

        auctionService.createAuction(auctionDTO);

        GetAuctionDTO auction = auctionService.getAuctionById(1L);

        assertEquals(tags.size(), auction.getTags().size(), "Expected auction tags to match");
        assertEquals(tags.get(0), auction.getTags().get(0).getTagName(), "Expected auction tags to match");
        assertEquals(tags.get(1), auction.getTags().get(1).getTagName(), "Expected auction tags to match");
        assertEquals(tags.get(2), auction.getTags().get(2).getTagName(), "Expected auction tags to match");
        assertEquals(tags.get(3), auction.getTags().get(3).getTagName(), "Expected auction tags to match");
        assertEquals(tags.get(4), auction.getTags().get(4).getTagName(), "Expected auction tags to match");

    }

    @Test
    void Test031_AuctionServiceWhenFilterAllAuctionsWithUpcomingDeadlineThenGetFew() throws Exception {
        new AuctionGenerator(userRepository, tagService).generateAndSaveListOfAuctions(100, auctionRepository);

        Pageable pageable = PageRequest.of(0, 100);

        Page<AuctionDTO> actualAuctions = auctionService.getAuctionsEnding(pageable);

        List<AuctionDTO> auctionList = actualAuctions.getContent();

        LocalDateTime currentTime = LocalDateTime.now();
        for (int i = 1; i < auctionList.size(); i++) {
            AuctionDTO auctionDTO = auctionList.get(i);
            assertTrue(auctionDTO.getDeadline().isAfter(currentTime));
            assertTrue(auctionDTO.getDeadline().isBefore(currentTime.plusDays(1)));
        }
    }

    @Test
    void Test032_AuctionServiceWhenFilterAllAuctionsWithNewAuctionsThenGetFew() throws Exception {
        new AuctionGenerator(userRepository, tagService).generateAndSaveListOfAuctions(100, auctionRepository);

        Pageable pageable = PageRequest.of(0, 100);

        Page<AuctionDTO> actualAuctions = auctionService.getAuctionsNew(pageable);

        List<AuctionDTO> auctionList = actualAuctions.getContent();

        AuctionDTO firstAuction = auctionList.get(0);

        LocalDateTime createdAt = auctionRepository.findById(firstAuction.getId()).get().getCreatedAt();
        System.out.println("first" + createdAt);
        for (int i = 1; i < auctionList.size(); i++) {
            AuctionDTO auctionDTO = auctionList.get(i);
            System.out.println();
            assertTrue(auctionRepository.findById(auctionDTO.getId()).get().getCreatedAt().isBefore(createdAt));
            System.out.println(auctionRepository.findById(auctionDTO.getId()).get().getCreatedAt());
            createdAt = auctionRepository.findById(auctionDTO.getId()).get().getCreatedAt();

        }
    }

    @Test
    void Test033_AuctionServiceWhenGetAuctionByIdShouldReturnGetAuctionDTOWithTop5NewestBids() throws Exception {
        CreateAuctionDTO auctionDTO = new CreateAuctionDTO(1L, "Subasta de automovil", "text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0), "Toyota",
                "Corolla", 150000, 30000, GasType.GASOLINE, 2022, "Silver",
                4, GearShiftType.AUTOMATIC, null);
        auctionService.createAuction(auctionDTO);
        CreateBidDTO bidDto=new CreateBidDTO(150001,2L,1L);
        bidService.crateBid(bidDto);
        CreateBidDTO bidDto2=new CreateBidDTO(160000,2L,1L);
        bidService.crateBid(bidDto2);
        CreateBidDTO bidDto3=new CreateBidDTO(170000,2L,1L);
        bidService.crateBid(bidDto3);
        CreateBidDTO bidDto4=new CreateBidDTO(180000,2L,1L);
        bidService.crateBid(bidDto4);
        CreateBidDTO bidDto5=new CreateBidDTO(190000,2L,1L);
        bidService.crateBid(bidDto5);

        GetAuctionDTO auction=auctionService.getAuctionById(1L);
        assertEquals(5,auction.getTopBids().size());
        assertEquals(190000,auction.getTopBids().get(0).getAmount());
        assertEquals(180000,auction.getTopBids().get(1).getAmount());
        assertEquals(170000,auction.getTopBids().get(2).getAmount());
        assertEquals(160000,auction.getTopBids().get(3).getAmount());
        assertEquals(150001,auction.getTopBids().get(4).getAmount());
    }
    @Test
    void Test034_AuctionServiceWhenGetAuctionsBiddedByUserShouldReturnAllAuctionsSortedByDeadline(){
        CreateAuctionDTO auctionDTO = new CreateAuctionDTO(1L, "Subasta de automovil1", "text",
                LocalDateTime.of(2035, 9, 27, 2, 11, 0), "Toyota",
                "Corolla", 150000, 30000, GasType.GASOLINE, 2022, "Silver",
                4, GearShiftType.AUTOMATIC, null);
        auctionService.createAuction(auctionDTO);

        CreateAuctionDTO auctionDTO2 = new CreateAuctionDTO(1L, "Subasta de automovil2", "text",
                LocalDateTime.of(2031, 6, 27, 2, 11, 0), "Toyota",
                "Corolla", 150000, 30000, GasType.GASOLINE, 2022, "Silver",
                4, GearShiftType.AUTOMATIC, null);
        auctionService.createAuction(auctionDTO2);

        CreateAuctionDTO auctionDTO3 = new CreateAuctionDTO(1L, "Subasta de automovil3", "text",
                LocalDateTime.of(2032, 3, 27, 2, 11, 0), "Toyota",
                "Corolla", 150000, 30000, GasType.GASOLINE, 2022, "Silver",
                4, GearShiftType.AUTOMATIC, null);
        auctionService.createAuction(auctionDTO3);

        CreateBidDTO bidDto=new CreateBidDTO(150001,2L,1L);
        bidService.crateBid(bidDto);
        CreateBidDTO bidDto2=new CreateBidDTO(160000,2L,2L);
        bidService.crateBid(bidDto2);
        CreateBidDTO bidDto3=new CreateBidDTO(170000,2L,3L);
        bidService.crateBid(bidDto3);

        Pageable pageable = PageRequest.of(0, 10);
        Page<AuctionDTO> actualAuctions = auctionService.getAuctionsBiddedByUser(2L, pageable);
        List<AuctionDTO> auctionList = actualAuctions.getContent();
        assertEquals(3,auctionList.size());
        assertEquals("Subasta de automovil2",auctionList.get(0).getTitle());
        assertEquals("Subasta de automovil3",auctionList.get(1).getTitle());
        assertEquals("Subasta de automovil1",auctionList.get(2).getTitle());

    }
    @Test
    void Test035_AuctionServiceWhenDeleteAuctionWithValidIdShouldBeDoneWithoutAnyException() {
        CreateUserDTO userDTO= new CreateUserDTO("Marcos", "Benji", "marcosbenji@dusyum.com",
                "+5491112341678", "1Afjfslkjfl");
        userService.createUser(userDTO);
        auctionService.createAuction(auctionDTO);
        long bidforUser = bidService.crateBid(new CreateBidDTO(150000, 2L, 1L)).getId();
        long bidforUser2 = bidService.crateBid(new CreateBidDTO(5000000, 3L, 1L)).getId();
        SignInRequest signInRequest=new SignInRequest("bejero7623@dusyum.com","1Afjfslkjfl");
        String token = authenticateAndGetToken(signInRequest, restTemplate);
        auctionService.deleteAuction(1L,"Bearer "+token);
        assertFalse(auctionRepository.existsById(1L));
        assertFalse(bidRepository.existsById(bidforUser));
        assertFalse(bidRepository.existsById(bidforUser2));
    }

    @Test
    void Test036_AuctionServiceWhenDeleteAuctionAndUserNotOwnsTheAuctionShouldThrowEntityNotFoundException() {
        CreateUserDTO userDTO= new CreateUserDTO("Marcos", "Benji", "marcosbenji@dusyum.com",
                "+5491112341678", "1Afjfslkjfl");
        userService.createUser(userDTO);
        auctionService.createAuction(auctionDTO);
        SignInRequest signInRequest=new SignInRequest("marcosbenji@dusyum.com","1Afjfslkjfl");
        String token = authenticateAndGetToken(signInRequest, restTemplate);
        assertThrows(EntityNotFoundException.class,()->auctionService.deleteAuction(1L,"Bearer "+token));
    }

    @Test
    void Test037_AuctionServiceWhenDeleteAuctionWithValidIdAndIsExpiredShouldThrowAuctionExpiredException(){
        auctionDTO.setDeadline(LocalDateTime.now().minusDays(1));
        auctionService.createAuction(auctionDTO);
        SignInRequest signInRequest=new SignInRequest("bejero7623@dusyum.com","1Afjfslkjfl");
        String token = authenticateAndGetToken(signInRequest, restTemplate);
        assertThrows(AuctionExpiredException.class,()->auctionService.deleteAuction(1L,"Bearer "+token));}

    @Test
    void Test038_AuctionServiceWhenUpdateAuctionStatusThenAllAuctionsUpdatedCorrectly(){
        auctionService.createAuction(auctionDTO);
        auctionService.createAuction(auctionDTO);
        auctionService.createAuction(auctionDTO);
        auctionService.createAuction(auctionDTO);
        auctionDTO.setDeadline(LocalDateTime.of(2010, 10, 10, 10, 10));
        auctionService.createAuction(auctionDTO);
        auctionService.createAuction(auctionDTO);
        auctionService.createAuction(auctionDTO);
        auctionService.createAuction(auctionDTO);

        auctionService.updateAuctionStatus();

        List<Auction> auctions = auctionRepository.findAuctionByStatusAndDeadlineLessThan(AuctionStatus.OPEN,
                LocalDateTime.of(3000, 12, 30, 23, 59));
        assertEquals(4, auctions.size());
    }

    @Test
    void Test040_AuctionServiceWhenRemoveAuctionItShouldRemoveTagsThatAreNotInUsed(){
        List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5");
        tagRepository.saveAll(tags.stream().map(Tag::new).toList());
        auctionDTO.setTags(tags);
        auctionService.createAuction(auctionDTO);
        CreateAuctionDTO auctionDTO2 = new CreateAuctionDTO(1L, "Subasta de automovil2", "text",
                LocalDateTime.of(2031, 6, 27, 2, 11, 0), "Toyota",
                "Corolla", 150000, 30000, GasType.GASOLINE, 2022, "Silver",
                4, GearShiftType.AUTOMATIC, List.of("tag4"));
        auctionService.createAuction(auctionDTO2);
        SignInRequest signInRequest=new SignInRequest("bejero7623@dusyum.com","1Afjfslkjfl");
        String token = authenticateAndGetToken(signInRequest, restTemplate);
        auctionService.deleteAuction(1L,"Bearer "+token);
        assertEquals(1,tagRepository.findAll().size());
        assertEquals("tag4",tagRepository.findAll().get(0).getTagName());
    }

    @Test
    void Test041_AuctionServiceWhenFinishAuctionByTheWinnerThenAllCorrect(){
        CreateAuctionDTO auction = auctionService.createAuction(auctionDTO);
        bidService.crateBid(new CreateBidDTO(1000000, 2L, 1L));

        Optional<Auction> auctionHelper = auctionRepository.findById(auction.getAuctionId());
        if (auctionHelper.isEmpty()) throw new RuntimeException("Expected to found auction");
        auctionHelper.get().setStatus(AuctionStatus.AWATINGDELIVERY);
        auctionRepository.save(auctionHelper.get());

        auctionService.finishAuction(auction.getAuctionId(), 2L);

        Optional<Auction> auctionR = auctionRepository.findById(auction.getAuctionId());

        assertTrue(auctionR.isPresent());
        assertEquals(auction.getAuctionId(), auctionR.get().getId());
        assertEquals(AuctionStatus.FINISHED, auctionR.get().getStatus());
    }

    @Test
    void Test042_AuctionServiceWhenFinishAuctionByUserBeforeFinishingThenBadCredentialsException(){
        CreateAuctionDTO auction = auctionService.createAuction(auctionDTO);
        bidService.crateBid(new CreateBidDTO(1000000, 2L, 1L));

        assertThrows(BadCredentialsException.class, ()->auctionService.finishAuction(auction.getAuctionId(), 2L));
    }

    @Test
    void Test043_AuctionServiceWhenFinishAuctionNotByTheWinnerThenEntityNotFoundException(){
        CreateAuctionDTO auction = auctionService.createAuction(auctionDTO);
        bidService.crateBid(new CreateBidDTO(1000000, 2L, 1L));

        Optional<Auction> auctionHelper = auctionRepository.findById(auction.getAuctionId());
        if (auctionHelper.isEmpty()) throw new RuntimeException("Expected to found auction");
        auctionHelper.get().setStatus(AuctionStatus.AWATINGDELIVERY);
        auctionRepository.save(auctionHelper.get());

        assertThrows(EntityNotFoundException.class, ()->auctionService.finishAuction(auction.getAuctionId(), 3L));
    }
}
