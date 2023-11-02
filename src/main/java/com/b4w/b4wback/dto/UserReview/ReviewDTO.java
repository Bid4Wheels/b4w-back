package com.b4w.b4wback.dto.UserReview;

import com.b4w.b4wback.dto.UserDTO;
import com.b4w.b4wback.enums.UserReviewType;
import com.b4w.b4wback.model.UserReview;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReviewDTO {
    private String review;
    private float rating;
    private UserReviewType type;
    private UserDTO reviewer;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDTO reviewed;
    private LocalDateTime createdAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long auctionId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String auctionName;

    public ReviewDTO(UserReview userReview){
        review = userReview.getReview();
        rating = userReview.getPunctuation();
        type = userReview.getType();
        reviewer = new UserDTO(userReview.getReviewer());
        reviewed = new UserDTO(userReview.getReviewed());
        createdAt = userReview.getDate();
    }

    public ReviewDTO(UserReview userReview, UserDTO reviewer, UserDTO reviewed){
        review = userReview.getReview();
        rating = userReview.getPunctuation();
        type = userReview.getType();
        this.reviewer = reviewer;
        this.reviewed = reviewed;
        createdAt = userReview.getDate();

        this.auctionId = userReview.getAuction().getId();
        this.auctionName = userReview.getAuction().getTitle();
    }

    //TODO: Is possible to make it similar to the upper constructor
    public ReviewDTO(UserReview userReview, UserDTO userReviewerDto,Long auctionId, String auctionName){
        review = userReview.getReview();
        rating = userReview.getPunctuation();
        type = userReview.getType();
        reviewer = userReviewerDto;
        createdAt = userReview.getDate();
        this.auctionId = auctionId;
        this.auctionName = auctionName;
    }
}
