package com.b4w.b4wback.repository;

import com.b4w.b4wback.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
}
