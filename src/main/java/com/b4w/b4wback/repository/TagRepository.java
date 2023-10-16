package com.b4w.b4wback.repository;

import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByTagNameIn(Iterable<String> tag);

    @Query("SELECT t.auctions FROM Tag t WHERE t.tagName = :tagName")
    List<Auction> findAuctionsByTagId(@Param("tagName") String tagName);
}
