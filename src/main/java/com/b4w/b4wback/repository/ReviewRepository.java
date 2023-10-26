package com.b4w.b4wback.repository;

import com.b4w.b4wback.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
