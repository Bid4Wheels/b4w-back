package com.b4w.b4wback.dto;

import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import lombok.*;
import org.springframework.data.repository.query.Param;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterAuctionDTO {
    private Integer milageMin;
    private Integer milageMax;
    private Integer modelYearMin;
    private Integer modelYearMax;
    private Integer priceMin;
    private Integer priceMax;
    private String brand;
    private String color;
    private GasType gasType;
    private Integer doorsAmount;
    private GearShiftType gearShiftType;
    private String model;
}
