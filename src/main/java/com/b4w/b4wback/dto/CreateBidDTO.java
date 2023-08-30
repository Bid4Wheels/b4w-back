package com.b4w.b4wback.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CreateBidDTO {
    @NotNull(message = "The amount can't be blank")
    private Integer amount;
    @NotNull(message = "The userId can't be blank")
    private Long userId;
    @NotNull(message = "The auctionId can't be blank")
    private Long auctionId;
}
