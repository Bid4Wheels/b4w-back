package com.b4w.b4wback.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "tag")
    private List<TagAuction> tagAuctions = new ArrayList<>();

    public Tag(String tagName){
        this.tagName = tagName;
    }
}
