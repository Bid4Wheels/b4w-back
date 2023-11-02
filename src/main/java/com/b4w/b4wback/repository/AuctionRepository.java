package com.b4w.b4wback.repository;

import com.b4w.b4wback.dto.AuctionDTO;
import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface AuctionRepository extends JpaRepository<Auction,Long> {


    List<AuctionDTO> findAllByUserId(Long id);

    Page<Auction> findByUser(User user, Pageable pageable);


    List<Auction> findByUserAndStatus(User user, AuctionStatus status);


    /**
    @Query("SELECT NEW com.b4w.b4wback.dto.AuctionDTO(auction.id, auction.title, auction.deadline, auction.status , " +
            "COALESCE((SELECT MAX(bid.amount) FROM Bid bid WHERE bid.auction.id = auction.id), auction.basePrice)) " +
            "FROM Auction auction WHERE " +
            "(:milageMin IS NULL OR auction.milage >= :milageMin) AND " +
            "(:milageMax IS NULL OR auction.milage <= :milageMax) AND " +
            "(:modelYearMin IS NULL OR auction.modelYear >= :modelYearMin) AND " +
            "(:modelYearMax IS NULL OR auction.modelYear <= :modelYearMax) AND " +
            "(:brand IS NULL OR lower(auction.brand) = lower(:brand)) AND " +
            "(:color IS NULL OR lower(auction.color) = lower(:color)) AND " +
            "(:gasType IS NULL OR auction.gasType = :gasType) AND " +
            "(:doorsAmount IS NULL OR auction.doorsAmount = :doorsAmount) AND " +
            "(:gearShiftType IS NULL OR auction.gearShiftType = :gearShiftType) AND " +
            "(:model IS NULL OR lower(auction.model) = lower(:model)) AND " +
            "auction.status = com.b4w.b4wback.enums.AuctionStatus.OPEN AND " +
            "(:priceMin IS NULL OR COALESCE((SELECT " +
                "MAX(bid.amount) FROM Bid bid WHERE bid.auction.id = auction.id), auction.basePrice " +
            ") >= :priceMin) AND" +
            "(:priceMax IS NULL OR COALESCE((SELECT " +
                "MAX(bid.amount) FROM Bid bid WHERE bid.auction.id = auction.id), auction.basePrice " +
            ") <= :priceMax) AND " +
            "(:tagsIds IS NULL OR auction.tags IN (SELECT t FROM Tag t WHERE t.id IN :tagsIds))"
    )
    Page<AuctionDTO> findWithFilter(@Param("milageMin") Integer milageMin, @Param("milageMax") Integer milageMax,
                                    @Param("modelYearMin") Integer modelYearMin, @Param("modelYearMax") Integer modelYearMax,
                                    @Param("priceMin") Integer priceMin, @Param("priceMax") Integer priceMax,
                                    @Param("brand") String brand,
                                    @Param("color") String color,
                                    @Param("gasType") GasType gasType,
                                    @Param("doorsAmount") Integer doorsAmount,
                                    @Param("gearShiftType") GearShiftType gearShiftType,
                                    @Param("model") String model,
                                    @Param("tagsIds") List<Long> tagsIds,
                                    Pageable pageable);
     **/

    @Query(name = "getAuctionDTOWithFilter", nativeQuery = true)
    Page<AuctionDTO> findWithFilter(@Param("milageMin") Integer milageMin, @Param("milageMax") Integer milageMax,
                                    @Param("modelYearMin") Integer modelYearMin, @Param("modelYearMax") Integer modelYearMax,
                                    @Param("priceMin") Integer priceMin, @Param("priceMax") Integer priceMax,
                                    @Param("brand") String brand,
                                    @Param("color") String color,
                                    @Param("gasType") Integer gasType,
                                    @Param("doorsAmount") Integer doorsAmount,
                                    @Param("gearShiftType") Integer gearShiftType,
                                    @Param("model") String model,
                                    @Param("tagsIds") List<Long> tagsIds,
                                    Pageable pageable);


    @Query("SELECT NEW com.b4w.b4wback.dto.AuctionDTO(auction.id, auction.title, auction.deadline, auction.createdAt, auction.status , " +
            "COALESCE((SELECT MAX(bid.amount) FROM Bid bid WHERE bid.auction.id = auction.id), auction.basePrice)) " +
            "FROM Auction auction " +
            "WHERE auction.deadline > :currentDateTime " +
            "ORDER BY auction.deadline ASC")
    Page<AuctionDTO> findUpcomingAuctions(@Param("currentDateTime") LocalDateTime currentDateTime, Pageable pageable);

    @Query("SELECT NEW com.b4w.b4wback.dto.AuctionDTO(auction.id, auction.title, auction.deadline,auction.createdAt, auction.status , " +
            "COALESCE((SELECT MAX(bid.amount) FROM Bid bid WHERE bid.auction.id = auction.id), auction.basePrice)) " +
            "FROM Auction auction " +
            "WHERE auction.createdAt < :currentDateTime " +
            "ORDER BY auction.createdAt DESC ")
    Page<AuctionDTO> findNewAuctions(@Param("currentDateTime") LocalDateTime currentDateTime,Pageable pageable);


    Auction findAuctionByIdAndUserId(long auctionID, long userID);
    @Query("SELECT auction FROM Auction auction WHERE auction.id IN (SELECT bid.auction.id FROM Bid bid WHERE bid.bidder.id = :bidder_id) ORDER BY auction.deadline ASC")
    Page<Auction> findAuctionsByBidderIdOrderByDeadline(long bidder_id, Pageable pageable);

    @Query(value = "SELECT t.tag_name" +
                    " FROM auction a" +
                    " JOIN auction_tag at ON a.id = at.auction_id" +
                    " JOIN tag t ON at.tag_id = t.id" +
                    " WHERE a.id = :auction_id", nativeQuery = true)
    List<String> findTagsOfAuctionID(@Param("auction_id") long auction_id);

    List<Auction> findAuctionByStatusAndDeadlineLessThan(AuctionStatus status, LocalDateTime deadLineLimit);
}
