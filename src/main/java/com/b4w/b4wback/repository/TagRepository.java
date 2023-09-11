package com.b4w.b4wback.repository;

import com.b4w.b4wback.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByTagNameIn(Iterable<String> tag);

}
