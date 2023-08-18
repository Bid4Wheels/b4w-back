package com.b4w.b4wback.repository;

import com.b4w.b4wback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository  extends JpaRepository<User,Long> {
    Optional<User> findUserByEmail(String email);
}
