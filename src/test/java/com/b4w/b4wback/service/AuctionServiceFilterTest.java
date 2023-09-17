package com.b4w.b4wback.service;

import com.b4w.b4wback.dto.AuctionDTO;
import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.dto.CreateUserDTO;
import com.b4w.b4wback.dto.FilterAuctionDTO;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Tag;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.service.interfaces.AuctionService;
import com.b4w.b4wback.service.interfaces.TagService;
import com.b4w.b4wback.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuctionServiceFilterTest {

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private AuctionRepository auctionRepository;
    private List<Tag> tags;

    private List<Auction> auctions;
    private CreateAuctionDTO auctionDTO;

    @BeforeEach
    public void setup() throws Exception {
        CreateUserDTO userDTO = new CreateUserDTO("Nico", "Borja", "bejero7623@dusyum.com",
                "+5491112345678", "1Afjfslkjfl");
        CreateUserDTO userDTO2 = new CreateUserDTO("Esteban", "Chiquito", "bejero@dusyum,com","+5491112345678",
                "1Afjfslkjfl");
        userService.createUser(userDTO);
        userService.createUser(userDTO2);


        GenerateTags();
        GenerateAuctions();
    }


    @Test
    void Test001_AuctionServiceWhenFilterAllAuctionsThenGetAll() {
        List<Long> tagsList = new ArrayList<>();
        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setTags(tagsList);

        Page<AuctionDTO> auctionsGot = auctionService.getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), auctionsGot.getTotalElements());
    }

    @Test
    void Test002_AuctionServiceWhenFilterWithTagsOnlyTHenGetAllAuctionsThatHaveAllTheExistingTags(){
        List<Long> tagsList = tags.subList(0,2).stream().map(Tag::getId).toList();
        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setTags(tagsList);

        List<Auction> filteredAuctions = auctions.stream().filter(a->AuctionHasAllTags(a, tags.subList(0,2))).toList();

        Page<AuctionDTO> auctionsGot = auctionService.getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(filteredAuctions.size(), auctionsGot.getTotalElements());
    }

    @Test
    void Test003_AuctionServiceWhenFilterAllAuctionsWithMilageThenGetFew(){
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
    void Test004_AuctionServiceWhenFilterAllAuctionsWithModelYearThenGetFew(){
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
    void Test005_AuctionServiceWhenFilterAllAuctionsWithPriceAndNoBidsThenGetFew() {
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
    void Test006_AuctionServiceWhenFilterAllAuctionsWithBrandThenGetFew() {
        String value = "Toyota";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setBrand(value);

        auctions = auctions.stream().filter(a-> a.getBrand().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test007_AuctionServiceWhenFilterAllAuctionsWithColorThenGetFew() {
        String value = "Blue";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setColor(value);

        auctions = auctions.stream().filter(a-> a.getColor().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test008_AuctionServiceWhenFilterAllAuctionsWithGasTypeThenGetFew() {
        GasType value = GasType.DIESEL;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setGasType(value);

        auctions = auctions.stream().filter(a-> a.getGasType().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test009_AuctionServiceWhenFilterAllAuctionsWithGearshiftTypeThenGetFew() {
        GearShiftType value = GearShiftType.MANUAL;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setGearShiftType(value);

        auctions = auctions.stream().filter(a-> a.getGearShiftType().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test010_AuctionServiceWhenFilterAllAuctionsWithModelThenGetFew() {
        String value = "2";

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setModel(value);

        auctions = auctions.stream().filter(a-> a.getModel().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    @Test
    void Test011_AuctionServiceWhenFilterAllAuctionsWithDoorsAmountThenGetFew() {
        Integer value = 4;

        FilterAuctionDTO filter = FilterAuctionDTO.builder().build();
        filter.setDoorsAmount(value);

        auctions = auctions.stream().filter(a-> a.getDoorsAmount().equals(value)).toList();

        Page<AuctionDTO> page = auctionService
                .getAuctionsFiltered(filter, Pageable.ofSize(10));
        assertEquals(auctions.size(), page.getTotalElements());
    }

    private boolean AuctionHasAllTags(Auction auction, List<Tag> tagsIds){
        for (Tag tagsId : tagsIds)
            if (!AuctionHasTag(auction, tagsId.getId()))
                return false;
        return true;
    }
    private boolean AuctionHasTag(Auction auction, Long tagId){
        for (Tag auctionTag : auction.getTags()) {
            if (auctionTag.getId() == tagId) return true;
        }
        return false;
    }

    private void GenerateAuctions(){
        List<String> tagNames = new ArrayList<>(tags.stream().map(Tag::getTagName).toList());

        auctionDTO = new CreateAuctionDTO(1L,"Subasta de automovil","text",
                LocalDateTime.of(2030, 8, 27, 2, 11, 0),"Toyota",
                "Corolla",150000,30000, GasType.GASOLINE,2022,"Silver",
                4, GearShiftType.AUTOMATIC, null);

        auctionService.createAuction(auctionDTO);

        auctionDTO.setTags(tagNames.subList(0,0));
        auctionService.createAuction(auctionDTO);

        auctionDTO.setTags(tagNames.subList(0,1));
        auctionDTO.setBasePrice(4500);
        auctionDTO.setGearShiftType(GearShiftType.MANUAL);
        auctionService.createAuction(auctionDTO);

        auctionDTO.setTags(tagNames.subList(0,3));
        auctionDTO.setMilage(1000);
        auctionService.createAuction(auctionDTO);

        auctionDTO.setTags(tagNames.subList(1,1));
        auctionDTO.setBrand("BMW");
        auctionService.createAuction(auctionDTO);

        auctionDTO.setTags(tagNames.subList(1,2));
        auctionDTO.setGasType(GasType.GASOLINE);
        auctionService.createAuction(auctionDTO);

        auctionDTO.setTags(tagNames.subList(1,3));
        auctionDTO.setModelYear(2009);
        auctionDTO.setDoorsAmount(3);
        auctionDTO.setModel("2");
        auctionService.createAuction(auctionDTO);

        auctionDTO.setTags(tagNames.subList(2,4));
        auctionDTO.setColor("Blue");
        auctionService.createAuction(auctionDTO);

        auctions = auctionRepository.findAll();
    }

    private void GenerateTags(){
        tags = tagService.getOrCreateTagsFromStringList(List.of("tag1", "tag2", "tag3", "tag4", "tag5"));
    }
}
