package com.b4w.b4wback.dto;

import com.b4w.b4wback.enums.AuctionStatus;
import com.b4w.b4wback.enums.GasType;
import com.b4w.b4wback.enums.GearShiftType;
import com.b4w.b4wback.validation.DurationMinAfterCreation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;



import java.time.LocalDateTime;
@Getter
@Setter
public class CreateAuctionDTO {
    @NotNull(message = "User id can't be blank.")
    private Long id;
    @NotBlank(message = "Description can't be blank.")
    @Size(max = 500, message = "The content must not exceed 500 characters.")
    private String description;
    @NotNull(message = "Deadline can't be null.")
    @DurationMinAfterCreation
    private LocalDateTime deadline;
    private LocalDateTime createdAt= LocalDateTime.now();

    @NotBlank(message = "Brand can't be blank.")
    private String brand;
    @NotBlank(message = "Model can't be blank.")
    private String model;

    @NotNull(message = "Base price can't be blank.")
    private Integer basePrice;

    private AuctionStatus status= AuctionStatus.OPEN;

    @NotNull(message = "Milage can't be blank.")
    private Integer milage;

    @NotNull(message = "Gas type can't be blank.")
    private GasType gasType;

    @NotNull(message = "Year model can't be blank.")
    private Integer modelYear;

    @NotBlank(message = "Color can't be blank.")
    private String color;

    @NotNull(message = "Doors amount can't be blank.")
    private Integer doorsAmount;

    @NotNull(message = "Gear type can't be blank.")
    private GearShiftType gearShiftType;

    public CreateAuctionDTO(Long id, String description, LocalDateTime deadline, String brand, String model,
                            Integer basePrice, Integer milage, GasType gasType, Integer modelYear,
                            String color, Integer doorsAmount, GearShiftType gearShiftType) {
        this.id = id;
        this.description = description;
        this.deadline = deadline;
        this.brand = brand;
        this.model = model;
        this.basePrice = basePrice;
        this.milage = milage;
        this.gasType = gasType;
        this.modelYear = modelYear;
        this.color = color;
        this.doorsAmount = doorsAmount;
        this.gearShiftType = gearShiftType;
    }
}
