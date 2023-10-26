package com.b4w.b4wback.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
public class BidNotificationDTO {
    private final int amount;

    private final String firstName;

    private final String lastName;
    public BidNotificationDTO(int amount, String firstName, String lastName) {
        this.amount = amount;
        this.firstName = firstName;
        this.lastName = lastName;
    }



}