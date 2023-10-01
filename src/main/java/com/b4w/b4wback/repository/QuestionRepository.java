package com.b4w.b4wback.repository;

import com.b4w.b4wback.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
