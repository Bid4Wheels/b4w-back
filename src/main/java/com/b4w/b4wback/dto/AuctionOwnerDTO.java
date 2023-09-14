package com.b4w.b4wback.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuctionOwnerDTO {
    private long id;
    private String name;
    private String lastName;
    private String profilePicture;
}
