package com.b4w.b4wback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AuctionOwnerDTO {
    private long id;
    private String name;
    private String lastName;
    //private profilePicture profilePicture;
}