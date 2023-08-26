package com.b4w.b4wback.dto;

import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.time.DurationMin;


import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuctionDTO {
    @NotBlank(message = "Description can't be blank.")
    @Size(max = 500, message = "The content must not exceed 500 characters.")
    private String description;

    @NotBlank(message = "Deadline can't be blank.")
    @DurationMin(hours = 24L,message = "Duration must be more o equals to 24 hours.")
    private LocalDateTime deadline;
    private LocalDateTime createdAt;

    @NotBlank(message = "Brand can't be blank.")
    private String brand;
    @NotBlank(message = "Model can't be blank.")
    private String model;

    @NotBlank(message = "Base price can't be blank.")
    private Integer basePrice;

    private AuctionStatus status= AuctionStatus.OPEN;

    @NotBlank(message = "Milage can't be blank.")
    private Integer milage;

    @NotBlank(message = "Gas type can't be blank.")
    private GasType gasType;

    @NotBlank(message = "Year model can't be blank.")
    private Integer modelYear;

    @NotBlank(message = "Color can't be blank.")
    private String color;

    @NotBlank(message = "Doors amount can't be blank.")
    private Integer doorsAmount;

    @NotBlank(message = "Gear type can't be blank.")
    private GearShiftType gearShiftType;
}
