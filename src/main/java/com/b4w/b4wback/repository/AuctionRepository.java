package com.b4w.b4wback.repository;

import com.b4w.b4wback.dto.CreateAuctionDTO;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.model.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction,Long> {
    List<CreateAuctionDTO> findAllByUserId(Long id);


    @Query("SELECT auction, " +
            "COALESCE((SELECT MAX(bid.amount) FROM Bid bid WHERE bid.auction.id = auction.id), auction.basePrice) AS price" +
            " FROM Auction auction WHERE " +
            "(:milageMin IS NULL OR auction.milage >= :milageMin) AND " +
            "(:milageMax IS NULL OR auction.milage <= :milageMax) AND " +
            "(:modelYearMin IS NULL OR auction.modelYear >= :modelYearMin) AND " +
            "(:modelYearMax IS NULL OR auction.modelYear <= :modelYearMax) AND " +
            "(:brand IS NULL OR auction.brand = :brand) AND " +
            "(:color IS NULL OR auction.color = :color) AND " +
            "(:gasType IS NULL OR auction.gasType = :gasType) AND " +
            "(:doorsAmount IS NULL OR auction.doorsAmount = :doorsAmount) AND " +
            "(:gearShiftType IS NULL OR auction.gearShiftType = :gearShiftType) AND " +
            "(:model IS NULL OR auction.model = :model) AND " +
            "(:priceMin IS NULL OR COALESCE((SELECT " +
                "MAX(bid.amount) FROM Bid bid WHERE bid.auction.id = auction.id), auction.basePrice " +
            ") >= :priceMin) AND" +
            "(:priceMax IS NULL OR COALESCE((SELECT " +
                "MAX(bid.amount) FROM Bid bid WHERE bid.auction.id = auction.id), auction.basePrice " +
            ") <= :priceMax)"
    )
    Page<Auction> findWithFilter(@Param("milageMin") Integer milageMin, @Param("milageMax") Integer milageMax,
                                    @Param("modelYearMin") Integer modelYearMin, @Param("modelYearMax") Integer modelYearMax,
                                    @Param("priceMin") Integer priceMin, @Param("priceMax") Integer priceMax,
                                    @Param("brand") String brand,
                                    @Param("color") String color,
                                    @Param("gasType") GasType gasType,
                                    @Param("doorsAmount") Integer doorsAmount,
                                    @Param("gearShiftType") GearShiftType gearShiftType,
                                    @Param("model") String model,
                                    Pageable pageable);
}
