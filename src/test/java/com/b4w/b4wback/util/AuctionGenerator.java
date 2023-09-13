package com.b4w.b4wback.util;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.repository.AuctionRepository;
import com.b4w.b4wback.repository.TagRepository;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.TagService;

import java.time.LocalDateTime;
import java.util.*;

public class AuctionGenerator {
    private UserRepository userRepository;
    private TagService tagService;
    private List<String> titles;
    private List<String> brands;
    private List<String> models;
    private List<String> colors;

    private List<String> tags;
    private int basePriceMin;
    private int basePriceMax;
    private int milageMin;
    private int milageMax;

    private int minModelYear;
    private int maxModelYear;
    private int minDoorsAmount;
    private int maxDoorsAmount;

    private final static Random random = new Random();

    public List<String> GetTags() {
        return tags;
    }

    public AuctionGenerator(UserRepository userRepository, TagService tagService){
        this.userRepository = userRepository;

        titles = new ArrayList<>(List.of(new String[]{"On sale", "Fallout", "Linux"}));
        brands = new ArrayList<>(List.of(new String[]{"Audi", "Toyota", "Fiat"}));
        models = new ArrayList<>(List.of(new String[]{"1", "2", "3"}));
        colors = new ArrayList<>(List.of(new String[]{"Black", "White", "Blue", "Green"}));
        tags = new ArrayList<>(List.of("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10"));

        basePriceMin = 1000;
        basePriceMax = 100000;
        milageMin = 0;
        milageMax = 500000;
        minModelYear = 1980;
        maxModelYear = 2023;
        minDoorsAmount = 2;
        maxDoorsAmount = 5;
        this.tagService = tagService;
    }

    public List<Auction> generateAndSaveListOfAuctions(int amount, AuctionRepository auctionRepository) throws Exception {
        List<Auction> auctions = new ArrayList<>();
        while (amount>0){
            auctions.add(generateAndSaveRandomAuction(auctionRepository));
            amount--;
        }
        return auctions;
    }
    public List<Auction> generateListOfAuctions(int amount) throws Exception {
        List<Auction> auctions = new ArrayList<>();
        while (amount>0){
            auctions.add(generateRandomAuction());
            amount--;
        }
        return auctions;
    }

    public Auction generateAndSaveRandomAuction(AuctionRepository auctionRepository) throws Exception {
        return auctionRepository.save(generateRandomAuction());
    }

    public Auction generateRandomAuction() throws Exception {
        List<String> tagsAux = pickRandomTags(random.nextInt(tags.isEmpty() ? 0 : tags.size() > 6? 6 : tags.size() - 1));
        return new Auction(new CreateAuctionDTO(
                getRandomUserId(),
                titles.get(random.nextInt(titles.size()-1)),
                "",
                generateRandomTime(),
                brands.get(random.nextInt(brands.size()-1)),
                models.get(random.nextInt(models.size())),
                random.nextInt(basePriceMin, basePriceMax),
                random.nextInt(milageMin, milageMax),
                GasType.values()[random.nextInt(GasType.values().length-1)],
                random.nextInt(minModelYear, maxModelYear),
                colors.get(random.nextInt(colors.size()-1)),
                random.nextInt(minDoorsAmount, maxDoorsAmount),
                GearShiftType.values()[random.nextInt(GearShiftType.values().length)],
                tagsAux),
                tagService.getOrCreateTagsFromStringList(tagsAux));
    }

    private LocalDateTime generateRandomTime(){
       return LocalDateTime.of(random.nextInt(2020, 2030),
               random.nextInt(1, 12),
               random.nextInt(1, 28),
               random.nextInt(24),
               random.nextInt(60));
    }

    private long getRandomUserId() throws Exception {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) throw new Exception("There must be at least one user in the user repository");
        return users.get(random.nextInt(users.size()-1)).getId();
    }

    private List<String> pickRandomTags(int tagsAmount){
        Set<String> randomTags = new HashSet<>(tagsAmount);
        while (tagsAmount > randomTags.size())
            randomTags.add(tags.get(random.nextInt(tags.size() - 1)));
        return randomTags.stream().toList();
    }

    }

