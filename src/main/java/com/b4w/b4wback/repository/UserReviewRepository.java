package com.b4w.b4wback.repository;

import com.b4w.b4wback.model.User;
import com.b4w.b4wback.model.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserReviewRepository extends JpaRepository<UserReview, Long> {
    List<UserReview> findAllByReviewed(User user);
}
