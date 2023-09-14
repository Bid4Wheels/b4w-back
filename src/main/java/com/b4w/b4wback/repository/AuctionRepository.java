package com.b4w.b4wback.repository;

import com.b4w.b4wback.dto.AuctionDTO;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
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
            ") <= :priceMax)"
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
                                    Pageable pageable);

    @Query("SELECT NEW com.b4w.b4wback.dto.AuctionDTO(auction.id, auction.title, auction.deadline, auction.status , " +
            "COALESCE((SELECT MAX(bid.amount) FROM Bid bid WHERE bid.auction.id = auction.id), auction.basePrice)) " +
            "FROM Auction auction " +
            "WHERE auction.deadline > :currentDateTime " +
            "ORDER BY auction.deadline ASC")
    Page<AuctionDTO> findUpcomingAuctions(@Param("currentDateTime") LocalDateTime currentDateTime, Pageable pageable);

    @Query("SELECT NEW com.b4w.b4wback.dto.AuctionDTO(auction.id, auction.title, auction.deadline, auction.status , " +
            "COALESCE((SELECT MAX(bid.amount) FROM Bid bid WHERE bid.auction.id = auction.id), auction.basePrice)) " +
            "FROM Auction auction " +
            "WHERE auction.createdAt < :currentDateTime " +
            "ORDER BY auction.createdAt DESC ")
    Page<AuctionDTO> findNewAuctions(@Param("currentDateTime") LocalDateTime currentDateTime,Pageable pageable);
}
