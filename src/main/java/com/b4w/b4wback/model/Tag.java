package com.b4w.b4wback.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String tagName;

    @ManyToMany(mappedBy = "tags")
    private List<Auction> auctions;
    public Tag(String tagName){
        this.tagName = tagName;
    }
}
