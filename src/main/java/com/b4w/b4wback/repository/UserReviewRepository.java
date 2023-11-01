package com.b4w.b4wback.repository;

import com.b4w.b4wback.model.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserReviewRepository extends JpaRepository<UserReview, Long> {
    @Query("SELECT ur FROM UserReview ur WHERE ur.punctuation > :punctuation AND (ur.reviewer.id = :userId OR ur.reviewed.id = :userId)")
    List<UserReview> findUserReviewByPunctuationAfterAndReviewerIdOrReviewedId(float punctuation, long userId);
}
