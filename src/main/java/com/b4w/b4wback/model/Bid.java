package com.b4w.b4wback.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column
    @NotNull
    private Date date;
    @Column
    @NotNull
    private Integer amount;
    @ManyToOne
    @NotNull
    private User bidder;

    @ManyToOne
    @NotNull
    private Auction auction;

    public Bid(Integer amount, User bidder, Auction auction){
        date = Date.from(Instant.now());
        this.amount = amount;
        this.bidder = bidder;
        this.auction = auction;
    }
}
