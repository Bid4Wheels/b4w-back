package com.b4w.b4wback.repository;

import com.b4w.b4wback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AuthUserRepository  extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
}
